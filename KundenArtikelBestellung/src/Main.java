import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    static Connection c = null;
    static Statement st = null;
    public static void main(String[] args) {
        getConnection("//localhost:3306/");
        Costumer costm = new Costumer(st);
        Artikel art = new Artikel(st);
        Order ord = new Order(st);

        System.out.println("Bestellprojekt:");
        Scanner scan = new Scanner(System.in);
        boolean orderProcess = true;
        orderStatus ordStat = orderStatus.chooseCostumer;
        String currentCostumer = "";
        while (orderProcess){
            switch (ordStat){
                case chooseCostumer :
                    System.out.println("Entweder erstellen Sie einen Kunden oder sie geben ihren Namen ein, wenn sie schon etwas bestellt haben");
                    System.out.println("erstellen oder suchen");
                    String choose = scan.nextLine();
                    if (choose.equals("erstellen")){
                        System.out.print("Name:");
                        String name = scan.nextLine();
                        System.out.print("Gebutsdatum (Format tt.mm.yyyy):");
                        String birthday = scan.nextLine();
                        System.out.print("Email: ");
                        costm.createCostumer(name,scan.nextLine(),birthday);
                        currentCostumer = name;
                        System.out.println("Eingeloggt als "+name);
                        ordStat = orderStatus.chooseArticle;
                    } else if (choose.equals("suchen")){
                        System.out.println("Geben sie ihren Namen ein: ");
                        currentCostumer = scan.nextLine();
                        if(currentCostumer.equals("master")) ordStat = orderStatus.masterStatus;
                        else if (!currentCostumer.isEmpty()) ordStat = orderStatus.chooseArticle;
                    }else System.out.println("Bitte geben sie entweder erstellen oder wählen ein..");
                    break;
                case chooseArticle:
                    art.showAllArticles();
                    System.out.println("Wählen Sie einen Artikel aus: ");
                    String arikl = scan.next();
                    System.out.println("Wie viele möchten die vom "+arikl+" haben?");
                    ord.createOrder(currentCostumer,arikl, Integer.parseInt(scan.next()));
                    ordStat = orderStatus.showOrder;
                    break;
                case showOrder:
                    System.out.println("Hier ist alles was sie bestellt haben:");
                    ord.showOrdersCostumer(currentCostumer);
                    System.out.println("Möchten sie noch weitere Sachen bestellen, die Bestellung abschließen oder den User wechseln? (bestellen/abschliessen oder wechseln bitte eingeben!)");
                    String decision = scan.next();
                    switch (decision) {
                        case "bestellen" -> ordStat = orderStatus.chooseArticle;
                        case "abschliessen" -> {
                            System.out.println("Auf wiedersehen!");
                            orderProcess = false;
                        }
                        case "wechseln" -> {
                            ordStat = orderStatus.chooseCostumer;
                            currentCostumer = "";
                        }
                        default -> {System.out.println("Ungültige Eingabe!! ");
                                ordStat = orderStatus.showOrder;}
                    }
                    break;
                case masterStatus:
                    System.out.println("Sie befinden sich im Master-Modus!");
                    System.out.println("Möchten sie einen Artikel/Kunden/Bestellung erstellen oder alle Kunden/Artikel/Bestellungen anschauen? (Eingabeformat Artikel erstellen oder so)");
                    String decis = scan.nextLine();
                    switch (decis){
                        case "Artikel erstellen":{
                            System.out.println("Name:");
                            String na = scan.nextLine();
                            System.out.println("Preis:");
                            art.createArtikel(na, Integer.parseInt(scan.nextLine()));
                            art.showAllArticles();
                        }
                        case "Kunden erstellen":
                        case "Bestellung erstellen":
                        case "Kunden anschauen":
                        case "Artikel anschauen":
                        case "Bestellungen anschauen":
                    }
                    break;
            }
        }
        closeConnection();
    }

    //build up DB connection
    public static void getConnection(String url)
    {
        try
        {
            c = DriverManager.getConnection("jdbc:mysql:"+url,"user2","MySQLDB5");
            st = c.createStatement();
            st.executeUpdate("DROP DATABASE IF EXISTS bestellproject;");
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS bestellproject;");
            st.executeUpdate("USE bestellproject;");
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
        try {
            st.close();
            c.close();
        } catch (Exception e) {
            System.out.println("There was an Error thrown in closeConnection Method");
            System.out.println(e.getMessage());
        }
    }
}