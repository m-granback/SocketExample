import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Server {
    // Attributes
    private List<Integer> secretNumbers = new ArrayList<>();
    private List<Integer> availableGuesses = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
    private boolean gameIsRunning;
    private BufferedReader reader;
    private PrintWriter writer;

    // Contructor
    public Server() {

    }

    public void start() throws IOException {
        try {
            // Create server socket, waiting for client
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server started, waiting for client...");

            // When client connects, accept socket and store
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");

            // Create an input stream
            InputStream inputStream = socket.getInputStream();

            // Create a reader
            reader = new BufferedReader(new InputStreamReader(inputStream));

            OutputStream outputStream = socket.getOutputStream();
            // Create output stream
            writer = new PrintWriter(outputStream, true);

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        while (gameIsRunning){
            if(reader.ready()){
                String incomingMessage = reader.readLine();
                System.out.println("Client says: " + incomingMessage);
                String outputText = checkResultAndCreateReply(incomingMessage);
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    System.out.println("Could not pause due to " + e.getMessage());
                }
                System.out.println(outputText);
                writer.println(outputText);
            }
        }
    }

    public void initialize(){
        System.out.print("Generating secret numbers: ");
        while(secretNumbers.size() < 3){
            int number = (int)(Math.random() * 10) + 1;
            if(!secretNumbers.contains(number)){
                System.out.print(number + " ");
                secretNumbers.add(number);
            }
        }
        System.out.println();
        gameIsRunning = true;
        System.out.println("Server initiated!");
    }
    public String checkResultAndCreateReply(String input){
        if(input.equals("I lost")){
            gameIsRunning = false;
            return "Yay! I won!";
        } else {
            // Split string on " ", returns an array of strings of size 4
            // Example "Correct! im guessing 5"
            //  index     0      1      2    3
            String[] inputData = input.split(" ");
            int guessedNumber = Integer.parseInt(inputData[3]);
            if(secretNumbers.contains(guessedNumber)){
                secretNumbers.remove(secretNumbers.indexOf(guessedNumber));
                if(secretNumbers.size() == 0){
                    gameIsRunning = false;
                    return "I lost";
                } else {
                    return "Correct! I'm guessing " + getNewNumber();
                }
            } else {
                return "Wrong! I'm guessing " + getNewNumber();
            }
        }
    }
    public int getNewNumber(){
        Collections.shuffle(availableGuesses);
        return availableGuesses.remove(0);
    }
}