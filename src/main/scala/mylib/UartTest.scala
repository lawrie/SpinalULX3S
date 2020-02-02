package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.com.uart._

class UartTest extends Component{
  val io = new Bundle{
    val uart = master(Uart())
    val sw = in Bits(4 bits)
    val led = out Bits(8 bits)
    val wifi_gpio0 = out Bool
    val btn = in Bits(7 bits)
    val clk = in Bool
  }.setName("")

  io.wifi_gpio0 := True

  val clkCtrl = new Area {
    val pll = new PLL_BB("PLL")
    pll.io.RESET := !io.btn(0)
    pll.io.CLK_IN1 := io.clk

    //using 8mhz so that UART running 115200 BAUD
    val clk8Domain = ClockDomain.internal(name = "core8",  frequency = FixedFrequency(8.33333 MHz))

    clk8Domain.clock := pll.io.CLK_OUT1
    clk8Domain.reset := ResetCtrl.asyncAssertSyncDeassert(
      input = !io.btn(0) || !pll.io.LOCKED,
      clockDomain = clk8Domain
    )
  }

  val core8 = new ClockingArea(clkCtrl.clk8Domain) {
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
}

//Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one. This configuration can be resued everywhere
object TopSpinalConfig extends SpinalConfig(
    //targetDirectory = "..",
    oneFilePerComponent = true,
    defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC),
    defaultClockDomainFrequency = FixedFrequency(25 MHz)
)

object UartTest{
  def main(args: Array[String]) {
    TopSpinalConfig.generateVerilog(new UartTest).printPruned
  }
}
