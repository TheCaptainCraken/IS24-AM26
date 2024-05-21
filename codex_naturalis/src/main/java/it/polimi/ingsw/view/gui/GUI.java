package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.client.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {
    private Controller controller;
    public static LoginController loginController;
    private static Scene scene;
    private Parent root;


    @Override
    public void start(Stage stage) throws IOException {
        loginController = new LoginController();
        root = loadFXML("gui");
        scene = new Scene(root, 1920, 1080);
        loginController.setStage(stage);
        loginController.setController(controller);
        stage.setScene(scene);
        stage.show();
    }

    public  void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private  Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(it.polimi.ingsw.App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }

    public void showInsertNumberOfPlayer(){
         loginController.showInsertNumberOfPlayer();
    }

    public void waitLobby(){
        loginController.waitLobby();
    }


}