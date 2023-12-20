import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

public class Artikel {
    private Statement st = null;
    public Artikel(Statement stmt) {
        try{
            st = stmt;
            st.executeUpdate("DROP TABLE IF EXISTS article;");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS article" +
                    "(articleNr int PRIMARY KEY auto_increment," +
                    "description char(50)," +
                    "price decimal(6,2));");
            loadData();
        }
        catch (Exception e){
            System.out.println("There was an Error thrown in the Konstruktor of Artikel");
            System.out.println(e.getMessage());
        }
    }
    public void createArtikel(String bez, double price){
        try{
            st.executeUpdate("INSERT INTO article (description, price) VALUES ('"+bez+"', '"+price+"');");
        } catch (Exception e){
            System.out.println("There was an Error thrown in Article-Klass in createArtikle-Method:");
            System.out.println(e.getMessage());
        }
    }
    public void searchArticle(String name){
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM article WHERE description = '"+name+"';");
            boolean result = rs.next();
            //check if a data is in ResultSet
            if (result) System.out.println(rs.getMetaData().getColumnName(1)+" | "+rs.getMetaData().getColumnName(2)+"     | "+rs.getMetaData().getColumnName(3));
            else System.out.println("There was no article named "+name+" found.");
            while (result){
                System.out.println(rs.getString("articleNr")+"          "+rs.getString("description")+"        "+rs.getString("price"));
                result = rs.next();
            }
            rs.close();
        }catch (Exception e){
            System.out.println("There was an Error thrown in Article-Klass in searchArticle-Method:");
            System.out.println(e.getMessage());
        }
    }
    public void showAllArticles(){
        try{
            ResultSet rs = st.executeQuery("SELECT * FROM article;");
            boolean result = rs.next();
            if (result) System.out.println(rs.getMetaData().getColumnName(1)+" | "+rs.getMetaData().getColumnName(2)+"     | "+rs.getMetaData().getColumnName(3));
            else System.out.println("There were no articles found.");
            while (result){
                System.out.println(rs.getString("articleNr")+"          "+rs.getString("description")+"        "+rs.getString("price"));
                result = rs.next();
            }
            rs.close();
        }catch (Exception e){
            System.out.println("There was an Error thrown in Article-Klass in showAllArticles-Method:");
            System.out.println(e.getMessage());
        }
    }
    private void loadData(){
        try (BufferedReader reader = new BufferedReader(new FileReader("article.txt"))){
            String line = reader.readLine();
            while (line != null){
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                createArtikel(tokenizer.nextToken(), Double.parseDouble(tokenizer.nextToken()));
                line = reader.readLine();
            }
        }catch (Exception e){
            System.out.println("There was an Exeption thrown in Costumer-klass in loadDate-Method:");
            System.out.println(e.getMessage());
            System.out.println(e.fillInStackTrace());
        }
    }
}
