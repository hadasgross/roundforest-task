import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Queries
{
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DB_URL = "jdbc:sqlite:database.sqlite";

    public static void connect()
    {

        Connection conn = null;
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connecting to database..."); //YEEE that works!
        } catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        catch (java.lang.ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static void firstQuery()
    {
        Statement statement = null;
    }

    public static void main(String[] args)
    {
        connect();
    }
}

