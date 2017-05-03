/**
 * Created by eugene on 28/04/17.
 */
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

public class ECoSClient {

    public int serverPort;
    public String serverAddress;
    public Socket clientSocket;

    public ECoSClient() throws IOException {
        readServerAddressFromFile();
        readServerPortFromFile();
        initializeECoSSocket();
    }

    public ECoSClient(String _serverAddress, int _serverPort) throws IOException {
        serverAddress=_serverAddress;
        serverPort=_serverPort;
        initializeECoSSocket();
    }

    public void readServerAddressFromFile() throws IOException {
        serverAddress = ECoSHandler.readEcOSConfigFromFile("ServerAddress");;
    }

    public void readServerPortFromFile() throws IOException {
        String port = ECoSHandler.readEcOSConfigFromFile("PortNumber");
        serverPort = Integer.parseInt(port);
    }

    public void readServerAddressFromConsole() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Server URL or IP Address:");
        serverAddress = inFromUser.readLine();
    }

    public void readServerPortFromConsole() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Listening Port Number:");
        String portNumberInput = inFromUser.readLine();
        serverPort = Integer.parseInt(portNumberInput);
    }

    public void initializeECoSSocket() throws IOException {
        System.out.println("Establishing a socket with ECoS Server...");
        clientSocket = new Socket(serverAddress, serverPort);
        System.out.println("Socket with ECoS Server established");
        System.out.println("Allocated local port is: "+ clientSocket.getLocalPort()+ "\n");
    }

    public void closeECoSSocket() throws IOException {
        clientSocket.close();
        System.out.println("Socket with ECoS Server closed");
    }

    public void printSocketStatus(){
        if (clientSocket.isConnected()){
            System.out.println("Socket open");
        } else {
            System.out.println("Socket closed");
        }
    }

    public String executeCommand(String request) throws IOException {
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        StringBuilder output = new StringBuilder();
        outToServer.writeBytes(request);
        String line = "";

        while((line = inFromServer.readLine()) != null) {
            output.append(line + '\n');
            if(line.contains("<END")) {
                break;
            }
        }

        return output.toString();
    }

}
