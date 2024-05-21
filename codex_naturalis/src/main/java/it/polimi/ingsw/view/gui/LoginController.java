package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginController {
    private GUI gui;
    private Controller controller;
    Group root;
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    Label label1,name1,name2,name3,name4;
    Dialog<String> dialog;

    @FXML
    TextField input;
    @FXML
    Button button1,debug;
    @FXML
    ImageView avatar1,avatar2,avatar3,avatar4,logo;

    public void handleSubmit(){
        //controller.login(input.getText());
        dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label label = new Label("Please Insert A Username");
        label.setFont(Font.font(22));
        TextField input = new TextField();
        Pane pane = new Pane();
        VBox content = new VBox(label,pane,input);
        content.setPrefSize(300,100);
        content.setSpacing(15);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                button1.setVisible(false);
                controller.login(input.getText());
            }
            return null;
        });
        dialog.showAndWait();

    }

    public void handleDebug(){
        //showInsertNumberOfPlayer();
        //waitLobby();
        //showColorPrompt();
    }

    public void showInsertNumberOfPlayer() {

        dialog = new Dialog<>();
        dialog.setTitle("NumberOfPlayers");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label label = new Label("You are the first player. Please enter the number of players");
        label.setFont(Font.font(20));
        ChoiceBox<String> menu = new ChoiceBox<>();
        menu.getItems().addAll("2","3","4");
        menu.setValue("2");

        VBox content = new VBox(label,menu);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(15);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                controller.insertNumberOfPlayers(Integer.parseInt(menu.getValue()));
            }
            return null;
        });
        dialog.showAndWait();
    }

    public void waitLobby(){
        button1.setVisible(false);
        label1.setText("You are connected to the server.\n Please wait for the first player \n to choose the number of players ");

    }

    public void stopWaiting(){
        button1.setVisible(false);
        label1.setText("The Game is Starting!");
    }

    public void askColor(){
        button1.setVisible(true);
        button1.setOnMouseClicked(event -> {
            showColorPrompt();
        });
    }

    public void showColorPrompt(){
        dialog = new Dialog<>();
        dialog.setTitle("chooseColor");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label l = new Label("Please Choose a Color");
        l.setFont(Font.font(16));
        ChoiceBox<String> colors = setupColors();
        VBox content = new VBox(l,colors);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(10);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Color color = StringToColor(colors.getValue());
                controller.chooseColor(color);
            }
            return null;
        });
        dialog.showAndWait();

    }

    private ChoiceBox<String> setupColors(){
        ChoiceBox<String> box = new ChoiceBox<>();
        box.getItems().addAll("Yellow","Blue","Green","Red");
        box.setValue("Yellow");
        return box;

    }

    private Color StringToColor(String color){
        Color c = Color.YELLOW;
        switch(color){
            case "Yellow": c = Color.YELLOW;
            case "Blue":c = Color.BLUE;
            case "Green": c = Color.GREEN;
            case "Red": c = Color.RED;
        }
        return c;
    }



}
