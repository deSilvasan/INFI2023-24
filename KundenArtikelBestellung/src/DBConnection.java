import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.StringTokenizer;

public class DBConnection
{
    private static Statement st = null;
    private static Connection c = null;
    static {
        try{
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/","user2","MySQLDB5");
            st = c.createStatement();
            st.executeUpdate("DROP DATABASE IF EXISTS bestellproject;");
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS bestellproject;");
            st.executeUpdate("USE bestellproject;");
        }catch (Exception e) {
            System.out.println("There was an Error thrown in building up the Connection: ");
            System.out.println(e.getMessage());
        }
    }
    private static void editDB(String sql){
        try{
            st.executeUpdate(sql);
        }catch (Exception e){
            System.out.println("There was an Error thrown: ");
            System.out.println("editDB doesn't work");
            System.out.println(e.getLocalizedMessage());
        }
    }
    private static ResultSet selectDB(String sql){
        try{
            return st.executeQuery(sql);
        }catch (SQLIntegrityConstraintViolationException sq){
            System.out.println("Bitten Abchecken, ob es den Datensatz gibt!");
            System.out.println(sq.getMessage());
        }catch (Exception e){
            System.out.println("There was an Error thrown: ");
            System.out.println("selectDB doesn't work");
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }
    public void closeConnection(){
        try {
            st.close();
            c.close();
        } catch (Exception e) {
            System.out.println("There was an Error thrown in closeConnection Method");
            System.out.println(e.getMessage());
        }
    }

    //class Costumer
    public static class Costumer {
       static{
            editDB("DROP TABLE IF EXISTS costumer;");
            editDB("CREATE TABLE IF NOT EXISTS costumer" +
                    " (kundenNr int PRIMARY KEY," +
                    "name char(35)," +
                    "email char(50)," +
                    "birthday date);");
           System.out.println("costumer db was correctly");
            loadData();
        }
        public static void createCostumer(String name, String email, String birthday) {
            try {
                String kundenNr = birthday.subSequence(6, 10) + birthday.subSequence(3, 5).toString() + birthday.subSequence(0, 2);
                //check if primary key is unique, when not we simply add a 1
                while (selectDB("SELECT * FROM costumer WHERE kundenNr = '" + kundenNr + "';").next())
                    kundenNr = kundenNr + "1";
                editDB("INSERT INTO costumer VALUES (" + kundenNr + ", '" + name + "' , '" + email + "' , '" + birthday.subSequence(6, 10) + "-" + birthday.subSequence(3, 5) + "-" + birthday.subSequence(0, 2) + "' );");
            } catch (Exception ex) {
                System.out.println("There was an Error thrown in Costumer-Klass in createTableCostumers-Method:");
                System.out.println(ex.getMessage());
            }
        }
        public static int searchCostumerName(String name){
            int result = 0;
           try {
              ResultSet rs = selectDB("SELECT kundenNr FROM costumer WHERE name LIKE '%" + name + "%'  OR name = '"+name+"';");
              rs.next();
              result = Integer.parseInt(rs.getString(1));
              rs.close();
           }catch (Exception e){
               System.out.println("There was an Error thrown in Costumer-Klass in CustumerName-Method: ");
               System.out.println(e.getMessage());
           }
           return result;
        }
        public void showAllCostumers() {
            try {
                ResultSet rs = selectDB("SELECT * FROM costumer;");
                boolean result = rs.next();
                if (result) System.out.println(rs.getMetaData().getColumnName(1) + " | " + rs.getMetaData().getColumnName(2) + "     | " + rs.getMetaData().getColumnName(3) + "         | " + rs.getMetaData().getColumnName(4));
                else System.out.println("There were no costumers found.");
                while (result) {
                    System.out.println(rs.getString("kundenNr") + "          " + rs.getString("name") + "        " + rs.getString("email") + "        " + rs.getString("birthday"));
                    result = rs.next();
                }
                rs.close();
            } catch (Exception e) {
                System.out.println("There was an Error thrown in Costumer-Klass in showAllCostumers-Method:");
                System.out.println(e.getMessage());
            }
        }
        public void updateCostumer(int nr,String name, String email, String birthday){
            editDB("UPDATE costumer SET name = '"+name+"', email = '"+email+"', birthday = "+ birthday.subSequence(6, 10) + "-" + birthday.subSequence(3, 5) + "-" + birthday.subSequence(0, 2) + "' WHERE kundenNr = "+nr+");");
        }
        public void deleteCostumer(int nr){
            editDB("DELETE FROM costumerorder WHERE kundenId = "+nr+";");
            editDB("DELETE FROM costumer WHERE kundenNr = "+nr+";");
        }
        private static void loadData() {
            try (BufferedReader reader = new BufferedReader(new FileReader("costumer.txt"))) {
                String line = reader.readLine();
                while (line != null) {
                    StringTokenizer tokenizer = new StringTokenizer(line, ",");
                    String name = tokenizer.nextToken();
                    String birthday = tokenizer.nextToken();
                    createCostumer(name, tokenizer.nextToken(), birthday);
                    line = reader.readLine();
                }
            } catch (Exception e) {
                System.out.println("There was an Exeption thrown in Costumer-klass in loadDate-Method:");
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            }
        }
        }
        //Class Artikel
        public static class Artikel {
            static {
                editDB("DROP TABLE IF EXISTS article;");
                editDB("CREATE TABLE IF NOT EXISTS article" +
                        "(articleNr int PRIMARY KEY auto_increment," +
                        "description char(50)," +
                        "price decimal(6,2));");
                System.out.println("atricle db was correctly");
                loadData();
            }
            public static void createArtikel(String bez, double price){
                editDB("INSERT INTO article (description, price) VALUES ('"+bez+"', '"+price+"');");
            }
            public void searchArticle(String name){
                try {
                    ResultSet rs = selectDB("SELECT * FROM article WHERE description = '"+name+"';");
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
                    System.out.println("There was an Error thrown in Article-class in searchArticle-Method:");
                    System.out.println(e.getMessage());
                }
            }
            public void showAllArticles(){
                try{
                    ResultSet rs = selectDB("SELECT * FROM article;");
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
            public void updateArticle(String name, double price){
                try{
                    ResultSet rs = selectDB("SELECT articleNr FROM article WHERE name = '"+name+"';");
                    rs.next();
                    editDB("UPDATE TABLE article SET description = '"+name+"', price = "+price+" WHERE articleNr = "+rs.getString(1)+";");
                    System.out.println("Article sucessfully updated!");
                    rs.close();
                } catch (Exception e ){
                    System.out.println("There was an Error thrown in Article-class in updateArticle-Method: ");
                    System.out.println(e.getMessage());
                }
            }
            public void deleteArticle(String name){
                try{
                    ResultSet rs = selectDB("SELECT articleNr FROM article WHERE name = '"+name+"';");
                    rs.next();
                    editDB("DELETE FROM costumerorder WHERE artikelId = "+rs.getString(1)+";");
                    editDB("DELETE FROM article WHERE articleNr = "+rs.getString(1)+";");
                    rs.close();
                }catch (Exception e){
                    System.out.println("There was an Error thrown in Article-class in deleteArticle-Method: ");
                    System.out.println(e.getMessage());
                }
            }
            private static void loadData(){
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
                }
            }
        }
        //Class Order
        public static class Order {
            static {
                editDB("DROP TABLE IF EXISTS costumerorder;");
                editDB("CREATE TABLE IF NOT EXISTS costumerorder" +
                        " (number int PRIMARY KEY auto_increment," +
                        "kundenId int," +
                        "artikelId int," +
                        "amount int," +
                        "FOREIGN KEY (kundenId) REFERENCES costumer(kundenNr) ON DELETE SET NULL ON UPDATE CASCADE," +
                        "FOREIGN KEY (artikelId) REFERENCES article(articleNr) ON DELETE SET NULL ON UPDATE CASCADE);");
                System.out.println("order db was correctly");
                loadData();
            }
            public static void createOrder(int kunde, String article, int amoun){
                try {
                    ResultSet rs = selectDB("SELECT articleNr FROM article WHERE description ='"+article+"' ;");
                    rs.next();
                    int articleNr = Integer.parseInt(rs.getString(1));
                    if(Storage.checkStorage(articleNr,amoun)){
                        editDB("INSERT INTO costumerorder (kundenId, artikelId, amount) VALUES ('"+kunde+"', '"+articleNr+"', '"+amoun+"');");
                    }
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
                    ResultSet rs = selectDB("SELECT k.name, a.description, a.price, o.amount FROM costumerorder o " +
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
            public void showOrdersCostumer(int nr){
                try{
                    ResultSet rs = selectDB("SELECT o.kundenId, k.name, a.description, a.price, o.amount FROM costumerorder o " +
                            "JOIN costumer k ON k.kundenNr = o.kundenId " +
                            "JOIN article a ON a.articleNr= o.artikelId " +
                            "WHERE o.kundenId = "+nr+";");
                    boolean result = rs.next();
                    if(result) System.out.println(rs.getMetaData().getColumnName(3)+"    | "+rs.getMetaData().getColumnName(4)+"   | "+rs.getMetaData().getColumnName(5));
                    else System.out.println("Ihr Warenkorb ist leer...");
                    while(result) {
                        System.out.println(rs.getString("description")+"      "+rs.getString("price")+"      "+rs.getString("amount"));
                        result = rs.next();
                    }
                    rs.close();
                } catch (Exception e){
                    System.out.println("There was an Error thrown in Order-Klasse in showOrdersCostumer-Method:");
                    System.out.println(e.getMessage());
                }
            }
            public void updateOrder(int nr, String artikelName,int amount){
                try{
                    ResultSet rs = selectDB("SELECT articleNr FROM article WHERE description = '"+artikelName+"';");
                    rs.next();
                    editDB("UPDATE costumerorder SET amount = "+amount+" WHERE kundenId = '"+nr+"' AND artikelId = "+rs.getString("articleNr")+";");
                    rs.close();
                }catch (Exception e){
                    System.out.println("There was an Error thrown in Order-class in updateOrder-Method: ");
                    System.out.println(e.getMessage());
                }
            }
            public void deleteOrder(int nr, String articleName){
                try {
                    ResultSet articleNr = selectDB("SELECT articleNr FROM article WEHRE description = '"+articleName+"';");
                    articleNr.next();
                    editDB("DELETE from costeumerorder WHERE kundenId = "+nr+" AND artikelId ="+articleNr.getString(1)+";");
                    articleNr.close();
                }catch (Exception e){
                    System.out.println("There was an Error thrown in Order-class in deleteOrder-Method: ");
                    System.out.println(e.getMessage());
                }
            }
            private static void loadData(){
                try (BufferedReader reader = new BufferedReader(new FileReader("order.txt"))){
                    String line = reader.readLine();
                    while (line != null){
                        StringTokenizer tokenizer = new StringTokenizer(line, ",");
                        int kundenNr = Costumer.searchCostumerName(tokenizer.nextToken());
                        createOrder(kundenNr, tokenizer.nextToken(), Integer.parseInt(tokenizer.nextToken()));
                        line = reader.readLine();
                    }
                }catch (Exception e){
                    System.out.println("There was an Exeption thrown in Order-klass in loadDate-Method:");
                    System.out.println(e.getMessage());
                }
            }
        }
        //Class storage
        public static class Storage{
            static {
                editDB("DROP TABLE IF EXISTS storage;");
                editDB("CREATE TABLE IF NOT EXISTS storage" +
                        "(artikleId int PRIMARY KEY," +
                        "amount int," +
                        "FOREIGN KEY (artikleId) REFERENCES article(articleNr) ON DELETE CASCADE ON UPDATE CASCADE);");

                loadData();
                System.out.println("storage db was correctly");
            }
            public static boolean checkStorage(int articleNr, int amount){
                try{
                    ResultSet rs = selectDB("SELECT amount FROM storage WHERE artikleId ="+articleNr+";");
                    rs.next();
                    int storedAmount = Integer.parseInt(rs.getString(1));
                    boolean result = false;
                    if (storedAmount >amount){
                        editDB("UPDATE storage SET amount = "+(storedAmount-amount)+" WHERE artikleId = "+articleNr+";");
                        result = true;
                    }else if(storedAmount == amount){
                        editDB("UPDATE storage SET amount = "+(0)+" WHERE artikleId = "+articleNr+";");
                        System.out.println("Im Lager sind keine Produkte der Nummer "+articleNr+" im Lager vorhanden. Wenn möglich bitte nachbestellen!");
                        result = true;
                    }else System.out.println("Es sind nicht genug Produkte der Nummer "+articleNr+" im Lager vorhanden. ");
                    rs.close();
                    return result;
                }catch (Exception e){
                    System.out.println("There was an Error thrown in Storage-class in checkStorage-Method: ");
                    System.out.println(e.getMessage());
                }
                return false;
            }
            public static void putArticlesIntoStorage(String articleName, int amount){
                try{
                    ResultSet rs = selectDB("SELECT articleNr FROM article WHERE description = '"+articleName+"';");
                    rs.next();
                    int articleId = Integer.parseInt(rs.getString(1));
                    rs = selectDB("SELECT * FROM storage WHERE artikleId = "+articleId+";");
                    if (rs.next()){
                        editDB("UPDATE storage SET amount = amount+ "+amount+" WHERE artikleId = "+articleId+";");
                    } else {
                        editDB("INSERT INTO storage VALUES ("+articleId+", "+amount+");");
                    }
                    rs.close();
                }catch (Exception e){
                    System.out.println("There was an Error thrown in storage-class in putArticlesIntoStorage-Method: ");
                    System.out.println(e.getMessage());
                }
            }
            private static void loadData(){
                try (BufferedReader reader = new BufferedReader(new FileReader("storage.txt"))){
                    String line = reader.readLine();
                    while (line != null){
                        StringTokenizer tokenizer = new StringTokenizer(line, ",");
                        putArticlesIntoStorage(tokenizer.nextToken(),Integer.parseInt(tokenizer.nextToken()));
                        line = reader.readLine();
                    }
                }catch (Exception e){
                    System.out.println("There was an Exeption thrown in Costumer-klass in loadDate-Method:");
                    System.out.println(e.getMessage());
                }
            }
        }
    }
