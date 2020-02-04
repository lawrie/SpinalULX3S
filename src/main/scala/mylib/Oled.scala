package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.misc._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Oled extends Component {
  val C_init_file = "oled_init_xflip_16bit.mem"
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

class OledHex extends Component {
  val io = new Bundle {
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
    val led = out Bits(8 bits)
    val btn = in Bits(7 bits)
  }.setName("")

  val oled = new Oled()
  io.oled_csn := oled.io.oled_csn
  io.oled_clk := oled.io.oled_clk
  io.oled_mosi := oled.io.oled_mosi
  io.oled_dc := oled.io.oled_dc
  io.oled_resn := oled.io.oled_resn

  val x = oled.io.x
  val y = oled.io.y

  val C_font_file = "oled_font.mem"
  val C_font_size = 136
  val C_oled_font = Mem(Bits(5 bits), wordCount=C_font_size)
  C_oled_font.initialContent = Tools.readmemb(C_font_file)

  val C_data_len = 64
  val C_char_line_bits = 5
  val C_color_black = B(0x0000, 16 bits)
  val C_color_cyan = B(0x0208, 16 bits)
  val C_color_white = B(0xffff, 16 bits)

  val R_data = Reg(Bits(C_data_len bits))              // Current data value
  val R_increment = Reg(UInt(10 bits)) init 1          // Increments through pixels in font
  val R_pixel = Reg(Bits(oled.C_color_bits bits))      // Current pixel color
  val R_cpixel = Reg(UInt(3 bits)) init 0              // Column of font
  val R_data_index = Reg(UInt(7 bits)) init U"0000010" // Index of hex digit in R_data
  val R_indexed_data = Reg(Bits(4 bits))               // The current hex_digit
  val R_char_line = Reg(Bits(5 bits)) init B"01110"    // The current row of the font

  // Combinatorial signals
  val S_pixel = R_char_line(0) ? C_color_white | C_color_black // Current pixel color
  val S_row = R_increment(9 downto 7)                          // Current row of screen
  val S_column = R_increment(3 downto 0)                       // Current column of screen
  val S_scanline = R_increment(6 downto 4)                     // Current line in font
  val S_indexed_data = R_data((R_data_index @@ U"00"), 4 bits) // The current hex digit 

  val data = Reg(Bits(C_data_len bits))                        // Data to display

  // Show which buttons are pressed
  data := 0
  for(i <- 0 to 6)
    data(i << 3, 8 bits) := (io.btn(i).asUInt * (i+1)).resize(8).asBits

  // Leds can be used for diagnostics
  io.led := 0

  // Fill Oled screen with hex digits
  when (oled.io.next_pixel) { // Next pixel requested
    // Set the pixel color
    R_pixel := S_pixel 

    when (R_cpixel === 3) { // Get the data index for the next digit
      R_data_index := S_row @@ S_column
    } elsewhen (R_cpixel === 4) { // Get the data for the next digit
      R_indexed_data := S_indexed_data
    } 
    
    when (R_cpixel === 5) { // End of current digit
      R_cpixel := 0
      // Get font line
      R_char_line := C_oled_font((R_indexed_data ## S_scanline).asUInt.resized)
      R_increment := R_increment + 1 // Go on to next digit column

      when (R_increment === 0) { // At end of screen, sample data
        R_data := data
      } 
    } otherwise {
      // Shift out font line
      R_char_line := B"0" ## R_char_line(C_char_line_bits-1 downto 1)
      R_cpixel := R_cpixel + 1 // Move to next pixel column
    }
  }

  oled.io.color := R_pixel
}

object OledHex {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new OledHex)
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

