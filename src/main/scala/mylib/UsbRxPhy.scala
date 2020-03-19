package mylib

import spinal.core._
import spinal.lib._

class UsbRxPhy(inputHz : Int = 6000000, 
                bitHz : Int = 1500000,
                paBits : Int = 8) extends Component {
  val io = new Bundle() {
    val usbDif = in Bool
    val usbDp = in Bool
    val usbDn = in Bool
    val lineState = out Bits(2 bits)
    val clkRecovered = out Bool
    val clkRecoveredEdge = out Bool
    val rawData = out Bool
    val rxEn = in Bool
    val rxActive = out Bool
    val rxError = out Bool
    val valid = out Bool
    val data = out Bits(8 bits)
  }

  val paInc = U((1 << (paBits - 1)) * (inputHz / bitHz), paBits bits)
  val paCompensate = paInc(paBits-2 downto 0) + paInc(paBits-2 downto 0) + paInc(paBits-2 downto 0)
  val paInit = paCompensate
  val validInit = 0x80
  val idleCntInit = B"100000"

  val rPa = Reg(UInt(paBits bits))
  val rDifShift = Reg(Bits(2 bits))
  val rClkRecoveredShift = Reg(Bits(2 bits))
  val rFrame = Reg(Bool)
  val rData = Reg(Bits(8 bits))
  val rValid = Reg(Bits(8 bits)) init validInit
  val rValidPrev = Reg(Bool)
  val rDataLatch = Reg(Bits(8 bits))
  val rLineState = Reg(Bits(2 bits))
  val rLineStatePrev = Reg(Bits(2 bits))
  val rLineStateSync = Reg(Bits(2 bits))
  val rRxEn = Reg(Bool)
  val rPreamble = Reg(Bool)
  val rRxActive = Reg(Bool)
  val rLineBitPrev = Reg(Bool)
  val rIdleCnt = Reg(Bits(7 bits)) init idleCntInit

  val sClkRecovered = rPa.msb
  val sLineBit = rDifShift(0)
  val sBit = !(sLineBit ^ rLineBitPrev)

  rClkRecoveredShift := sClkRecovered.asBits ## rClkRecoveredShift.msb.asBits
  rLineState := io.usbDp.asBits ## io.usbDp.asBits
  rLineStatePrev := rLineState
  rRxEn := io.rxEn
  rValidPrev := rValid(0)
  
  when (io.usbDp && io.usbDn && io.rxEn) {
    rDifShift := io.usbDif.asBits ## rDifShift(1).asBits
  }

  when (rDifShift(0) =/= rDifShift(1)) {
    rPa(paBits - 2 downto 0) := paInit(paBits - 2 downto 0)
  } otherwise {
    rPa := rPa + paInc
  }

  when (rRxEn) {
    // Synchronous with recovered clock
    when (rClkRecoveredShift(1) =/= sClkRecovered) {
      when (rLineBitPrev === sLineBit) {
        rIdleCnt := rIdleCnt(0).asBits ## rIdleCnt(6 downto 1)
      } otherwise {
        rIdleCnt := idleCntInit
      }
      rLineBitPrev := sLineBit

      when ((!rIdleCnt(0) && rFrame) || !rFrame) {
        when (rLineStateSync === B"00") {
          rData := 0
        } otherwise {
          rData := sBit.asBits ## rData(7 downto 1)
        }
      }

      when (rFrame && rValid(1)) {
        rDataLatch := rData
      }
    }

    when (rLineStateSync === B"00") {
      rFrame := False
      rValid := 0
      rPreamble := False
      rRxActive := False
    } otherwise {
      when (rFrame) {
        when (rPreamble) {
          when (rData(6 downto 1) === B"100000") {
            rPreamble := False
            rValid := validInit
            rRxActive := True
          } otherwise {
            when (!rIdleCnt(0)) {
              rValid := rValid(0).asBits ## rValid(7 downto 1)
            } otherwise {
              when (sBit) {
                rValid := 0
                rFrame := False
                rRxActive := False
              }
            }
          }
        } otherwise { // !rFrame
          rFrame := True
          rPreamble := True
          rValid := 0
          rRxActive := False
        }
      }
      rLineStateSync := rLineState
    } // Synchronous with recovered clock
  } otherwise { // !rxEn
    rValid := 0
    rFrame := False
    rRxActive := False
  }
  
  io.data := rDataLatch
  io.rawData := rLineBitPrev
  io.lineState := rLineState
  io.rxActive := rFrame
  io.valid := rValid(0) & !rValidPrev
  io.rxError := False
  io.clkRecovered := sClkRecovered
  io.clkRecoveredEdge := (rClkRecoveredShift(1) =/= sClkRecovered)
}
