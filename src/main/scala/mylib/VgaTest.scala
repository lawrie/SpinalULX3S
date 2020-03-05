package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.graphic._
import spinal.lib.graphic.vga._

class StripeTest(rgbConfig: RgbConfig) extends Component{
  val io = new Bundle{
    val vga = master(Vga(rgbConfig))
  }

  val x = Reg(UInt(10 bits)) init 0
  val y = Reg(UInt(9 bits)) init 0

  val counter = Reg(UInt(rgbConfig.gWidth bits))
  val ctrl = new VgaCtrl(rgbConfig)
  ctrl.io.softReset := False
  ctrl.io.timings.setAs_h640_v480_r60
  ctrl.io.pixels.valid := True
  ctrl.io.pixels.payload.r := (y <= 160) ? U"1111" | U"0000"
  ctrl.io.pixels.payload.g := (y > 160 && y <= 320) ? U"1111" | U"0000"
  ctrl.io.pixels.payload.b := (y >= 320) ? U"1111" | U"0000"
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

class VgaTest() extends Component {
  val io = new Bundle {
    val vSync = out Bool
    val hSync = out Bool
    val red = out Bits(4 bits)
    val green = out Bits(4 bits)
    val blue = out Bits(4 bits)
  }

  val vgaTest = new StripeTest(RgbConfig(4, 4, 4))
  io.vSync := vgaTest.io.vga.vSync
  io.hSync := vgaTest.io.vga.hSync
  io.red := vgaTest.io.vga.colorEn ? vgaTest.io.vga.color.r.asBits | B"0000"
  io.green := vgaTest.io.vga.colorEn ? vgaTest.io.vga.color.g.asBits | B"0000"
  io.blue := vgaTest.io.vga.colorEn ? vgaTest.io.vga.color.b.asBits | B"0000"
}

object VgaTest {
  def main (args: Array[String]) {
    SpinalVerilog(new VgaTest()).toplevel
  }
}

