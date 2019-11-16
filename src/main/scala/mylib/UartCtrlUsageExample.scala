package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.com.uart._

class UartCtrlUsageExample extends Component{
  val io = new Bundle{
    val uart = master(Uart())
    val sw = in Bits(4 bits)
    val led = out Bits(8 bits)
    val wifi_gpio0 = out Bool
  }.setName("")

  io.wifi_gpio0 := True

  val uartCtrl = new UartCtrl()
  uartCtrl.io.config.setClockDivider(115200 Hz)
  uartCtrl.io.config.frame.dataLength := 7  //8 bits
  uartCtrl.io.config.frame.parity := UartParityType.NONE
  uartCtrl.io.config.frame.stop := UartStopType.ONE
  uartCtrl.io.uart <> io.uart

  //Assign io.led with a register loaded each time a byte is received
  io.led := uartCtrl.io.read.toReg()

  //Write the value of switch on the uart each 2000 cycles
  val write = Stream(Bits(8 bits))
  write.valid := CounterFreeRun(2000).willOverflow
  write.payload := B"0011" ## io.sw
  write >-> uartCtrl.io.write
}

object UartCtrlUsageExample{
  def main(args: Array[String]) {
    SpinalConfig(
      mode = Verilog,
      defaultConfigForClockDomains = ClockDomainConfig(resetKind = BOOT),
      defaultClockDomainFrequency=FixedFrequency(25 MHz)
    ).generate(new UartCtrlUsageExample)
  }
}
