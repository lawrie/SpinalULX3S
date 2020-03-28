package mylib

import spinal.core._
import spinal.lib._

class Spdif(
  clkFreq: Int = 25000000,
  sampleFreq : Int = 48000,
  phaseAccuBits : Int = 24
  ) extends Component {

  val io = new Bundle {
    val dataIn = in UInt(24 bits)
    val addressOut = out(Reg(Bool))
    val spdifOut = out Bool
  }

  val phaseIncrement = ((((sampleFreq /100f) * (1 << (phaseAccuBits - 3))) / (clkFreq/100f)) * (1 << 10)).toInt 

  val rPhaseAccu = Reg(UInt(phaseAccuBits bits))
  val rClkdivShift = Reg(UInt(2 bits))

  val rDataInBuffer = Reg(UInt(24 bits))
  val rBitCounter = Reg(UInt(6 bits)) init 0
  val rFrameCounter = Reg(UInt(9  bits)) init 0
  val rDataBiphase = Reg(Bool) init False
  val rDataOutBuffer = Reg(UInt(8 bits))
  val rParity = Reg(Bool)
  val rChannelStatusShift = Reg(UInt(24 bits))
  val rChannelStatus = Reg(UInt(24 bits)) init U"001000000000000001000000"
 
  rPhaseAccu := rPhaseAccu + phaseIncrement
  rClkdivShift := rClkdivShift(0).asUInt @@ rClkdivShift(1).asUInt

  when (rClkdivShift === U"01") {
    rBitCounter := rBitCounter + 1
  }

  when (rClkdivShift === U"01") {
    rParity := rDataInBuffer(23) ^ rDataInBuffer(22) ^ rDataInBuffer(21) ^ 
               rDataInBuffer(20) ^ rDataInBuffer(19) ^ rDataInBuffer(18) ^ 
               rDataInBuffer(17)  ^ rDataInBuffer(16) ^ rDataInBuffer(15) ^ 
               rDataInBuffer(14) ^ rDataInBuffer(13) ^ rDataInBuffer(12) ^ 
               rDataInBuffer(11) ^ rDataInBuffer(10) ^ rDataInBuffer(9) ^ 
               rDataInBuffer(8) ^ rDataInBuffer(7) ^ rDataInBuffer(6) ^ 
               rDataInBuffer(5) ^ rDataInBuffer(4) ^ rDataInBuffer(3) ^ 
               rDataInBuffer(2) ^ rDataInBuffer(1) ^ rDataInBuffer(0) ^ 
               rChannelStatusShift(23)
    when (rBitCounter === U"000011") {
      rDataInBuffer := io.dataIn
    }

    when (rBitCounter === U"111111") {
      when (rFrameCounter === U"101111111") {
        rFrameCounter := 0
      } otherwise {
        rFrameCounter := rFrameCounter + 1
      }
    }
  }

  when (rClkdivShift === U"01") {
    when (rBitCounter === U"111111") {
      when (rFrameCounter === U"10010011") {
        io.addressOut := False
        rChannelStatusShift := rChannelStatus
        rDataOutBuffer := U"10010110"
      } otherwise {
        when (rFrameCounter(0)) {
          rChannelStatusShift := rChannelStatusShift(22 downto 0) @@ U"0"
          rDataOutBuffer := U"10010110"
          io.addressOut := False
        } otherwise {
          rDataOutBuffer := U"10010011"
          io.addressOut := True
        }
      }
    } otherwise {
      when (rBitCounter === U"111") {
        switch (rBitCounter(5 downto 3)) {
          is(U"000") {
            rDataOutBuffer := U"1" @@ rDataInBuffer(0).asUInt @@
                              U"1" @@ rDataInBuffer(1).asUInt @@
                              U"1" @@ rDataInBuffer(2).asUInt @@
                              U"1" @@ rDataInBuffer(3).asUInt
          }
          is(U"001") {
            rDataOutBuffer := U"1" @@ rDataInBuffer(4).asUInt @@
                              U"1" @@ rDataInBuffer(5).asUInt @@
                              U"1" @@ rDataInBuffer(6).asUInt @@
                              U"1" @@ rDataInBuffer(7).asUInt
          }
          is(U"010") {
            rDataOutBuffer := U"1" @@ rDataInBuffer(8).asUInt @@
                              U"1" @@ rDataInBuffer(9).asUInt @@
                              U"1" @@ rDataInBuffer(10).asUInt @@
                              U"1" @@ rDataInBuffer(11).asUInt
          }
          is(U"011") {
            rDataOutBuffer := U"1" @@ rDataInBuffer(12).asUInt @@
                              U"1" @@ rDataInBuffer(13).asUInt @@
                              U"1" @@ rDataInBuffer(14).asUInt @@
                              U"1" @@ rDataInBuffer(15).asUInt
          }
          is(U"100") {
            rDataOutBuffer := U"1" @@ rDataInBuffer(16).asUInt @@
                              U"1" @@ rDataInBuffer(17).asUInt @@
                              U"1" @@ rDataInBuffer(18).asUInt @@
                              U"1" @@ rDataInBuffer(19).asUInt
          }
          is(U"101") {
            rDataOutBuffer := U"1" @@ rDataInBuffer(20).asUInt @@
                              U"1" @@ rDataInBuffer(21).asUInt @@
                              U"1" @@ rDataInBuffer(22).asUInt @@
                              U"1" @@ rDataInBuffer(23).asUInt
          }
          is(U"110") {
            rDataOutBuffer := U"10101" @@ rChannelStatusShift(23).asUInt @@ U"1" @@ rParity.asUInt
          }
        }
      } otherwise {
        rDataOutBuffer := rDataOutBuffer(6 downto 0) @@ U"0"
      }
    }
  }

  when (rClkdivShift === U"01") {
    when (rDataOutBuffer(7)) {
      rDataBiphase := ~rDataBiphase
    }
  }

  io.spdifOut := rDataBiphase
}

