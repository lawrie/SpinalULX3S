package mylib

import spinal.core._
import spinal.lib._

// Driver for SSD1331 Oled display
class SSD1331(init_file: String = "oled_init_16bit.mem", init_size: Int = 44) extends Component {
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

  // Load the SSD1331 initialisation file
  val C_oled_init = Mem(Bits(8 bits), wordCount=init_size)
  C_oled_init.initialContent = Tools.readmemh(init_file)

  val resetCnt = Reg(UInt(2 bits))
  val initCnt = Reg(UInt(10 bits)) // Max 64 bytes of init sequence
  val data = Reg(Bits(8 bits))
  val dc = Reg(Bool)
  val byteToggle = Reg(Bool)

  io.oled_resn := ~resetCnt(0) // Does reset on first cycle
  io.oled_csn := resetCnt(0)   // Then sets cs on second and keeps it set
  io.oled_dc := dc             // Set for data
  io.oled_clk := ~initCnt(0)   // Oled clock is half system clock speed
  io.oled_mosi := data(7)      // Shifts data out to MOSI pin

  // Send initialisation commands first, then start sending 16-bit color pixels
  // Bit 0 of initCnt is Oled clock, bits 1 - 3 shift out data
  when (resetCnt =/= 2) {
    resetCnt := resetCnt + 1
  } elsewhen (initCnt(9 downto 4) < init_size) {
    initCnt := initCnt + 1
    when (initCnt(3 downto 0) === 0) {
      when (!dc) { // Sending init sequence
        data := C_oled_init(initCnt(9 downto 4))
      } otherwise { // Sending pixels
        byteToggle := ~byteToggle
        data := byteToggle ? io.color(7 downto 0) | io.color(15 downto 8)
        when (!byteToggle) {
          io.next_pixel := True // Request next pixel
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
        // Shift out data
        data := data(6 downto 0) ## B"0"
      }
    }
  } otherwise {
    // Sending pixels
    dc := True
    initCnt(9 downto 4) := init_size - 1
  }
}

// Display hex bytes on SSD1331 Oled display
// Width of data to be displayed can be 8, 16, 32, 64, 128, 256 0r 512
// Values less than 512 bits will be repeated across and down the screen
class SSD1331Hex(width : Int = 64) extends Component {
  val io = new Bundle {
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
    val data = in Bits(width bits)
  }

  val oled = new SSD1331("oled_init_xflip_16bit.mem") // Use horizontal flip init sequence
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
  }

  oled.io.color := R_pixel
}

// Test of SSD13311Hex with 64-bit value set by pressing buttons
// The value is repeated on each line of the screen
class SSD1331HexTest extends Component {
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

  val oledHex = new SSD1331Hex(64)
  io.oled_csn := oledHex.io.oled_csn
  io.oled_clk := oledHex.io.oled_clk
  io.oled_mosi := oledHex.io.oled_mosi
  io.oled_dc := oledHex.io.oled_dc
  io.oled_resn := oledHex.io.oled_resn
  oledHex.io.data := data
}

object SSD1331HexTest {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new SSD1331HexTest)
  }
}

// Checkered flag test of SSD1331
class SSD1331Checkered extends Component {
  val io = new Bundle {
    val oled_csn = out Bool
    val oled_clk = out Bool
    val oled_mosi = out Bool
    val oled_dc = out Bool
    val oled_resn = out Bool
  }.setName("")

  val oled = new SSD1331()
  io.oled_csn := oled.io.oled_csn
  io.oled_clk := oled.io.oled_clk
  io.oled_mosi := oled.io.oled_mosi
  io.oled_dc := oled.io.oled_dc
  io.oled_resn := oled.io.oled_resn

  val x = oled.io.x
  val y = oled.io.y

  oled.io.color := (x(3) ^ y(3)) ? (B"00000" ## x(6 downto 1).asBits ## B"00000") | (y(5 downto 1).asBits ## B"000000" ## B"00000")

}

object SSD1331Checkered {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new SSD1331Checkered)
  }
}

