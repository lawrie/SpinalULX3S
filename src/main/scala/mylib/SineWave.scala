package mylib

import spinal.core._
import spinal.lib._

class SineWave(
  delay : Int = 8,
  pcmBits : Int = 12,
  spdBits : Int = 10,
  posToSpdShift : Int = 8,
  spdToPosShift : Int = 3,
  posInit : Int = 0,
  spdInit : Int = 277) extends Component {
  
  val io = new Bundle {
    val pcm = out SInt(pcmBits bits)
  }

  val rSpd = Reg(SInt(spdBits bits)) init spdInit
  val rPos = Reg(SInt(pcmBits bits)) init posInit

  val sPosShift = rPos(pcmBits-1 downto posToSpdShift).resize(spdBits)
  val sSpdShift = rSpd(spdBits-1 downto spdToPosShift).resize(pcmBits)

  val rPcm = Reg(UInt(pcmBits bits))
  val rDelay = Reg(UInt(delay bits))

  when (rDelay(delay -1 downto 1) === 0) {
    when (!rDelay(0)) {
      rPos := rPos + sSpdShift
    } otherwise {
      rSpd := rSpd - sPosShift
    }
  }
  rDelay := rDelay + 1

  io.pcm := rPos
}

