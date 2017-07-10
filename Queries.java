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
        Statement statement = null;

        try
        {
            statement = conn.createStatement();


            String countNames = "SELECT ProfileName FROM\n(SELECT ProfileName, COUNT(Id)\n" +
                    "FROM Reviews\nGROUP BY ProfileName\nORDER BY COUNT(Id) DESC) LIMIT 1000";
            statement.executeQuery(countNames);


            statement.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        connect();
        firstQuery();
        try {
            //DatabaseMetaData md = conn.getMetaData();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

