package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.graphic._
import spinal.lib.graphic.vga._
import spinal.lib.bus.amba3.apb.{Apb3, Apb3Config, Apb3SlaveFactory}
import spinal.lib.bus.misc.BusSlaveFactory

case class Hdmi() extends Bundle with IMasterSlave{
  val gpdi_dp = Bits(4 bits)
  val gpdi_dn = Bits(4 bits)

  override def asMaster() = this.asOutput()
  override def asSlave = this.asInput()
}

// Puts a vertically striped flag on the screen
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

object Apb3HdmiConsoleCtrl{
  def getApb3Config = Apb3Config(
    addressWidth = 4,
    dataWidth    = 32
  )
}

class Apb3HdmiConsoleCtrl(rgbConfig: RgbConfig = RgbConfig(8, 8, 8)) extends Component{
  val io = new Bundle {
    val bus = slave(Apb3(Apb3HdmiConsoleCtrl.getApb3Config))
    val vga = master(Vga(rgbConfig))
  }

  // Instantiate an HDMI console controller
  val hdmiConsoleCtrl = new HdmiConsoleCtrl(rgbConfig)
  io.vga <> hdmiConsoleCtrl.io.vga

  val busCtrl = Apb3SlaveFactory(io.bus)

  hdmiConsoleCtrl.driveFrom(busCtrl)
}  

// Controller for sending characters to VGA/HDMI screen
// Has uart-style interface
class HdmiConsoleCtrl(rgbConfig: RgbConfig) extends Component{
  val io = new Bundle{
    val vga = master(Vga(rgbConfig))
    val chars = slave Flow(Bits(8 bits))
    val led = out Bits(8 bits)
  }

  // 8x8 character font
  val font = Mem(Bits(8 bits), wordCount= 8 * 256)
  font.initialContent = Tools.readmemh("font.hex")

  val w = 80            // Width of screen in characters
  val h = 60            // Height of screen in characters
  val wBits = log2Up(w) // Number of bits for width
  val hBits = log2Up(h) // Nimber of bits for height

  // 80 x 60 character frame buffer
  val frameBuffer = Mem(Bits(8 bits), wordCount = w * h)
  
  val lineStart = Vec(UInt(hBits + wBits bits), h) // Pointers to start of lines in the frame buffer
  val lineLength = Reg(Vec(UInt(wBits bits), h))   // Array of line lengths
  val currLine = Reg(UInt(hBits bits)) init 0      // The line being written to
  val linePos = Reg(UInt(wBits bits)) init 0       // The character position in the line being written

  // The line after the current one with wraparound. 
  // This is also the start line of the screen,
  // as the line being written to is always at the bottom of the screen
  val nextLine = (currLine < h - 1) ? (currLine + U(1, hBits bits)) | U(0, hBits bits)

  // Set up the line start and lengths arrays
  for (i <- 0 to h - 1) {
    lineStart(i) := i * w
    lineLength(i).init(0)
  }

  // Write incoming character to the next position in the current line in the frame buffer
  when (io.chars.valid) {
    // Put the character in the frame buffer, unless a newline
    when (io.chars.payload =/= 0x0a) {
      frameBuffer(lineStart(currLine) + linePos) := io.chars.payload
      linePos := linePos + 1
      lineLength(currLine) := lineLength(currLine) + 1
    }

    // Start new line when current full or newline character received
    when (linePos === w - 1 || io.chars.payload === 0x0a) {
      linePos := 0
      lineLength(nextLine) := U(0, wBits bits)
      currLine := nextLine
    }
  }

  // Generate the VGA/HDMI screen
  val x = Reg(UInt(wBits + 3 bits)) init 0  // Pixel x coordinate on screen
  val y = Reg(UInt(hBits + 3 bits)) init 0  // Pixel y co-ordinate

  val screenStartLine = Reg(UInt(hBits bits)) init 1 // The line at the top of the screen

  io.led := screenStartLine.asBits.resized

  // The current row being output
  val currY = ((screenStartLine + y(hBits + 2 downto 3)) < h) ? 
        (screenStartLine + y(hBits + 2 downto 3)) | 
        (screenStartLine + y(hBits + 2 downto 3) - h)

  // The current character being output 
  val currChar = frameBuffer(lineStart(currY) + x(wBits + 2 downto 3))

  // The current row of the font or blank if past the end of the line
  val fontLine = (x(wBits + 2 downto 3) < lineLength(currY)) ? 
        font(currChar.asUInt @@ y(2 downto 0)) | B(0, 8 bits)

  val pixel = fontLine(x(2 downto 0))                // Set for pixel visible 
  val intensity = pixel ? U"11111111" | U"00000000"  // Convert to 8-bit intensity

  // The VGA/HDMI Controller at 640x480 60Hz resolution, 24-bit color
  val ctrl = new VgaCtrl(rgbConfig)
  ctrl.io.softReset := False
  ctrl.io.timings.setAs_h640_v480_r60
  ctrl.io.pixels.valid := True
  ctrl.io.pixels.payload.r := 0
  ctrl.io.pixels.payload.g := intensity
  ctrl.io.pixels.payload.b := 0
  ctrl.io.vga <> io.vga

  // Update x, y pixel co-ordinates on screen
  when (ctrl.io.pixels.ready) {
    x := x + 1
    when (x === w * 8 - 1) {
      x := 0
      y := y + 1
      when (y === h * 8 -1) {
        y := 0
        // Reset the line at the top of the screen, as it might have changed
        screenStartLine := nextLine
      }
    }
  }

   def driveFrom(busCtrl : BusSlaveFactory, baseAddress : Int = 0) = new Area {
     busCtrl.createAndDriveFlow(Bits(8 bits), address = 0) >-> io.chars
   }
}

// Test of HdmiConsoleCtrl
class HdmiTest() extends Component {
  val io = new Bundle {
    val clk = in Bool
    val reset = in Bool
    val hdmi = master(Hdmi())
    val led = out Bits(8 bits)
  }

  val pll = new HdmiPll()
  pll.io.clkin := io.clk

  val coreClockDomain = ClockDomain(io.clk, io.reset)

  val coreArea = new ClockingArea(coreClockDomain) {
    val hdmiConsoleCtrl = new HdmiConsoleCtrl(RgbConfig(8, 8, 8))
    
    val vga2Hdmi = new Vga2Hdmi()
    vga2Hdmi.io.pixclk := pll.io.clkout1
    vga2Hdmi.io.pixclk_x5 := pll.io.clkout0
    
    io.hdmi <> vga2Hdmi.io.hdmi

    // VGA to HDMI converter needs signals blanked when colorEn is false
    vga2Hdmi.io.vga.color.r := hdmiConsoleCtrl.io.vga.colorEn ? hdmiConsoleCtrl.io.vga.color.r | U"00000000"
    vga2Hdmi.io.vga.color.g := hdmiConsoleCtrl.io.vga.colorEn ? hdmiConsoleCtrl.io.vga.color.g | U"00000000"
    vga2Hdmi.io.vga.color.b := hdmiConsoleCtrl.io.vga.colorEn ? hdmiConsoleCtrl.io.vga.color.b | U"00000000"
    vga2Hdmi.io.vga.hSync := hdmiConsoleCtrl.io.vga.hSync
    vga2Hdmi.io.vga.vSync := hdmiConsoleCtrl.io.vga.vSync
    vga2Hdmi.io.vga.colorEn := hdmiConsoleCtrl.io.vga.colorEn

    io.led := hdmiConsoleCtrl.io.led

    hdmiConsoleCtrl.io.chars.valid := False
    hdmiConsoleCtrl.io.chars.payload := 0
    
    val cw = 24
    val count = Reg(UInt(cw bits)) init 0
    val pattern = Reg(UInt(5 bits)) init 31

    count := count + 1
    
    // Slowly send characters, with decreasing line length
    when (count(cw - 6 downto 0) === 0) {
      when (count(cw -1 downto cw - 5) < 10) {
        hdmiConsoleCtrl.io.chars.payload := (count(cw - 1 downto cw - 5) + 0x30).asBits.resized
        hdmiConsoleCtrl.io.chars.valid := (count(cw - 1 downto cw - 5) <= pattern)
      } elsewhen (count(cw - 1 downto cw - 5) === 31) {
        hdmiConsoleCtrl.io.chars.payload := 0x0a
        hdmiConsoleCtrl.io.chars.valid := True
        pattern := pattern - 1
      } otherwise {
        hdmiConsoleCtrl.io.chars.payload := (0x41 + (count(cw - 1 downto cw - 5) - 10)).asBits.resized
        hdmiConsoleCtrl.io.chars.valid := (count(cw - 1 downto cw - 5) <= pattern)
      }
    }

  }
}

object HdmiTest {
  def main (args: Array[String]) {
    SpinalVerilog(new HdmiTest()).toplevel
  }
}

