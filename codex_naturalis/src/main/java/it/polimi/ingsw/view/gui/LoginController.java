package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.client.Controller;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

    @FXML
    TextField input;
    @FXML
    Button button1,debug;
    @FXML
    ImageView avatar1,avatar2,avatar3,avatar4,logo;

    public void handleSubmit(){
        //controller.login(input.getText());
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label label = new Label("Please Insert A Username");
        label.setFont(Font.font(22));
        TextField input = new TextField();
        Pane pane = new Pane();
        VBox content = new VBox(label,pane,input);
        content.setPrefSize(300,100);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                controller.login(input.getText());
            }
            return null;
        });
        dialog.showAndWait();

    }

    public void handleDebug(){
        //test.showInsertNumberOfPlayer();
    }

    public void showInsertNumberOfPlayer() {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("NumberOfPlayers");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Label label = new Label("You are the first player. Please enter the number of players");
        label.setFont(Font.font(20));
        MenuButton menu = new MenuButton("Number");
        MenuItem menuItem1 = new MenuItem("2");
        MenuItem menuItem2 = new MenuItem("3");
        MenuItem menuItem3 = new MenuItem("4");
        menu.getItems().addAll(menuItem1, menuItem2, menuItem3);

        menuItem1.setOnAction(event -> controller.insertNumberOfPlayers(2));
        menuItem2.setOnAction(event -> controller.insertNumberOfPlayers(3));
        menuItem3.setOnAction(event -> controller.insertNumberOfPlayers(4));
        VBox content = new VBox(label,menu);
        dialog.showAndWait();
    }


}
