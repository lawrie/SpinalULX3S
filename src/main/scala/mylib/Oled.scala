package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.misc._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Oled extends Component {
  val C_init_file = "oled_init_16bit.mem"
  val C_init_size = 44
  val C_color_bits = 16
  val C_x_size = 96
  val C_y_size = 64
  val C_x_bits = log2Up(C_x_size)
  val C_y_bits = log2Up(C_y_size)

  val io = new Bundle {
    val x = out(Reg(UInt(C_x_bits bits)))
    val y = out(Reg(UInt(C_y_bits bits)))
    val next_pixel = out(Reg(Bool))
    val color = in Bits(C_color_bits bits)

    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
  }

  val C_oled_init = Mem(Bits(8 bits), wordCount=C_init_size)
  C_oled_init.initialContent = Tools.readmemh(C_init_file)

  val resetCnt = Reg(UInt(2 bits))
  val initCnt = Reg(UInt(10 bits))
  val data = Reg(Bits(8 bits))
  val dc = Reg(Bool)
  val byte = Reg(Bool)

  io.oled_resn := ~resetCnt(0)
  io.oled_csn := resetCnt(0)
  io.oled_dc := dc
  io.oled_clk := ~initCnt(0)
  io.oled_mosi := data(7)

  when (resetCnt =/= 2) {
    resetCnt := resetCnt + 1
    data := C_oled_init(U"000000")
  } elsewhen (initCnt(9 downto 4) =/= C_init_size) {
    initCnt := initCnt + 1
    when (initCnt(3 downto 0) === 0) {
      when (!dc) {
        data := C_oled_init(initCnt(9 downto 4))
      } otherwise {
        byte := ~byte
        data := byte ? io.color(7 downto 0) | io.color(15 downto 8)
        when (!byte) {
          io.next_pixel := True
          when (io.x === C_x_size-1) {
            io.x := 0
            io.y := io.y + 1
          } otherwise {
            io.x := io.x + 1
          }
        }
      } 
    } otherwise {
      io.next_pixel := False
      when (!initCnt(0)) {
        data := data(6 downto 0) ## B"0"
      }
    }
  } otherwise {
    dc := True
    initCnt(9 downto 4) := C_init_size - 1
  }
}

class OledTest extends Component {
  val io = new Bundle {
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
  }.setName("")

  val oled = new Oled()
  io.oled_csn := oled.io.oled_csn
  io.oled_clk := oled.io.oled_clk
  io.oled_mosi := oled.io.oled_mosi
  io.oled_dc := oled.io.oled_dc
  io.oled_resn := oled.io.oled_resn

  val x = oled.io.x
  val y = oled.io.y

  oled.io.color := (x(3) ^ y(3)) ? (B"00000" ## x(6 downto 1).asBits ## B"00000") | (y(5 downto 1).asBits ## B"000000" ## B"00000")

}

object OledTest {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new OledTest)
  }
}

