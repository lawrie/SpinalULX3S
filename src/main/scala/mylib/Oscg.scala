package mylib

import spinal.core._

case class OSCG(div : Int) extends BlackBox {
  val OSC = out Bool

  addGeneric("DIV", div)
}

class OscgTest(width: Int = 28) extends Component {
  val io = new Bundle {
    val led = out Bits(8 bits)
  }.setName("");

  val oscgArea = new ClockingArea(new ClockDomain(OSCG(12).OSC)) {
    val counter = Reg(UInt(width bits))

    counter := counter + 1
    io.led := counter(width-9, 8 bits).asBits
  }
}

object OscgTest {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new OscgTest)
  }
}

