package it.polimi.ingsw;

import it.polimi.ingsw.controller.client.Controller;

import java.util.Scanner;

/**
 * This is the main class for the application.
 * It provides a command-line interface for the user to choose the type of connection (RMI or Socket)
 * and the type of view (TUI or GUI).
 *
 * The user is prompted to enter their choices and the application is configured accordingly.
 * If an invalid choice is entered, the user is asked to enter their choice again.
 *
 * The application is started by calling the main method.
 */
public class App {
    /**
     * JavaFX App
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        Controller controller = null;
        controller = new Controller();
        Scanner myObj = new Scanner(System.in);
        String choice;

        System.out.println("Choice your type of connection: RMI or Socket");
        do {
            choice = myObj.nextLine();

            switch (choice) {
                case "RMI":
                case "Socket":
                    controller.createInstanceOfConnection(choice);
                    break;
                default:
                    System.out.println("Invalid choice, please insert RMI or Socket");
            }
        } while (!choice.equals("RMI") && !choice.equals("Socket"));

        System.out.println("Choice your type of view: TUI or GUI");
        do {
            choice = myObj.nextLine();
            switch (choice) {
                case "TUI":
                case "GUI":
                    try {
                        controller.setView(choice);
                    } catch (InterruptedException e) {
                        System.out.println("Error in setting the view");
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("Invalid choice, please insert TUI or GUI");
            }
        } while (!choice.equals("TUI") && !choice.equals("GUI"));
    }
}


