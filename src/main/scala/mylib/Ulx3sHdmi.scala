package mylib

import spinal.core._
import spinal.lib._

class Ulx3sHdmi extends BlackBox {
  val io = new Bundle {
    val pixclk = in Bool
    val pixclk_x5 = in Bool
    val red = in Bits(8 bits)
    val green = in Bits(8 bits)
    val blue = in Bits(8 bits)
    val vde = in Bool
    val hSync = in Bool 
    val vSync = in Bool
    val gpdi_dp = out Bits(4 bits)
    val gpdi_dn = out Bits(4 bits)
  }

  noIoPrefix()
  setBlackBoxName("hdmi")
}

