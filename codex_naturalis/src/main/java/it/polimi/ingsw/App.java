package it.polimi.ingsw;

import it.polimi.ingsw.controller.client.Controller;

/**
 * JavaFX App
 */
public class App {
    public static void main(String[] args) {
        String connectionType = args[0];
        String ip = args[1];
        Integer port = null;
        try {
            port = Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("port not valid.");
            System.exit(0);
        }

        String viewType = args[3];
        Controller controller = new Controller();

        switch(connectionType){
            case "RMI":
            case "Socket":
                controller.createInstanceOfConnection(connectionType, ip, port);
                break;
            default:
                System.out.println("Invalid choice, please insert RMI or Socket");
                System.exit(0);
        }

        switch(viewType){
            case "TUI":
            case "GUI":
                try {
                    controller.setView(viewType);
                } catch (InterruptedException e) {
                    System.out.println("Error in setting the view");
                    System.exit(0);
                }
                break;
            default:
                System.out.println("Invalid choice, please insert TUI or GUI");
                System.exit(0);
        }

    }
}