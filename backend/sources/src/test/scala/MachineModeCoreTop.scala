import chisel3._

import scala.collection.immutable.SortedMap

import RISCV.interfaces.machine_mode._
import RISCV.interfaces.generic._

import RISCV.model._

class MachineModeCoreTop extends Module {
    val io_reset = IO(new ResetInterface)
    val io_instr = IO(new InstructionInterface)
    val io_data = IO(new DataInterface)
    val io_rvfi = IO(new RVFIInterface)
    implicit val csrMappingTypeOrdering: Ordering[CSR_MAPPING.Type] = Ordering.by(csr => csr.litValue)
    val csr_rvfi: SortedMap[CSR_MAPPING.Type, CSR_RVFI] = SortedMap(CSR_MAPPING.all.sortBy(csr => csr.litValue).map(x => x -> IO(new CSR_RVFI)): _*)
    val io_interrupt = IO(new InterruptInterface)

    val core = Module(new MachineModeCore)
    core.io.clock := clock
    core.io.reset := reset

    core.io.io_reset_rst_n := io_reset.rst_n
    core.io.io_reset_boot_addr := io_reset.boot_addr

    io_instr.instr_req := core.io.io_instr_instr_req
    io_instr.instr_addr := core.io.io_instr_instr_addr
    core.io.io_instr_instr_gnt := io_instr.instr_gnt
    core.io.io_instr_instr_rdata := io_instr.instr_rdata

    io_data.data_req := core.io.io_data_data_req
    io_data.data_addr := core.io.io_data_data_addr
    io_data.data_be := core.io.io_data_data_be
    io_data.data_we := core.io.io_data_data_we
    io_data.data_wdata := core.io.io_data_data_wdata
    core.io.io_data_data_gnt := io_data.data_gnt
    core.io.io_data_data_rdata := io_data.data_rdata

    io_rvfi.rvfi_valid := core.io.io_rvfi_rvfi_valid
    io_rvfi.rvfi_order := core.io.io_rvfi_rvfi_order
    io_rvfi.rvfi_insn := core.io.io_rvfi_rvfi_insn
    io_rvfi.rvfi_trap := core.io.io_rvfi_rvfi_trap
    io_rvfi.rvfi_halt := core.io.io_rvfi_rvfi_halt
    io_rvfi.rvfi_intr := core.io.io_rvfi_rvfi_intr
    io_rvfi.rvfi_mode := core.io.io_rvfi_rvfi_mode
    io_rvfi.rvfi_ixl := core.io.io_rvfi_rvfi_ixl
    io_rvfi.rvfi_rs1_addr := core.io.io_rvfi_rvfi_rs1_addr
    io_rvfi.rvfi_rs2_addr := core.io.io_rvfi_rvfi_rs2_addr
    io_rvfi.rvfi_rs1_rdata := core.io.io_rvfi_rvfi_rs1_rdata
    io_rvfi.rvfi_rs2_rdata := core.io.io_rvfi_rvfi_rs2_rdata
    io_rvfi.rvfi_rd_addr := core.io.io_rvfi_rvfi_rd_addr
    io_rvfi.rvfi_rd_wdata := core.io.io_rvfi_rvfi_rd_wdata
    io_rvfi.rvfi_pc_rdata := core.io.io_rvfi_rvfi_pc_rdata
    io_rvfi.rvfi_pc_wdata := core.io.io_rvfi_rvfi_pc_wdata
    io_rvfi.rvfi_mem_addr := core.io.io_rvfi_rvfi_mem_addr
    io_rvfi.rvfi_mem_rmask := core.io.io_rvfi_rvfi_mem_rmask
    io_rvfi.rvfi_mem_wmask := core.io.io_rvfi_rvfi_mem_wmask
    io_rvfi.rvfi_mem_rdata := core.io.io_rvfi_rvfi_mem_rdata
    io_rvfi.rvfi_mem_wdata := core.io.io_rvfi_rvfi_mem_wdata

    csr_rvfi(CSR_MAPPING.MSTATUS).rdata := core.io.csr_rvfi_0_2_rdata
    csr_rvfi(CSR_MAPPING.MSTATUS).rmask := core.io.csr_rvfi_0_2_rmask
    csr_rvfi(CSR_MAPPING.MSTATUS).wdata := core.io.csr_rvfi_0_2_wdata
    csr_rvfi(CSR_MAPPING.MSTATUS).wmask := core.io.csr_rvfi_0_2_wmask

    csr_rvfi(CSR_MAPPING.MISA).rdata := core.io.csr_rvfi_1_2_rdata
    csr_rvfi(CSR_MAPPING.MISA).rmask := core.io.csr_rvfi_1_2_rmask
    csr_rvfi(CSR_MAPPING.MISA).wdata := core.io.csr_rvfi_1_2_wdata
    csr_rvfi(CSR_MAPPING.MISA).wmask := core.io.csr_rvfi_1_2_wmask

    csr_rvfi(CSR_MAPPING.MEDELEG).rdata := core.io.csr_rvfi_2_2_rdata
    csr_rvfi(CSR_MAPPING.MEDELEG).rmask := core.io.csr_rvfi_2_2_rmask
    csr_rvfi(CSR_MAPPING.MEDELEG).wdata := core.io.csr_rvfi_2_2_wdata
    csr_rvfi(CSR_MAPPING.MEDELEG).wmask := core.io.csr_rvfi_2_2_wmask

    csr_rvfi(CSR_MAPPING.MIDELEG).rdata := core.io.csr_rvfi_3_2_rdata
    csr_rvfi(CSR_MAPPING.MIDELEG).rmask := core.io.csr_rvfi_3_2_rmask
    csr_rvfi(CSR_MAPPING.MIDELEG).wdata := core.io.csr_rvfi_3_2_wdata
    csr_rvfi(CSR_MAPPING.MIDELEG).wmask := core.io.csr_rvfi_3_2_wmask

    csr_rvfi(CSR_MAPPING.MIE).rdata := core.io.csr_rvfi_4_2_rdata
    csr_rvfi(CSR_MAPPING.MIE).rmask := core.io.csr_rvfi_4_2_rmask
    csr_rvfi(CSR_MAPPING.MIE).wdata := core.io.csr_rvfi_4_2_wdata
    csr_rvfi(CSR_MAPPING.MIE).wmask := core.io.csr_rvfi_4_2_wmask

    csr_rvfi(CSR_MAPPING.MTVEC).rdata := core.io.csr_rvfi_5_2_rdata
    csr_rvfi(CSR_MAPPING.MTVEC).rmask := core.io.csr_rvfi_5_2_rmask
    csr_rvfi(CSR_MAPPING.MTVEC).wdata := core.io.csr_rvfi_5_2_wdata
    csr_rvfi(CSR_MAPPING.MTVEC).wmask := core.io.csr_rvfi_5_2_wmask

    csr_rvfi(CSR_MAPPING.MCOUNTEREN).rdata := core.io.csr_rvfi_6_2_rdata
    csr_rvfi(CSR_MAPPING.MCOUNTEREN).rmask := core.io.csr_rvfi_6_2_rmask
    csr_rvfi(CSR_MAPPING.MCOUNTEREN).wdata := core.io.csr_rvfi_6_2_wdata
    csr_rvfi(CSR_MAPPING.MCOUNTEREN).wmask := core.io.csr_rvfi_6_2_wmask

    csr_rvfi(CSR_MAPPING.MSTATUSH).rdata := core.io.csr_rvfi_7_2_rdata
    csr_rvfi(CSR_MAPPING.MSTATUSH).rmask := core.io.csr_rvfi_7_2_rmask
    csr_rvfi(CSR_MAPPING.MSTATUSH).wdata := core.io.csr_rvfi_7_2_wdata
    csr_rvfi(CSR_MAPPING.MSTATUSH).wmask := core.io.csr_rvfi_7_2_wmask

    csr_rvfi(CSR_MAPPING.MEDELEGH).rdata := core.io.csr_rvfi_8_2_rdata
    csr_rvfi(CSR_MAPPING.MEDELEGH).rmask := core.io.csr_rvfi_8_2_rmask
    csr_rvfi(CSR_MAPPING.MEDELEGH).wdata := core.io.csr_rvfi_8_2_wdata
    csr_rvfi(CSR_MAPPING.MEDELEGH).wmask := core.io.csr_rvfi_8_2_wmask

    csr_rvfi(CSR_MAPPING.MCOUNTINHIBIT).rdata := core.io.csr_rvfi_9_2_rdata
    csr_rvfi(CSR_MAPPING.MCOUNTINHIBIT).rmask := core.io.csr_rvfi_9_2_rmask
    csr_rvfi(CSR_MAPPING.MCOUNTINHIBIT).wdata := core.io.csr_rvfi_9_2_wdata
    csr_rvfi(CSR_MAPPING.MCOUNTINHIBIT).wmask := core.io.csr_rvfi_9_2_wmask

    csr_rvfi(CSR_MAPPING.MSCRATCH).rdata := core.io.csr_rvfi_10_2_rdata
    csr_rvfi(CSR_MAPPING.MSCRATCH).rmask := core.io.csr_rvfi_10_2_rmask
    csr_rvfi(CSR_MAPPING.MSCRATCH).wdata := core.io.csr_rvfi_10_2_wdata
    csr_rvfi(CSR_MAPPING.MSCRATCH).wmask := core.io.csr_rvfi_10_2_wmask

    csr_rvfi(CSR_MAPPING.MEPC).rdata := core.io.csr_rvfi_11_2_rdata
    csr_rvfi(CSR_MAPPING.MEPC).rmask := core.io.csr_rvfi_11_2_rmask
    csr_rvfi(CSR_MAPPING.MEPC).wdata := core.io.csr_rvfi_11_2_wdata
    csr_rvfi(CSR_MAPPING.MEPC).wmask := core.io.csr_rvfi_11_2_wmask

    csr_rvfi(CSR_MAPPING.MCAUSE).rdata := core.io.csr_rvfi_12_2_rdata
    csr_rvfi(CSR_MAPPING.MCAUSE).rmask := core.io.csr_rvfi_12_2_rmask
    csr_rvfi(CSR_MAPPING.MCAUSE).wdata := core.io.csr_rvfi_12_2_wdata
    csr_rvfi(CSR_MAPPING.MCAUSE).wmask := core.io.csr_rvfi_12_2_wmask

    csr_rvfi(CSR_MAPPING.MTVAL).rdata := core.io.csr_rvfi_13_2_rdata
    csr_rvfi(CSR_MAPPING.MTVAL).rmask := core.io.csr_rvfi_13_2_rmask
    csr_rvfi(CSR_MAPPING.MTVAL).wdata := core.io.csr_rvfi_13_2_wdata
    csr_rvfi(CSR_MAPPING.MTVAL).wmask := core.io.csr_rvfi_13_2_wmask

    csr_rvfi(CSR_MAPPING.MIP).rdata := core.io.csr_rvfi_14_2_rdata
    csr_rvfi(CSR_MAPPING.MIP).rmask := core.io.csr_rvfi_14_2_rmask
    csr_rvfi(CSR_MAPPING.MIP).wdata := core.io.csr_rvfi_14_2_wdata
    csr_rvfi(CSR_MAPPING.MIP).wmask := core.io.csr_rvfi_14_2_wmask

    csr_rvfi(CSR_MAPPING.MTINST).rdata := core.io.csr_rvfi_15_2_rdata
    csr_rvfi(CSR_MAPPING.MTINST).rmask := core.io.csr_rvfi_15_2_rmask
    csr_rvfi(CSR_MAPPING.MTINST).wdata := core.io.csr_rvfi_15_2_wdata
    csr_rvfi(CSR_MAPPING.MTINST).wmask := core.io.csr_rvfi_15_2_wmask

    csr_rvfi(CSR_MAPPING.MCYCLE).rdata := core.io.csr_rvfi_16_2_rdata
    csr_rvfi(CSR_MAPPING.MCYCLE).rmask := core.io.csr_rvfi_16_2_rmask
    csr_rvfi(CSR_MAPPING.MCYCLE).wdata := core.io.csr_rvfi_16_2_wdata
    csr_rvfi(CSR_MAPPING.MCYCLE).wmask := core.io.csr_rvfi_16_2_wmask

    csr_rvfi(CSR_MAPPING.MINSTRET).rdata := core.io.csr_rvfi_17_2_rdata
    csr_rvfi(CSR_MAPPING.MINSTRET).rmask := core.io.csr_rvfi_17_2_rmask
    csr_rvfi(CSR_MAPPING.MINSTRET).wdata := core.io.csr_rvfi_17_2_wdata
    csr_rvfi(CSR_MAPPING.MINSTRET).wmask := core.io.csr_rvfi_17_2_wmask

    csr_rvfi(CSR_MAPPING.MCYCLEH).rdata := core.io.csr_rvfi_18_2_rdata
    csr_rvfi(CSR_MAPPING.MCYCLEH).rmask := core.io.csr_rvfi_18_2_rmask
    csr_rvfi(CSR_MAPPING.MCYCLEH).wdata := core.io.csr_rvfi_18_2_wdata
    csr_rvfi(CSR_MAPPING.MCYCLEH).wmask := core.io.csr_rvfi_18_2_wmask

    csr_rvfi(CSR_MAPPING.MINSTRETH).rdata := core.io.csr_rvfi_19_2_rdata
    csr_rvfi(CSR_MAPPING.MINSTRETH).rmask := core.io.csr_rvfi_19_2_rmask
    csr_rvfi(CSR_MAPPING.MINSTRETH).wdata := core.io.csr_rvfi_19_2_wdata
    csr_rvfi(CSR_MAPPING.MINSTRETH).wmask := core.io.csr_rvfi_19_2_wmask

    csr_rvfi(CSR_MAPPING.CYCLE).rdata := core.io.csr_rvfi_20_2_rdata
    csr_rvfi(CSR_MAPPING.CYCLE).rmask := core.io.csr_rvfi_20_2_rmask
    csr_rvfi(CSR_MAPPING.CYCLE).wdata := core.io.csr_rvfi_20_2_wdata
    csr_rvfi(CSR_MAPPING.CYCLE).wmask := core.io.csr_rvfi_20_2_wmask

    csr_rvfi(CSR_MAPPING.TIME).rdata := core.io.csr_rvfi_21_2_rdata
    csr_rvfi(CSR_MAPPING.TIME).rmask := core.io.csr_rvfi_21_2_rmask
    csr_rvfi(CSR_MAPPING.TIME).wdata := core.io.csr_rvfi_21_2_wdata
    csr_rvfi(CSR_MAPPING.TIME).wmask := core.io.csr_rvfi_21_2_wmask

    csr_rvfi(CSR_MAPPING.INSTRET).rdata := core.io.csr_rvfi_22_2_rdata
    csr_rvfi(CSR_MAPPING.INSTRET).rmask := core.io.csr_rvfi_22_2_rmask
    csr_rvfi(CSR_MAPPING.INSTRET).wdata := core.io.csr_rvfi_22_2_wdata
    csr_rvfi(CSR_MAPPING.INSTRET).wmask := core.io.csr_rvfi_22_2_wmask

    csr_rvfi(CSR_MAPPING.CYCLEH).rdata := core.io.csr_rvfi_23_2_rdata
    csr_rvfi(CSR_MAPPING.CYCLEH).rmask := core.io.csr_rvfi_23_2_rmask
    csr_rvfi(CSR_MAPPING.CYCLEH).wdata := core.io.csr_rvfi_23_2_wdata
    csr_rvfi(CSR_MAPPING.CYCLEH).wmask := core.io.csr_rvfi_23_2_wmask

    csr_rvfi(CSR_MAPPING.TIMEH).rdata := core.io.csr_rvfi_24_2_rdata
    csr_rvfi(CSR_MAPPING.TIMEH).rmask := core.io.csr_rvfi_24_2_rmask
    csr_rvfi(CSR_MAPPING.TIMEH).wdata := core.io.csr_rvfi_24_2_wdata
    csr_rvfi(CSR_MAPPING.TIMEH).wmask := core.io.csr_rvfi_24_2_wmask

    csr_rvfi(CSR_MAPPING.INSTRETH).rdata := core.io.csr_rvfi_25_2_rdata
    csr_rvfi(CSR_MAPPING.INSTRETH).rmask := core.io.csr_rvfi_25_2_rmask
    csr_rvfi(CSR_MAPPING.INSTRETH).wdata := core.io.csr_rvfi_25_2_wdata
    csr_rvfi(CSR_MAPPING.INSTRETH).wmask := core.io.csr_rvfi_25_2_wmask

    csr_rvfi(CSR_MAPPING.MVENDORID).rdata := core.io.csr_rvfi_26_2_rdata
    csr_rvfi(CSR_MAPPING.MVENDORID).rmask := core.io.csr_rvfi_26_2_rmask
    csr_rvfi(CSR_MAPPING.MVENDORID).wdata := core.io.csr_rvfi_26_2_wdata
    csr_rvfi(CSR_MAPPING.MVENDORID).wmask := core.io.csr_rvfi_26_2_wmask

    csr_rvfi(CSR_MAPPING.MARCHID).rdata := core.io.csr_rvfi_27_2_rdata
    csr_rvfi(CSR_MAPPING.MARCHID).rmask := core.io.csr_rvfi_27_2_rmask
    csr_rvfi(CSR_MAPPING.MARCHID).wdata := core.io.csr_rvfi_27_2_wdata
    csr_rvfi(CSR_MAPPING.MARCHID).wmask := core.io.csr_rvfi_27_2_wmask

    csr_rvfi(CSR_MAPPING.MIMPID).rdata := core.io.csr_rvfi_28_2_rdata
    csr_rvfi(CSR_MAPPING.MIMPID).rmask := core.io.csr_rvfi_28_2_rmask
    csr_rvfi(CSR_MAPPING.MIMPID).wdata := core.io.csr_rvfi_28_2_wdata
    csr_rvfi(CSR_MAPPING.MIMPID).wmask := core.io.csr_rvfi_28_2_wmask

    csr_rvfi(CSR_MAPPING.MHARTID).rdata := core.io.csr_rvfi_29_2_rdata
    csr_rvfi(CSR_MAPPING.MHARTID).rmask := core.io.csr_rvfi_29_2_rmask
    csr_rvfi(CSR_MAPPING.MHARTID).wdata := core.io.csr_rvfi_29_2_wdata
    csr_rvfi(CSR_MAPPING.MHARTID).wmask := core.io.csr_rvfi_29_2_wmask

    csr_rvfi(CSR_MAPPING.MCONFIGPTR).rdata := core.io.csr_rvfi_30_2_rdata
    csr_rvfi(CSR_MAPPING.MCONFIGPTR).rmask := core.io.csr_rvfi_30_2_rmask
    csr_rvfi(CSR_MAPPING.MCONFIGPTR).wdata := core.io.csr_rvfi_30_2_wdata
    csr_rvfi(CSR_MAPPING.MCONFIGPTR).wmask := core.io.csr_rvfi_30_2_wmask

    csr_rvfi(CSR_MAPPING.UNKNOWN).rdata := core.io.csr_rvfi_31_2_rdata
    csr_rvfi(CSR_MAPPING.UNKNOWN).rmask := core.io.csr_rvfi_31_2_rmask
    csr_rvfi(CSR_MAPPING.UNKNOWN).wdata := core.io.csr_rvfi_31_2_wdata
    csr_rvfi(CSR_MAPPING.UNKNOWN).wmask := core.io.csr_rvfi_31_2_wmask

    core.io.io_interrupt_m_ext := io_interrupt.m_ext
    core.io.io_interrupt_m_timer := io_interrupt.m_timer
}