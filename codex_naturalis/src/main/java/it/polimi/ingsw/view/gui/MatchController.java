package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.view.model.CardClient;
import it.polimi.ingsw.view.model.LittleModel;
import it.polimi.ingsw.view.ViewSubmissions;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;

/**
* Controller Class responsible for the handling of the GUI components that are used and updated
 * during the PlayingPhase of the game.*/
public class MatchController {
    //DATA FOR CARD MOVEMENTS
    //118 ALONG X, 60 ALONG Y (+/-), added to the position of the card where the card must be placed
    //Card height 100, length 150

    /**
     * This constant is used in the GUI class to calculate the position of cards on the game board.
     * It represents the horizontal displacement for each card in the grid.
     * The value is set to 118, which is the width of each card image in pixels.
     */
    private final int X = 118;
    /**
     * This constant is used in the GUI class to calculate the position of cards on the game board.
     * It represents the vertical displacement for each card in the grid.
     * The value is set to 60, which is the height of each card image in pixels.
     */
    private final int Y = 60;
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the default image for a card in the game.
     * The value is set to an Image object created from the "defaultCard.png" file.
     */
    private final Image defaultCard = new Image("defaultCard.png");
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the image of a mushroom resource.
     * The value is set to an Image object created from the "mushroom.png" file.
     */
    private final Image mushroom = new Image("mushroom.png");
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the image of a leaf resource.
     * The value is set to an Image object created from the "leaf.png" file.
     */
    private final Image leaf = new Image("leaf.png");
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the image of a wolf resource.
     * The value is set to an Image object created from the "wolf.png" file.
     */
    private final Image wolf = new Image("wolf.png");
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the image of an insect resource.
     * The value is set to an Image object created from the "insect.png" file.
     */
    private final Image insect= new Image("insect.png");
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the image of an inkwell resource.
     * The value is set to an Image object created from the "inkwell.png" file.
     */
    private final Image inkwell = new Image("inkwell.png");
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the image of a scroll resource.
     * The value is set to an Image object created from the "scroll.png" file.
     */
    private final Image scroll = new Image("scroll.png");
    /**
     * This constant is used in the MatchController class of the GUI.
     * It represents the image of a quill resource.
     * The value is set to an Image object created from the "quill.png" file.
     */
    private final Image quill = new Image("quill.png");
    /**
     * These boolean fields are used in the MatchController class of the GUI.
     *
     * `cancelExists` is used to check if the cancel button exists in the current context.
     * `optionExist` is used to check if the option for the user to choose exists in the current context.
     * `positionSelected` is used to check if a position has been selected by the user in the current context.
     */
    private boolean cancelExists = false,optionExist = false,positionSelected = false;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the list of labels that display the player names and points in the game.
     */
    private ArrayList<Label> labels;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the list of image views that display the cards in the player's hand.
     */
    private ArrayList<ImageView> hand;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the map of player colors in the game.
     */
    private HashMap<String,Color> playerColors;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the latest button clicked on te board clicked button in the game.
     */
    private Button lastClicked = null;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the boolean value that indicates the side of the starting card in the game.
     */
    private boolean root_side = true;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the map of player boards in the game.
     */
    private HashMap<String, Scene> playerBoards;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the dialog box that displays error messages in the game.
     */
    private Dialog<String> dialog;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the scene of the GUI.
     */
    private Button cancel;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the current state of the game, including the game board, players, cards, and other game elements.
     * The value is set to an instance of the LittleModel class, which is a simplified model of the game state for the GUI.
     */
    private LittleModel model;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the current state of the game, including the game board, players, cards, and other game elements.
     * The value is set to an instance of the LittleModel class, which is a simplified model of the game state for the GUI.
     */
    private Stage stage;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the current scene of the JavaFX application.
     * The scene is the container for all content in a scene graph, such as UI controls or shapes.
     * The JavaFX Scene class is the container for all content.
     */
    Scene scene;
    /**
     * These ImageView fields are used in the MatchController class of the GUI.
     *
     * `root` is used to display the root card in the game.
     * `hand1`, `hand2`, and `hand3` are used to display the cards in the player's hand.
     * `common1` and `common2` are used to display the common objective cards.
     * `secret1` and `secret2` are used to display the secret objective cards.
     * `res_deck` is used to display the top card of the resource deck.
     * `gold_deck` is used to display the top card of the gold deck.
     * `gold1` and `gold2` are used to display the gold cards in the game.
     * `res1` and `res2` are used to display the resource cards in the game.
     */
    @FXML
    ImageView root,hand1,hand2,hand3,common1,common2,secret1,secret2,res_deck,gold_deck,gold1,gold2,res1,res2;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the label used to communicate with the player in the JavaFX application.
     * The status label is used to display various game status messages to the user.
     */
    @FXML
    Label status;
    /**
     * These VBox fields are used in the MatchController class of the GUI.
     *
     * `playerContainer` is used to display the player information, including player names, points, and resources.
     * `statusMenu` is used to display the game status, including the current player, game phase, and error messages.
     * `chatContainer` is used to display the chat messages received from the server.
     */
    @FXML
    VBox playerContainer,statusMenu,chatContainer;
    /**
     * These HBox fields are used in the MatchController class of the GUI.
     *
     * `secretContainer` is used to display the secret objective cards in the game.
     * `handContainer` is used to display the cards in the player's hand.
     * `statusButtons` is used to display the buttons related to game status, such as choosing card side or cancelling an action.
     * `bottom` is used as a container for various GUI elements at the bottom of the game interface.
     */
    @FXML
    HBox secretContainer,handContainer,statusButtons,bottom;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the StackPane that contains the game board in the JavaFX application.
     */
    @FXML
    StackPane board;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the TextField where the user inputs the chat message in the JavaFX application.
     * The TextField is a text input component that allows the user to enter a single line of unformatted text.
     */
    @FXML
    TextField messageContent;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the Button used to submit the chat message in the JavaFX application.
     * The Button is a control that can trigger an action when clicked.
     */
    @FXML
    Button messageSubmit;
    /**
     * This field is used in the MatchController class of the GUI.
     * It represents the Pane used as a spacer once the secret card has been chosen in the JavaFX application.
     */
    @FXML
    Pane spacer;

    /**
     * This method is used in the MatchController class of the GUI.
     * It initializes the game interface, setting up the player's hand, secret cards, common cards, and player information.
     * It also sets up the event handlers for the player labels and the message submit button.
     * The method uses the model to get the current game state and player information.
     * The player's hand, secret cards, and common cards are all initialized with the default card image.
     * The player information includes the player's name, score, and resources, which are displayed in the player container.
     * The event handlers for the player labels allow the user to switch to other players' boards by clicking on their names.
     * The event handler for the message submit button allows the user to send chat messages.
     */
    public void setup(){
        hand = new ArrayList<>();
        hand.add(hand1);
        hand.add(hand2);
        hand.add(hand3);
        for(ImageView i: hand){
            i.setImage(defaultCard);
        }
        secret1.setImage(defaultCard);
        secret2.setImage(defaultCard);
        common1.setImage(defaultCard);
        common2.setImage(defaultCard);

        labels = new ArrayList<>();
        for(String s: model.getTable().keySet()){
            ImageView img1 = new ImageView(loadColor(playerColors.get(s)));
            img1.setFitWidth(25);
            img1.setFitHeight(25);
            Label l = new Label(s);
            Label l1 = new Label("0");
            l.setFont(Font.font(25));
            l1.setFont(Font.font(25));
            l1.setTextFill(javafx.scene.paint.Color.GOLD);
            labels.add(l);
            HBox box = new HBox(img1,l,l1);
            box.setSpacing(10);
            FlowPane resources = new FlowPane();
            for(int i = 0; i < 7; i++){
                ImageView img = new ImageView();
                switch(i){
                    case 0:
                        img.setImage(mushroom);
                        break;
                    case 1:
                        img.setImage(leaf);
                        break;
                    case 2:
                        img.setImage(wolf);
                        break;
                    case 3:
                        img.setImage(insect);
                        break;
                    case 4:
                        img.setImage(quill);
                        break;
                    case 5:
                        img.setImage(scroll);
                        break;
                    case 6:
                        img.setImage(inkwell);
                        break;

                }
                Label l2 = new Label("0"); //Figlio 1 di FlowPane
                resources.getChildren().add(img);
                resources.getChildren().add(l2);
            }
            resources.setAlignment(Pos.TOP_LEFT);
            VBox container = new VBox(box,resources);
            container.setSpacing(15);
            container.setAlignment(Pos.CENTER);
            playerContainer.getChildren().add(container);
        }
        int i = 0;
        for(String s: model.getTable().keySet()){
            labels.get(i).setText(s);
            if(!s.equals(ViewSubmissions.getInstance().getNickname())){
                int finalI = i;
                labels.get(i).setOnMouseEntered(event -> {
                    labels.get(finalI).setTextFill(javafx.scene.paint.Color.DARKCYAN);
                    labels.get(finalI).setUnderline(true);
                });
                labels.get(i).setOnMouseExited(event -> {
                    labels.get(finalI).setTextFill(javafx.scene.paint.Color.BLACK);
                    labels.get(finalI).setUnderline(false);
                });
                labels.get(i).setOnMouseClicked(event -> {
                    stage.setScene(playerBoards.get(s));
                });
            } else {
                labels.get(i).setTextFill(javafx.scene.paint.Color.RED);
            }
            i++;
        }
        for(String s: playerBoards.keySet()) {
            Parent parent = playerBoards.get(s).getRoot();
            SplitPane split = (SplitPane) parent.getChildrenUnmodifiable().get(0);
            VBox v = (VBox) split.getItems().get(0);
            for (Node n : v.getChildren()) {
                 if(n instanceof Label){
                    Label label = (Label) n;
                    label.setText("Waiting for " + s + "'s hand...");
                }
            }
        }
        messageSubmit.setOnMouseClicked(event-> handleSendMessage());
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the common table in the game interface.
     * The common table includes resource cards and gold cards.
     *
     * The method retrieves the resource cards and gold cards from the model and updates the corresponding ImageView fields.
     * If a card is available, the method sets the image of the card and assigns a click event handler to it.
     * If a card is not available, the method sets the image to the default card image and removes any click event handlers.
     *
     * The click event handler for each card calls the `setupDeckActions` method, which handles the action of drawing a card from the deck.
     */
    public void showCommonTable(){
        Integer[] resourceCards = model.getResourceCards();

        if(!(resourceCards[0] == null)){
            res1.setImage(new Image("frontCard"+resourceCards[0]+".png"));
            res1.setOnMouseClicked(event -> setupDeckActions(false,0));
        } else {
            res1.setImage(new Image("defaultCard.png"));
            res1.setOnMouseClicked(null);
        }

        if(!(resourceCards[1] == null)){
            res2.setImage(new Image("frontCard"+resourceCards[1]+".png"));
            res2.setOnMouseClicked(event -> setupDeckActions(false,1));
        } else {
            res2.setImage(defaultCard);
            res2.setOnMouseClicked(null);
        }
        Integer[] goldCard = model.getGoldCards();

        if(!(goldCard[0] == null)) {
            gold1.setImage(new Image("frontCard" + goldCard[0] + ".png"));
            gold1.setOnMouseClicked(event -> setupDeckActions(true,0));
        } else {
            gold1.setImage(defaultCard);
            gold1.setOnMouseClicked(null);
        }

        if(!(goldCard[1] == null)) {
            gold2.setImage(new Image("frontCard" + goldCard[1] + ".png"));
            gold2.setOnMouseClicked(event -> setupDeckActions(true,1));
        } else {
            gold2.setImage(defaultCard);
            gold2.setOnMouseClicked(null);
        }

        if(!(model.getHeadDeckResource() == null)) {
            res_deck.setImage(KingdomToCard(model.getHeadDeckResource(),false));
            res_deck.setOnMouseClicked(event -> setupDeckActions(false,-1));
        } else {
            res_deck.setImage(defaultCard);
            res_deck.setOnMouseClicked(null);
        }

        if(!(model.getHeadDeckGold() == null)) {
            gold_deck.setImage(KingdomToCard(model.getHeadDeckGold(),true));
            gold_deck.setOnMouseClicked(event -> setupDeckActions(true,-1));
        } else {
            gold_deck.setImage(defaultCard);
            gold_deck.setOnMouseClicked(null);
        }
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the starting card chosen for the user in the game interface.
     * The method receives an integer representing the ID of the starting card.
     *
     * The method sets the image of the root ImageView to the image of the starting card.
     * It also sets up a click event handler for the root ImageView, which allows the user to flip the starting card by clicking on it.
     *
     * The method also creates two buttons, "Front" and "Back", and adds them to the statusButtons HBox.
     * The "Front" button sets the image of the root ImageView to the front of the starting card and submits the user's choice to the server.
     * The "Back" button sets the image of the root ImageView to the back of the starting card and submits the user's choice to the server.
     *
     * @param id An integer representing the ID of the starting card.
     */
    public void showStartingCard(int id){
        root.setImage(loadStartingCardResource(id,true));
        root.setOnMouseClicked(event -> {
            if(root_side){
                root.setImage(loadStartingCardResource(id,true));
                root_side = false;
            } else{
                root.setImage(loadStartingCardResource(id,false));
                root_side = true;
            }
        });
        Button b1 = new Button("Front"), b2 = new Button("Back");
        statusButtons.getChildren().addAll(b1,b2);
        b1.setOnMouseClicked(event -> {
            root.setImage(loadStartingCardResource(id,true));
            ViewSubmissions.getInstance().chooseStartingCard(true);
        });
        b2.setOnMouseClicked(event -> {
            root.setImage(loadStartingCardResource(id,false));
            ViewSubmissions.getInstance().chooseStartingCard(false);
        });
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the side on which the starting card was played by the user on the game board.
     * The method removes any existing buttons from the statusButtons HBox.
     * It also sets up a click event handler for the root ImageView, which calls the `revealSpots` method when the user clicks on the starting card.
     * The `revealSpots` method is used to display the available spots on the game board where the user can place their starting card.
     */
    public void showStartingCard(){
        statusButtons.getChildren().removeIf(node -> node instanceof Button);
        root.setOnMouseClicked(event -> {
            revealSpots(root.getTranslateX(),root.getTranslateY());
        });
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the common objective cards in the game interface.
     *
     * The method receives an array of integers representing the IDs of the common objective cards.
     * It sets the images of the `common1` and `common2` ImageView fields to the images of the common objective cards.
     *
     * @param objectiveCardIds An array of integers representing the IDs of the common objective cards.
     */
    public void showCommonObjectives(Integer[] objectiveCardIds){
        common1.setImage(new Image("frontCard"+objectiveCardIds[0]+".png"));
        common2.setImage(new Image("frontCard"+objectiveCardIds[1]+".png"));
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the secret objective cards that the user can choose from in the game interface.
     *
     * The method receives an array of integers representing the IDs of the secret objective cards.
     * It sets the images of the `secret1` and `secret2` ImageView fields to the images of the secret objective cards.
     * It also sets up click event handlers for the `secret1` and `secret2` ImageView fields, which allow the user to choose a secret objective card.
     *
     * @param objectiveCardIds An array of integers representing the IDs of the secret objective cards.
     */
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds){
        secret1.setImage(new Image("frontCard"+objectiveCardIds[0]+".png"));
        secret2.setImage(new Image("frontCard"+objectiveCardIds[1]+".png"));
        secret1.setOnMouseClicked(event -> {
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(0);
        });
        secret2.setOnMouseClicked(event -> {
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(1);
        });
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the secret objective card chosen by the user in the game interface.
     *
     * The method receives an integer representing the index of the secret objective card chosen by the user.
     * It sets the image of the `secret1` ImageView field to the image of the chosen secret objective card.
     * It also removes the `secret2` ImageView field from the secretContainer VBox.
     *
     * @param indexCard An integer representing the index of the secret objective card chosen by the user.
     */
    public void showSecretObjectiveCard(int indexCard){
        secretContainer.getChildren().remove(secret2);
        spacer.setPrefHeight(200);
        spacer.setPrefWidth(230);
        secret1.setImage(new Image("frontCard"+indexCard+".png"));
        secret1.setOnMouseClicked(null);
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the player's hand in the game interface.
     *
     * The method retrieves the player's hand from the model and updates the images of the `hand1`, `hand2`, and `hand3` ImageView fields.
     * If a card is available, the method sets the image of the card and assigns a click event handler to it.
     * If a card is not available, the method sets the image to the default card image and removes any click event handlers.
     */
    public void showHand(){
        Integer[] cards = model.getHand();
        int i = 0;
        for(ImageView v: hand){
            if(cards[i] != null){
                v.setImage(new Image("frontCard"+cards[i]+".png"));
            } else {
                v.setImage(defaultCard);
            }
            i++;
        }

    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the points of each player in the game interface.
     *
     * The method retrieves the points of each player from the model and updates the corresponding Label fields.
     * The points of each player are displayed next to their names in the player container.
     */
    public void showPoints(){
        for(String s: model.getPoints().keySet()){
            for(Label l: labels){
                if(l.getText().equals(s)){
                    Node n = l.getParent();
                    Label label = (Label) ((HBox) n).getChildren().get(2);
                    label.setText(model.getPoints().get(s).toString());
                }
            }
        }
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the resources of each player in the game interface.
     *
     * The method retrieves the resources of each player from the model and updates the corresponding Label fields.
     * The resources of each player are displayed next to their names in the player container.
     */
    public void showResourcesPlayer(){
        for(String s: model.getResources().keySet()){
            for(Label l: labels){
                if(l.getText().equals(s)){
                    Node box = l.getParent().getParent();
                    Node flow = ((VBox)box).getChildren().get(1);
                    Sign sign = null;
                    for(Node n: ((FlowPane)flow).getChildren()){
                        if(n instanceof ImageView){
                            Image image = ((ImageView) n).getImage();
                            if (image.equals(mushroom)) {
                                sign = Sign.MUSHROOM;
                            } else if (image.equals(leaf)) {
                                sign = Sign.LEAF;
                            } else if (image.equals(wolf)) {
                                sign = Sign.WOLF;
                            } else if (image.equals(insect)) {
                                sign = Sign.BUTTERFLY;
                            } else if (image.equals(quill)) {
                                sign = Sign.QUILL;
                            } else if (image.equals(scroll)) {
                                sign = Sign.SCROLL;
                            } else if (image.equals(inkwell)) {
                                sign = Sign.INKWELL;
                            } else {
                                sign = null;
                            }
                        }
                        if(n instanceof Label){
                            if(sign != null){
                                ((Label)n).setText(model.getResources().get(s).get(sign).toString());
                            } else{
                                System.out.println("Sign not found");
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for updating the game board of a specific player in the game interface.
     *
     * The method receives a string representing the nickname of the player.
     * It retrieves the list of cards of the player from the model and finds the card that was placed last.
     * If no card is found, the method prints a message to the console and returns.
     *
     * The method creates an ImageView for the latest card and sets up a click event handler for it.
     * If the player is the current user, the click event handler calls the `revealSpots` method, which displays the available spots on the game board where the user can place a card.
     * The ImageView is then added to the game board.
     *
     * If the player is not the current user, the method retrieves the player's game board from the playerBoards map and adds the ImageView to it.
     *
     * @param nickname A string representing the nickname of the player.
     */
    public void showTableOfPlayer(String nickname){
        ArrayList<CardClient> cards = model.getListOfCards(nickname);
        System.out.println("cards size is" + cards.size());
        Optional<CardClient> c = cards.stream().max(Comparator.comparing(CardClient::getTurnOfPositioning));
        if(c.isEmpty()){
            System.out.println("No card found");
            return;
        }
        CardClient latestCard = c.get();
        ImageView img = setCard(latestCard);
        if(ViewSubmissions.getInstance().getNickname().equals(nickname)){
            img.setOnMouseClicked(event ->{
                revealSpots(img.getTranslateX(),img.getTranslateY());
            });

            board.getChildren().add(img);
        } else {
            AnchorPane parent = (AnchorPane) playerBoards.get(nickname).getRoot();
            SplitPane s = (SplitPane) parent.getChildrenUnmodifiable().get(0);
            ScrollPane scroll = (ScrollPane) s.getItems().get(1);
            AnchorPane pane = (AnchorPane) scroll.getContent();
            StackPane area = (StackPane) pane.getChildren().get(0);
            area.getChildren().add(img);
        }

    }

    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for updating the game board of a specific player in the game interface.
     *
     * The method receives a string representing the nickname of the player.
     * It retrieves the list of cards of the player from the model and finds the card that was placed last.
     * If no card is found, the method prints a message to the console and returns.
     *
     * The method creates an ImageView for the latest card and sets up a click event handler for it.
     * If the player is the current user, the click event handler calls the `revealSpots` method, which displays the available spots on the game board where the user can place a card.
     * The ImageView is then added to the game board.
     *
     * If the player is not the current user, the method retrieves the player's game board from the playerBoards map and adds the ImageView to it.
     *
     * @param nickname A string representing the nickname of the player.
     * @param card A CardClient object representing the card that was placed last by the player.
     */
    public void rebuildView(CardClient card, String nickname){
        Optional<CardClient> c = Optional.ofNullable(card);
        if(c.isEmpty()){
            System.out.println("No card found");
            return;
        }
        CardClient latestCard = c.get();
        ImageView img = setCard(latestCard);
        if(ViewSubmissions.getInstance().getNickname().equals(nickname)){
            img.setOnMouseClicked(event ->{
                revealSpots(img.getTranslateX(),img.getTranslateY());
            });

            board.getChildren().add(img);
        } else {
            AnchorPane parent = (AnchorPane) playerBoards.get(nickname).getRoot();
            SplitPane s = (SplitPane) parent.getChildrenUnmodifiable().get(0);
            ScrollPane scroll = (ScrollPane) s.getItems().get(1);
            AnchorPane pane = (AnchorPane) scroll.getContent();
            StackPane area = (StackPane) pane.getChildren().get(0);
            area.getChildren().add(img);
        }

    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying who plays first in the game interface.
     *
     * The method receives a string representing the nickname of the first player.
     * It iterates over the labels representing the players and sets the image of the first player's label to a token.
     * If the first player is the current user, the method updates the status label to inform the user that they are the first player and should play a card.
     * If the first player is not the current user, the method updates the status label to inform the user that they should wait for their turn.
     *
     * @param name A string representing the nickname of the first player.
     */
    public void showIsFirst(String name){
        for(Label l: labels){
            Node n = l.getParent();
            for(Node n1: n.getParent().getChildrenUnmodifiable()){
                if(n1 instanceof ImageView && l.getText().equals(name)){
                    ((ImageView) n1).setImage(new Image("TokenBlack.png"));

                }
            }
        }
       if(name.equals(ViewSubmissions.getInstance().getNickname())){
           status.setText("You are the first Player!\nPlease play a card.");
       } else {
           status.setText("The first player is " + name +".\nPlease wait for your turn");
       }
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the current turn information in the game interface.
     *
     * The method receives a string representing the nickname of the current player and a GameState enum representing the current game state.
     * It constructs a string based on the game state and updates the status label to inform the user about the current player and game state.
     *
     * @param currentPlayer A string representing the nickname of the current player.
     * @param gameState A GameState enum representing the current game state.
     */
    public void showTurnInfo(String currentPlayer, GameState gameState){
        String s;
        switch(gameState){
            case CHOOSING_OBJECTIVE_CARD:
                s = "The Player has to choose an Objective Card";
                break;
            case CHOOSING_ROOT_CARD:
                s = "The Player has to choose the side of the Starting Card";
                break;
            case DRAWING_PHASE:
                s = "The Player has to draw a card";
                break;
            case PLACING_PHASE:
                s = "The Player has to place a card";
                break;
            case END:
                s = "End Phase";
                break;
            default:
                s = "Unknown Phase";
        }
        status.setText("It is "+currentPlayer+"'s turn.\n"+s+"!");
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for displaying the hidden hand of a specific player in the game interface.
     *
     * The method receives a string representing the nickname of the player.
     * It retrieves the hidden hand of the player from the model and updates the corresponding ImageView fields.
     * If a card is available, the method sets the image of the card.
     * If a card is not available, the method does not change the image.
     *
     * The method also updates the label of the player's hand in the game interface.
     *
     * @param nickname A string representing the nickname of the player.
     */
    public void showHiddenHand(String nickname){
        for(String s: playerBoards.keySet()) {
            Parent parent = playerBoards.get(s).getRoot();
            SplitPane split = (SplitPane) parent.getChildrenUnmodifiable().get(0);
            VBox v = (VBox) split.getItems().get(0);
            Pair<Kingdom, Boolean>[] hand = model.getHiddenHand(s);
            int i = 0;
            for (Node n : v.getChildren()) {
                if (n instanceof ImageView) {
                    ImageView img = (ImageView) n;
                    img.setImage(KingdomToCard(hand[i].getKey(), hand[i].getValue()));
                    i++;
                } else if(n instanceof Label){
                    Label label = (Label) n;
                    label.setText("Hand of " + s);
                }
            }
        }
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the reception of chat messages in the game interface.
     *
     * The method receives a string representing the sender of the message, the message itself, and a boolean indicating if the message is broadcasted to all players.
     * It constructs a string based on the sender and the message, and adds a new Label with this string to the chatContainer VBox.
     * If the message is broadcasted to all players, the method prepends "[All] " to the sender's name.
     *
     * @param sender A string representing the sender of the message.
     * @param message A string representing the message.
     * @param broadcast A boolean indicating if the message is broadcasted to all players.
     */
    public void receiveChatMessage(String sender, String message, boolean broadcast){
        chatContainer.getChildren().add(new Label((broadcast?"[All] ":"")+sender+": "+message));
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for notifying the end of the game in the game interface.
     * The method updates the status label to inform the user that the game is ending and they should wait for the final ranking.
     */
    public void gameIsEnding(){
        status.setText("The game is ending.\nPlease wait for the final ranking...");
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the situation when a player tries to perform an action during another player's turn.
     *
     * The method creates a Dialog object and sets its title to "Turn error!".
     * It also creates a Label with a message informing the player that it is not their turn and they should wait for the other players to finish their turn.
     * The Label is added to an HBox along with an ImageView displaying an error icon.
     * The HBox is then set as the content of the Dialog pane.
     * The Dialog is displayed to the user, effectively blocking any further user interaction until the Dialog is dismissed.
     */
    public void noTurn(){
        dialog = new Dialog<>();
        dialog.setTitle("Turn error!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("It is not your turn.\nPlease wait for the other players to finish their turn.");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the situation when a player tries to place a card but does not have enough resources.
     *
     * The method creates a Dialog object and sets its title to "Card Placement Refused!".
     * It also creates a Label with a message informing the player that they do not have the required resources to play the card and should try another one.
     * The Label is added to an HBox along with an ImageView displaying an error icon.
     * The HBox is then set as the content of the Dialog pane.
     * The Dialog is displayed to the user, effectively blocking any further user interaction until the Dialog is dismissed.
     */
    public void notEnoughResources(){
        dialog = new Dialog<>();
        dialog.setTitle("Card Placement Refused!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("You don't have the required resources to play this card.\nPlease try another one.");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the situation when a player tries to place a card in an invalid position.
     *
     * The method creates a Dialog object and sets its title to "Card Placement Refused!".
     * It also creates a Label with a message informing the player that they cannot place the card in the chosen position and should try another one.
     * The Label is added to an HBox along with an ImageView displaying an error icon.
     * The HBox is then set as the content of the Dialog pane.
     * The Dialog is displayed to the user, effectively blocking any further user interaction until the Dialog is dismissed.
     */
    public void cardPositionError(){
        dialog = new Dialog<>();
        dialog.setTitle("Card Placement Refused!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("You can't place the card in that position.\nPlease try another one.");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the situation when a player tries to perform an action during an incorrect phase of the game.
     *
     * The method creates a Dialog object and sets its title to "Action Refused!".
     * It also creates a Label with a message informing the player that the action cannot be performed during the current phase of the game.
     * The Label is added to an HBox along with an ImageView displaying an error icon.
     * The HBox is then set as the content of the Dialog pane.
     * The Dialog is displayed to the user, effectively blocking any further user interaction until the Dialog is dismissed.
     */
    public void wrongGamePhase(){
        dialog = new Dialog<>();
        dialog.setTitle("Action Refused!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("This action cannot be performed during this phase of the game ");
        l.setFont(Font.font(16));
        ImageView error = new ImageView("error_icon.png");
        HBox box = new HBox(error,l);
        dialog.getDialogPane().setContent(box);
        dialog.show();
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the situation when there is no active connection to the server.
     *
     * The method creates a Dialog object and sets its title to "No active connection".
     * It also creates a Label with a message informing the player that they are not connected to the server and the game will end soon.
     * The Label is added to an HBox along with an ImageView displaying an error icon.
     * The HBox is then set as the content of the Dialog pane.
     * The Dialog is displayed to the user, effectively blocking any further user interaction until the Dialog is dismissed.
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
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the situation when a player tries to perform an action with an unrecognized name.
     *
     * The method creates a Dialog object and sets its title to "Player Unrecognized!".
     * It also creates a Label with a message informing the player that the action was performed by an unknown player.
     * The Label is added to an HBox along with an ImageView displaying an error icon.
     * The HBox is then set as the content of the Dialog pane.
     * The Dialog is displayed to the user, effectively blocking any further user interaction until the Dialog is dismissed.
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

    //UTILITY FUNCTIONS
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for setting the player colors in the game.
     *
     * The method receives a HashMap where the keys are the player nicknames and the values are the Color enums representing the player colors.
     * It assigns this HashMap to the `playerColors` field of the MatchController class.
     *
     * @param playerColors A HashMap where the keys are the player nicknames and the values are the Color enums representing the player colors.
     */
    public void setPlayerColors(HashMap<String, Color> playerColors) {
        this.playerColors = playerColors;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for setting the player boards in the game.
     *
     * The method receives a HashMap where the keys are the player nicknames and the values are the Scene objects representing the player boards.
     * It assigns this HashMap to the `playerBoards` field of the MatchController class.
     *
     * @param playerBoards A HashMap where the keys are the player nicknames and the values are the Scene objects representing the player boards.
     */
    public void setPlayerBoards(HashMap<String, Scene> playerBoards) {
        this.playerBoards = playerBoards;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for setting the stage of the JavaFX application.
     *
     * The method receives a Stage object representing the stage of the JavaFX application.
     * It assigns this Stage to the `stage` field of the MatchController class.
     *
     * @param stage A Stage object representing the stage of the JavaFX application.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for setting the model of the game.
     *
     * The method receives a LittleModel object representing the model of the game.
     * It assigns this LittleModel to the `model` field of the MatchController class.
     *
     * @param model A LittleModel object representing the model of the game.
     */
    public void setModel(LittleModel model) {
        this.model = model;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for setting the scene of the JavaFX application.
     *
     * The method receives a Scene object representing the scene of the JavaFX application.
     * It assigns this Scene to the `scene` field of the MatchController class.
     *
     * @param scene A Scene object representing the scene of the JavaFX application.
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for calculating the position of a card on the game board.
     *
     * The method receives two double values representing the x and y coordinates of the card in the grid.
     * It calculates the true x and y coordinates in pixels based on the X and Y constants, which represent the horizontal and vertical displacements for each card in the grid.
     * The method returns a Pair of Double values representing the true x and y coordinates in pixels.
     *
     * @param x A double value representing the x coordinate of the card in the grid.
     * @param y A double value representing the y coordinate of the card in the grid.
     * @return A Pair of Double values representing the true x and y coordinates in pixels.
     */
    private Pair<Double,Double> loadPosition(double x, double y){
        double trueX = 0;
        double trueY = 0;
        trueX = X * x;
        trueY = -Y * x;
        trueX = trueX + -X * y;
        trueY = trueY + -Y * y;
        return new Pair<>(trueX,trueY);
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for calculating the original grid coordinates of a card on the game board from its pixel coordinates.
     *
     * The method receives two double values representing the x and y pixel coordinates of the card.
     * It calculates the original x and y grid coordinates based on the X and Y constants, which represent the horizontal and vertical displacements for each card in the grid.
     * The method returns a Point object representing the original x and y grid coordinates.
     *
     * @param x A double value representing the x pixel coordinate of the card on the game board.
     * @param y A double value representing the y pixel coordinate of the card on the game board.
     * @return A Point object representing the original x and y grid coordinates of the card.
     */
    private Point inversePosition(double x, double y){
        double og_x = (x/(2 * X)) - (y/(2 * Y));
        double og_y = -((x/(2 * X)) + (y/(2 * Y)));
        return new Point((int)og_x,(int)og_y);
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for loading the image resource for a starting card based on its ID and side.
     *
     * The method receives an integer representing the ID of the starting card and a boolean indicating the side of the card.
     * If the side is true, the method loads the image for the front of the card.
     * If the side is false, the method loads the image for the back of the card.
     * The method returns an Image object representing the loaded image resource.
     *
     * @param id An integer representing the ID of the starting card.
     * @param side A boolean indicating the side of the card. True for the front side, false for the back side.
     * @return An Image object representing the loaded image resource for the starting card.
     */
    private Image loadStartingCardResource(int id,boolean side){
        Image img = null;
        if(side){
            img = new Image("frontCard"+id+".png");
        } else {
            img = new Image("backCard"+id+".png");
        }
        return img;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for finding the image resource for a card based on its ID and side.
     *
     * The method receives a CardClient object representing the card.
     * If the side of the card is true, the method loads the image for the front of the card.
     * If the side of the card is false, the method loads the image for the back of the card based on its ID.
     * The method returns an ImageView object representing the loaded image resource.
     *
     * @param newCard A CardClient object representing the card.
     * @return An ImageView object representing the loaded image resource for the card.
     */
    private ImageView findResourceOfCard(CardClient newCard){
        Image img = null;
        int id = newCard.getId();
        if(newCard.getSide()){
            img = new Image("frontCard"+id+".png");
        } else {
            if(id > 16 && id < 27){
                img = new Image("fungi_res_back.png");
            } else if (id > 26 && id < 37) {
                img = new Image("plant_res_back.png");
            } else if(id > 36 && id < 47){
                img = new Image("animal_res_back.png");
            } else if(id > 46 && id < 57){
                img = new Image("insect_res_back.png");
            } else if(id > 56 && id < 67){
                img = new Image("fungi_gold_back.png");
            } else if (id > 66 && id < 77) {
                img = new Image("plant_gold_back.png");
            } else if(id > 76 && id < 87){
                img = new Image("animal_gold_back.png");
            } else if(id > 86 && id < 97) {
                img = new Image("insect_gold_back.png");
            } else if(id > 96 && id < 103){
                img = new Image("backCard"+id+".png");
            } else{
                System.out.println("Card not found");
            }
        }
        return new ImageView(img);
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for setting up an ImageView for a card in the game.
     *
     * The method receives a CardClient object representing the card.
     * It retrieves the image resource for the card using the `findResourceOfCard` method and sets it to a new ImageView.
     * The ImageView's height and width are set to 100 and 150 respectively.
     * The method also calculates the true x and y coordinates in pixels of the card on the game board using the `loadPosition` method.
     * The ImageView's translateX and translateY properties are set to these coordinates, effectively positioning the ImageView on the game board.
     * The method returns the ImageView.
     *
     * @param newCard A CardClient object representing the card.
     * @return An ImageView object representing the card on the game board.
     */
    public ImageView setCard(CardClient newCard){
        ImageView card = findResourceOfCard(newCard);
        card.setFitHeight(100);
        card.setFitWidth(150);
        Pair<Double,Double> pos = loadPosition(newCard.getPosition().getX(),newCard.getPosition().getY());
        card.setTranslateX(pos.getKey());
        card.setTranslateY(pos.getValue());
        return card;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for loading the image resource for a card based on its Kingdom type and whether it is a gold card.
     *
     * The method receives a Kingdom enum representing the type of the card and a Boolean indicating if the card is a gold card.
     * If the card is a gold card, the method loads the image for the gold back of the card.
     * If the card is not a gold card, the method loads the image for the resource back of the card.
     * The method returns an Image object representing the loaded image resource.
     *
     * @param kingdom A Kingdom enum representing the type of the card.
     * @param isGold A Boolean indicating if the card is a gold card. True for a gold card, false for a resource card.
     * @return An Image object representing the loaded image resource for the card.
     */
    private Image KingdomToCard(Kingdom kingdom, Boolean isGold){

        Image img = null;

        if(kingdom == null){
            img = defaultCard;
            return img;
        }
        switch(kingdom){
            case FUNGI:
                if(isGold){
                    img = new Image("fungi_gold_back.png");
                } else {
                    img = new Image("fungi_res_back.png");
                }
                break;
            case PLANT:
                if(isGold){
                    img = new Image("plant_gold_back.png");
                } else {
                    img = new Image("plant_res_back.png");
                }
                break;
            case ANIMAL:
                if(isGold){
                    img = new Image("animal_gold_back.png");
                } else {
                    img = new Image("animal_res_back.png");
                }
                break;
            case INSECT:
                if(isGold){
                    img = new Image("insect_gold_back.png");
                } else {
                    img = new Image("insect_res_back.png");
                }
                break;
        }
        return img;
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for loading the image resource for a player's token based on their color.
     *
     * The method receives a Color enum representing the color of the player's token.
     * It loads the image for the token based on the color.
     * The method returns an Image object representing the loaded image resource.
     *
     * @param c A Color enum representing the color of the player's token.
     * @return An Image object representing the loaded image resource for the token.
     */
    public Image loadColor(Color c){
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

    //EVENT FUNCTIONS
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for revealing the available spots on the game board where the user can place a card.
     *
     * The method receives two double values representing the x and y coordinates of the card in the grid.
     * It creates four Button objects representing the four possible spots around the card where a new card can be placed.
     * The method checks each spot to see if it is occupied by another card or button.
     * If a spot is not occupied, the method sets up a Button at that spot and assigns a click event handler to it.
     * The click event handler calls the `handlePositionRequest` method, which handles the user's request to place a card at that spot.
     * The Buttons for the available spots are then added to the game board.
     * The method also sets up a "Go Back" Button which allows the user to cancel the card placement operation.
     * The "Go Back" Button is added to the statusButtons HBox if it does not already exist.
     *
     * @param x A double value representing the x coordinate of the card in the grid.
     * @param y A double value representing the y coordinate of the card in the grid.
     */
    public void revealSpots(double x,double y){
        Button b1 = new Button(), b2 = new Button(), b3 = new Button(), b4 = new Button();
        cancel = new Button("Go Back");
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<Button> trueButtons = new ArrayList<>();
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
        buttons.add(b4);

        int i = 0;
        for(Button b: buttons){
            boolean isOccupied = false;
            Pair <Double,Double> pos = null;
            switch(i){
                case 0:
                    pos = loadPosition(1,0);
                    break;
                case 1:
                    pos = loadPosition(0,1);
                    break;
                case 2:
                    pos = loadPosition(-1,0);
                    break;
                case 3:
                    pos = loadPosition(0,-1);
                    break;
            }
            Pair<Double,Double> p = new Pair<>(pos.getKey() + x,pos.getValue() + y);
            for(Node node: board.getChildren()){
              if(node instanceof ImageView){
                  ImageView card = (ImageView) node;
                  if(card.getTranslateX() == p.getKey() && card.getTranslateY() == p.getValue()){
                      isOccupied = true;
                      break;
                  }
              } else if(node instanceof Button){
                  Button button = (Button) node;
                  if(button.getTranslateX() == p.getKey() && button.getTranslateY() == p.getValue()){
                      isOccupied = true;
                      break;
                  }
              }
            }
            if(!isOccupied){
                setupButton(b,p.getKey(),p.getValue());
                trueButtons.add(b);
            }
            i++;
        }
        for(Button b: trueButtons){
            b.setOnMouseClicked(event -> {
                handlePositionRequest(b);
            });
        }
        board.getChildren().addAll(trueButtons);
        cancel.setOnMouseClicked(event -> {
            board.getChildren().removeIf(node -> node instanceof Button);
            statusButtons.getChildren().removeIf(node -> node instanceof Button);
            cancelExists = false;
            positionSelected = false; //avrei potuto settare a null le azioni delle carte, sono troppo pigro
            optionExist = false;

        });
        for(Node n: statusButtons.getChildren()){
            if(n instanceof Button && ((Button) n).getText().equals("Go Back")){
                cancelExists = true;
                break;
            }
        }
        if(!cancelExists){
            statusButtons.getChildren().add(cancel);
        }
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the action of drawing a card from a deck.
     *
     * The method receives a boolean indicating if the deck is a gold deck and an integer representing the index of the deck.
     * It calls the `drawCard` method of the ViewSubmissions singleton instance, passing the boolean and the integer as arguments.
     * The `drawCard` method is responsible for handling the user's request to draw a card from a deck.
     *
     * @param gold A boolean indicating if the deck is a gold deck. True for a gold deck, false for a resource deck.
     * @param i An integer representing the index of the deck.
     */
    public void setupDeckActions(boolean gold,int i){
        ViewSubmissions.getInstance().drawCard(gold,i);
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for setting up a Button in the game interface.
     *
     * The method receives a Button object and two double values representing the x and y coordinates of the Button.
     * It sets the translateX and translateY properties of the Button to the received coordinates, effectively positioning the Button on the game board.
     * The method also sets the opacity, border, background color, width, and height of the Button.
     * The border color is set to DARKGOLDENROD and the background color is set to gold.
     * The width and height of the Button are set to 150 and 100 respectively.
     *
     * @param b A Button object representing the Button to be set up.
     * @param x A double value representing the x coordinate of the Button.
     * @param y A double value representing the y coordinate of the Button.
     */
    public void setupButton(Button b,double x,double y){
        b.setTranslateX(x);
        b.setTranslateY(y);
        b.setOpacity(0.2);
        b.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.DARKGOLDENROD,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        b.setStyle("-fx-background-color: #FFD700;"); //gold color
        b.setPrefWidth(150);
        b.setPrefHeight(100);
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the user's request to place a card at a specific position on the game board.
     *
     * The method receives a Button object representing the selected position on the game board.
     * If a position was previously selected (i.e., lastClicked is not null), the method resets the opacity and border of the previously selected Button.
     * The method then sets the opacity and border of the received Button to indicate that it is the currently selected position.
     * The method also sets the `lastClicked` field to the received Button and the `positionSelected` field to true.
     *
     * The method calculates the original grid coordinates of the Button using the `inversePosition` method.
     * It then assigns a click event handler to each card in the user's hand.
     * The click event handler removes all other Buttons from the game board, except for the selected Button, and calls the `placeCardRequest` method, which handles the user's request to place a card at the selected position.
     *
     * @param b A Button object representing the selected position on the game board.
     */
    public void handlePositionRequest(Button b){
        if(lastClicked != null){
            lastClicked.setOpacity(0.2);
            b.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.DARKGOLDENROD,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        }
        b.setOpacity(0.8);
        b.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.RED,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        lastClicked = b;
        positionSelected = true;
        Point pos = inversePosition(b.getTranslateX(),b.getTranslateY());
        int i = 0;
        for(ImageView card: hand){
            int finalI = i;
            card.setOnMouseClicked(event -> {
                board.getChildren().removeIf(item -> item instanceof Button && !item.equals(b));
                placeCardRequest(b,finalI,pos);
            });

            i++;
        }
    }
    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the user's request to place a card at a specific position on the game board.
     *
     * The method receives a Button object representing the selected position on the game board, an integer representing the index of the card in the user's hand, and a Point object representing the grid coordinates of the selected position.
     * It creates two Button objects representing the two possible sides of the card that the user can choose to place face up.
     * If a side has not been selected yet and a position has been selected, the method adds the two Button objects to the statusButtons HBox and sets the `optionExist` field to true.
     *
     * The method assigns a click event handler to each of the two Button objects.
     * The click event handler resets the `cancelExists`, `optionExist`, and `positionSelected` fields to false, sets the `lastClicked` field to null, removes the selected position Button from the game board, removes all Button objects from the statusButtons HBox, and calls the `placeCard` method of the ViewSubmissions singleton instance, passing the index of the card in the user's hand, the grid coordinates of the selected position, and a boolean indicating the side of the card to place face up.
     *
     * @param b A Button object representing the selected position on the game board.
     * @param handIndex An integer representing the index of the card in the user's hand.
     * @param position A Point object representing the grid coordinates of the selected position.
     */
    public void placeCardRequest(Button b,int handIndex, Point position){
        Button b1 = new Button("Front"), b2 = new Button("Back");
        if(!optionExist && positionSelected){
            statusButtons.getChildren().add(0,b1);
            statusButtons.getChildren().add(1,b2);
            optionExist = true;
        }

        b1.setOnMouseClicked(event -> {
            cancelExists = false;
            optionExist = false;
            positionSelected = false;
            lastClicked = null;
            board.getChildren().remove(b);
            statusButtons.getChildren().removeIf(node -> node instanceof Button);
            ViewSubmissions.getInstance().placeCard(handIndex,position,true);
        });
        b2.setOnMouseClicked(event -> {
            cancelExists = false;
            optionExist = false;
            positionSelected = false;
            lastClicked = null;
            board.getChildren().remove(b);
            statusButtons.getChildren().removeIf(node -> node instanceof Button);
            ViewSubmissions.getInstance().placeCard(handIndex,position,false);

        });
    }

    /**
     * This method is used in the MatchController class of the GUI.
     * It is responsible for handling the action of sending a chat message.
     *
     * The method retrieves the text from the messageContent TextField.
     * If the text is not blank, it checks if the text contains the "@" character, which is used to mention other players in the chat.
     * If the text contains fewer "@" characters than the number of players, the method calls the `sendChatMessage` method of the ViewSubmissions singleton instance, passing the text as an argument.
     * The `sendChatMessage` method is responsible for handling the user's request to send a chat message.
     */
    public void handleSendMessage(){
        String s = messageContent.getText();
        if(!s.isBlank()){
            int count = 1;
            if(s.toLowerCase().contains("@")){
                for(String name:playerColors.keySet()){
                    if(s.toLowerCase().contains("@"+name.toLowerCase())){
                        break;
                    }
                    count++;
                }
            }
            if(count < playerColors.size() + 1){
                ViewSubmissions.getInstance().sendChatMessage(s);
                messageContent.setText("");
            } else {
                dialog = new Dialog<>();
                dialog.setTitle("Invalid Username!");
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                Label l = new Label("The user you tried to message is not present in the game\n " +
                        "Please choose a valid username.");
                l.setFont(Font.font(16));
                ImageView error = new ImageView("error_icon.png");
                HBox box = new HBox(error,l);
                dialog.getDialogPane().setContent(box);
                dialog.show();
            }

        }
    }
}
