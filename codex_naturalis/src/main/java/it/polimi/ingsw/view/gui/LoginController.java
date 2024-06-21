package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
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
/**
 * Controller Class responsible for the handling of the GUI components that are used and updated
 * during the LoginPhase of the game.
 *  */
public class LoginController {
    private Stage stage;
    /**
     * This is an ArrayList of Label objects in the LoginController.
     * Each Label represents a player's name in the GUI.
     * This field is used to update the player's names in the GUI during the login phase of the game.
     */
    private ArrayList<Label> names;

    /**
     * This is an ArrayList of ImageView objects in the LoginController.
     * Each ImageView represents a player's avatar in the GUI.
     * This field is used to update the player's avatars in the GUI during the login phase of the game.
     */
    private ArrayList<ImageView> avatars;
    /**
     * This is a constant String in the LoginController.
     * It represents the default text displayed for player names in the GUI during the login phase of the game.
     * This field is used to initialize the text of the player name labels to "Awaiting Players...".
     */
    private final String DEFAULT_LABEL = "Awaiting Players...";
    /**
     * This is a constant Image in the LoginController.
     * It represents the default image displayed for player avatars in the GUI during the login phase of the game.
     * This field is used to initialize the image of the player avatar labels to a black token.
     */
    private final Image DEFAULT_TOKEN = new Image("TokenBlack.png");

    /**
     * Each Label represents a specific label in the GUI.
     * label1 is used to display various messages to the user during the login phase of the game.
     * name1, name2, name3, and name4 are used to display the names of the players in the GUI during the login phase of the game.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    Label label1,name1,name2,name3,name4;
    /**
     * It is used to create dialog boxes that inform the user about various events or errors during the login phase of the game.
     * For example, it is used to inform the user when the username they have chosen is already taken by another player, or when the color they have selected for their player token is already chosen by another player.
     */
    Dialog<String> dialog;
    /**
     * In the context of the LoginController, it is used to hold various input components such as TextField and ChoiceBox during the login phase of the game.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    HBox inputBox;
    /**
     * It is a text input component that allows the user to enter a string.
     * In the context of the LoginController, it is used to collect user input such as the player's chosen nickname during the login phase of the game.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    TextField input;
    /**
     * In the context of the LoginController, it is used to trigger various actions during the login phase of the game, such as submitting user input.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    Button button1;
    /**
     * Each ImageView represents a player's avatar or the game logo in the GUI.
     * avatar1, avatar2, avatar3, and avatar4 are used to display the avatars of the players in the GUI during the login phase of the game.
     * logo is used to display the game logo in the GUI.
     * They are annotated with @FXML, which means they are injected by the JavaFX FXMLLoader.
     */
    @FXML
    ImageView avatar1,avatar2,avatar3,avatar4,logo;

    //METHODS CALLED BY VIEW COMPONENTS
    /**
     * This method is used to handle the submission of the user's input after clicking the "Submit button".
     * It first retrieves the text from the input field.
     * If the input is blank, it creates a dialog box to inform the user that the username cannot be empty.
     * If the input is not blank, it submits the user's chosen nickname and sets the text of the label label1 to "Please Wait...".
     */
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
            label1.setText("Please Wait...");
        }
    }

    //METHODS CALLED BY GUI CLASS

    /**
     * This method is used to handle the scenario where the user is the first player and needs to enter the number of players for the game.
     * It sets the text of `label1` to inform the user that they are the first player and need to enter the number of players.
     * It sets the text of `button1` to "Submit".
     * It creates a `ChoiceBox` (a type of dropdown menu in JavaFX) named `box` that allows the user to select the number of players (2, 3, or 4).
     * It sets the default value of `box` to 2.
     * It removes the `input` TextField from `inputBox` and adds `box` to it.
     * It sets an `onMouseClicked` event handler for `button1` that submits the chosen number of players when the button is clicked.
     */
    public void showInsertNumberOfPlayers() {
        label1.setText("You are the first player.\n Please enter the number of players");
        button1.setText("Submit");
        ChoiceBox <Integer> box = new ChoiceBox<>();
        box.getItems().addAll(2,3,4);
        box.setValue(2);
        inputBox.getChildren().remove(input);
        inputBox.getChildren().add(box);
        button1.setOnMouseClicked(event -> ViewSubmissions.getInstance().chooseNumberOfPlayers(box.getValue()));
    }

    /**
     * This method is used to handle the scenario where the user has connected to the server and is waiting for the first player to choose the number of players for the game.
     * It makes the `button1` invisible.
     * It sets the text of `label1` to inform the user that they are connected to the server and are waiting for the first player to choose the number of players.
     * It makes the `input` TextField invisible.
     */
    public void waitLobby(){
        button1.setVisible(false);
        label1.setText("You are connected to the server.\n Please wait for the first player \n to choose the number of players ");
        input.setVisible(false);

    }

    /**
     * This method is used to handle the scenario where the lobby is full and no other players can join the game.
     * It sets the text of `label1` to inform the user that the lobby is full, no other players can join, and they will be disconnected soon.
     * * It makes the `button1` invisible.
     * input.setVisible(false);
     */
    public void disconnect(){
        button1.setVisible(false);
        input.setVisible(false);
        label1.setText("The lobby is full. No other players can join\n You will be disconnected soon. Goodbye!");
    }

    /**
     * This method is used to transition the user from the waiting state to the color selection state.
     * It calls the `showColorPrompt` method which prompts the user to choose a color for their player token.
     */
    public void stopWaiting(){
        showColorPrompt();
    }


    /**
     * This method is used to handle the scenario where the user needs to choose a color for their player token.
     * It sets the text of `label1` to prompt the user to choose a color.
     * It makes the `button1` invisible.
     * It removes all nodes from `inputBox` and calls the `setupColors` method to add color choice buttons to it.
     */
    public void showColorPrompt(){
        label1.setText("Choose your Color!");
        button1.setVisible(false);
        inputBox.getChildren().removeIf(node -> node instanceof TextField);
        setupColors();
    }

    /**
     * This method is used to update the user interface with the current players' names and colors.
     * It iterates over a HashMap where the keys are player names and the values are their chosen colors.
     * For each player, it finds the corresponding label in the `names` ArrayList and sets its text to the player's name.
     * If the player has chosen a color, it also updates the corresponding ImageView in the `avatars` ArrayList with an image of the player's color.
     * If the player has not chosen a color, the ImageView is set to a default image.
     *
     * @param map A HashMap where the keys are player names (String) and the values are their chosen colors (Color).
     */
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
        //reset label if someone exits the lobby
        for(i = 0; i < 4; i++){
            if(!names.get(i).getText().equals(DEFAULT_LABEL)){
                boolean find = false;
               for(String s: map.keySet()){
                   if(s.equals(names.get(i).getText())){
                       find = true;
                       break;
                   }
               }
                if(!find){
                     names.get(i).setText(DEFAULT_LABEL);
                     avatars.get(i).setImage(DEFAULT_TOKEN);
                }
            }
        }
    }
    /**
     * This method is used to handle the scenario where the user has correctly set the number of players for the game.
     * It sets the text of `label1` to inform the user that they have correctly set the number of players and displays the number.
     * It removes the `ChoiceBox` (if any) from `inputBox`.
     * It makes the `button1` invisible.
     *
     * @param number The number of players that the user has set for the game.
     */
    public void correctNumberOfPlayers(int number){
        label1.setText("You have correctly set the number of players\nThe number of players is " + number);
        inputBox.getChildren().removeIf(node->node instanceof ChoiceBox);
        button1.setVisible(false);
    }


    //Exception Handling
    /**
     * This method is used to handle the scenario where the user has chosen a color that has already been taken by another player.
     * It creates a dialog box to inform the user that the color they have selected has been chosen by another player and prompts them to try again.
     * It then calls the `showColorPrompt` method to prompt the user to choose a different color.
     */
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

    /**
     * This method is used to handle the scenario where the user has chosen a username that has already been taken by another player.
     * It clears the input TextField and creates a dialog box to inform the user that the username they have chosen has already been selected by another player.
     * It prompts the user to try again and sets the text of the label label1 to "Please enter a new Username...".
     *
     * @param nickname The username that the user has chosen, which has already been taken by another player.
     */
    public void sameName(String nickname) {
        input.setText("");
        dialog = new Dialog<>();
        dialog.setTitle("Username Already Taken!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("The username "+"'"+ nickname +"'"+ "\n has already been selected by another player.\nPlease try again");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
        label1.setText("Please enter a new Username...");
    }
    /**
     * This method is used to handle the scenario where there is no active connection to the server.
     * It creates a dialog box to inform the user that they are not connected to the server and the game will end soon.
     * It sets the title of the dialog box to "No active connection".
     * It adds an OK button to the dialog box.
     * It creates a Label and an ImageView to display the error message and an error icon, respectively, in the dialog box.
     * It shows the dialog box to the user.
     */
    public void noConnection() {
        dialog = new Dialog<>();
        dialog.setTitle("No active connection");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("You are not connected to the server. Game will end soon.\nThank you for playing. Goodbye!");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
    }

    /**
     * This method is used to handle the scenario where the lobby is full and no other players can join the game.
     * It makes the `input` TextField and `button1` invisible.
     * It sets the text of `label1` to inform the user that the lobby is full and no other players can join.
     */
    public void lobbyComplete() {
        input.setVisible(false);
        button1.setVisible(false);
        label1.setText("The lobby is full. No other players can join");
    }
    /**
     * This method is used to handle the scenario where the lobby is full and no other players can join the game.
     * It makes the `input` TextField and `button1` invisible.
     * It sets the text of `label1` to inform the user that the lobby is full and no other players can join.
     */
    public void closingLobbyError(){
        dialog = new Dialog<>();
        dialog.setTitle("Error");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("An error occurred while closing the lobby.");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
    }
    /**
     * This method is used to handle the scenario where an action was performed by an unknown player.
     * It creates a dialog box to inform the user that the action was performed by an unknown player.
     * It creates a Label and an ImageView to display the error message and an error icon, respectively, in the dialog box.
     * It shows the dialog box to the user.
     */
    public void noPlayer(){
        dialog = new Dialog<>();
        dialog.setTitle("Player Unrecognized!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("The Action was performed by an unkonwn player.\n");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
    }
    //Utility Functions

    /**
     * This method is used to initialize the GUI components for the login phase of the game.
     * It sets up the images and labels for the player avatars and names.
     * It initializes the `names` and `avatars` ArrayLists and adds the corresponding labels and image views to them.
     * It sets the default text for the name labels and the default image for the avatar image views.
     */
    public void setup(){
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
    /**
     * This method is used to set up the color selection interface for the user.
     * It creates Image and ImageView objects for each color (yellow, green, blue, red) and sets their dimensions.
     * It creates a Button for each color and sets the corresponding ImageView as the graphic of the button.
     * It also sets an onMouseClicked event handler for each button that submits the chosen color when the button is clicked.
     * It creates a VBox for each button and label pair and adds them to the `inputBox`.
     */
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
    /**
     * This method is used to load the image corresponding to a specific color.
     * It takes a Color enum as input and returns an Image object.
     * The method uses a switch statement to determine which image to load based on the input color.
     * If the color is GREEN, it returns an image of a green token.
     * If the color is RED, it returns an image of a red token.
     * If the color is BLUE, it returns an image of a blue token.
     * If the color is YELLOW, it returns an image of a yellow token.
     * If the color does not match any of the above, it returns null.
     *
     * @param c The color for which to load the corresponding image.
     * @return The Image object corresponding to the input color, or null if the color does not match any of the predefined colors.
     */
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

    /**
     * This method is used to set the Stage for the LoginController.
     * @param stage The Stage object to be set for the LoginController.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
