package mylib

import spinal.core._
import spinal.lib._

class I2s(
  fmt : Int = 0,
  clkHz : Int = 25000000,
  lrckHz : Int = 48000,
  paBits : Int = 32) extends Component{

  val io = new Bundle {
    val l = in UInt(16 bits)
    val r = in UInt(16 bits)
    val din = out Bool
    val bck = out Bool
    val lrck = out Bool
  }

  val paInc = ((1 << paBits + 5) * (lrckHz.toFloat / clkHz)).toInt
  val sPaInc = U(paInc, 64 bits)

  val rPa = Reg(UInt(paBits bits))

  rPa := rPa(paBits-2 downto 0).resize(paBits) + paInc

  val rI2sData = Reg(UInt(32 bits))
  val rI2sCnt = Reg(UInt(6 bits))

  val sLatchPhase = (U(fmt) === 1) ? U"11111" | U"00000"

  when (rPa.msb) {
    when (rI2sCnt(0)) {
      when (rI2sCnt(5 downto 1) === sLatchPhase) {
        rI2sData := io.l @@ io.r
      } otherwise {
        rI2sData(31 downto 1) := rI2sData(30 downto 0)
      }
    }
    rI2sCnt := rI2sCnt + 1
  }
 
  io.lrck := (U(fmt) === 1) ? ~rI2sCnt(5) | rI2sCnt(5)
  io.bck := rI2sCnt(0)
  io.din := rI2sData.msb
}

