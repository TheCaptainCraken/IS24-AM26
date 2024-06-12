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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;

/**
* Controller responsible for the handling of the GUI components that are used during the Playing Phase of the game*/
public class MatchController {
    //DATI PER SPOSTAMENTI CARTE
    //118 LUNGO X,60 LUNGO Y (+/-), sommati alla posizione della carta a cui deve essere piazzata la carta
    //Altezza carta 100, lunghezza 150

    private final int X = 118;
    private final int Y = 60;
    private final Image defaultCard = new Image("defaultCard.png");
    private final Image mushroom = new Image("mushroom.png");
    private final Image leaf = new Image("leaf.png");
    private final Image wolf = new Image("wolf.png");
    private final Image insect= new Image("insect.png");
    private final Image inkwell = new Image("inkwell.png");
    private final Image scroll = new Image("scroll.png");
    private final Image quill = new Image("quill.png");
    private boolean cancelExists = false,optionExist = false,positionSelected = false;
    private ArrayList<Label> labels;
    private ArrayList<ImageView> hand;
    private HashMap<String,Color> playerColors;
    private Button lastClicked = null;
    private boolean root_side = true;
    private HashMap<String, Scene> playerBoards;
    private Dialog<String> dialog;
    private Scene scene;
    private Button cancel;

    LittleModel model;
    Stage stage;

    @FXML
    ImageView root,hand1,hand2,hand3,common1,common2,secret1,secret2,res_deck,gold_deck,gold1,gold2,res1,res2;
    @FXML
    Label status;
    @FXML
    VBox playerContainer,statusMenu;
    @FXML
    HBox secretContainer,handContainer,statusButtons;
    @FXML
    StackPane board;
    @FXML
    Button debug;

    //TODO  capire come poter fare sparire i bottoni per posizionare le carte + gestire endgame + completare otherplayerView

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
            //Puo valere la pena di inserire ogni coppia (IconaRisorsa,Numero) in hbox dedicata per ordine
            for(int i = 0; i < 7; i++){
                ImageView img = new ImageView(); //figlo 0 di FlowPane
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
    }
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

    public void showStartingCard(int id){ //TODO va sistemato il fatto di scegliere starting card quando non è il proprio turno
        status.setText("Please Choose the side of the starting Card");
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

    public void showStartingCard(){
        status.setText("Please wait for the other players...");
        statusButtons.getChildren().removeIf(node -> node instanceof Button);
        root.setOnMouseClicked(event -> {
            revealSpots(root.getTranslateX(),root.getTranslateY());
        });
    }

    public void showCommonObjectives(Integer[] objectiveCardIds){
        status.setText("These are the Common Objectives for this match");
        common1.setImage(new Image("frontCard"+objectiveCardIds[0]+".png"));
        common2.setImage(new Image("frontCard"+objectiveCardIds[1]+".png"));
    }

    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds){
        status.setText("Choose your Secret Objective Card");
        secret1.setImage(new Image("frontCard"+objectiveCardIds[0]+".png"));
        secret2.setImage(new Image("frontCard"+objectiveCardIds[1]+".png"));
        secret1.setOnMouseClicked(event -> {
            status.setText("Please wait for the other players...");
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(0);
        });
        secret2.setOnMouseClicked(event -> {
            status.setText("Please wait for the other players...");
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(1);
        });
    }

    public void showSecretObjectiveCard(int indexCard){
        status.setText("You have successfully chosen your Secret Objective Card!");
        secretContainer.getChildren().remove(secret2);
        secret1.setImage(new Image("frontCard"+indexCard+".png"));
        secret1.setOnMouseClicked(null);
    }

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

    public void showTableOfPlayer(String nickname){
        ArrayList<CardClient> cards = model.getListOfCards(nickname);
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

    public void showTurnInfo(String currentPlayer, GameState gameState){
        String s;
        switch(gameState){
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

    public void gameIsEnding(){
        status.setText("The game is ending.\nPlease wait for the final ranking...");
    }

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

    /*public void handleDebug(){
        root.setImage(new Image("frontCard97.png"));
        ImageView img = new ImageView(new Image("frontCard23.png"));
        img.setTranslateX(X);
        img.setTranslateY(Y);
        img.setFitWidth(150);
        img.setFitHeight(100);
        board.getChildren().add(img);
        root.setOnMouseClicked(event -> revealSpots(root.getX(),root.getY()));
    }*/


    //UTILITY FUNCTIONS

    public void setPlayerColors(HashMap<String, Color> playerColors) {
        this.playerColors = playerColors;
    }

    public void setPlayerBoards(HashMap<String, Scene> playerBoards) {
        this.playerBoards = playerBoards;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setModel(LittleModel model) {
        this.model = model;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private Pair<Double,Double> loadPosition(double x, double y){
        double trueX = 0;
        double trueY = 0;
        trueX = X * x;
        trueY = -Y * x;
        trueX = trueX + -X * y;
        trueY = trueY + -Y * y;
        return new Pair<>(trueX,trueY);
    }

    private Point inversePosition(double x, double y){
        double og_x = (x/(2 * X)) - (y/(2 * Y));
        double og_y = -((x/(2 * X)) + (y/(2 * Y)));
        return new Point((int)og_x,(int)og_y);
    }

    private Image loadStartingCardResource(int id,boolean side){
        Image img = null;
        if(side){
            img = new Image("frontCard"+id+".png");
        } else {
            img = new Image("backCard"+id+".png");
        }
        return img;
    }
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
    public ImageView setCard(CardClient newCard){
        ImageView card = findResourceOfCard(newCard);
        card.setFitHeight(100);
        card.setFitWidth(150);
        Pair<Double,Double> pos = loadPosition(newCard.getPosition().getX(),newCard.getPosition().getY());
        card.setTranslateX(pos.getKey());
        card.setTranslateY(pos.getValue());
        return card;
    }

    private Image KingdomToCard(Kingdom kingdom,Boolean isGold){
        Image img = null;
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

    public void setupDeckActions(boolean gold,int i){
        ViewSubmissions.getInstance().drawCard(gold,i);
    }

    public void setupButton(Button b,double x,double y){
        b.setTranslateX(x);
        b.setTranslateY(y);
        b.setOpacity(0.2);
        b.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.DARKGOLDENROD,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        b.setStyle("-fx-background-color: #FFD700;"); //gold color
        b.setPrefWidth(150);
        b.setPrefHeight(100);
    }

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
}
