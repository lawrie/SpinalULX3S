package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.graphic._
import spinal.lib.graphic.vga._

class HdmiStripeTest(rgbConfig: RgbConfig) extends Component{
  val io = new Bundle{
    val vga = master(Vga(rgbConfig))
  }

  val x = Reg(UInt(10 bits)) init 0
  val y = Reg(UInt(9 bits)) init 0

  val ctrl = new VgaCtrl(rgbConfig)
  ctrl.io.softReset := False
  ctrl.io.timings.setAs_h640_v480_r60
  ctrl.io.pixels.valid := True
  ctrl.io.pixels.payload.r := (y <= 160) ? U"11111111" | U"00000000"
  ctrl.io.pixels.payload.g := (y > 160 && y <= 320) ? U"11111111" | U"00000000"
  ctrl.io.pixels.payload.b := (y >= 320) ? U"11111111" | U"00000000"
  ctrl.io.vga <> io.vga

  when (ctrl.io.pixels.ready) {
    x := x + 1
    when (x === 640 - 1) {
      x := 0
      y := y + 1
      when (y === 480 -1) {
        y := 0
      }
    }
  }
}

class HdmiTest() extends Component {
  val io = new Bundle {
    val clk = in Bool
    val reset = in Bool
    val gpdi_dp = out Bits(4 bits)
    val gpdi_dn = out Bits(4 bits)
  }

  val pll = new HdmiPll()
  pll.io.clkin := io.clk

  val coreClockDomain = ClockDomain(io.clk, io.reset)

  val coreArea = new ClockingArea(coreClockDomain) {

    val vgaTest = new HdmiStripeTest(RgbConfig(8, 8, 8))
    val vSync = vgaTest.io.vga.vSync
    val hSync = vgaTest.io.vga.hSync
    val red = vgaTest.io.vga.colorEn ? vgaTest.io.vga.color.r.asBits | B"00000000"
    val green = vgaTest.io.vga.colorEn ? vgaTest.io.vga.color.g.asBits | B"00000000"
    val blue = vgaTest.io.vga.colorEn ? vgaTest.io.vga.color.b.asBits | B"00000000"

    val hdmi = new Ulx3sHdmi()
    hdmi.io.pixclk := pll.io.clkout1
    hdmi.io.pixclk_x5 := pll.io.clkout0
    hdmi.io.red := red
    hdmi.io.green := green
    hdmi.io.blue := blue
    hdmi.io.hSync := hSync
    hdmi.io.vSync := vSync
    hdmi.io.vde := vgaTest.io.vga.colorEn

    io.gpdi_dp := hdmi.io.gpdi_dp
    io.gpdi_dn := hdmi.io.gpdi_dn
  }
}

object HdmiTest {
  def main (args: Array[String]) {
    SpinalVerilog(new HdmiTest()).toplevel
  }
}

