import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

public class Costumer {
    private Statement st = null;
    public Costumer(Statement stmt) {
        try{
            st = stmt;
            st.executeUpdate("DROP TABLE IF EXISTS costumer;");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS costumer" +
                    " (kundenNr int PRIMARY KEY," +
                    "name char(35)," +
                    "email char(50)," +
                    "birthday date);");
            loadData();
        } catch (Exception ex){
            System.out.println("There was an Error thrown in Konstructor of Costumer:");
            System.out.println(ex.getMessage());
        }
    }
    public void createCostumer(String name, String email, String birthday){
        try{
            String kundenNr = birthday.subSequence(6,10)+birthday.subSequence(3,5).toString()+birthday.subSequence(0,2);
            //check if primary key is unique, when not we simply add a 1
            while (st.executeQuery("SELECT * FROM costumer WHERE kundenNr = '"+kundenNr+"';").next()) kundenNr = kundenNr+"1";
            st.executeUpdate("INSERT INTO costumer VALUES ("+kundenNr+", '"+name+"' , '"+email+"' , '"+ birthday.subSequence(6,10)+"-"+birthday.subSequence(3,5)+"-"+birthday.subSequence(0,2)+"' );");
        } catch (Exception ex) {
            System.out.println("There was an Error thrown in Costumer-Klass in createTableCostumers-Method:");
            System.out.println(ex.getMessage());
        }
    }
    /*public String searchCostumerName(String name) {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM costumer WHERE name = '"+name+"' ;");
            boolean result = rs.next();
            if (result) return(rs.getString("kundenNr"));
            else{
                System.out.println("There was no costumer named "+name+" found.");
                return ("");
            }
            rs.close();
        } catch (Exception ex){
            System.out.println("There was an Error thrown in Costumer-Klass in searchCostumerName-Method:");
            System.out.println(ex.getMessage());
        }
    }*/
    public void searchCostumerNumber(int nr) {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM costumer WHERE kundenNr = '"+nr+"' ;");
            boolean result = rs.next();
            if(result) System.out.println(rs.getMetaData().getColumnName(1)+" | "+rs.getMetaData().getColumnName(2)+"     | "+rs.getMetaData().getColumnName(3)+"     | "+rs.getMetaData().getColumnName(4));
            else System.out.println("There was no costumer number "+nr+" found.");
            while (result){
                System.out.println(rs.getString("name")+" "+rs.getString("kundenNr")+" "+rs.getString("email"));
                result = rs.next();
            }
            rs.close();
        } catch (Exception ex){
            System.out.println("There was an Error thrown in Costumer-Klass in searchCostumerName-Method:");
            System.out.println(ex.getMessage());
        }
    }
    public void showAllCostumers(){
        try{
            ResultSet rs = st.executeQuery("SELECT * FROM costumer;");
            boolean result = rs.next();
            if (result) System.out.println(rs.getMetaData().getColumnName(1)+" | "+rs.getMetaData().getColumnName(2)+"     | "+rs.getMetaData().getColumnName(3)+"         | "+rs.getMetaData().getColumnName(4));
            else System.out.println("There were no costumers found.");
            while (result){
                System.out.println(rs.getString("kundenNr")+"          "+rs.getString("name")+"        "+rs.getString("email")+"        "+rs.getString("birthday"));
                result = rs.next();
            }
            rs.close();
        }catch (Exception e){
            System.out.println("There was an Error thrown in Costumer-Klass in showAllCostumers-Method:");
            System.out.println(e.getMessage());
        }
    }
    private void loadData(){
        try (BufferedReader reader = new BufferedReader(new FileReader("costumer.txt"))){
            String line = reader.readLine();
            while (line != null){
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                String name = tokenizer.nextToken();
                String birthday = tokenizer.nextToken();
                createCostumer(name, tokenizer.nextToken(), birthday );
                line = reader.readLine();
            }
        }catch (Exception e){
            System.out.println("There was an Exeption thrown in Costumer-klass in loadDate-Method:");
            System.out.println(e.getMessage());
            System.out.println(e.fillInStackTrace());
        }
    }
}
