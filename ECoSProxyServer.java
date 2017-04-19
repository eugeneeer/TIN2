
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

public class ECoSProxyServer implements Runnable {

    PriorityBlockingQueue<String> commandsQueue = new PriorityBlockingQueue<>();

    public void run() {
        ServerSocket welcomeSocket = null;
        Socket clientSocket = null;
        try {
            welcomeSocket = new ServerSocket(55557);
            clientSocket = welcomeSocket.accept();
            BufferedReader inFromClient;
            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line = "";
            while ((line = inFromClient.readLine()) != null) {
                output.append(line + '\n');
                if (line.contains("(10)")) {
                    commandsQueue.add(output.toString());
                    output.delete(0, output.toString().length());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
