package mylib

import spinal.core._
import spinal.lib._

class UsbHidTest(fast : Boolean = false) extends Component {
  val io = new Bundle() {
    val clk_25mhz = in Bool
    val reset = in Bool
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
    val usb_fpga_bd_dp = inout(Analog(Bool))
    val usb_fpga_bd_dn = inout(Analog(Bool))
    val usb_fpga_pu_dp = out Bool
    val usb_fpga_pu_dn = out Bool
    val led = out Bits(8 bits)
  }.setName("")

  val pllUsb = new PllUsb
  pllUsb.io.clkin := io.clk_25mhz

  // Always use 48Mhz clock
  val clk = pllUsb.io.clkout1

  val coreClockDomain = ClockDomain(clk, io.reset)

  val coreArea = new ClockingArea(coreClockDomain) {
    val counter = Reg(UInt(3 bits))
    counter := counter + 1

    val enableArea = new ClockEnableArea(counter === 7) {
      val usbHostHid = new UsbHostHid(UsbHostHidGenerics(
        usbSpeed = if (fast) 1 else 0,
        reportLength = 10,
        reportLengthStrict = false,
        reportInterval = 16,
        reportEndpoint = 1,
        setupRetry = 4,
        setupInterval = 17,
        keepaliveSetup = true,
        keepaliveStatus = true,
        keepaliveReport = true,
        keepalivePhase = 2048,
        keepalivePhaseBits = if (fast) 15 else 12,
        keepaliveType = !fast))

      usbHostHid.io.usb.dif := io.usb_fpga_bd_dp
      usbHostHid.io.usb.dp <> io.usb_fpga_bd_dp
      usbHostHid.io.usb.dn <> io.usb_fpga_bd_dn

      io.usb_fpga_pu_dp := False
      io.usb_fpga_pu_dn := False
    
      val hidReport = usbHostHid.io.hid.report

      val usbHid2Ascii = new UsbHid2Ascii
      usbHid2Ascii.io.hidReport := hidReport(23 downto 16) ## hidReport(7 downto 0)

      val oledHex = new SSD1331Hex(64)
      io.oled_csn := oledHex.io.oled_csn
      io.oled_clk := oledHex.io.oled_clk
      io.oled_mosi := oledHex.io.oled_mosi
      io.oled_dc := oledHex.io.oled_dc
      io.oled_resn := oledHex.io.oled_resn
      oledHex.io.data := usbHid2Ascii.io.ascii ## hidReport(55 downto 0)

      io.led := usbHostHid.io.led
    }
  }
}

object UsbHidTest {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new UsbHidTest(fast = false))
  }
}

