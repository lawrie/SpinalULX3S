package mylib

import spinal.core._
import spinal.lib._

class TopAudio extends Component {
  val io = new Bundle {
    val btn = in Bits(7 bits)
    val led = out Bits(8 bits)
    val audio_l = out Bits(4 bits)
    val audio_r = out Bits(4 bits)
    val audio_v = out Bits(4 bits)
    val gp = out Bits(28 bits)
    val gn = out Bits(28 bits)
    val wifi_gpio0 = out Bool
  }.setName("")

  io.wifi_gpio0 := io.btn(0)

  val triangleWave = new TriangleWave(delay = 6)
  val sineWave = new SineWave(delay = 6)

  val pcm = io.btn(1) ? triangleWave.io.pcm | sineWave.io.pcm
  
  val dacPwm = new DacPwm
  dacPwm.io.pcm := pcm

  io.audio_l := dacPwm.io.dac.asBits
  io.audio_r := dacPwm.io.dac.asBits

  val pcm24s = pcm(11).asUInt @@ pcm.asUInt @@ U(0, 11 bits)
  
  val spdif = new Spdif
  spdif.io.dataIn := pcm24s
  
  io.audio_v(3 downto 2) := B"00"
  io.audio_v(1) := spdif.io.spdifOut
  io.audio_v(0) := False

  val i2sFmt = 1

  val i2s = new I2s(fmt = i2sFmt, lrckHz = 44100)
  i2s.io.l := pcm24s(23 downto 8)
  i2s.io.r := pcm(11).asUInt @@ pcm.asUInt @@ U"000"

  val lrck = i2s.io.lrck
  val din = i2s.io.din
  val bck = i2s.io.bck

  io.led := pcm(11 downto 4).asBits

  io.gp := 0
  io.gn := 0

  io.gp(0) := lrck         // LCK
  io.gp(1) := din          // DIN
  io.gp(2) := bck          // BCK
  io.gp(3) := False        // SCL

  io.gn(0) := lrck         // LCK
  io.gn(1) := din          // DIN
  io.gn(2) := bck          // BCK
  io.gn(3) := False        // SCL

  io.gp(7)  := U(i2sFmt) === 1      // FMT 0=i2s
  io.gp(8)  := lrck        // LCK
  io.gp(9)  := din         // DIN
  io.gp(10) := bck         // BCK
  io.gp(11) := False       // SCL
  io.gp(12) := True        // DMP
  io.gp(13) := True        // FLT

  io.gn(7)  := U(i2sFmt) === 1     // FMT 0=i2s
  io.gn(8)  := lrck| io.btn(6) // LCK
  io.gn(9)  := din        // DIN
  io.gn(10) := bck         // BCK
  io.gn(11) := False       // SCL
  io.gn(12) := True        // DMP
  io.gn(13) := True        // FLT
}

object TopAudio {
  def main(args: Array[String]) {
    ULX3SSpinalConfig.generateVerilog(new TopAudio)
  }
}

