package it.polimi.ingsw;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.server.NetworkHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        Controller controller = null;
        Scanner myObj = new Scanner(System.in);
        System.out.println("Do you want to be client or server?\n"
                + "Type 'client' to be a client or 'server' to be a server.");

        String choice = myObj.nextLine();  // Read user input
        if (choice.equals("client")) {
            controller = new Controller();
        } else if (choice.equals("server")) {
           new NetworkHandler();
        } else {
            System.out.println("Invalid choice. Please type 'client' or 'server'.");
        }

        System.out.println("Choice your type of connection: RMI or Socket");
        choice = myObj.nextLine();
        controller.createInstanceOfConnection(choice);

        System.out.println("Choice your type of view: TUI or GUI");
        controller.setView(myObj.nextLine());
    }

}