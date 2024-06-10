package it.polimi.ingsw;

import it.polimi.ingsw.controller.client.Controller;
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

        String choice;
        controller = new Controller();
        System.out.println("Choice your type of connection: RMI or Socket");

      do{
          choice = myObj.nextLine();

          switch(choice){
              case "RMI":
              case "Socket":
                  controller.createInstanceOfConnection(choice);
                  break;
              default:
                  System.out.println("Invalid choice, please insert RMI or Socket");
          }
      }while(!choice.equals("RMI") && !choice.equals("Socket"));

       System.out.println("Choice your type of view: TUI or GUI");
       do{
           choice = myObj.nextLine();
           switch(choice){
               case "TUI":
               case "GUI":
                   controller.setView(choice);
                   break;
               default:
                   System.out.println("Invalid choice, please insert TUI or GUI");
           }
       }while(!choice.equals("TUI") && !choice.equals("GUI"));

    }
}

