package mylib

import spinal.core._
import spinal.lib._

class PllUsb extends BlackBox {
    val io = new Bundle {
        val clkin      = in  Bool

        val clkout0    = out Bool
        val clkout1    = out Bool
        val clkout2    = out Bool
        val clkout3    = out Bool
    }
    noIoPrefix()
    setBlackBoxName("pll_usb")
}
