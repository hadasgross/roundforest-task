Rounforest task:

this project includes the following files:
-Queries.java: Connecting and performing the tasks 1-3 on the database through SQL queries.

To run the program, you need to:
1. add the database to the folder where you run from. (the files database.sqlite and Reviews.csv).
2. download open-csv library from:  https://sourceforge.net/projects/opencsv/
3. download sqlite jdbc driver from: https://bitbucket.org/xerial/sqlite-jdbc/downloads/
4. add the sqlite driver for jdbc and open-csv to the class path in compilation:
$javac Queries.java
$java -classpath ".;sqlite-jdbc-3.19.3.jar;opencsv-3.10.jar;" Queries
(Or add it through the IDE you're working with)
5. give the compiled program the full path of the sqlite database and the csv file: 
$Queries [sqlite_path] [csv_path]

Comments:
I was new to connecting to a database and executing queries through java (I have experience working directly with PostgreSQL), 
so understanding the process and the different modules and classes of JDBC was quite a challenge for me. 
That's one of the reasons the task took me longer than expected. For this reason (and others, such as exams period (-: ), as you can see I didn't get to the 4th and probably most complicated (and interesting) task. 
Hope this still shows a little of my programming skills, I did my best in the short time I had but obviously it could be much better.


