package mylib

import spinal.core._
import spinal.lib._

class Tone(clkFreq : Int = 25000000)  extends Component {
  val io = new Bundle {
    val audio_l = out(Reg(Bits(4 bits))) init 0
    val audio_r = out(Reg(Bits(4 bits))) init 0
  }.setName("")

  val A4 = 440
  val A5 = 880

  def note(freq : Int) = (clkFreq/freq/2)

  val counter = Reg(UInt(26 bits))
  val tone = Reg(UInt(24 bits)) init 0
  tone := tone  + 1

  when (counter === 0) {
    counter := tone.msb ? U(note(A4) - 1, 26 bits) | U(note(A5) - 1, 26 bits)
    io.audio_l := ~io.audio_l
    io.audio_r := ~io.audio_r
  } otherwise {
    counter := counter - 1
  }
}

object Tone {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new Tone)
  }
}

