package MGPGPU

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage

object ALUFN {
  val SZ_ALU_FN = 4
  def FN_X      = BitPat("b????")
  def FN_ADD    = 0.U
  def FN_SL     = 1.U
  def FN_SEQ    = 2.U
  def FN_SNE    = 3.U
  def FN_XOR    = 4.U
  def FN_SR     = 5.U
  def FN_OR     = 6.U
  def FN_AND    = 7.U
  def FN_CZEQZ  = 8.U
  def FN_CZNEZ  = 9.U
  def FN_SUB    = 10.U
  def FN_SRA    = 11.U
  def FN_SLT    = 12.U
  def FN_SGE    = 13.U
  def FN_SLTU   = 14.U
  def FN_SGEU   = 15.U
}

class ALU(operandWidth: Int) extends Module {
  val io = IO(new Bundle {
    val op1    = Input(UInt(operandWidth.W))
    val op2    = Input(UInt(operandWidth.W))
    val aluOp  = Input(UInt(ALUFN.SZ_ALU_FN.W))
    val result = Output(UInt(operandWidth.W))
  })

  io.result := MuxLookup(
    io.aluOp,
    0.U,
    Seq(
      ALUFN.FN_ADD   -> (io.op1 + io.op2),
      ALUFN.FN_SL    -> (io.op1 << io.op2(4, 0)),
      ALUFN.FN_SEQ   -> (io.op1 === io.op2),
      ALUFN.FN_SNE   -> (io.op1 =/= io.op2),
      ALUFN.FN_XOR   -> (io.op1 ^ io.op2),
      ALUFN.FN_SR    -> (io.op1 >> io.op2(4, 0)),
      ALUFN.FN_OR    -> (io.op1 | io.op2),
      ALUFN.FN_AND   -> (io.op1 & io.op2),
      ALUFN.FN_CZEQZ -> Mux(io.op1 === 0.U, 1.U, 0.U),
      ALUFN.FN_CZNEZ -> Mux(io.op1 =/= 0.U, 1.U, 0.U),
      ALUFN.FN_SUB   -> (io.op1 - io.op2),
      ALUFN.FN_SRA   -> (io.op1.asSInt >> io.op2(4, 0)).asUInt,
      ALUFN.FN_SLT   -> (io.op1.asSInt < io.op2.asSInt).asUInt,
      ALUFN.FN_SGE   -> (io.op1.asSInt >= io.op2.asSInt).asUInt,
      ALUFN.FN_SLTU  -> (io.op1 < io.op2),
      ALUFN.FN_SGEU  -> (io.op1 >= io.op2),
    ),
  )
}

object ALU extends App {

  val firtoolOptions = Array(
    "--lowering-options=" + List(
      "disallowLocalVariables",
      "disallowPackedArrays",
      "locationInfoStyle=wrapInAtSquareBracket",
    ).reduce(_ + "," + _)
  )

  ChiselStage.emitSystemVerilogFile(new ALU(operandWidth = 64), args, firtoolOptions)
}
