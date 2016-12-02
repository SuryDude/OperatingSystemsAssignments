# OperatingSystemsAssignments
Operating Systems Assignments - Professor Allan Gottlieb

Lab 1 - Linker: Create a two-pass linker in Java. 

The target machine is word addressable and has a memory of 600 words, each consisting of 4 decimal digits.
The first (leftmost) digit is the opcode, which is unchanged by the linker. The next three digits (called the
address field) are either
(1) an immediate operand, which is unchanged;
(2) an absolute address, which is unchanged;
(3) a relative address, which is relocated; or
(4) an external address, which is resolved.
Relocating relative addresses and resolving external references were discussed in class and are in the notes.
Input consists of a series of object modules, each of which contains three parts: definition list, use list, and
program text, in that order.
The linker processes the input twice (that is why it is called two-pass). Pass one determines the base
address for each module and the absolute address for each external symbol, storing the later in the symbol
table it produces. The first module has base address zero; the base address for module I+1 is equal to the
base address of module I plus the length of module I. The absolute address for a relative address defined in
module M is the base address of M plus the relative address of S within M. Pass two uses the base addresses
and the symbol table computed in pass one to generate the actual output by relocating relative addresses
and resolving external references.


Lab 2 - Scheduler: Simulate scheduling in order to see how the time required depends on the scheduling algorithm
and the request patterns.

You are to read a file describing n processes (i.e., n quadruples of numbers) and then simulate the n processes until
they all terminate. The way to do this is to keep track of the state of each process and advance time making any state
transitions needed. At the end of the run you first print an identification of the run including the scheduling algorithm
used, any parameters (e.g. the quantum for RR), and the number of processes simulated. You then print for
each process
• (A, B,C, M)
• Finishing time.
• Turnaround time (i.e., finishing time - A).
• I/O time (i.e., time in Blocked state).
• Waiting time (i.e., time in Ready state).
Then print the following summary data.
• Finishing time (i.e., when all the processes have finished).
• CPU Utilization (i.e., percentage of time some job is running).
• I/O Utilization (i.e., percentage of time some job is blocked).
• Throughput, expressed in processes completed per hundred time units.
• Average turnaround time.
• Average waiting time.
You must simulate each of the following scheduling algorithms, assuming, for simplicity, that a context switch takes
zero time. You need only do calculations every time unit (e.g., you may assume nothing exciting happens at time
2.5).
• FCFS.
• RR with quantum 2.
• Uniprogrammed. Just one process active. When it is blocked, the system waits.
• SJF (This is not preemptive, but is not uniprogrammed, i.e. switch on I/O bursts). Recall that SJF is shortest job
first, not shortest burst first. So the time you use to determine priority is the total time remaining (i.e., the input
value C minus the number of cycles this process has run).

Lab 3 - Banker: The goal of this lab is to do resource allocation using both an optimistic resource manager and the
banker’s algorithm of Dijkstra. 

The optimistic resource manager is simple: Satisfy a request if possible, if
not make the task wait; when a release occurs, try to satisfy pending requests in a FIFO manner.
Your program takes one command line argument, the name of file containing the input. After reading
(all) the input, the program performs two simulations: one with the optimistic manager and one with the
banker. Output is written to stdout (the screen). Input files for thirteen required runs are available on the
web, together with the expected output. We will test your program on additional runs as well.
The input begins with two values T, the number of tasks, and R, the number of resource types, followed
by R additional values, the number of units present of each resource type. (If you set ‘‘arbitrary limits’’ on
say T or R, you must, document this in your readme, check that the input satisfies the limits, print an error
if it does not, and set the limits high enough so that the required inputs all pass.) Then come multiple
inputs, each representing the next activity of a specific task. The possible activities are initiate, request,
compute, release, and terminate. Time is measured in fixed units called cycles and, for simplicity, no fractional
cycles are used. The manager can process one activity (initiate, request, or release) for each task in
one cycle. However, the terminate activity does NO T require a cycle.