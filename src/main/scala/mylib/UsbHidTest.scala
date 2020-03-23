package mylib

import spinal.core._
import spinal.lib._

class UsbHidTest(fast : Boolean = false) extends Component {
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

  val clk = if (fast) pllUsb.io.clkout1 else  pllUsb.io.clkout2

  val coreClockDomain = ClockDomain(clk, io.reset)

  val coreArea = new ClockingArea(coreClockDomain) {
    val usbHostHid = new UsbHostHid(
      C_usb_speed = if (fast) 1 else 0,
      C_report_length = 10,
      C_report_length_strict = false,
      C_report_interval = 16,
      C_report_endpoint = 1,
      C_setup_retry = 4,
      C_setup_interval = 17,
      C_keepalive_setup = true,
      C_keepalive_status = true,
      C_keepalive_report = true,
      C_keepalive_phase = 2048,
      C_keepalive_phase_bits = if (fast) 15 else 12,
      C_keepalive_type = !fast)

    usbHostHid.io.usbDif := io.usb_fpga_bd_dp
    usbHostHid.io.usbDp <> io.usb_fpga_bd_dp
    usbHostHid.io.usbDn <> io.usb_fpga_bd_dn

    io.usb_fpga_pu_dp := False
    io.usb_fpga_pu_dn := False

    io.led := usbHostHid.io.hidReport(23 downto 16)
  }
}

object UsbHidTest {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new UsbHidTest(fast = false))
  }
}

