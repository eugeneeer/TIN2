import java.io.IOException;

/**
 * Created by eugene on 28/04/17.
 */
public class ECoSElement {

    public int id;
    public String name;
    public int speed;
    public boolean Light;
    public int lightFunction;
    public boolean sound;
    public int soundFunction;
    public ECoSClient client;
    public boolean forwardDirection;
    public int maxSpeed;

    ECoSElement(ECoSClient _client) throws IOException {
        client = _client;
        name = ECoSHandler.readElementConfigFromFile("Name");
        System.out.println("Resolving the element name " + name + " to corresponding id...");
        String elements = ECoSHandler.getECoSElements(client);
        System.out.println("Parsing ECoS Response...");
        String[] lines = elements.split("\n");
        String result = null;
        for (String line : lines) {
            if (line.contains(name)){
                result = line.split(" name")[0];
                break;
            }
        }
        id = Integer.parseInt(result);
        System.out.println("ECoS Element Id found:" + id + "\n");
        soundFunction = Integer.parseInt(ECoSHandler.readElementConfigFromFile("Sound"));
        lightFunction = Integer.parseInt(ECoSHandler.readElementConfigFromFile("Light"));
        maxSpeed = Integer.parseInt(ECoSHandler.readElementConfigFromFile("MaxSpeed"));
        sound = false;
        Light = false;
        speed = 0;
        forwardDirection=true;
    }

    public String setSpeed(int _speed) throws IOException {
        String request = "set(" + id + ",speed[" + _speed + "])";
        String result = client.executeCommand(request);
        System.out.println("Setting element speed...");
        ECoSHandler.printCommunicationLog(request, result);
        speed = _speed;
        return result;
    }

    public String setFrontLight(boolean _frontLight) throws IOException {
        String request;
        if (_frontLight == true) {
            System.out.println("Setting light on...");
            request = "set(" + id + ", func[" + lightFunction+", 1])";
        } else {
            System.out.println("Setting light off...");
            request = "set(" + id + ", func[" + lightFunction+", 0])";
        }
        String result = client.executeCommand(request);
        Light = _frontLight;
        ECoSHandler.printCommunicationLog(request, result);
        return result;
    }

    public String setSound(boolean _sound) throws IOException {
        String request;
        if (_sound == true) {
            System.out.println("Setting sound on...");
            request = "set(" + id + ", func[" + soundFunction+", 1])";
        } else {
            System.out.println("Setting sound off...");
            request = "set(" + id + ", func[" + soundFunction+", 0])";
        }
        String result = client.executeCommand(request);
        sound = _sound;
        ECoSHandler.printCommunicationLog(request, result);
        return result;
    }

    public void incrementSpeed () throws IOException {
        if (speed <maxSpeed) {
            System.out.println("Incrementing speed...");
            String result = setSpeed(speed+1);
        }
        else {
            System.out.println("Maximum safe speed reached.");
        }
    }

    public void decrementSpeed() throws IOException {
        if (speed > 0) {
            System.out.println("Decrementing speed...");
            setSpeed(speed-1);
        }
        else {
            System.out.println("Speed is already set to zero");
        }
    }

    public void setForwardDirection () throws IOException {
        String request = null;
        if (forwardDirection == false && speed ==0) {
            System.out.println("Setting direction to forward...");
            request = "set(" + id + ", dir[0])";
            client.executeCommand(request);
            forwardDirection=true;
            ECoSHandler.printCommunicationLog(request, request);
        }
        else if (forwardDirection == true){
            System.out.println("Forward direction was already set");
        }
        else if (speed != 0){
            System.out.println("Not possible to change direction for the moving train. Stop the train first.");
        }
    }

    public void setBackwardDirection () throws IOException {
        String request = null;
        if (forwardDirection == true && speed ==0) {
            System.out.println("Setting direction to backward...");
            request = "set(" + id + ", dir[1])";
            client.executeCommand(request);
            forwardDirection=false;
            ECoSHandler.printCommunicationLog(request, request);
        }
        else if (forwardDirection == false){
            System.out.println("Backward direction was already set");
        }
        else if (speed != 0){
            System.out.println("Not possible change direction for the moving train. Stop the train first.");
        }
    }
}
