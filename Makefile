
BUILD_DIR = ./build
CHISEL_VERSION = chisel

PRJ = MGPGPU

init:
	git submodule update --init
	cd dependencies/rocket-chip && git submodule update --init hardfloat cde

test:
	mill -i $(PRJ)[$(CHISEL_VERSION)].test

verilog:
	mkdir -p $(BUILD_DIR)
	mill -i $(PRJ)[$(CHISEL_VERSION)].runMain Gcd.Main --target-dir $(BUILD_DIR)

alu:
	mkdir -p $(BUILD_DIR)
	mill -i $(PRJ)[$(CHISEL_VERSION)].runMain MGPGPU.ALU --target-dir $(BUILD_DIR)

help:
	mill -i $(PRJ).runMain Gcd.DCache --help

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

clean:
	-rm -rf $(BUILD_DIR)
	rm -rf ./out
	rm -rf ./test_run_dir

include Makefile.test

.PHONY: test verilog help reformat checkformat clean