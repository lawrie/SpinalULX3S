package mylib

import spinal.core._
import spinal.lib._

class UsbHidTest extends Component {
  val io = new Bundle() {
    val clk_25mhz = in Bool
    val reset = in Bool
    val usb_fpga_bd_dp = inout(Analog(Bool))
    val usb_fpga_bd_dn = inout(Analog(Bool))
    val usb_fpga_pu_dp = out Bool
    val usb_fpga_pu_dn = out Bool
    val led = out Bits(8 bits)
  }.setName("")

  val pllUsb = new PllUsb
  pllUsb.io.clkin := io.clk_25mhz

  val coreClockDomain = ClockDomain(pllUsb.io.clkout2, io.reset)

  val coreArea = new ClockingArea(coreClockDomain) {
    val C_report_length = 20

    val sReport = Bits(C_report_length * 8 bits)
    val sValid = Bool

    val usbHostHid = new UsbHostHid
    usbHostHid.io.usbDif := io.usb_fpga_bd_dp
    usbHostHid.io.usbDp <> io.usb_fpga_bd_dp
    usbHostHid.io.usbDn <> io.usb_fpga_bd_dn

    io.usb_fpga_pu_dp := False
    io.usb_fpga_pu_dn := False

    io.led := 0
  }
}

object UsbHidTest {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new UsbHidTest)
  }
}

