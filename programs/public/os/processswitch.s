setup: 
# Set CSRs
    la t0, exception_handler        # setting up exception handler
    csrw mtvec, t0                  # setting mtvec to exception handler
    la t0, fibonacci                # load address of user program
    csrw mepc, t0                   # set mepc to point to fibonacci
    li t0, 136                      # set mstatus to 136 => enable interrupts
    csrw mstatus, t0
    li t0, 128                      # set mie to 128 => enable timer interrupts
    csrw mie, t0
    li t0, 0
    csrw mscratch, t0               # we use mscratch to store if the first or second process was running
# Store default values for the registers
    li t1, 0x800
    la t0, factorial
    sw t0, 0(t1)                    # 0x800 contains PC of second process 
    li t1, 0x0
    la t0, fibonacci         
    sw t0, 0(t1)                    # 0x0 contains PC of first process
    jal ra, update_timer
    li t0, 128  
    mret                            # return to user mode
    csrw mip, t0                    # set mip to 128 => timer interrupt is expected soon 

# when a timer interrupt occours
exception_handler:
    addi sp, sp, -12
    sw t0, 0(sp)
    sw t1, 4(sp)
    sw ra, 8(sp)
    csrr t1, mscratch
    li t0, 0                        # set mip to 0 => no timer interrupt while syscall is handled
    csrw mip, t0
    beqz t1, first
second:                             # When the second process was running
    li t1, 0x800
    jal ra, store
    li t1, 0
    csrw mscratch, t1               # Sign next process as the first one
    li t1, 0x0
    j restore
first:                              # When the first process was running
    li t1, 0x0
    jal ra, store
    li t1, 1
    csrw mscratch, t1               # Sign next process as the second one
    li t1, 0x800
    j restore
store:                               # Store all the current registers
    csrr t0, mepc
    addi t0, t0, 4
    sw t0, 0(t1)                    # Store the PC
    lw t0, 8(sp)
    sw t0, 4(t1)                    # Store the return address
    lw t0, 4(sp)
    sw t0, 24(t1)                   # Store t1
    lw t0, 0(sp)
    addi sp, sp, 12
    sw x2, 8(t1)
    sw x3, 12(t1)
    sw x4, 16(t1)
    sw x5, 20(t1)
    sw x7, 28(t1)
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
    sw x28, 112(t1)
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
    mret
    li t0, 128                      # set mip to 128 => timer interrupt is expected soon
    csrw mip, t0
    lw t0, 0(sp)
    addi sp, sp, 4
    
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