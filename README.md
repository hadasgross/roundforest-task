Rounforest task:

this project includes the following files:
-Queries.java: Connecting and performing the tasks 1-3 on the database through SQL queries.

To run the program, you need to:
1. download open-csv library from:  https://sourceforge.net/projects/opencsv/
2. add the sqlite driver for jdbc and open-csv to the class path in compilation:
$javac Queries.java
$java -classpath ".;sqlite-jdbc-3.19.3.jar;opencsv-3.10.jar;" Queries
(Or add it through the IDE you're working with)
3. add the database to the folder where you run from. (the file database.sqlite)
