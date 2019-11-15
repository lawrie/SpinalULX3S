VERILOG ?= LedButtons.v

prog: bin/toplevel.bit
	ujprog $<

IDCODE ?= 0x21111043 # 12f

bin/toplevel.json: ${VERILOG}
	mkdir -p bin
	yosys \
		-p "synth_ecp5 -json $@" \
		$<

bin/toplevel.config: bin/toplevel.json
	nextpnr-ecp5 \
		--json $< \
		--textcfg $@ \
		--lpf ulx3s_v20.lpf \
		--25k \
		--package CABGA381

bin/toplevel.bit: bin/toplevel.config
	ecppack --idcode $(IDCODE) $< $@

clean:
	$(RM) -rf bin
