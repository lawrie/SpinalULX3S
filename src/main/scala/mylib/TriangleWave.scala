package mylib

import spinal.core._
import spinal.lib._

class TriangleWave(delay : Int = 10, pcmBits: Int = 12) extends Component {
  val io = new Bundle {
    val pcm = out SInt(pcmBits bits)
  }

  val rCounter = Reg(UInt(delay + pcmBits bits))
  val rDirection = Reg(Bool)

  when (rDirection) {
    rCounter := rCounter + 1 
  } otherwise {
    rCounter := rCounter - 1
  }

  when (rCounter(delay+pcmBits-1 downto delay) === ~U(1770, 12 bits) && ~rDirection) {
    rDirection := True
  } elsewhen (rCounter(delay+pcmBits-1 downto delay) === U(1770, 12 bits) && rDirection) {
    rDirection := False
  }

  io.pcm := rCounter(delay+pcmBits-1 downto delay).asSInt
}
