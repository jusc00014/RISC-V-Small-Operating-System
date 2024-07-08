 justborn:
    li a7, 220          # clone the process
    ecall               # ------- " -------
    beqz a0, justborn
    li a7, 172          # get own process id
    ecall               # ------- " -------
    li a7, 0x4000000    
    slli a1, a0, 2      # a0 = pid
    add a1, a1, a7      # a1 = 0x4000000 + 4 * pid, used to continuously store result of computation

fibonacci:              # should compute the 2n-th fibonacci number, where n is in a0
    addi x3, a0, 0      # x3 = a0
    addi x1, x0, 0      # x1 = 0
    addi x2, x0, 1      # x2 = 1
loop:
    add x1, x1, x2      # x1 = x1 + x2
    add x2, x1, x2      # x2 = x1 + x2
    addi x3, x3, -1     # a0 = a0 - 1
    bnez x3, loop       # if a0 != 0, loop
    sw x1, 0(a1)        # store result in memory
    j fibonacci         # repeat
