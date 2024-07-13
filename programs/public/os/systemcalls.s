# Bootup
_start: 
    la t0, exception_handler        # setting up exception handler
    csrw mtvec, t0                  # setting mtvec to exception handler

    la t0, user_systemcalls         # load address of user program
    csrw mepc, t0                   # set mepc to point to user program

    mret                            # return to user mode

exception_handler:
    addi sp, sp, -32                # make space on stack to save registers
    sw ra, 0(sp)                    # save ra
    sw t0, 4(sp)                    # save t0
    sw t1, 8(sp)                    # save t1
    sw t2, 12(sp)                   # save t2
    sw a0, 16(sp)                   # save a0
    sw a1, 20(sp)                   # save a1
    sw a7, 24(sp)                   # save a7
    csrr t0, mepc                   # read mepc to t0
    sw t0, 28(sp)                   # save mepc

    csrr t0, mcause                 # read mcause to t0
    li t1, 8                        # load syscall code into t1
    beq t0, t1, handle_syscall      # check if exception is a system call

    j restore_registers             # restore registers if not a syscall

handle_syscall:
    li t1, 11
    beq a7, t1, syscall_print_char  # check if syscall is print_char
    li t1, 4
    beq a7, t1, syscall_print_string # check if syscall is print_string
    j restore_registers

syscall_print_char:
    la t1, display_ready            # load adress of display_ready
wait_display_ready:
    lw t0, 0(t1)                    # check display status
    andi t0, t0, 1                  # bitwise and t0 wtih 1
    beqz t0, wait_display_ready     # wait for display to be ready
    la t1, display_data             # load adress of display_data
    sb a0, 0(t1)                    # print character to display
    j update_mepc                   # update mepc and return

syscall_print_string:
    mv t2, a0                       # t2 points to the string in memory
print_string_loop:
    lb a0, 0(t2)                    # load byte from string
    beqz a0, update_mepc            # if null terminator, return
    la t1, display_ready
wait_display_ready_str:
    lw t0, 0(t1)
    andi t0, t0, 1
    beqz t0, wait_display_ready_str # wait for display to be ready
    la t1, display_data
    sb a0, 0(t1)                    # print character to display
    addi t2, t2, 1                  # move to next character
    j print_string_loop

update_mepc:
    csrr t0, mepc
    addi t0, t0, 4                  # increment mepc to skip ecall
    csrw mepc, t0
    j restore_registers

restore_registers:
    lw t0, 28(sp)                   # restore mepc
    csrw mepc, t0
    lw ra, 0(sp)                    # restore ra
    lw t0, 4(sp)                    # restore t0
    lw t1, 8(sp)                    # restore t1
    lw t2, 12(sp)                   # restore t2
    lw a0, 16(sp)                   # restore a0
    lw a1, 20(sp)                   # restore a1
    lw a7, 24(sp)                   # restore a7
    addi sp, sp, 32                 # restore stack pointer
    mret     