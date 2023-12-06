import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class randomNumberMySQL
{
    static Connection c = null;
    static ResultSet rs = null;
    static Statement st = null;
    public static void main(String args[])
    {
        Random rdm = new Random();
        try
        {
            getConnection("//localhost:3306/randomnumber");

            //Create table
            st.executeUpdate("DROP TABLE IF EXISTS randomNumbers;");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS randomNumbers "+
                    "(id int PRIMARY KEY auto_increment," +
                    "value int," +
                    "value2 int);");
            //Insert records
            for (int i = 0; i < 20; i++)
            {
                int rdmNumber = rdm.nextInt(10)+1;
                st.executeUpdate("INSERT INTO randomNumbers (value, value2)" +
                        "VALUES( "+rdmNumber+", "+(rdmNumber%2)+");");
            }

            //select statement
            rs = st.executeQuery("SELECT * FROM randomNumbers;");
            System.out.println("ID\tvalue\tvalue2");
            while (rs.next())
            {
                System.out.println(rs.getInt("id")+"\t"+rs.getInt("value")+"\t"+rs.getInt("value2"));
            }
            checkOddEven(args[0]);
            closeConnection();
        }
        catch (Exception e)
        {
            System.out.println("An Error was thrown:");
            System.out.println(e.getMessage());
        }
    }
    //build up DB connection
    public static void getConnection(String url)
    {
        try
        {
            c = DriverManager.getConnection("jdbc:mysql:"+url,"root","root");
            st = c.createStatement();
        }
        catch (Exception e)
        {
            System.out.println("There was an Error thrown in getConnection Method ");
            System.out.println(e.getMessage());
        }
    }
    //close DB connection
    public static void closeConnection()
    {
        try
        {
            rs.close();
            st.close();
            c.close();
        }
        catch (Exception e)
        {
            System.out.println("There was an Error thrown in closeConnection Method");
            System.out.println(e.getMessage());
        }
    }
    //java args is read out as parameter and it checks if odd or even
    public static void checkOddEven(String checkString)
    {
        try
        {
            switch (checkString)
            {
                case "odd":
                    rs = st.executeQuery("SELECT count(id) FROM randomNumbers WHERE value2 = 1;");
                    rs.next();
                    System.out.println("Anzahl ungerader Zahlen: "+rs.getInt("count(id)"));
                    break;
                case "even":
                    rs = st.executeQuery("SELECT count(id) AS 'even number' FROM randomNumbers WHERE value2 = 0;");
                    rs.next();
                    System.out.println("Anzahl gerader Zahlen: "+rs.getInt("even number"));
                    break;
            }
        }
        catch (Exception e)
        {
            System.out.println("There was an Error thrown in checkOffEven Method");
            System.out.println(e.getMessage());
        }
    }
}
