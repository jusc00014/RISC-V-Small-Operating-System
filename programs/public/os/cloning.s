# TODO: setup exception handler
# TODO: setup mepc to point to the first instruction of the selfcloning function
# TODO: enable and setup interrupts as needed
# TODO: jump to user mode

exception_handler:
    # TODO: save some registers
    # TODO: check the cause of the exception: ecall or timer interrupt


handle_ecall:
    # TODO: check which system call is requested and jump to the corresponding handler


handle_interrupt:
    # TODO: switch to next process and set up new timer interrupt


clone_process:
    # TODO: assign new process ID
    # TODO: set up new process control block based on parent process
    # TODO: set return value correctly for both processes
    # TODO: continue with parent process


return_pid:
    # TODO: return the process ID of the current process
