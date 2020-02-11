package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.misc._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class ST7735 extends Component {
  val C_init_file = "st7735_init.mem"
  val C_init_size = 110
  val C_color_bits = 16
  val C_x_size = 128
  val C_y_size = 160
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

    val led = out(Bits(8 bits))
    val gn = out(Bits(14 bits))
    val gp = out(Bits(14 bits))
  }

  val C_oled_init = Mem(Bits(8 bits), wordCount=C_init_size)
  C_oled_init.initialContent = Tools.readmemh(C_init_file)

  val resetCnt = Reg(UInt(2 bits))
  val initCnt = Reg(UInt(11 bits))
  val data = Reg(Bits(8 bits))
  val dc = Reg(Bool)
  val byteToggle = Reg(Bool)
  val init = Reg(Bool) init True
  val numArgs = Reg(UInt(5 bits))
  val delayCnt = Reg(UInt(25 bits))
  val arg = Reg(UInt(6 bits))
  val delaySet = Reg(Bool) init False
  val cmd = Reg(UInt(5 bits))
  val lastCmd = Reg(Bits(8 bits))
  val nextByte = C_oled_init(initCnt(10 downto 4).resized)

  val msCycles = 25008

  io.led := data
  io.gp := dc.asBits.resized
  io.gn := 0

  io.oled_resn := ~resetCnt(0)
  io.oled_csn := resetCnt(0)
  io.oled_dc := dc
  io.oled_clk := initCnt(0)
  io.oled_mosi := data(7)

  when (resetCnt < 2) {
    resetCnt := resetCnt + 1
  } elsewhen (delayCnt > 0) {
    delayCnt := delayCnt - 1
  } elsewhen (initCnt(10 downto 4) < C_init_size) {
    initCnt := initCnt + 1
    when (initCnt(3 downto 0) === 0) { // Start of byte
      //delayCnt := 25000000
      when (init) { // Still initialsation
        dc := False
        arg := arg + 1
        when (arg === 0) { // New command
          cmd := cmd + 1
          data := 0
          lastCmd := nextByte
        } elsewhen (arg === 1) { // numArgs and delaySet
          numArgs := nextByte(4 downto 0).asUInt
          delaySet := nextByte(7)
          when (nextByte === 0) { // No args or delay
            arg := 0
          }
          data := lastCmd
        } elsewhen (arg <= numArgs+1) { // argument
          data := nextByte
          dc := True
          when (arg === numArgs+1 && !delaySet) {
            arg := 0
          }
        } elsewhen (delaySet) { // delay
          when (nextByte =/= 0xff) {
            delayCnt := (nextByte.asUInt * msCycles).resized
          } otherwise {
            delayCnt := 500 * msCycles
          }
          delaySet := False
          arg := 0
        }
      } otherwise {
        byteToggle := ~byteToggle
        dc := True
        data := byteToggle ? io.color(7 downto 0) | io.color(15 downto 8)
        when (!byteToggle) {
          io.next_pixel := True
          when (io.x === C_x_size-1) {
            io.x := 0
            when (io.y === C_y_size-1) {
              io.y := 0
            } otherwise {
              io.y := io.y + 1
            }
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
    init := False
    initCnt(10 downto 4) := C_init_size - 1
  }
}

// Display hex bytes on ST7735 Oled display
// Width of data to be displayed can be 8, 16, 32, 64, 128, 256 0r 512
// Values less than 512 bits will be repeated across and down the screen
class ST7735Hex(width : Int = 64) extends Component {
  val io = new Bundle {
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
    val data = in Bits(width bits)
  }

  val oled = new ST7735()
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

  val C_char_line_bits = 5
  val C_color_black = B(0x0000, 16 bits)
  val C_color_white = B(0xffff, 16 bits)

  val R_data = Reg(Bits(width bits))                   // Current data value
  val R_increment = Reg(UInt(10 bits)) init 1          // Increments through digits and scan lines
  val R_pixel = Reg(Bits(oled.C_color_bits bits))      // Current pixel color
  val R_cpixel = Reg(UInt(3 bits)) init 0              // Column of font
  val R_data_index = Reg(UInt(7 bits)) init U"0000010" // Index of hex digit in R_data
  val R_indexed_data = Reg(Bits(4 bits))               // The current hex_digit
  val R_char_line = Reg(Bits(C_char_line_bits bits)) init B"01110" // The current row of the font

  // Combinatorial signals
  val S_pixel = R_char_line(0) ? C_color_white | C_color_black // Current pixel color
  val S_row = R_increment(9 downto 7)                          // Current row of screen
  val S_column = R_increment(3 downto 0)                       // Current column of screen
  val S_scanline = R_increment(6 downto 4)                     // Current line in font
  val S_indexed_data = R_data((R_data_index @@ U"00"), 4 bits) // The current hex digit

  // Fill Oled screen with hex digits
  when (oled.io.next_pixel) { // Next pixel requested
    when (x >= 16 && x < 112) {
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
          R_data := io.data
        }
      } otherwise {
        // Shift out font line
        R_char_line := B"0" ## R_char_line(C_char_line_bits-1 downto 1)
        R_cpixel := R_cpixel + 1 // Move to next pixel column
      }
    } otherwise {
      R_pixel := C_color_black
    }
  }

  oled.io.color := R_pixel
}

// Test of ST5535Hex with 64-bit value set by pressing buttons
// The value is repeated on each line of the screen
class ST7735HexTest extends Component {
  val io = new Bundle {
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
    val led = out Bits(8 bits)
    val btn = in Bits(7 bits)
  }.setName("")

  val data = Reg(Bits(64 bits)) // Data to display

  // Show which buttons are pressed
  data := 0
  for(i <- 0 to 6)
    data(i << 3, 8 bits) := (io.btn(i).asUInt * (i+1)).resize(8).asBits

  // Leds can be used for diagnostics
  io.led := 0

  val oledHex = new ST7735Hex(64)
  io.oled_csn := oledHex.io.oled_csn
  io.oled_clk := oledHex.io.oled_clk
  io.oled_mosi := oledHex.io.oled_mosi
  io.oled_dc := oledHex.io.oled_dc
  io.oled_resn := oledHex.io.oled_resn
  oledHex.io.data := data
}

object ST7735HexTest {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new ST7735HexTest)
  }
}

class ST7735Test extends Component {
  val io = new Bundle {
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
    val led = out Bits(8 bits)
    val gp = out Bits(14 bits)
    val gn = out Bits(14 bits)
  }.setName("")

  val oled = new ST7735()
  io.oled_csn := oled.io.oled_csn
  io.oled_clk := oled.io.oled_clk
  io.oled_mosi := oled.io.oled_mosi
  io.oled_dc := oled.io.oled_dc
  io.oled_resn := oled.io.oled_resn

  val x = oled.io.x
  val y = oled.io.y

  io.led := oled.io.led
  io.gp := oled.io.gp
  io.gn := oled.io.gn

  /*when (y < 42) {
    oled.io.color := 0xF800
  } elsewhen (y < 85) {
    oled.io.color := 0x07E0
  } otherwise {
    oled.io.color := 0x001F
  }*/

  //oled.io.color := ((x(3) ^ y(3)) ? (B"00000" ## x(6 downto 1).asBits ## B"00000") | (y(7 downto 3).asBits  ## B"000000"  ## B"00000"))
  oled.io.color := ((x(3) ^ y(3)) ? (B"00000" ## B"111111" ## B"00000") | (B"11111"  ## B"000000"  ## B"00000"))

}

object ST7735Test {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new ST7735Test)
  }
}

