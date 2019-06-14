package app;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class App {
    static ChatClient client;
    static boolean shouldLog = false;
    
    static String[] kamers = {"Zolder", "Woonkamer", "WC"};

    public static void main(String[] args) throws Exception {
       
        System.out.println("Welkom bij deze chatapp, we hebben uw naam nodig om te kunnen chatten..");
        String username = getUserInput("Wat is uw naam?");

        // start de chatclient
        client = new ChatClient();
        client.start(username, new IWantNewMessages(){
            @Override public void processNewMessage(String message) {  
                while (message.contains("room ")) {
                    char roomnumber = message.charAt(message.indexOf("room ") + 5);
                    if (roomnumber != 'u') {
                        int roomno = Integer.parseInt(roomnumber + "");
                        String kamer = kamers[roomno-1];
                        message = message.replace("room " + roomno,kamer);
                    } else {
                        message = message.replace("room undefined", "onbekend");
                    }
                }
                System.out.println(Log("SERVER SENDS: " + message));
            }
        }); 

        client.sendMessage(Log("USER:" + username));

        // zolang de gebruiker iets wil, doen we dat
        String command = getUserInput("client :>");
        while (!command.equals("q")){
            processCommand(command);
            command = getUserInput("client :>");
        }

        System.out.println("Ok, bye!");
    }

    public static void processCommand(String command){
        switch (command) {
        case "h": {
            System.out.println("De beschikbare commando's zijn: ");
            System.out.println("\th: toont deze hulp functie");
            System.out.println("\tq: eindigt dit programma");
            System.out.println("\tr: verander van kamer");
            System.out.println("\ts: stuur een bericht");
            System.out.println("\tx: voer een ban uit (admin only)");
            System.out.println("\tb: vraag om een ban");
            System.out.println("\t?: vraag om status informatie");
            break;
        }
        case "r": {
            System.out.println("De beschikbare kamers zijn: ");
            for (int i = 0; i < kamers.length; i++) {
                System.out.println("\th: " + (i + 1) + ". " + kamers[i]);
            }
            String kamer = getUserInput("Welke kamer wilt u in?");
            client.sendMessage(Log("ROOM:" + kamer));
            break;
        }
        case "s": {
            String datatosend = getUserInput("Wat wilt u versturen?");
            client.sendMessage(Log("MESSAGE:" + datatosend ));
            break;
        }
        case "log": {
            shouldLog = !shouldLog;
            System.out.println(Log("Logging is set to " + shouldLog));
            break;
        }
        case "x": {
            String user = getUserInput("execute ban on user?");
            client.sendMessage(Log("EXECUTEBAN:" + user ));
            break;
        }
        case "xx": {
            String user = getUserInput("execute superban on?");
            client.sendMessage(Log("EXECUTEBAN2:" + user ));
            break;
        }
        case "b": {
            String user = getUserInput("Wie wilt u bannen?");
            client.sendMessage(Log("BAN:" + user ));
            break;
        }
        case "?": {
             client.sendMessage(Log("STATUS"));
            break;
        }
        default: {
            System.out.println(Log("onbekend commando!"));
            break;
            }
        }

    }

    public static String Log(String line) {
        if (shouldLog) { 
            try { 
                FileWriter logFile = new FileWriter("./log.txt", true);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	            LocalDateTime now = LocalDateTime.now();
                logFile.append(dtf.format(now) + " " + line + "\r\n");
                logFile.close(); 
            } catch (IOException io){
                System.out.println("error: " + io.getMessage());
            } 
        }
        return line;
    }
           
    public static String getUserInput(String prompt){
        System.out.println(prompt);
        Scanner s = new Scanner(System.in);
        String data = s.nextLine();
        return data;
    }
}