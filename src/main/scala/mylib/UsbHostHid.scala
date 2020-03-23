package mylib

import spinal.core._
import spinal.lib._

class UsbHostHid(
  C_setup_retry : Int = 4,
  C_setup_interval : Int = 17,
  C_report_interval : Int = 16,
  C_report_endpoint : Int = 1,
  C_report_length : Int = 10,
  C_report_length_strict : Boolean = false,
  C_keepalive_setup : Boolean = true,
  C_keepalive_status : Boolean = true,
  C_keepalive_report : Boolean = true,
  C_keepalive_phase_bits : Int = 12, // keepalive/sof frequency 12:low speed, 15:full speed
  // NOTE: C_keepalive_phase_bits=12 at C_usb_speed=0 ( 6 MHz)
  //    or C_keepalive_phase_bits=15 at C_usb_speed=1 (48 MHz)
  //  will send keepalive/SOF every 0.68 ms
  // USB standard requires keepalive < 1 ms but SOF every 1 ms +-1%
  // so far all full-speed devices tested accept SOF at 0.68 ms rate
  C_keepalive_phase : Int = 2048, // 4044:KEEPALIVE low speed, 2048:low/full speed good for both
  C_keepalive_type : Boolean = true, // 1:KEEPALIVE low speed (may work for full speed, LUT saver), 0:SOF full speed
  C_setup_rom_file : String = "usbh_setup_rom.mem",
  C_setup_rom_len : Int = 16,
  // FIXME: For C_usb_speed=1 reset timing is 4x shorter than required by USB standard
  C_usb_speed : Int = 0 // '0':6 MHz low speed '1':48 MHz full speed
) extends Component {
  val io = new Bundle {
    val usbDif = in Bool
    val usbDp = inout(Analog(Bool))
    val usbDn = inout(Analog(Bool))
    val led = out Bits(8 bits)
    val rxCount = out UInt(16 bits)
    val rxDone = out Bool
    val hidReport = out Bits(C_report_length * 8 bits)
    val hidValid = out Bool
  }

  val C_datastatus_enable = False

  val C_STATE_DETACHED = B"00"
  val C_STATE_SETUP = B"01"
  val C_STATE_REPORT = B"10"
  val C_STATE_DATA = B"11"

  // Registers
  val rSetupRomAddr = Reg(UInt(8 bits)) init 0
  val rSetupRomAddrAcked = Reg(UInt(8 bits)) init 0

  val rSetupByteCounter = Reg(UInt(3 bits)) init 0
  val rCtrlIn = Reg(Bool) init False
  val rDataStatus = Reg(Bool) init False
  val rPacketCounter = Reg(UInt(16 bits)) init 0
  val rState = Reg(Bits(2 bits)) init 0
  val rRetry = Reg(UInt(C_setup_retry+1 bits)) init 0
  val rSlow = Reg(UInt(18 bits)) init 0
  val rResetPending = Reg(Bool) init True
  val rResetAccepted = Reg(Bool) init False
  val startI = Reg(Bool) init False
  val rTimeout = Reg(Bool) init False
  val inTransferI = Reg(Bool) init False
  val sofTransferI = Reg(Bool) init False
  val respExpectedI = Reg(Bool) init False
  val tokenPidI = Reg(Bits(8 bits)) init 0
  val tokenDevI = Reg(Bits(7 bits)) init 0
  val tokenEpI = Reg(Bits(4 bits)) init 0
  val dataLenI = Reg(UInt(16 bits)) init 0
  val dataIdxI = Reg(Bool) init False

  val rSetAddressFound = Reg(Bool) init False
  val rDevAddressRequested = Reg(Bits(7 bits)) init 0
  val rDevAddressConfirmed = Reg(Bits(7 bits)) init 0
  val rStoredResponse = Reg(Bits(8 bits)) init 0

  val rWLength = Reg(UInt(16 bits)) init 0
  val rBytesRemaining = Reg(UInt(16 bits)) init 0
  val rAdvanceData = Reg(Bool) init False
  val rFirstByte0Found = Reg(Bool) init False

  val rTxOverDebug = Reg(Bool) init True
  val rSofCounter = Reg(UInt(11 bits)) init 0
  
  val rReportBuf = Mem(Bits(8 bits), C_report_length)
  val rRxCount = Reg(UInt(16 bits)) init 0
  val rRxDone = Reg(Bool) init False
  val rCrcErr = Reg(Bool) init False
  val rHidValid = Reg(Bool) init False
  
  val C_setup_rom = Mem(Bits(8 bits), wordCount = C_setup_rom_len)
  C_setup_rom.initialContent = Tools.readmemh(C_setup_rom_file)

  // Wires
  val sRxd = Bool
  val sRxdp = Bool
  val sRxdn = Bool
  val sTxdp = Bool
  val sTxdn = Bool
  val sTxoe = Bool
  val sOled = Bits(64 bits)
  val sLINECTRL = Bool
  val sTXVALID = Bool
  val sDATAOUT = Bits(8 bits)

  val rxDoneO = Bool
  val timeoutO = Bool

  val idleO = Bool
  val responseO = Bits(8 bits)
  val txPopO = Bool
  val txDoneO = Bool
  val rxCountO = UInt(16 bits)
  val rxDataO = Bits(8 bits)
  val crcErrO = Bool
  val rxPushO = Bool

  val txDataI = C_setup_rom(rSetupRomAddr.resized)
  val sSofDev = rSofCounter(10 downto 4).asBits
  val sSofEp = rSofCounter(3 downto 0).asBits

  val reverseTokenDevI = tokenDevI(0).asBits ##
                         tokenDevI(1).asBits ##
                         tokenDevI(2).asBits ##
                         tokenDevI(3).asBits ##
                         tokenDevI(4).asBits ##
                         tokenDevI(5).asBits ##
                         tokenDevI(6).asBits

  val reverseTokenEpI = tokenEpI(0).asBits ##
                         tokenEpI(1).asBits ##
                         tokenEpI(2).asBits ##
                         tokenEpI(3).asBits

  val sReportLengthOK = Bool(C_report_length_strict) ? (rRxCount === C_report_length) | (rRxCount =/= 0)

  if (C_usb_speed == 1) {
    sRxd := io.usbDif
    sRxdp := io.usbDp
    sRxdn := io.usbDn

    when (!sTxoe) (io.usbDp := sTxdp)

    when (!sTxoe) (io.usbDn := sTxdn)
  } else {
    sRxd := ~io.usbDif
    sRxdp := io.usbDn
    sRxdn := io.usbDp

    when (!sTxoe) (io.usbDp := sTxdn)

    when (!sTxoe) (io.usbDn := sTxdp)
  }

  val usbPhy = new UsbPhy
  usbPhy.io.phyTxMode := True
  usbPhy.io.lineCtrlI := sLINECTRL
  usbPhy.io.txValidI := sTXVALID
  usbPhy.io.dataOutI := sDATAOUT
  usbPhy.io.rxd := sRxd
  usbPhy.io.rxdp := sRxdp
  usbPhy.io.rxdn := sRxdn

  val sTXREADY = usbPhy.io.txReadyO
  val sRXVALID = usbPhy.io.rxValidO
  val sDATAIN = usbPhy.io.dataInO
  val sRXACTIVE = usbPhy.io.rxActiveO
  val sRXERROR = usbPhy.io.rxErrorO
  val sLINESTATE = usbPhy.io.lineStateO
  sTxdp := usbPhy.io.txdp
  sTxdn := usbPhy.io.txdn
  sTxoe := usbPhy.io.txoe

  val sTransmissionOver = rxDoneO || (timeoutO && !rTimeout)

  rTimeout := timeoutO

  when (rResetAccepted) {
    rSetupRomAddr := 0
    rSetupRomAddrAcked := 0
    rSetupByteCounter := 0
    rRetry := 0
    rResetPending := False
    rTxOverDebug := False
  } otherwise {
    switch (rState) {
      is(C_STATE_DETACHED) {
        rDevAddressConfirmed := 0
        rRetry := 0
      }
      is(C_STATE_SETUP) {
        when (sTransmissionOver) {
          rTxOverDebug := True
          when (tokenPidI === 0x2d) {
            when (rxDoneO && responseO === 0xd2) {
              rSetupRomAddrAcked := rSetupRomAddr
              rRetry := 0
            } otherwise {
              rSetupRomAddr := rSetupRomAddrAcked
              when (!rRetry(C_setup_retry)) {
                rRetry := rRetry + 1
              }
            }
          }
        } otherwise {
          when (txPopO) {
            rSetupRomAddr := rSetupRomAddr + 1
            rSetupByteCounter := rSetupByteCounter + 1
          }
        }
        rStoredResponse := 0
      }
      is(C_STATE_REPORT) {
        when (sTransmissionOver) {
          when (timeoutO && !rTimeout) {
            when (!rRetry(C_setup_retry)) {
              rRetry := rRetry + 1
            }
          } otherwise {
            when (rxDoneO) {
              rRetry := 0
            }
          }
        }
      }
      default {
        when (sTransmissionOver) {
          when (tokenPidI === 0xe1) {
            when (rxDoneO && responseO === 0xd2) {
              rStoredResponse := responseO
              rSetupRomAddrAcked := rSetupRomAddr
              rRetry := 0
            } otherwise {
              rSetupRomAddr := rSetupRomAddrAcked
              when (!rRetry(C_setup_retry)) {
                rRetry := rRetry + 1
              }
            }
          } otherwise {
            when (timeoutO && !rTimeout) {
              when (!rRetry(C_setup_retry)) {
                rRetry := rRetry + 1
              }
            } otherwise {
              when (rxDoneO) {
                rStoredResponse := responseO
                when (responseO === 0x4b) {
                  rRetry := 0
                  rDevAddressConfirmed := rDevAddressRequested
                } otherwise {
                  rRetry := rRetry + 1
                }
              }
            }
          }
        } otherwise {
          when (txPopO) {
            rSetupRomAddr := rSetupRomAddr + 1
          }
        }
      }
    }
  }

  switch (rState) {
    is(C_STATE_DETACHED) {
      rDevAddressRequested := 0
      rSetAddressFound := False
      rWLength := 0
    }
    is(C_STATE_SETUP) {
      switch (rSetupByteCounter(2 downto 0)) {
        is(U"000") {
          rFirstByte0Found := txDataI === 0
        }
        is(U"001") {
          when (txDataI === 0x05) {
            rSetAddressFound := rFirstByte0Found 
          }
          rWLength := 0
        }
        is(U"010") {
          when (rSetAddressFound) {
            rDevAddressRequested := txDataI(6 downto 0)
          }
        }
        is(U"110") {
          rWLength(7 downto 0) := txDataI.asUInt
        }
        is(U"111") {
          rWLength(15 downto 8) := 0
        }
      }
    }
    default {
      rWLength := 0
      rSetAddressFound := False
    }
  }

  rAdvanceData := False

  switch (rState) {
    is(C_STATE_DETACHED) {
      rResetAccepted := False

      when (sLINESTATE === B"01") {
        when (!rSlow(17)) {
          rSlow := rSlow + 1
        } otherwise {
          rSlow := 0
          sofTransferI := True
          inTransferI := True
          tokenPidI(1 downto 0) := B"11"
          tokenDevI := 0
          respExpectedI := False
          rCtrlIn := False
          startI := True
          rPacketCounter := 0
          rSofCounter := 0
          rState := C_STATE_SETUP
        }
      } otherwise {
        startI := False
        rSlow := 0
      }
    }
    is(C_STATE_SETUP) {
      when (idleO) {
        when (!rSlow(C_setup_interval)) {
          rSlow := rSlow + 1

          when (rRetry(C_setup_retry)) {
            rResetAccepted := True
            rState := C_STATE_DETACHED
          }

          when (rSlow(C_keepalive_phase_bits - 1 downto 0) === C_keepalive_phase && Bool(C_keepalive_setup)) {
            sofTransferI := True
            inTransferI := Bool(C_keepalive_type)

            if (C_keepalive_type) {
              tokenPidI(1 downto 0) := B"00"
            } else {
              tokenPidI := 0xa5
              tokenDevI := sSofDev
              tokenEpI := sSofEp
              dataLenI := 0
              rSofCounter := rSofCounter + 1
            }
            respExpectedI := False
            startI := True
          } otherwise {
            startI := False
          }
        } otherwise {
          rSlow := 0
          sofTransferI := False
          tokenDevI := rDevAddressConfirmed
          tokenEpI := 0
          respExpectedI := True

          when (rSetupRomAddr === C_setup_rom_len) {
            dataLenI := 0
            startI := False
            rState := C_STATE_REPORT
          } otherwise {
            inTransferI := False
            tokenPidI := 0x2d
            dataLenI := 8

            when (rSetAddressFound || rCtrlIn || rWLength =/= 0) {
              rBytesRemaining := rWLength

              when (rSetAddressFound) {
                rCtrlIn := True
                rDataStatus := False
              } otherwise {
                rDataStatus := C_datastatus_enable
              }

              dataIdxI := True
              rState := C_STATE_DATA
            } otherwise {
              dataIdxI := False
              rCtrlIn := txDataI(7)
              rPacketCounter := rPacketCounter + 1
              startI := True
            }
          }
        }
      } otherwise {
        startI := False
      }
    }
    is(C_STATE_REPORT) {
      when (idleO) {
        when (!rSlow(C_report_interval)) {
          rSlow := rSlow + 1

          when (rSlow(C_keepalive_phase_bits-1 downto 0) === C_keepalive_phase & Bool(C_keepalive_report)) {
            sofTransferI := True
            inTransferI := Bool(C_keepalive_type)

            if (C_keepalive_type) {
              tokenPidI(1 downto 0) := B"00"
            } else {
              tokenPidI := 0xa5
              tokenDevI := sSofDev
              tokenEpI := sSofEp
              dataLenI := 0
              rSofCounter := rSofCounter + 1
            }
            respExpectedI := False
            startI := True
          } otherwise {
            startI := False
          }
        } otherwise {
          rSlow := 0
          sofTransferI := False
          inTransferI := True
          tokenPidI := 0x69

          if (!C_keepalive_type) {
            tokenDevI := rDevAddressConfirmed
          }

          tokenEpI := C_report_endpoint
          dataIdxI := False
          respExpectedI := True
          startI := True

          when (rResetPending || sLINESTATE === B"00" || rRetry(C_setup_retry)) {
            rResetAccepted := True
            rState := C_STATE_DETACHED
          }
        }
      } otherwise {
        startI := False
      }
    }
    default {
      when (idleO) {
        when (!rSlow(C_setup_interval)) {
          rSlow := rSlow + 1

          when (rRetry(C_setup_retry)) {
            rResetAccepted := True
            rState := C_STATE_DETACHED
          }

          when (rSlow(C_keepalive_phase_bits-1 downto 0) === C_keepalive_phase && Bool(C_keepalive_status)){
            sofTransferI := True
            inTransferI := Bool(C_keepalive_type)

            if (C_keepalive_type) {
              tokenPidI(1 downto 0) := B"00"
            } else {
              tokenPidI := 0xa5
              tokenDevI := sSofDev
              tokenEpI := sSofEp
              dataLenI := 0
              rSofCounter := rSofCounter + 1
            }

            respExpectedI := False
            startI := True
          } otherwise {
            startI := False
          }
        } otherwise {
          rSlow := 0
          sofTransferI := False
          inTransferI := rCtrlIn

          when (rCtrlIn) {
            tokenPidI := 0x69
          } otherwise {
            tokenPidI := 0xe1
          }

          if (!C_keepalive_type) {
            tokenDevI := rDevAddressConfirmed
          }

          tokenEpI := 0
          respExpectedI := True

          when (rBytesRemaining =/= 0) {
            when (rBytesRemaining(15 downto 3) =/= 0) {
              dataLenI := 8
            } otherwise {
              dataLenI := U(0, 13 bits) @@ rBytesRemaining(2 downto 0)
            }
          } otherwise {
            dataLenI := 0
          }

          when (rCtrlIn) {
            when (rStoredResponse === B(0x4B, 8 bits) || rStoredResponse === B(0xc3, 8 bits)) {
              rAdvanceData := True

              when (rBytesRemaining(15 downto 3) === 0) {
                rCtrlIn := False

                when (!rDataStatus) {
                  rState := C_STATE_SETUP
                }
              } otherwise {
                rAdvanceData := True
                rPacketCounter := rPacketCounter + 1
                startI := True
              }
            } otherwise {
              rPacketCounter := rPacketCounter + 1
              startI := True
            } 
          } otherwise {
            when (rStoredResponse === 0xd2) {
              rAdvanceData := True

              when (rDataStatus) {
                rState := C_STATE_SETUP
              } otherwise {
                when (rBytesRemaining === 0) {
                  rCtrlIn := True
                }
              }

            } otherwise {
              rPacketCounter := rPacketCounter + 1
              startI := True
            }
          }
        }
      } otherwise {
        startI := False
      }
      
      when (rAdvanceData) {
        when (rBytesRemaining =/= 0) {
          when (rBytesRemaining(15 downto 3) =/= 0) {
            rBytesRemaining(15 downto 3) := rBytesRemaining(15 downto 3) - 1
          } otherwise {
            rBytesRemaining(2 downto 0) := 0
          }
          dataIdxI := ~dataIdxI
        } otherwise {
          when (rCtrlIn) {
            dataIdxI := True
          }
        }
      }
    }
  }

  val usbhSie = new UsbhSie
  usbhSie.io.startI := startI
  usbhSie.io.inTransferI := inTransferI
  usbhSie.io.sofTransferI := sofTransferI
  usbhSie.io.respExpectedI := respExpectedI
  usbhSie.io.tokenPidI := tokenPidI
  usbhSie.io.tokenDevI := reverseTokenDevI
  usbhSie.io.tokenEpI := reverseTokenEpI
  usbhSie.io.dataLenI := dataLenI
  usbhSie.io.dataIdxI := dataIdxI
  usbhSie.io.txDataI := txDataI
  usbhSie.io.utmiTxReadyI := sTXREADY
  usbhSie.io.utmiDataI := sDATAIN
  usbhSie.io.utmiRxValidI := sRXVALID
  usbhSie.io.utmiRxActiveI := sRXACTIVE

  txPopO := usbhSie.io.txPopO
  rxDataO := usbhSie.io.rxDataO
  rxPushO := usbhSie.io.rxPushO
  txDoneO := usbhSie.io.txDoneO
  rxDoneO := usbhSie.io.rxDoneO
  crcErrO := usbhSie.io.crcErrO
  timeoutO := usbhSie.io.timeoutO
  responseO := usbhSie.io.responseO
  rxCountO := usbhSie.io.rxCountO
  idleO := usbhSie.io.idleO
  sLINECTRL := usbhSie.io.utmiLineCtrlO
  sDATAOUT := usbhSie.io.utmiDataO
  sTXVALID := usbhSie.io.utmiTxValidO

  rRxCount := rxCountO

  when (rxPushO) {
    rReportBuf(rRxCount.resized) := rxDataO
  }

  rRxDone := rxDoneO

  when (rRxDone && !rxDoneO) {
    rCrcErr := False
  } otherwise {
    when (crcErrO) {
      rCrcErr := True
    }
  }

  rHidValid := (rRxDone && !rxDoneO && !rCrcErr && !timeoutO && 
                rState === C_STATE_REPORT && sReportLengthOK)

  for (i <- 0 to C_report_length - 1) {
    io.hidReport(i*8+7 downto i*8) := rReportBuf(U(i, log2Up(C_report_length) bits))
  }

  io.hidValid := rHidValid
  io.rxCount := rxCountO
  io.rxDone := rxDoneO

  io.led := B"0" ## rResetPending.asBits ## rTxOverDebug.asBits ## 
            rSetupRomAddrAcked(3).asBits ## sLINESTATE ## rState
}

