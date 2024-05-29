package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.view.ViewSubmissions;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController {
    private GUI gui;
    Group root;
    private Stage stage;
    private ArrayList<Label> names;
    private ArrayList<ImageView> avatars;
    private final String DEFAULT_LABEL = "Awaiting Players...";
    private final Image DEFAULT_TOKEN = new Image("TokenBlack.png");

    @FXML
    Label label1,name1,name2,name3,name4;
    Dialog<String> dialog;
    @FXML
    HBox inputBox;
    @FXML
    TextField input;
    @FXML
    Button button1,debug;
    @FXML
    ImageView avatar1,avatar2,avatar3,avatar4,logo;

    //METHODS CALLED BY VIEW COMPONENTS
    public void handleSubmit(){
        String name = input.getText();
        if(name.isBlank()){
            dialog = new Dialog<>();
            dialog.setTitle("Format Error");
            Label label = new Label("Username Can't be Empty!");
            ImageView error = new ImageView("error_icon.png");
            label.setFont(Font.font(22));
            HBox box = new HBox(error,label);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.getDialogPane().setContent(box);
            dialog.show();
        } else {
            ViewSubmissions.getInstance().chooseNickname(name);
            input.setVisible(false);
            label1.setText("Please Wait...");
        }
    }

    public void handleDebug() throws InterruptedException {
        //showInsertNumberOfPlayers();
        //waitLobby();
        //showColorPrompt();
        //stopWaiting();
        //colorAlreadyTaken();
        //sameName("test");
        /*HashMap <String,Color> map = new HashMap<>();
        map.put("Arturo",Color.BLUE);
        map.put("Pietro",null);
        map.put("Daniel",Color.GREEN);
        refreshUsers(map);
        HashMap <String,Color> map1 = new HashMap<>();
        map1.put("Arturo",Color.BLUE);
        map1.put("Pietro",Color.RED);
        map1.put("Daniel",Color.GREEN);
        refreshUsers(map1);*/
    }

    //METHODS CALLED BY GUI CLASS
    public void showInsertNumberOfPlayers() {

        label1.setText("You are the first player.\n Please enter the number of players");
        button1.setText("Submit");
        button1.setVisible(true);
        ChoiceBox <Integer> box = new ChoiceBox<>();
        box.getItems().addAll(2,3,4);
        box.setValue(2);
        inputBox.getChildren().remove(input);
        inputBox.getChildren().add(box);
        button1.setOnMouseClicked(event -> ViewSubmissions.getInstance().chooseNumberOfPlayers(box.getValue()));

    }

    public void waitLobby(){
        button1.setVisible(false);
        label1.setText("You are connected to the server.\n Please wait for the first player \n to choose the number of players ");

    }

    public void disconnect(){
       label1.setText("The lobby is full. No other players can join");
    }

    public void stopWaiting(){
        label1.setText("Choose your Color!");
        showColorPrompt();
    }


    public void showColorPrompt(){
        button1.setVisible(false);
        inputBox.getChildren().remove(input);
        setupColors();
    }

    /*public void showIsFirst(String firstPlayer){
        label1.setText("The first player is " + firstPlayer +".\nThe game is about to start!");
    }*/

    /**
     * method used to Set Up the Login View with its images
     */
    public void setup(){
        debug.setVisible(false);
        Image img = new Image("logo.png");
        names = new ArrayList<>();
        avatars = new ArrayList<>();

        logo.setImage(img);
        names.add(name1);
        names.add(name2);
        names.add(name3);
        names.add(name4);
        avatars.add(avatar1);
        avatars.add(avatar2);
        avatars.add(avatar3);
        avatars.add(avatar4);

        for(Label l: names)
            l.setText(DEFAULT_LABEL);

        for(ImageView i: avatars)
            i.setImage(DEFAULT_TOKEN);

    }
    private void setupColors(){
        Image yellow = new Image("TokenYellow.png");
        ImageView y = new ImageView(yellow);
        y.setFitWidth(75);
        y.setFitHeight(75);
        y.setPreserveRatio(true);
        y.setSmooth(true);

        Image green = new Image("TokenGreen.png");
        ImageView g = new ImageView(green);
        g.setFitWidth(75);
        g.setFitHeight(75);

        Image blue = new Image("TokenBlue.png");
        ImageView b = new ImageView(blue);
        b.setFitWidth(75);
        b.setFitHeight(75);

        Image red = new Image("TokenRed.png");
        ImageView r = new ImageView(red);
        r.setFitWidth(75);
        r.setFitHeight(75);

        Button rButton = new Button();
        rButton.setGraphic(r);
        Label l1 = new Label("RED");
        rButton.setOnMouseClicked(event -> {
            label1.setText("Color Submitted!");
            inputBox.getChildren().clear();
            ViewSubmissions.getInstance().chooseColor(Color.RED);
        });

        Button bButton = new Button();
        bButton.setGraphic(b);
        Label l2 = new Label("BLUE");
        bButton.setOnMouseClicked(event -> {
                label1.setText("Color Submitted!");
                inputBox.getChildren().clear();
                ViewSubmissions.getInstance().chooseColor(Color.BLUE);
        });

        Button gButton = new Button();
        gButton.setGraphic(g);
        Label l3 = new Label("GREEN");
        gButton.setOnMouseClicked(event -> {
            label1.setText("Color Submitted!");
            inputBox.getChildren().clear();
            ViewSubmissions.getInstance().chooseColor(Color.GREEN);
        });

        Button yButton = new Button();
        yButton.setGraphic(y);
        Label l4 = new Label("YELLOW");
        yButton.setOnMouseClicked(event -> {
            label1.setText("Color Submitted!");
            inputBox.getChildren().clear();
            ViewSubmissions.getInstance().chooseColor(Color.YELLOW);
        });

        VBox b1 = new VBox(rButton,l1);
        b1.setAlignment(Pos.CENTER);
        VBox b2 = new VBox(bButton,l2);
        b2.setAlignment(Pos.CENTER);
        VBox b3 = new VBox(gButton,l3);
        b3.setAlignment(Pos.CENTER);
        VBox b4 = new VBox(yButton,l4);
        b4.setAlignment(Pos.CENTER);

        inputBox.getChildren().addAll(b1,b2,b3,b4);
        inputBox.setSpacing(10);

    }
    private Image loadColor(Color c){
        switch(c){
            case GREEN:
                return new Image("TokenGreen.png");
            case RED:
                return new Image("TokenRed.png");
            case BLUE:
                return new Image("TokenBlue.png");
            case YELLOW:
                return new Image("TokenYellow.png");
        }
        return null;
    }

    public void refreshUsers(HashMap<String,Color> map){
        int i;
        for(String s: map.keySet()){
            i = 0;
            for(Label l: names){
                Color c = map.get(s);
                if(l.getText().equals(s)){
                    if(c != null && avatars.get(i).getImage().equals(DEFAULT_TOKEN) ){
                        Image img = loadColor(c);
                        avatars.get(i).setImage(img);
                    }
                    break;
                } else if(l.getText().equals(DEFAULT_LABEL)){
                    l.setText(s);
                    if(c != null){
                        Image img = loadColor(c);
                        avatars.get(i).setImage(img);

                    }
                    break;
                }
                i++;
            }
        }
    }

    public void correctNumberOfPlayers(int number){
        label1.setText("You have correctly set the number of players\nThe number of players is " + number);
    }


    //Exception Handling
    public void colorAlreadyTaken(){
        dialog = new Dialog<>();
        dialog.setTitle("Color Already Taken!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("The color you have selected\n has been chosen by another player.\n Please try again");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
        showColorPrompt();
    }

    public void sameName(String nickname) {
        dialog = new Dialog<>();
        dialog.setTitle("Username Already Taken!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("The username "+"'"+ nickname +"'"+ "\n has already been selected by another player.\n Please try again");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
        button1.setVisible(true);
    }
    public void noConnection() {

    }

    public void lobbyComplete() {
        input.setVisible(false);
        button1.setVisible(false);
        label1.setText("The lobby is full. No other players can join");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
