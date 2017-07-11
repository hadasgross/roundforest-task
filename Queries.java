import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;

import com.opencsv.CSVParser;
class ValueComparator implements Comparator<String>{

    HashMap<String, Integer> map = new HashMap<String, Integer>();

    public ValueComparator(HashMap<String, Integer> map){
        this.map.putAll(map);
    }

    @Override
    public int compare(String s1, String s2) {
        if(map.get(s1) >= map.get(s2)){
            return -1;
        }else{
            return 1;
        }
    }
}
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
            System.out.println("Connecting to database...");
        }
        catch(SQLException | ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static void firstQuery()
    {


        Statement insertStmt = null;
        try
        {

            insertStmt = conn.createStatement();

            String insert = "INSERT INTO MostActiveUsers (Name)" +
                    "SELECT ProfileName FROM\n" +
                    "(SELECT ProfileName, COUNT(Id)\nFROM Reviews\nGROUP BY ProfileName\nORDER BY COUNT(Id) DESC) " +
                    "LIMIT 1000";

            insertStmt.execute(insert);

            insertStmt.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void secondQuery()
    {

        Statement insertStmt = null;
        try
        {

            insertStmt = conn.createStatement();

            insertStmt.execute("INSERT INTO MostCommentedFoods (ProductId)" +
                    "SELECT ProductId FROM\n" +
                    "(SELECT ProductId, COUNT(Id)\nFROM Reviews\nGROUP BY ProductId\nORDER BY COUNT(Id) DESC) " +
                    "LIMIT 1000");

            insertStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void thirdQuery()
    {

        Statement insertStmt = null;
        try
        {

            insertStmt = conn.createStatement();

            insertStmt.execute("INSERT INTO MostUsedWords(Word) SELECT DISTINCT\n" +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(Reviews.Text, ' ', numbers.n), ' ', -1) Text\n" +
                    "FROM\n" +
                    "(SELECT 1 n UNION ALL SELECT 2\n" +
                    "UNION ALL SELECT 3 UNION ALL SELECT 4) numbers INNER JOIN Reviews\n" +
                    "  ON CHAR_LENGTH(Reviews.Text)\n" +
                    "     -CHAR_LENGTH(REPLACE(Reviews.Text, ' ', ''))>=numbers.n-1\n" +
                    "ORDER BY\n" +
                    "  Text");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void dropTables()
    {
        Statement dropStmt = null;
        try {
            dropStmt = conn.createStatement();
            String drop = "DROP TABLE MostActiveUsers";
            String drop2 = "DROP TABLE MostCommentedFoods";
            String drop3 = "DROP TABLE MostUsedWords";
            dropStmt.execute(drop);
            dropStmt.execute(drop2);
            dropStmt.execute(drop3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static void parseCsv()
    {
        HashMap<String, Integer> map = new HashMap();
        Queue<String> mostUsedWords = new PriorityQueue<>();
        CSVParser parser = new CSVParser();
        int maxNum = 0;
        try {
            FileReader fileReader = new FileReader("D:/task/src/Reviews.csv");
            BufferedReader reader= new BufferedReader(fileReader);
            String line = null;
            String text;
            String [] parts;
            String [] words;
            line = reader.readLine();
            while ((line = reader.readLine()) != null)
            {
                parts = parser.parseLine(line);
                if (parts.length < 10)
                {
                    continue;
                }
                text = parts[9];
                if (parts.length > 10)
                {
                    for (int i = 10; i < parts.length; i++) {
                        text += parts[i];
                    }
                }
                //text = text.replaceAll("!|.|,|;", "");
                words = text.split(" ");
                for (int i = 0; i < words.length; i++)
                {
                    words[i] = words[i].replace(",", "");
                    words[i] = words[i].replace(".", "");
                    words[i] = words[i].replace("(", "");
                    words[i] = words[i].replace(")", "");
                    words[i] = words[i].replace("/", "");
                    words[i] = words[i].replace("<", "");
                    words[i] = words[i].replace(">", "");

                    if (words[i].equals("") || words[i].equals(".") || words[i].equals("&") ||
                            words[i].equals("?") || words[i].equals("-") || words[i].equals("--") ||
                            words[i].equals(":"))
                    {
                        continue;
                    }

                    if (map.containsKey(words[i]))
                    {
                        int currVal = map.get(words[i]) + 1;
                        map.put(words[i], currVal);
                    }
                    else
                    {
                        map.put(words[i], 1);
                    }
                }
            }

            reader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TreeMap<String,Integer> sortedMap = sortMapByValue(map);
        int count = 0;
        for(Map.Entry<String,Integer> entry : sortedMap.entrySet()) {
            if (count == 1000)
            {
                break;
            }
            mostUsedWords.add(entry.getKey());

            count++;
        }

        try
        {
            String SQL = "INSERT INTO MostUsedWords VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(SQL);


            for (String word : mostUsedWords) {
                pstmt.setString(1, word);
                pstmt.executeUpdate();
            }
            pstmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map){
        Comparator<String> comparator = new ValueComparator(map);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(map);
        return result;
    }
    public static void createTables()
    {

        try {
            Statement createStmt = conn.createStatement();
            createStmt.execute("CREATE TABLE MostUsedWords (Word VARCHAR(255))");
            createStmt.execute("CREATE TABLE MostCommentedFoods (ProductId VARCHAR(255))");
            createStmt.execute("CREATE TABLE MostActiveUsers (Name VARCHAR(255))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args)
    {
        connect();
        dropTables(); //for me only
        createTables();
        firstQuery();
        secondQuery();
        parseCsv();
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

