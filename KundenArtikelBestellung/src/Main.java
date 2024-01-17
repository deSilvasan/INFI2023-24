import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBConnection con = new DBConnection();
        DBConnection.Costumer costumer = new DBConnection.Costumer();
        DBConnection.Artikel art = new DBConnection.Artikel();
        DBConnection.Order ord = new DBConnection.Order();
        DBConnection.Storage storage = new DBConnection.Storage();

        boolean orderProcess = true;
        orderStatus ordStat = orderStatus.chooseCostumer;
        int currentCostumerNr = 0;
        String temporary = "";
        Scanner scan = new Scanner(System.in);

        //check if there is any given Input in args, when there is an input
        if(args.length != 0){
            if (args[0].equals("master")) ordStat = orderStatus.masterStatus;
            else if (costumer.searchCostumerName(args[0]) != 0){
                currentCostumerNr = costumer.searchCostumerName(args[0]);
                ordStat = orderStatus.chooseArticle;
            }else ordStat = orderStatus.chooseCostumer;
        }
        System.out.println("Bestellprojekt:");
        while (orderProcess){
            switch (ordStat) {
                case chooseCostumer -> {
                    System.out.println("Entweder erstellen Sie einen Kunden oder sie geben ihren Namen ein, wenn sie schon etwas bestellt haben");
                    System.out.println("erstellen [a] oder suchen[b]");
                    String choose = scan.nextLine();
                    if (choose.equals("a")) {
                        System.out.print("Name (Format vn nn):");
                        String name = scan.nextLine();
                        System.out.print("Gebutsdatum (Format tt.mm.yyyy):");
                        String birthday = scan.nextLine();
                        System.out.print("Email: ");
                        DBConnection.Costumer.createCostumer(name, scan.nextLine(), birthday);
                        currentCostumerNr = costumer.searchCostumerName(name);
                        System.out.println("Eingeloggt als " + name);
                        ordStat = orderStatus.chooseArticle;
                    } else if (choose.equals("b")) {
                        System.out.println("Geben sie ihren Namen ein (Format vn nn): ");
                        temporary = scan.nextLine();
                        if (temporary.equals("master")) ordStat = orderStatus.masterStatus;
                        else if (costumer.searchCostumerName(temporary)!=0){
                            ordStat = orderStatus.chooseArticle;
                            currentCostumerNr = costumer.searchCostumerName(temporary);
                        }
                        else System.out.println("There is no costumer named "+temporary+" in our system!");
                    } else System.out.println("Bitte geben sie entweder erstellen [a] oder suchen [b] ein..");
                }
                case chooseArticle -> {
                    System.out.println("Ihr aktueller Warenkorb: ");
                    ord.showOrdersCostumer(currentCostumerNr);
                    System.out.println("Wählen Sie einen neuen Artikel aus [a], bearbeiten sie die Menge der Artikel [b] oder löschen sie einen Artikel aus dem Warenkorb [c]:");
                    String decision = scan.next();
                    if(decision.equals("a")) {
                        art.showAllArticles();
                        System.out.println("Welchen Artikel möchten sie bestellen (geben sie den Namen des Artikels an)?");
                        String arikl = scan.next();
                        System.out.println("Wie viele möchten die vom " + arikl + " haben?");
                        DBConnection.Order.createOrder(currentCostumerNr, arikl, Integer.parseInt(scan.next()));
                        ordStat = orderStatus.showOrder;
                    } else if (decision.equals("b")) {
                        System.out.println("Bei welchem Artikel möchten Sie die Menge bearbeiten?");
                        String artikl = scan.next();
                        System.out.println("Wie viele möchten die vom " + artikl + " haben?");
                        int amount = scan.nextInt();
                        ord.updateOrder(currentCostumerNr, artikl,amount);
                        ordStat = orderStatus.showOrder;
                    } else if (decision.equals("c")) {
                        System.out.println("Bei welchem Artikel möchten Sie löschen?");
                        String artikl = scan.next();
                        ord.deleteOrder(currentCostumerNr,artikl);
                        ordStat = orderStatus.showOrder;
                    } else {
                        System.out.println("Ungültige Eingabe!!");
                    }
                }
                case showOrder -> {
                    System.out.println("Hier ist alles was sie bestellt haben:");
                    ord.showOrdersCostumer(currentCostumerNr);
                    System.out.println("Möchten sie noch weitere Sachen bestellen[a], die Bestellung abschließen [b], den User wechseln [c], das Profl löschen [d] oder den Warenkorb bearbeiten [e]?");
                    String decision = scan.next();
                    switch (decision) {
                        case "a" -> ordStat = orderStatus.chooseArticle;
                        case "b" -> {
                            System.out.println("Auf wiedersehen!");
                            orderProcess = false;
                        }
                        case "c" -> {
                            ordStat = orderStatus.chooseCostumer;
                            currentCostumerNr = 0;
                        }
                        case "d" ->{
                            costumer.deleteCostumer(currentCostumerNr);
                            ordStat = orderStatus.chooseCostumer;
                        }
                        case "e" ->{
                            ordStat = orderStatus.chooseArticle;
                        }
                        default -> {
                            System.out.println("Ungültige Eingabe!! ");
                            ordStat = orderStatus.showOrder;
                        }
                    }
                }
                case masterStatus -> {
                    System.out.println("Sie befinden sich im Master-Modus!");
                    System.out.println("Möchten sie einen Artikel/Kunden/Bestellung erstellen oder alle Kunden/Artikel/Bestellungen anschauen? (Eingabeformat Artikel erstellen oder so)");
                    String decis = scan.nextLine();
                    switch (decis) {
                        case "Artikel erstellen": {
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
                }
            }
        }
        con.closeConnection();
    }
}