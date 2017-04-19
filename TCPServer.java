/**
 * Created by eugene on 18/04/17.
 */

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        System.out.println("README: The listening port is 55557. " +
                "Configuration has still to be made to separate messages coming from Samsung VR correctly. " +
                "Now a dummy delimiter '(10)' is used. " +
                "Hence only commands including '(10)' will be correcntly interpreted. " +
                "Responses from ECoS are delimited with lines containing '<END>'. " +
                "Request transformation rules also have to be implemented in method transformRequest()." + "\n");
        ECoSProxyServer listeningProxyServer = new ECoSProxyServer();
        Thread t = new Thread(listeningProxyServer);
        t.start();
        Socket ECoSSocket = initializeECoSSocket();
        String VRToProxyRequest = "";
        String ECoSRequest = "";
        while (true) {
            VRToProxyRequest = listeningProxyServer.commandsQueue.poll();
            if (VRToProxyRequest == null) {
                continue;
            }
            System.out.println("---------------------------");
            System.out.println("Request Samsung VR -> Proxy Server:");
            System.out.println(VRToProxyRequest);
            System.out.println("---------------------------");
            ECoSRequest = transformRequest(VRToProxyRequest);
            System.out.println("Request Proxy Server -> ECos Request:");
            String ECoSResponse = executeCommand(ECoSSocket, ECoSRequest);
            System.out.println(ECoSRequest);
            System.out.println("---------------------------");
            System.out.println("Response ECoS -> Proxy Server:");
            System.out.println(ECoSResponse);
            System.out.println("---------------------------");
        }
    }

    public static String readURL() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Server URL or IP Address:");
        String serverAddress = inFromUser.readLine();
        return serverAddress;
    }

    public static int readListeningPort() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Listening Port Number:");
        String portNumberInput = inFromUser.readLine();
        int portNumber = Integer.parseInt(portNumberInput);
        System.out.println(portNumber);
        return portNumber;
    }

    public static String readRequest() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter command or type 'close' to exit:");
        String request = inFromUser.readLine();
        return request;
    }

    public static void runECoSClientInConsole() throws IOException {
        Socket clientSocket = new Socket("bmec.mine.nu", 15471);
        String ECoSRequest = "";
        while (!ECoSRequest.equals("close")) {
            ECoSRequest = readRequest();
            if (ECoSRequest.equals("close")) {
                break;
            }
            String ECoSResponse = executeCommand(clientSocket, ECoSRequest);
            System.out.println(ECoSResponse);
            System.out.println("-------------------------------");
        }
        closeECoSSocket(clientSocket);
    }

    public static void runECoSClientInFile() throws IOException {
        Socket clientSocket = initializeECoSSocket();
        String ECoSRequest = "";
        PrintWriter logFile = new PrintWriter("ECoS_Log.txt");
        while (!ECoSRequest.equals("close")) {
            ECoSRequest = readRequest();
            logFile.append("ECoS Request:" + '\n');
            logFile.append(ECoSRequest + '\n');
            logFile.append("-------------------------------" + '\n');
            logFile.flush();
            if (ECoSRequest.equals("close")) {
                break;
            }
            String ECoSResponse = executeCommand(clientSocket, ECoSRequest);
            logFile.append("ECoS Response:" + '\n');
            logFile.append(ECoSResponse);
            logFile.append("-------------------------------" + '\n');
            logFile.flush();
        }
        closeECoSSocket(clientSocket);
        logFile.append("Connection closed");
        logFile.close();
    }

    public static Socket initializeECoSSocket() throws IOException {
        Socket clientSocket = new Socket("bmec.mine.nu", 15471);
        System.out.println("Connection established");
        return clientSocket;
    }

    public static void closeECoSSocket(Socket input) throws IOException {
        input.close();
        System.out.println("Connection closed");
    }

    public static String executeCommand(Socket clientSocket, String request) throws IOException {
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        StringBuilder output = new StringBuilder();
        outToServer.writeBytes(request);
        String line = "";
        while ((line = inFromServer.readLine()) != null) {
            output.append(line + '\n');
            if (line.contains("<END")) {
                break;
            }
        }
        return output.toString();
    }

    /* TODO implement transformation rules*/
    public static String transformRequest(String request) {
        return request;
    }
}
