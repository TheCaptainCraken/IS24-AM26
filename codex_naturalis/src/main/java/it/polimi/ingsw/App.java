package it.polimi.ingsw;

import it.polimi.ingsw.controller.client.Controller;

import java.util.Scanner;

public class App {
    /**
     * JavaFX App
     */
    public static void main(String[] args) {
        String connectionType = args[0];
        String ip = args[1];
        String port = args[2];
        String viewType = args[1];
        Controller controller = null;

        controller = new Controller();
        System.out.println("Choice your type of connection: RMI or Socket");

        switch(connectionType){
                case "RMI":
                case "Socket":
                    controller.createInstanceOfConnection(connectionType);
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


