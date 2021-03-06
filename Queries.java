import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.Collections;

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
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DB_URL = "jdbc:sqlite:";
    static Connection conn = null;

    public static void connect(String dbPath)
    {
        try
        {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL + dbPath);
            System.out.println("Connecting to database...\n");
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

            String query =
                    "SELECT ProfileName FROM\n" +
                    "(SELECT ProfileName, COUNT(Id)\nFROM Reviews\nGROUP BY ProfileName\nORDER BY COUNT(Id) DESC LIMIT 1000) " +
                    "ORDER BY ProfileName ASC";
            ResultSet rs = statement.executeQuery(query);
            System.out.println("1000 MOST ACTIVE USERS:");
            while (rs.next()) {
                System.out.println(rs.getString(1)); //gets the first column's rows.
            }
            statement.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void secondQuery()
    {
        Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ProductId FROM\n" +
                    "(SELECT ProductId, COUNT(Id)\nFROM Reviews\nGROUP BY ProductId\nORDER BY COUNT(Id) DESC LIMIT 1000) " +
                    "ORDER BY ProductId ASC");
            System.out.println("\n1000 MOST COMMENTED PRODUCTS:");
            while (rs.next()) {
                System.out.println(rs.getString(1)); //gets the first column's rows.
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void thirdQuery(String csvPath)
    {
        HashMap<String, Integer> map = new HashMap();
        List<String> mostUsedWords = new ArrayList<>();
        CSVParser parser = new CSVParser();
        int maxNum = 0;
        //Parsing csv file:
        try
        {
            FileReader fileReader = new FileReader(csvPath);
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

                words = text.split(" ");
                for (int i = 0; i < words.length; i++)
                {
                    //Trying to avoid unwanted chars and duplicate words (lower/upper case)
                    words[i] = words[i].toLowerCase();
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
        }
        //There is an exception from csvParser I didn't take care of because it doesn't collapse the program.
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //Sort map by value (number of appearances of each word), and putting first 1000 into a list.
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
        //Sort list of 1000 most used words by alphabetical order and printing it.
        Collections.sort(mostUsedWords);
        System.out.println("\n1000 MOST USED WORDS:");
        for (String word: mostUsedWords)
        {
            System.out.println(word);
        }
    }

    public static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map){
        Comparator<String> comparator = new ValueComparator(map);
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(map);
        return result;
    }
    
    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Usage: Queries [fullpath_to_sqlite_db] [fullpath_to_csv_file]");
        }

        connect(args[0]);
        firstQuery();
        secondQuery();
        thirdQuery(args[1]);
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

