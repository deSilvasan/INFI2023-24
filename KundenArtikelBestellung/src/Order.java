import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.StringTokenizer;

public class Order {
    private Statement st = null;
    public Order(Statement stmt){
        try{
            st = stmt;
            st.executeUpdate("DROP TABLE IF EXISTS costumerorder;");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS costumerorder" +
                    " (number int PRIMARY KEY auto_increment," +
                    "kundenId int," +
                    "artikelId int," +
                    "amount int," +
                    "FOREIGN KEY (kundenId) REFERENCES costumer(kundenNr) ON DELETE SET NULL ON UPDATE CASCADE," +
                    "FOREIGN KEY (artikelId) REFERENCES article(articleNr) ON DELETE SET NULL ON UPDATE CASCADE);");
            loadData();
        }catch (Exception e){
            System.out.println("There was an Error thrown in the Kostructor of Order:");
            System.out.println(e.getMessage());
        }
    }
    public void createOrder(String kunde, String article, int amoun){
        try {
            ResultSet rs = st.executeQuery("SELECT kundenNr FROM costumer WHERE name = '"+kunde+"' ;");
            rs.next();
            String kundeNr = rs.getString(1);
            rs = st.executeQuery("SELECT articleNr FROM article WHERE description ='"+article+"' ;");
            rs.next();
            st.executeUpdate("INSERT INTO costumerorder (kundenId, artikelId, amount) VALUES ('"+kundeNr+"', '"+rs.getString(1)+"', '"+amoun+"');");
            rs.close();
        }catch (SQLIntegrityConstraintViolationException sql){
            System.out.println("Bitte Abchecken ob es die Kunden/ Artikel gibt!!");
        }catch (Exception e){
            System.out.println("There was an Error thrown in Order-Klass in Method create Order: ");
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
        }
    }
    public void showAllOrders(){
        try{
            ResultSet rs = st.executeQuery("SELECT k.name, a.description, a.price, o.amount FROM costumerorder o " +
                    "JOIN costumer k ON k.kundenNr=o.kundenId " +
                    "JOIN article a ON a.articleNr=o.artikelId;");
            boolean result = rs.next();
            if (result) System.out.println(rs.getMetaData().getColumnName(1)+"    | "+rs.getMetaData().getColumnName(2)+"    | "+rs.getMetaData().getColumnName(3)+"   | "+rs.getMetaData().getColumnName(4));
            else System.out.println("There were no Orders found.");
            while (result){
                System.out.println(rs.getString("name")+"  "+rs.getString("description")+"      "+rs.getString("price")+"      "+rs.getString("amount"));
                result = rs.next();
            }
            rs.close();
        }catch (Exception e){
            System.out.println("There was an Error thrown in Order-Klass in showAllOrders-Method:");
            System.out.println(e.getMessage());
        }
    }
    public void showOrdersCostumer(String name){
        try{
            ResultSet kNr = st.executeQuery("SELECT kundenNr FROM costumer WHERE name = '"+name+"';");
            kNr.next();
            ResultSet rs = st.executeQuery("SELECT o.kundenId, k.name, a.description, a.price, o.amount FROM costumerorder o " +
                    "JOIN costumer k ON k.kundenNr = o.kundenId " +
                    "JOIN article a ON a.articleNr= o.artikelId " +
                    "WHERE o.kundenId = "+kNr.getString("kundenNr")+";");
            boolean result = rs.next();
            if(result) System.out.println(rs.getMetaData().getColumnName(1)+"    | "+rs.getMetaData().getColumnName(2)+"    | "+rs.getMetaData().getColumnName(3)+"   | "+rs.getMetaData().getColumnName(4));
            else System.out.println("There was no costumer named "+name);
            while(result) {
                System.out.println(rs.getString("name")+"  "+rs.getString("description")+"      "+rs.getString("price")+"      "+rs.getString("amount"));
                result = rs.next();
            }
            rs.close();
        } catch (Exception e){
            System.out.println("There was an Error thrown in Order-Klasse in showOrdersCostumer-Method:");
            System.out.println(e.getMessage());
        }
    }
    private void loadData(){
        try (BufferedReader reader = new BufferedReader(new FileReader("order.txt"))){
            String line = reader.readLine();
            while (line != null){
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                createOrder(tokenizer.nextToken(), tokenizer.nextToken(), Integer.parseInt(tokenizer.nextToken()));
                line = reader.readLine();
            }
        }catch (Exception e){
            System.out.println("There was an Exeption thrown in Costumer-klass in loadDate-Method:");
            System.out.println(e.getMessage());
            System.out.println(e.fillInStackTrace());
        }
    }
}
