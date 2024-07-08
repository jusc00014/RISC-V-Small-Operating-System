package RISCV.utils.assembler

object InstType extends Enumeration {
  type Type = Value
  val I, R, B, S, U, J, C, CI = Value
}

case class Instruction(
    name:      String,
    realName:  String = "",
    instType:  InstType.Type,
    funct3:    String = "",
    funct7:    String = "",
    opcode:    String,
    hasOffset: Boolean = false,
    isCsr:     Boolean = false,
    hasImm:    Boolean = false,
    fixed:     String = "",
  )

protected object Instructions {
  def apply(instruction: String): Option[Instruction] = {
    try {
      instructions.find(_.name == instruction.toUpperCase)
    } catch {
      case _: Exception => throw new RISCVAssemblerException(s"Instruction $instruction not found.")
    }
  }

  // scalafmt: { maxColumn = 130}
  private val instructions = List(
    Instruction(name = "LUI", instType     = InstType.U, opcode = "0110111"),
    Instruction(name = "AUIPC", instType   = InstType.U, opcode = "0010111", funct3 = "001"),
    Instruction(name = "JAL", instType     = InstType.J, opcode = "1101111"),
    Instruction(name = "JALR", instType    = InstType.I, opcode = "1100111", funct3 = "000"),
    Instruction(name = "BEQ", instType     = InstType.B, opcode = "1100011", funct3 = "000"),
    Instruction(name = "BNE", instType     = InstType.B, opcode = "1100011", funct3 = "001"),
    Instruction(name = "BLT", instType     = InstType.B, opcode = "1100011", funct3 = "100"),
    Instruction(name = "BGE", instType     = InstType.B, opcode = "1100011", funct3 = "101"),
    Instruction(name = "BLTU", instType    = InstType.B, opcode = "1100011", funct3 = "110"),
    Instruction(name = "BGEU", instType    = InstType.B, opcode = "1100011", funct3 = "111"),
    Instruction(name = "LB", instType      = InstType.I, opcode = "0000011", funct3 = "000", hasOffset  = true),
    Instruction(name = "LH", instType      = InstType.I, opcode = "0000011", funct3 = "001", hasOffset  = true),
    Instruction(name = "LW", instType      = InstType.I, opcode = "0000011", funct3 = "010", hasOffset  = true),
    Instruction(name = "LBU", instType     = InstType.I, opcode = "0000011", funct3 = "100", hasOffset  = true),
    Instruction(name = "LHU", instType     = InstType.I, opcode = "0000011", funct3 = "101", hasOffset  = true),
    Instruction(name = "SB", instType      = InstType.S, opcode = "0100011", funct3 = "000"),
    Instruction(name = "SH", instType      = InstType.S, opcode = "0100011", funct3 = "001"),
    Instruction(name = "SW", instType      = InstType.S, opcode = "0100011", funct3 = "010"),
    Instruction(name = "ADDI", instType    = InstType.I, opcode = "0010011", funct3 = "000"),
    Instruction(name = "SLTI", instType    = InstType.I, opcode = "0010011", funct3 = "010"),
    Instruction(name = "SLTIU", instType   = InstType.I, opcode = "0010011", funct3 = "011"),
    Instruction(name = "XORI", instType    = InstType.I, opcode = "0010011", funct3 = "100"),
    Instruction(name = "ORI", instType     = InstType.I, opcode = "0010011", funct3 = "110"),
    Instruction(name = "ANDI", instType    = InstType.I, opcode = "0010011", funct3 = "111"),
    Instruction(name = "SLLI", instType    = InstType.I, opcode = "0010011", funct3 = "001", fixed      = "0000000"),
    Instruction(name = "SRLI", instType    = InstType.I, opcode = "0010011", funct3 = "101", fixed      = "0000000"),
    Instruction(name = "SRAI", instType    = InstType.I, opcode = "0010011", funct3 = "101", fixed      = "0100000"),
    Instruction(name = "ADD", instType     = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "000"),
    Instruction(name = "SUB", instType     = InstType.R, opcode = "0110011", funct7 = "0100000", funct3 = "000"),
    Instruction(name = "SLL", instType     = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "001"),
    Instruction(name = "SLT", instType     = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "010"),
    Instruction(name = "SLTU", instType    = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "011"),
    Instruction(name = "XOR", instType     = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "100"),
    Instruction(name = "SRL", instType     = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "101"),
    Instruction(name = "SRA", instType     = InstType.R, opcode = "0110011", funct7 = "0100000", funct3 = "101"),
    Instruction(name = "OR", instType      = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "110"),
    Instruction(name = "AND", instType     = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "111"),
    Instruction(name = "FENCE", instType   = InstType.I, opcode = "0001111", funct3 = "000"),
    Instruction(name = "FENCE.I", instType = InstType.I, opcode = "0001111", funct3 = "001", fixed      = "000000000000"),
    Instruction(name = "ECALL", instType   = InstType.I, opcode = "1110011", funct3 = "000", fixed      = "000000000000"),
    Instruction(name = "EBREAK", instType  = InstType.I, opcode = "1110011", funct3 = "000", fixed      = "000000000001"),
    // Instructions below are still not implemented
    Instruction(name = "CSRRW", instType  = InstType.C, opcode = "1110011", funct3 = "001", isCsr = true, hasImm = false),
    Instruction(name = "CSRRS", instType  = InstType.C, opcode = "1110011", funct3 = "010", isCsr = true, hasImm = false),
    Instruction(name = "CSRRC", instType  = InstType.C, opcode = "1110011", funct3 = "011", isCsr = true, hasImm = false),
    Instruction(name = "CSRRWI", instType = InstType.CI, opcode = "1110011", funct3 = "101", isCsr = true, hasImm = true),
    Instruction(name = "CSRRSI", instType = InstType.CI, opcode = "1110011", funct3 = "110", isCsr = true, hasImm = true),
    Instruction(name = "CSRRCI", instType = InstType.CI, opcode = "1110011", funct3 = "111", isCsr = true, hasImm = true),

    Instruction(name = "MUL", instType     = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "000"),
    Instruction(name = "MULH", instType    = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "001"),
    Instruction(name = "MULHSU", instType  = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "010"),
    Instruction(name = "MULHU", instType   = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "011"),
    Instruction(name = "DIV", instType     = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "100"),
    Instruction(name = "DIVU", instType    = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "101"),
    Instruction(name = "REM", instType     = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "110"),
    Instruction(name = "REMU", instType    = InstType.R, opcode = "0110011", funct7 = "0000001", funct3 = "111"),

    Instruction(name = "SRET", instType  = InstType.I, opcode = "1110011", funct3 = "000", fixed      = "000100000010"),
    Instruction(name = "MRET", instType  = InstType.I, opcode = "1110011", funct3 = "000", fixed      = "001100000010"),
    Instruction(name = "MNRET", instType  = InstType.I, opcode = "1110011", funct3 = "000", fixed      = "011100000010"),
    Instruction(name = "WFI", instType  = InstType.I, opcode = "1110011", funct3 = "000", fixed      = "000100000101"),

    Instruction(name = "LA1", instType     = InstType.U, opcode = "0110111"),
    Instruction(name = "LA2", instType    = InstType.I, opcode = "0010011", funct3 = "000"),
    Instruction(name = "LI1", instType     = InstType.U, opcode = "0110111"),
    Instruction(name = "LI2", instType    = InstType.I, opcode = "0010011", funct3 = "000"),
  )
}

/**
 * This function transforms a pseudo instruction to it's real conterpart
 */
protected object PseudoInstructions {
  def apply(instructionData: Array[String]): Option[Array[String]] =
    if (instructionData.length < 1) throw new RISCVAssemblerException("Instruction data is empty, cannot parse it.")
    else
    instructionData(0).toUpperCase match {
      // Map received params to the corresponding RISC-V instruction
      case "NOP"  => { if (instructionData.length != 1) throw new RISCVAssemblerException("NOP should not have extra arguments.") else Some(Array("addi", "x0", "x0", "0")) }
      case "MV"   => { if (instructionData.length != 3) throw new RISCVAssemblerException("MV should have exactly two arguments.") else Some(Array("addi", instructionData(1), instructionData(2), "0")) }
      case "NOT"  => { if (instructionData.length != 3) throw new RISCVAssemblerException("NOT should have exactly two arguments.") else Some(Array("xori", instructionData(1), instructionData(2), "-1")) }
      case "NEG"  => { if (instructionData.length != 3) throw new RISCVAssemblerException("NEG should have exactly two arguments.") else Some(Array("sub", instructionData(1), "x0", instructionData(2))) }
      case "SEQZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("SEQZ should have exactly two arguments.") else Some(Array("sltiu", instructionData(1), instructionData(2), "1")) }
      case "SNEZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("SNEZ should have exactly two arguments.") else Some(Array("sltu", instructionData(1), "x0", instructionData(2))) }
      case "SLTZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("SLTZ should have exactly two arguments.") else Some(Array("slt", instructionData(1), instructionData(2), "x0")) }
      case "SGTZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("SGTZ should have exactly two arguments.") else Some(Array("slt", instructionData(1), "x0", instructionData(2))) }
      case "BEQZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("BEQZ should have exactly two arguments.") else Some(Array("beq", instructionData(1), "x0", instructionData(2))) }
      case "BNEZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("BNEZ should have exactly two arguments.") else Some(Array("bne", instructionData(1), "x0", instructionData(2))) }
      case "BLEZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("BLEZ should have exactly two arguments.") else Some(Array("bge", "x0", instructionData(1), instructionData(2))) }
      case "BGEZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("BGEZ should have exactly two arguments.") else Some(Array("bge", instructionData(1), "x0", instructionData(2))) }
      case "BLTZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("BLTZ should have exactly two arguments.") else Some(Array("blt", instructionData(1), "x0", instructionData(2))) }
      case "BGTZ" => { if (instructionData.length != 3) throw new RISCVAssemblerException("BGTZ should have exactly two arguments.") else Some(Array("blt", "x0", instructionData(1), instructionData(2))) }
      case "BGT"  => { if (instructionData.length != 4) throw new RISCVAssemblerException("BGT should have exactly three arguments.") else Some(Array("blt", instructionData(2), instructionData(1), instructionData(3))) }
      case "BLE"  => { if (instructionData.length != 4) throw new RISCVAssemblerException("BLE should have exactly three arguments.") else Some(Array("bge", instructionData(2), instructionData(1), instructionData(3))) }
      case "BGTU" => { if (instructionData.length != 4) throw new RISCVAssemblerException("BGTU should have exactly three arguments.") else Some(Array("bltu", instructionData(2), instructionData(1), instructionData(3))) }
      case "BLEU" => { if (instructionData.length != 4) throw new RISCVAssemblerException("BLEU should have exactly three arguments.") else Some(Array("bgeu", instructionData(2), instructionData(1), instructionData(3))) }
      case "J"    => { if (instructionData.length != 2) throw new RISCVAssemblerException("J should have exactly one argument.") else Some(Array("jal", "x0", instructionData(1))) }
      case "JR"   => { if (instructionData.length != 2) throw new RISCVAssemblerException("JR should have exactly one argument.") else Some(Array("jalr", "x0", instructionData(1), "0")) }
      case "RET"  => { if (instructionData.length != 1) throw new RISCVAssemblerException("RET should not have extra arguments.") else Some(Array("jalr", "x0", "x1", "0")) }
      case "CSRR" => { if (instructionData.length != 3) throw new RISCVAssemblerException("CSRR should have exactly two arguments.") else Some(Array("csrrs", instructionData(1), instructionData(2), "x0")) }
      case "CSRW" => { if (instructionData.length != 3) throw new RISCVAssemblerException("CSRW should have exactly two arguments.") else Some(Array("csrrw", "x0", instructionData(1), instructionData(2))) }
      case "CSRWI" => { if (instructionData.length != 3) throw new RISCVAssemblerException("CSRWI should have exactly two arguments.") else Some(Array("csrrwi", "x0", instructionData(1), instructionData(2))) }
      case "CSRS" => { if (instructionData.length != 3) throw new RISCVAssemblerException("CSRS should have exactly two arguments.") else Some(Array("csrrs", "x0", instructionData(1), instructionData(2))) }
      case "CSRC" => { if (instructionData.length != 3) throw new RISCVAssemblerException("CSRC should have exactly two arguments.") else Some(Array("csrrc", "x0", instructionData(1), instructionData(2))) }
      case "CSRSI" => { if (instructionData.length != 3) throw new RISCVAssemblerException("CSRSI should have exactly two arguments.") else Some(Array("csrrsi", "x0", instructionData(1), instructionData(2))) }
      case "CSRCI" => { if (instructionData.length != 3) throw new RISCVAssemblerException("CSRCI should have exactly two arguments.") else Some(Array("csrrci", "x0", instructionData(1), instructionData(2))) }
      case _      => None
    }
}