package mylib

import spinal.core._

//Define a custom SpinalHDL configuration with boot reset instead of the default asynchronous one. This configuration can be reused everywhere
object ULX3SSpinalConfig extends SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = BOOT))

