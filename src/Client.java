import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Client {
    // Attributes
    private List<Integer> secretNumbers = new ArrayList<>();
    private List<Integer> availableGuesses = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
    private boolean gameIsRunning;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean firstGuess;
    private String outGoingMessage;

    // Constructor
    public Client() {

    }

    public void start() throws IOException {
        try {

            // Connect to server and establish connection
            Socket socket = new Socket("127.0.0.1", 8080);
            // Connection is made, print to users own console
            System.out.println("Connected to server!");

            // Create an input stream of bytes
            InputStream inputStream = socket.getInputStream();

            // Create a reader         | Bridge byte streams->character streams
            reader = new BufferedReader(new InputStreamReader(inputStream));

            OutputStream outputStream = socket.getOutputStream();
            // PrintWriter takes an output stream as argument, and boolean for auto flush
            writer = new PrintWriter(outputStream, true);

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        while (gameIsRunning){
            if(firstGuess){
                outGoingMessage = "Ok! I'm guessing " + getNewNumber();
                System.out.println(outGoingMessage);
                writer.println(outGoingMessage);
                firstGuess = false;
            }else {
                if(reader.ready()){
                    String incomingMessage = reader.readLine();
                    System.out.println("Server says: " + incomingMessage);
                    String outputText = checkResultAndCreateReply(incomingMessage);
                    try{
                        Thread.sleep(2000);
                    } catch (InterruptedException e){
                        System.out.println("Could not pause due to:\n" + e.getMessage());
                    }
                    System.out.println(outputText);
                    writer.println(outputText);
                }
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
        firstGuess = true;
        gameIsRunning = true;
        System.out.println("Client initiated!");
    }
    public String checkResultAndCreateReply(String input){
        if(input.equals("I lost")){
            gameIsRunning = false;
            return "Yay! I won!";
        } else {
            // Split string on " ", returns an array of strings of size 4
            // Example "Correct! im guessing 5"
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
