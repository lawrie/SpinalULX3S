package mylib

import spinal.core._
import spinal.lib._

class DacPwm(pcmBits : Int =12, dacBits : Int = 4) extends Component {
  val io = new Bundle {
    val pcm = in SInt(pcmBits bits)
    val dac = out UInt(dacBits bits)
  }

  val pwmBits = pcmBits - dacBits

  val rDac0 = Reg(UInt(dacBits bits))
  val rDac1 = Reg(UInt(dacBits bits))
  val rPcmLow = Reg(UInt(pwmBits bits))

  rDac0 := (~io.pcm.msb.asUInt @@ io.pcm(pcmBits-2 downto pcmBits-dacBits).asUInt)
  rDac1:= (~io.pcm.msb.asUInt @@ io.pcm(pcmBits-2 downto pcmBits-dacBits).asUInt) + 1
  rPcmLow := io.pcm(pwmBits-1 downto 0).asUInt
  
  val rPwmCounter = Reg(UInt(pwmBits bits))
  rPwmCounter := rPwmCounter + 1

  val rDacOutput = Reg(UInt(dacBits bits))
  rDacOutput := (rPwmCounter >= rPcmLow) ? rDac0 | rDac1

  io.dac := rDacOutput
}

