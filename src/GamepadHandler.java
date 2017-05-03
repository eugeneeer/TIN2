/**
 * Created by eugene on 26/04/17.
 */

import net.java.games.input.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class GamepadHandler {

    public static void main(String argv[]) throws IOException, InterruptedException {
        Controller controller = getGamepadController();
        pollComponents(controller);
    }


    public static void printAllControllers() {
        // Get a list of the controllers JInput knows about and can interact with.
        Controller[] controllersList = ControllerEnvironment.getDefaultEnvironment().getControllers();

        // First print all controllers names.
        for (int i = 0; i < controllersList.length; i++) {
            System.out.println(controllersList[i].getType() + ": " + controllersList[i].getName());
        }
    }

    public static void printAllComponents(Controller controller) {
        Component[] components = controller.getComponents();
        for (Component component : components) {
            if (component.isAnalog()) {
                System.out.println(component.getName() + ": analog");
            } else {
                System.out.println(component.getName() + ": digital");
            }
        }
    }

    public static Controller getGamepadController() {
        Controller result = null;
        Controller[] controllersList = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (Controller controller : controllersList) {
            if (controller.getType().equals(Controller.Type.GAMEPAD)) {
                result = controller;
                break;
            }
        }
        if (result == null) {
            System.out.println("ERROR: Gamepad controller not connected. Please connect Gamepad and start the programm again.");
            System.out.println("Currently available controllers:");
            printAllControllers();
            System.exit(1);
        }
        return result;
    }

    public static String[] readMonitoredComponentsFromFile() throws IOException {
        File file = new File("GamepadConfig.txt");
        System.out.println("Retrieving components to be polled from " + file.getPath());
        List<String> lines = Files.readAllLines(file.toPath());
        String[] result = null;
        for (String line : lines) {
            result = ArrayUtils.add(result, line.split(":")[1]);
        }
        if (result == null) {
            System.out.println(" List of monitored components couldn't be read from " + file.getAbsolutePath());
        } else {
            System.out.println("Components to be polled read successfully. Polled components are:");
            for (String string:result){
                System.out.println(readMappedName(string) + " will be polled for " + readMappedFunction(string));
            }
            System.out.println();
        }

        return result;
    }

    public static String readMappedFunction(String id) throws IOException {
        File file = new File("GamepadConfig.txt");
        List<String> lines = Files.readAllLines(file.toPath());
        String result = null;
        for (String line : lines) {
            if (id.equals(line.split(":")[1])) {
                result = line.split(":")[0];
            }
        }
        if (result == null) {
            System.out.println("Mapping to function for component id" + id + " couldn't be read from " + file.getAbsolutePath());
        }
        return result;
    }

    public static String readMappedId(String function) throws IOException {
        File file = new File("GamepadConfig.txt");
        List<String> lines = Files.readAllLines(file.toPath());
        String result = null;
        for (String line : lines) {
            if (function.equals(line.split(":")[0])) {
                result = line.split(":")[1];
            }
        }
        if (result == null) {
            System.out.println("Mapping to id for the function" + function + " couldn't be read from " + file.getAbsolutePath());
        }
        return result;
    }

    public static String readMappedName(String function) throws IOException {
        File file = new File("GamepadConfig.txt");
        List<String> lines = Files.readAllLines(file.toPath());
        String result = null;
        for (String line : lines) {
            if (function.equals(line.split(":")[1])) {
                result = line.split(":")[2];
            }
        }
        if (result == null) {
            System.out.println("Mapping to name for the function" + function + " couldn't be read from " + file.getAbsolutePath());
        }
        return result;
    }

    public static void pollComponents(Controller gamepadController) throws IOException, InterruptedException {
        ECoSClient client = new ECoSClient();
        ECoSElement element = new ECoSElement(client);
        System.out.println("Configuring gamepad polling...");
        String[] monitoredComponents = readMonitoredComponentsFromFile();
        String speedAxis = readMappedId("Speed");
        Component speedComponent = null;
        Component[] allComponents = gamepadController.getComponents();
        for (Component comp:allComponents) {
            if (comp.getName().equals(speedAxis)) {
                speedComponent = comp;
            }
        }
        System.out.println("Polling configuration completed. You can now control the train");
        while (true) {
            Thread.sleep(10);
            gamepadController.poll();
            EventQueue queue = gamepadController.getEventQueue();
            Event event = new Event();
            while (queue.getNextEvent(event)) {
                Component component = event.getComponent();
                if (ArrayUtils.contains(monitoredComponents, component.getName().toString())) {
                    if (event.getComponent().isAnalog() == false && event.getValue() == 1.0f) {
                        if (readMappedFunction(component.getName()).equals("Sound")) {
                            if (element.sound == false) {
                                element.setSound(true);
                            } else {
                                element.setSound(false);
                            }
                        }
                        if (readMappedFunction(component.getName()).equals("Light")) {
                            if (element.Light == false) {
                                element.setFrontLight(true);
                            } else {
                                element.setFrontLight(false);
                            }
                        }
                        if (readMappedFunction(component.getName()).equals("Forward")) {
                            element.setForwardDirection();
                        }
                        if (readMappedFunction(component.getName()).equals("Backward")) {
                            element.setBackwardDirection();
                        }
                    }
                }
            }
            if (speedComponent.getPollData() < 1.5258789E-5F) {
                element.incrementSpeed();
            }
            if (speedComponent.getPollData() > 1.5258789E-5F) {
                element.decrementSpeed();
            }
        }
    }
}