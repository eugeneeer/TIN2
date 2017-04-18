/**
 * Created by eugene on 18/04/17.
 */

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) throws IOException {

//        TODO: Threads, process multiple messages that can be received
//        ServerSocket ECoSProxy = initializeECoSProxySocket();
//        String ECoSProxyRequest = "";
//        while (true) {
//            Socket connectionSocket = ECoSProxy.accept();
//            BufferedReader inFromClient =
//                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//            ECoSProxyRequest = inFromClient.readLine();
//            System.out.println("Received: " + ECoSProxyRequest);
//        }
//        Socket ECoSSocket = initializeECoSSocket();
//        String ECoSResponse = executeCommand(ECoSSocket, ECoSProxyRequest);
//        closeECoSSocket(ECoSSocket);
//        System.out.println(ECoSResponse);
        runECoSClientInConsole();
    }

    public static String readURL() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Server URL or IP Address:");
        String serverAddress = inFromUser.readLine();
        return serverAddress;
    }

    public static int readPort() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Port Number:");
        String portNumberInput = inFromUser.readLine();
        int portNumber = Integer.parseInt(portNumberInput);
        return portNumber;
    }

    public static String readRequest() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter command or type 'close' to exit:");
        String request = inFromUser.readLine();
        return request;
    }

    public static void runECoSClientInConsole() throws IOException {
        Socket clientSocket = new Socket("XXX", XXXX);
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

    public static Socket initializeECoSSocket() throws IOException {
        Socket clientSocket = new Socket("XXX", XXXX);
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

    public static ServerSocket initializeECoSProxySocket() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket();
        System.out.println("Allocated Port:" + welcomeSocket.getLocalPort());
        return welcomeSocket;
    }
}



