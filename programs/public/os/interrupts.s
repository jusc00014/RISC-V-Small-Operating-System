.text
_start:
    # Set up exception handler
    la t0, exception_handler       # Load the address of the exception handler
    csrw mtvec, t0                 # Set mtvec to the exception handler address

    # Set up mepc to point to the first instruction of the Fibonacci function
    la t0, fibonacci               # Load the address of the Fibonacci function
    csrw mepc, t0                  # Set mepc to point to the Fibonacci function

    # Enable interrupts as needed
    li t0, 0x880                   # Bitmask to enable external interrupts and timer interrupts
    csrrs x0, mie, t0              # Set the bits in mie register to enable external and timer interrupts, preserving existing values

    li t0, 0x8                     # Bitmask to enable global interrupts 
    csrrs x0, mstatus, t0          # Set the MIE bit in mstatus register to enable global interrupts, preserving existing values

    # Enable keyboard and display interrupts
    la t1, keyboard_ready
    lw t0, 0(t1)                   # Load current value of keyboard control port
    ori t0, t0, 0x2                # Set interrupt enable bit (bit 1)
    sw t0, 0(t1)                   # Store modified value back

    la t1, display_ready
    lw t0, 0(t1)                   # Load current value of display control port
    ori t0, t0, 0x2                # Set interrupt enable bit (bit 1)
    sw t0, 0(t1)                   # Store modified value back

    # Initialize memory for circular buffer and pointers
    li t1, 0                       # Initialize head and tail pointers to 0
    li t0, 0x0000007f              # Full 32-bit address for head pointer
    sw t1, 0(t0)                   # Store head pointer at 0x0000007f
    li t0, 0x00000083              # Full 32-bit address for tail pointer
    sw t1, 0(t0)                   # Store tail pointer at 0x00000083

    # Execute the Fibonacci function until an interrupt occurs
    mret                           # Return to user mode to start Fibonacci computation

exception_handler:
    # Save registers
    addi sp, sp, -32               # Make space on stack to save registers
    sw ra, 0(sp)                   # Save ra
    sw t0, 4(sp)                   # Save t0
    sw t1, 8(sp)                   # Save t1
    sw t2, 12(sp)                  # Save t2
    sw a0, 16(sp)                  # Save a0
    sw a1, 20(sp)                  # Save a1
    sw a7, 24(sp)                  # Save a7
    csrr t0, mepc                  # Read mepc to t0
    sw t0, 28(sp)                  # Save mepc

    # Check if the cause of the exception is an interrupt
    li t1, 0x80000000              # Create a bitmask with a 1 on the 31st bit
    csrr t0, mcause                # Read mcause to t0
    and t2, t0, t1                 # Mask mcause with the bitmask
    beq t2, t1, handle_interrupt   # If the result matches the bitmask, it's an interrupt

    j restore_registers            # Restore registers if not an interrupt

handle_interrupt:
    csrr t0, mip                   # Read mip to t0
    li t1, 0x800                   # External interrupt pending bitmask
    and t2, t0, t1                 # Mask mip with the external interrupt bitmask
    bnez t2, handle_external_interrupt

    li t1, 0x80                    # Timer interrupt pending bitmask
    and t2, t0, t1                 # Mask mip with the timer interrupt bitmask
    bnez t2, handle_timer_interrupt

    j restore_registers

handle_external_interrupt:
    # Check which device is ready and handle the interrupt
    la t1, keyboard_ready
    lw t2, 0(t1)                   # Check if keyboard is ready
    andi t2, t2, 1
    bnez t2, handle_keyboard

    la t1, display_ready
    lw t2, 0(t1)                   # Check if display is ready
    andi t2, t2, 1
    bnez t2, handle_display

    j update_mepc

handle_timer_interrupt:
    # Clear the timer interrupt bit in mip
    li t1, 0x80                    # Timer interrupt bitmask
    csrrc x0, mip, t1              # Clear the timer interrupt pending bit

    j update_mepc

handle_keyboard:
    la t1, keyboard_data
    lw t2, 0(t1)                   # Read character from keyboard

    li t0, 0x0000007f              # Full 32-bit address for head pointer
    lw t3, 0(t0)                   # Load head pointer
    li t0, 0x00000083              # Full 32-bit address for tail pointer
    lw t4, 0(t0)                   # Load tail pointer

    addi t5, t3, 1
    li t6, 16                      # Buffer size
    rem t5, t5, t6                 # Compute new head pointer (circular buffer)

    beq t5, t4, buffer_full        # Check if buffer is full
    li t0, 0x00000087              # Start address of the buffer
    add t0, t0, t3
    sb t2, 0(t0)                   # Store character in buffer at (0x87 + head)

    li t0, 0x0000007f              # Full 32-bit address for head pointer
    sw t5, 0(t0)                   # Update head pointer

buffer_full:
    # Check if keyboard or display are still or again ready
    la t1, keyboard_ready
    lw t2, 0(t1)                   # Check if keyboard is still or again ready
    andi t2, t2, 1
    bnez t2, handle_keyboard

    la t1, display_ready
    lw t2, 0(t1)                   # Check if display is ready
    andi t2, t2, 1
    bnez t2, handle_display

    # Clear the external interrupt bit in mip
    li t1, 0x800                   # External interrupt bitmask
    csrrc x0, mip, t1              # Clear the external interrupt pending bit

    j update_mepc

handle_display:
    li t0, 0x0000007f              # Full 32-bit address for head pointer
    lw t3, 0(t0)                   # Load head pointer
    li t0, 0x00000083              # Full 32-bit address for tail pointer
    lw t4, 0(t0)                   # Load tail pointer

    beq t3, t4, buffer_empty       # Check if buffer is empty

    li t0, 0x00000087              # Start address of the buffer
    add t0, t0, t4
    lb t2, 0(t0)                   # Load character from buffer at (0x87 + tail)
    la t1, display_data
    sb t2, 0(t1)                   # Write character to display

    addi t4, t4, 1
    li t6, 16                      # Buffer size
    rem t4, t4, t6                 # Compute new tail pointer (circular buffer)

    li t0, 0x00000083              # Full 32-bit address for tail pointer
    sw t4, 0(t0)                   # Update tail pointer

buffer_empty:
    # Check if keyboard or display are still or again ready
    la t1, keyboard_ready
    lw t2, 0(t1)                   # Check if keyboard is still or again ready
    andi t2, t2, 1
    bnez t2, handle_keyboard

    la t1, display_ready
    lw t2, 0(t1)                   # Check if display is still or again ready
    andi t2, t2, 1
    bnez t2, handle_display

    # Clear the external interrupt bit in mip
    li t1, 0x800                   # External interrupt bitmask
    csrrc x0, mip, t1              # Clear the external interrupt pending bit

    j update_mepc

update_mepc:
    j restore_registers

# Restore registers and return to user mode (to continue Fibonacci computation)
restore_registers:
    lw t0, 28(sp)                 # Restore mepc
    csrw mepc, t0
    lw ra, 0(sp)                  # Restore ra
    lw t0, 4(sp)                  # Restore t0
    lw t1, 8(sp)                  # Restore t1
    lw t2, 12(sp)                 # Restore t2
    lw a0, 16(sp)                 # Restore a0
    lw a1, 20(sp)                 # Restore a1
    lw a7, 24(sp)                 # Restore a7
    addi sp, sp, 32               # Restore stack pointer
    mret                          # Return to user mode