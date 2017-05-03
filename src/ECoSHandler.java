import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by eugene on 28/04/17.
 */
public class ECoSHandler {

    public static void runECoSClientInFile() throws IOException {
        ECoSClient client = new ECoSClient();
        String ECoSRequest = "";
        PrintWriter logFile = new PrintWriter("ECoS_Log.txt");

        while(!ECoSRequest.equals("close")) {
            ECoSRequest = readRequestFromConsole();
            logFile.append("ECoS Request:\n");
            logFile.append(ECoSRequest + '\n');
            logFile.append("-------------------------------\n");
            logFile.flush();
            if(ECoSRequest.equals("close")) {
                break;
            }

            String ECoSResponse = client.executeCommand(ECoSRequest);
            logFile.append("ECoS Response:\n");
            logFile.append(ECoSResponse);
            logFile.append("-------------------------------\n");
            logFile.flush();
        }

        client.closeECoSSocket();
        logFile.append("Connection closed");
        logFile.close();
    }
    public static String readRequestFromConsole() throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter command or type 'close':");
        String request = inFromUser.readLine();
        return request;
    }

    public static String getECoSElements(ECoSClient client) throws IOException {
        System.out.println("Requesting all available elements from ECoS...");
        String request = "queryObjects(10,name)";
        String response = client.executeCommand(request);
        printCommunicationLog(request, response);
        return response;
    }

    public static String readEcOSConfigFromFile (String parameter) throws IOException {
        File file = new File ("ECoSClientConf.txt");
        System.out.println("Reading " + parameter +  " from " + file.getPath());
        List<String> lines = Files.readAllLines(file.toPath());
        String result = null;
        for (String line:lines){
            if (line.contains(parameter)){
                result = line.split(":")[1];
                System.out.println("Parameter " + parameter + " read successfully");
                System.out.println(parameter + " is "+result + "\n");
                break;
            }
        }
        if (result == null) {
            System.out.println(parameter + " couldn't be read from " + file.getAbsolutePath());
        }
        return result;
    }

    public static String readElementConfigFromFile (String parameter) throws IOException {
        File file = new File ("EcOSElement.txt");
        System.out.println("Reading " + parameter +  " from " + file.getPath());
        List<String> lines = Files.readAllLines(file.toPath());
        String result = null;
        for (String line:lines){
            if (line.contains(parameter)){
                result = line.split(":")[1];
                System.out.println("Parameter " + parameter + " read successfully");
                System.out.println(parameter + " is "+result + "\n");
                break;
            }
        }
        if (result == null) {
            System.out.println(parameter + " couldn't be read from " + file.getAbsolutePath());
        }
        return result;
    }

    public static void printCommunicationLog(String request, String response){
        System.out.println("ECoS Request:");
        System.out.println(request);
        System.out.println("------------");

        System.out.println("Result:");
        System.out.println(response);
        System.out.println("------------------------------------------------\n");
    }

}
