# TODO: setup exception handler
# TODO: setup mepc to point to the first instruction of the selfcloning function
# TODO: enable and setup interrupts as needed
# TODO: jump to user mode

setup: 
# Set CSRs
    la t0, exception_handler        # setting up exception handler
    csrw mtvec, t0                  # setting mtvec to exception handler
    la t0, selfcloning              # load address of user program
    csrw mepc, t0                   # set mepc to point to user programm
    li t0, 136                      # set mstatus to 136 => enable interrupts
    csrw mstatus, t0
    li t0, 128                      # set mie to 128 => enable timer interrupts
    csrw mie, t0
    li t0, 17
    csrw mscratch, t0               # we use mscratch to store what process is running and how many processes are there (lower byte gives PID, upper bytes give the numbers of cloned processes)
    jal ra, update_timer
    li t0, 128  
    mret                            # return to user mode
    csrw mip, t0                    # set mip to 128 => timer interrupt is expected soon 





exception_handler:
    addi sp, sp, -24                    # make some space in registers
    sw t0, 0(sp)
    sw t1, 4(sp)
    li t0, 0                        # set mip to 0 => no timer interrupt while syscall is handled
    csrw mip, t0
    csrr t0, mepc
    addi t0, t0, 4
    csrw mepc, t0                   # continue the process after the syscall
    csrr t0, mcause
    li t1, 8
    beq t0, t1, handle_ecall            # was exception caused by ecall?
    li t1, 7
    beq t1, t1, handle_interrupt        # was exception caused by timer interrupt?
    lw t0, 0(sp)
    lw t1, 4(sp)
    addi sp, sp, 24
    addi sp, sp, -4
    sw x5, 0(sp)
    mret
    li t0, 128                      # set mip to 128 => timer interrupt is expected soon
    csrw mip, t0
    lw t0, 0(sp)
    addi sp, sp, 4
    # TODO: save some registers
    # TODO: check the cause of the exception: ecall or timer interrupt


handle_ecall:
    li t1, 220
    beq t1, a7, clone_process
    li t1, 172
    beq t1, a7, return_pid
    lw t0, 0(sp)
    lw t1, 4(sp)
    addi sp, sp, 24
    addi sp, sp, -4
    sw x5, 0(sp)
    mret
    li t0, 128                      # set mip to 128 => timer interrupt is expected soon
    csrw mip, t0
    lw t0, 0(sp)
    addi sp, sp, 4
    # TODO: check which system call is requested and jump to the corresponding handler


handle_interrupt:
    sw t2, 12(sp)
    sw t3, 16(sp)
    csrr t3, mscratch
    andi t2, t3, 0xfffffff0
    srli t2, t2, 4
    andi t3, t3, 0xf
    li t1, 400
    addi t3, t3, -1
    mul t1, t1, t3                  # calculate the base address of the currently running process
    addi t3, t3, 1
    sw ra, 4(t1)
    jal ra, store                   # store registers of the current process
    blt t3, t2, next                # if the current process is not the last one
    li t3, 0                        # the next process is the first one
next:
    li t1, 400
    mul t1, t1, t3                  # calculate the base address of the next process
    addi t3, t3, 1
    slli t2, t2, 4
    add t3, t3, t2
    csrw mscratch, t3               # update which process is running
    lw t0, 0(sp)
    lw t2, 12(sp)
    lw t3, 16(sp)
    j restore
    # TODO: switch to next process and set up new timer interrupt


clone_process:
    sw t2, 8(sp)
    sw t3, 12(sp)
    sw t4, 16(sp)
    sw t5, 20(sp)
    csrr t0, mscratch
    andi t2, t0, 0xfffffff0
    srli t2, t2, 4
    andi t0, t0, 0xf
    li t1, 7
    bgt t2, t1, to_much             # if we already have more than 7 clones
    li t3, 400
    mul t1, t2, t3                  # calculate the base address of the childs pcb
    addi t2, t2, 1                  # increase the number of running processes
    addi t4, ra, 0
    jal ra, store_copy                       # store the current registers in the pcb of the child process
    addi a0, t2, 0
    slli t2, t2, 4
    add t0, t0, t2
    csrw mscratch, t0               # update the mscratch that there is now another process; the running process stays the same
    lw t0, 4(sp)                    # (re)store the t-registers and ra
    addi ra, t4, 0
    sw ra, 4(t1)
    lw t2, 8(sp)
    lw t3, 12(sp)
    lw t4, 16(sp)
    lw t5, 20(sp)
    sw t0, 24(t1)
    sw t2, 28(t1)
    sw t3, 112(t1)
    sw t4, 116(t1)
    sw t5, 120(t1)
    sw t6, 124(t1)
    lw t0, 0(sp)
    sw t0, 20(t1)
    lw t1, 4(sp)
    addi sp, sp, 24
    addi sp, sp, -4
    sw x5, 0(sp)
    mret
    li t0, 128                      # set mip to 128 => timer interrupt is expected soon
    csrw mip, t0
    lw t0, 0(sp)
    addi sp, sp, 4


    # TODO: assign new process ID
    # TODO: set up new process control block based on parent process
    # TODO: set return value correctly for both processes
    # TODO: continue with parent process

to_much:
    li a0, -1
    lw t0, 0(sp)
    lw t1, 4(sp)
    addi sp, sp, 24
    addi sp, sp, -4
    sw x5, 0(sp)
    mret
    li t0, 128                      # set mip to 128 => timer interrupt is expected soon
    csrw mip, t0
    lw t0, 0(sp)
    addi sp, sp, 4

return_pid:
    csrr a0, mscratch               # read off the PID from the lower byte of mscratch
    andi a0, a0, 0xf
    lw t0, 0(sp)
    lw t1, 4(sp)
    addi sp, sp, 24
    addi sp, sp, -4
    sw x5, 0(sp)
    mret
    li t0, 128                      # set mip to 128 => timer interrupt is expected soon
    csrw mip, t0
    lw t0, 0(sp)
    addi sp, sp, 4 
    # TODO: return the process ID of the current process


update_timer:
# Set the timer-interrupt
    la a3 mtime                     # read off the current time
    lw a4, 0(a3)
    lw a1, 4(a3)
    addi a0, a4, 400                # the new cmp-value is the current time plus 320 (this will give a little less than 300 cycles because we take time to set the value)
    bge a0, a4, incrementcmp        # if no overflow in the lower bits of mtime occured continue incermenting the mtimecmp
    addi a1, a1, 1
incrementcmp:                       #increment the new comparison-value
    li a3, 1
    la a4, mtimecmp
    sw a3, 0(a4)                    # No smaller than old value.
    sw a1, 4(a4)                    # No smaller than new value.
    sw a0, 0(a4)                    # New value.
    jr ra

store_copy:
    csrr t5, mepc
    sw t5, 0(t1)                  # Store the PC    
    sw x2, 8(t1)                  # Store all but the t registers
    sw x3, 12(t1)
    sw x4, 16(t1)
    sw x8, 32(t1)
    sw x9, 36(t1)
    li x10, 0
    sw x10, 40(t1)
    sw x11, 44(t1)
    sw x12, 48(t1)
    sw x13, 52(t1)
    sw x14, 56(t1)
    sw x15, 60(t1)
    sw x16, 64(t1)
    sw x17, 68(t1)
    sw x18, 72(t1)
    sw x19, 76(t1)
    sw x20, 80(t1)
    sw x21, 84(t1)
    sw x22, 88(t1)
    sw x23, 92(t1)
    sw x24, 96(t1)
    sw x25, 100(t1)
    sw x26, 104(t1)
    sw x27, 108(t1)
    jr ra

store:                               # Store all the current registers
    csrr t0, mepc
    sw t0, 0(t1)                    # Store the PC
    lw t0, 4(sp)
    sw t0, 24(t1)                   # Store t1, t2, t3
    lw t0, 12(sp)
    sw t0, 28(t1)
    lw t0, 16(sp)
    sw t0, 112(sp)
    lw t0, 0(sp)
    sw x2, 8(t1)
    sw x3, 12(t1)
    sw x4, 16(t1)
    sw x5, 20(t1)
    sw x8, 32(t1)
    sw x9, 36(t1)
    sw x10, 40(t1)
    sw x11, 44(t1)
    sw x12, 48(t1)
    sw x13, 52(t1)
    sw x14, 56(t1)
    sw x15, 60(t1)
    sw x16, 64(t1)
    sw x17, 68(t1)
    sw x18, 72(t1)
    sw x19, 76(t1)
    sw x20, 80(t1)
    sw x21, 84(t1)
    sw x22, 88(t1)
    sw x23, 92(t1)
    sw x24, 96(t1)
    sw x25, 100(t1)
    sw x26, 104(t1)
    sw x27, 108(t1)
    sw x29, 116(t1)
    sw x30, 120(t1)
    sw x31, 124(t1)
    jr ra
restore:                             # Restore all the registers of the next process
    lw x2, 8(t1)
    lw x3, 12(t1)
    lw x4, 16(t1)
    lw x7, 28(t1)
    lw x8, 32(t1)
    lw x9, 36(t1)
    lw x10, 40(t1)
    lw x15, 60(t1)
    lw x16, 64(t1)
    lw x17, 68(t1)
    lw x18, 72(t1)
    lw x19, 76(t1)
    lw x20, 80(t1)
    lw x21, 84(t1)
    lw x22, 88(t1)
    lw x23, 92(t1)
    lw x24, 96(t1)
    lw x25, 100(t1)
    lw x26, 104(t1)
    lw x27, 108(t1)
    lw x28, 112(t1)
    lw x29, 116(t1)
    lw x30, 120(t1)
    lw x31, 124(t1)
    jal ra, update_timer
    lw t0, 0(t1)
    csrw mepc, t0                   # Restore PC
    lw x1, 4(t1)                    # Restore the registers that were still in use until now
    lw x11, 44(t1)
    lw x12, 48(t1)
    lw x13, 52(t1)
    lw x14, 56(t1)
    lw x5, 20(t1)
    lw x6, 24(t1)
    addi sp, sp, -4
    sw x5, 0(sp)
    li t0, 128                      # set mip to 128 => timer interrupt is expected soon
    mret
    csrw mip, t0
    lw t0, 0(sp)
    addi sp, sp, 4