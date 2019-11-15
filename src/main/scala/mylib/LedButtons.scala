package mylib

import spinal.core._

class LedButtons extends Component {
  val io = new Bundle {
    val led = out Bits(7 bits)
    val btn = in Bits(7 bits)
    val wifi_gpio0 = out Bool
  }.setName("");

  io.wifi_gpio0 := True;
  io.led := io.btn;
}

object LedButtons {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new LedButtons)
  }
}

