# TODO: wait for keyboard input
# TODO: read keyboard input
# TODO: wait for display ready
# TODO: print keyboard input to display
# TODO: start again



   # Polling loop

poll_loop:

    # TODO: wait for keyboard input

    la t0, keyboard_ready
wait_keyboard_ready:
    lw t1, 0(t0)                    # Load keyboard ready status
    andi t1, t1, 1                  # Check if keyboard is ready
    beqz t1, wait_keyboard_ready    # If not ready, wait

    # TODO: read keyboard input
    la t0, keyboard_data
    lw t2, 0(t0)                    # Load character from keyboard

    # TODO: wait for display ready
    la t0, display_ready
wait_display_ready:
    lw t1, 0(t0)                    # Load display ready status
    andi t1, t1, 1                  # Check if display is ready
    beqz t1, wait_display_ready     # If not ready, wait

    # TODO: print keyboard input to display
    la t0, display_data
    sb t2, 0(t0)                    # Store character to display

    # TODO: start again
    j poll_loop                     # Repeat the loop
