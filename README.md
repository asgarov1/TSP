# Travelling Salesman Problem
by Javid Asgarov &
   Agnaldo Oliveira Moura Junior

execute the program with -e|-n <nameOfFile.txt>

File should have the first line with the number of entries to read, and the rest of the lines contain C and Y coordinates in the following format:

5<br>
1.0 1.0<br>
5.5 0.9<br>
3.0 1.5<br>
0.5 4.3<br>
4.0 3.0<br>

-e will execute complete enumeration (brute forcing through all the routes)
-n will execute nearest-neighbour algorithm

In either case the program will display the found route, route distance, and the time it took for the calculation