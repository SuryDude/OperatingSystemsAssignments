This program is a Scheduler written in Java.

To run this program you must have javac and java commands and a text file for which you'll provide a name as input. The random-numbers.txt is provided at the root and will by the main function without the need for command line arguments. Optionally you may provide --verbose as the first command line argument for a detailed output. Either with or without --verbose will operate the First Come First Served, Round Robin, Uniprogrammed, and Shortest Job First algorithms.

Instructions:
>cd Lab2-Scheduler/src
>javac *.java
>java Schedule <input-filename>
or
>java Schedule --verbose <input-filename>

example:
>javac *.java
>java Schedule --verbose Inputs/input-4.txt

Note: Will only work with absolute file path or relevant input file in same directory as java classes (src)

Author- Paul Merritt
