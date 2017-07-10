import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Queries
{
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DB_URL = "jdbc:sqlite:D:/task/src/database.sqlite";
    static Connection conn = null;

    public static void connect()
    {

        try
        {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connecting to database..."); //YEEE that works!
        }
        catch(SQLException | ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static void firstQuery()
    {

        Statement createStmt = null;
        Statement insertStmt = null;
        try
        {

            insertStmt = conn.createStatement();
            createStmt = conn.createStatement();
            String createTable = "CREATE TABLE MostActiveUsers (Name VARCHAR(255))";
            createStmt.execute(createTable);

            String insert = "INSERT INTO MostActiveUsers (Name)" +
                    "SELECT ProfileName FROM\n" +
                    "(SELECT ProfileName, COUNT(Id)\nFROM Reviews\nGROUP BY ProfileName\nORDER BY COUNT(Id) DESC) " +
                    "LIMIT 1000";

            insertStmt.execute(insert);
            createStmt.close();
            insertStmt.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void dropFirstTable()
    {
        Statement dropStmt = null;
        try {
            dropStmt = conn.createStatement();
            String drop = "DROP TABLE MostActiveUsers";
            dropStmt.execute(drop);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args)
    {
        connect();
        dropFirstTable(); //for me only
        firstQuery();
        try {
            //DatabaseMetaData md = conn.getMetaData();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

