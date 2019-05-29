package app;

import java.util.Scanner;

public class App {
    static ChatClient client;
    static MessageResponder responder;

    public static void main(String[] args) throws Exception {
       
        System.out.println("Welkom bij deze chatapp, we hebben uw naam nodig om te kunnen chatten..");

        // start de chatclient
        client = new ChatClient(); 
        responder = new MessageResponder(); 

        String username = getUserInput("Wat is uw naam?");
        client.start(username); 
        client.addMessageReceiveListener(responder);


        // zolang de gebruiker iets wil, doen we dat
        String command = getUserInput("client :>");

        System.out.println("text");
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
            System.out.println("\ts: stuurt een nieuw bericht naar de chatroom");
            break;
        }
        case "s": {
            String datatosend = getUserInput("Wat wilt u versturen?");
            client.sendMessage(datatosend);
            break;
        }
        default: {
            System.out.println("onbekend commando!");
            break;
            }
        }

    }

    public static String getUserInput(String prompt){
        System.out.println(prompt);
        Scanner s = new Scanner(System.in);
        String data = s.nextLine();
        return data;
    }
}