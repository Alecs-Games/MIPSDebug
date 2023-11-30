# MIPSDebug
Command-line debugger for MIPS programs, meant for educational use

To compile: 
```
javac *.java
```
To run in interactive mode from a .asm file:
```
java MIPSDebug sample.asm
```
To run in script mode, which automatically enters commands from a file, with one command on each line:
```
java MIPSDebug sample.asm script
```
Commands:
```
"h", "help" - print out a usage message
"d", "dump" - print program counter and status of all registers
"s", "step" - execute a single line
"s", "step" [n] - execute [n] number of lines
"r", "run" - run until end of file
"m", "mem", "memory" [n1] [n2] - print out all memory from address [n1] to address [n2]
"c", "clear" - clear all memory and reset program counter
"pc" - print out the program counter
"pc add" [n] - add [n] to the program counter
"pc sub", "pc subtract" - subtract [n] from the program counter
"q", "quit" - exit
```
NOTE: You can also type out any valid and supported MIPS instruction in interactive mode to execute it immediately without incrementing the program counter. Invalid MIPS commands might crash the program.

Current supported MIPS instructions:
```
add, or, and, sll, sub, slt, jr
addi, beq, bne, lw, sw
j, jal
```
Current available registers:
```
0, v0, v1, a0, a1, a2, a3, t0, t1, t2, t3, t4, t5, t6, t7, s0, s1, s2, s3, s4 ,s5, s6, s7, t8, t9, sp, ra
```
	
