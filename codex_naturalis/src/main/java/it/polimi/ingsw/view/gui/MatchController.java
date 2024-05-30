package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.view.CardClient;
import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.ViewSubmissions;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
* Controller responsible for the handling of the GUI components that are used during the Playing Phase of the game*/
public class MatchController {
    //DATI PER SPOSTAMENTI CARTE
    //118 LUNGO X,60 LUNGO Y (+/-), sommati alla posizione della carta a cui deve essere piazzata la carta
    //Altezza carta 100, lunghezza 150

    private final int X = 118;
    private final int Y = 60;
    private ImageView lastPlayed,triedToPlay,lastDeleted;
    private ArrayList<Label> labels;
    private boolean root_side = true;
    private HashMap<String, Parent> playerBoards;
    private Dialog<String> dialog;


    LittleModel model;
    Stage stage;

    @FXML
    ImageView root,hand1,hand2,hand3,common1,common2,secret1,secret2,res_deck,gold_deck,gold1,gold2,res1,res2;
    @FXML
    Label status;
    @FXML
    VBox playerContainer,statusMenu;
    @FXML
    HBox secretContainer,handContainer;
    @FXML
    StackPane board;
    @FXML
    Button debug;

    //TODO Refreshare mappe del client del giocatore + quelle degli altri giocatori, pescare le carte, refreshare i punteggi e settare la mano iniziale

    public void showCommonTable(){
        //TODO setuppare hashmap di User/scene, capire quale scena è la propria, capire come inizializzare i vari elementi della view senza scrivere troppo codice
        labels = new ArrayList<>();
        for(String s: model.getPlayersAndCardsNumber().keySet()){
            ImageView img1 = new ImageView();
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
                ImageView img = new ImageView();
                switch(i){
                    case 0:
                        img.setImage(new Image("mushroom.png"));
                        break;
                    case 1:
                        img.setImage(new Image("leaf.png"));
                        break;
                    case 2:
                        img.setImage(new Image("wolf.png"));
                        break;
                    case 3:
                        img.setImage(new Image("insect.png"));
                        break;
                    case 4:
                        img.setImage(new Image("quill.png"));
                        break;
                    case 5:
                        img.setImage(new Image("scroll.png"));
                        break;
                    case 6:
                        img.setImage(new Image("inkwell.png"));
                        break;

                }
                    Label l2 = new Label("0");
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
        for(String s: model.getPlayersAndCardsNumber().keySet()){
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
                    stage.setScene(playerBoards.get(s).getScene());
                });
            } else {
                labels.get(i).setTextFill(javafx.scene.paint.Color.RED);
            }
            i++;
        }
        Integer[] resourceCards = model.getResourceCards();
        res1.setImage(new Image("frontCard"+resourceCards[0]+".png"));
        res2.setImage(new Image("frontCard"+resourceCards[1]+".png"));
        Integer[] goldCard = model.getGoldCards();
        gold1.setImage(new Image("frontCard"+goldCard[0]+".png"));
        gold2.setImage(new Image("frontCard"+goldCard[1]+".png"));
        res_deck.setImage(KingdomToCard(model.getHeadDeckResource(),false));
        gold_deck.setImage(KingdomToCard(model.getHeadDeckGold(),true));
    }

    public void showStartingCard(int id){
        status.setText("Please Choose the side of the starting Card");
        root.setImage(loadStartingCardResource(id,true));
        root.setOnMouseClicked((event) -> {
            if(root_side){
                root.setImage(loadStartingCardResource(id,true));
                root_side = false;
            } else{
                root.setImage(loadStartingCardResource(id,false));
                root_side = true;
            }
        });
        Button b1 = new Button("Front"), b2 = new Button("Back");
        HBox container = new HBox(b1,b2);
        container.setSpacing(15);
        container.setAlignment(Pos.CENTER);
        b1.setOnMouseClicked(event -> {
            status.setText("Please wait for the other players...");
            statusMenu.getChildren().removeIf(node -> node instanceof HBox);
            root.setOnMouseClicked(event1-> revealSpots(root.getTranslateX(),root.getTranslateY()));
            root.setImage(loadStartingCardResource(id,true));
            ViewSubmissions.getInstance().chooseStartingCard(true);
        });
        b2.setOnMouseClicked(event -> {
            status.setText("Please wait for the other players...");
            statusMenu.getChildren().removeIf(node -> node instanceof HBox);
            root.setOnMouseClicked(event1 -> revealSpots(root.getTranslateX(),root.getTranslateY()));
            root.setImage(loadStartingCardResource(id,false));
            ViewSubmissions.getInstance().chooseStartingCard(false);
        });
        statusMenu.getChildren().add(container);
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
            secret1.setOnMouseClicked(null);
            lastDeleted = secret2;
            secretContainer.getChildren().remove(secret2);
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(0);
        });
        secret2.setOnMouseClicked(event -> {
            status.setText("Please wait for the other players...");
            statusMenu.getChildren().removeIf(node -> node instanceof HBox);
            lastDeleted = secret1;
            secretContainer.getChildren().remove(secret1);
            secret2.setOnMouseClicked(null);
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(1);
        });
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
           status.setText("You are the first Player!\nThe game is about to start!");
       } else {
           status.setText("The first player is " + name +".\nThe game is about to start!");
       }
    }

    public void showTurnInfo(String currentPlayer, GameState gameState){
        status.setText("It is "+currentPlayer+"'s turn.\n"+gameState.toString());
    }

    public void noTurn(){
        dialog = new Dialog<>();
        dialog.setTitle("Turn error!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Label l = new Label("It is not your turn.\nPlease wait for the other players to finish their turn.");
        if(secretContainer.getChildren().size() == 1){
            secretContainer.getChildren().add(lastDeleted);
        }
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

    public void handleDebug(){
        root.setImage(new Image("frontCard97.png"));
        ImageView img = new ImageView(new Image("frontCard23.png"));
        img.setTranslateX(X);
        img.setTranslateY(Y);
        img.setFitWidth(150);
        img.setFitHeight(100);
        board.getChildren().add(img);
        root.setOnMouseClicked(event -> revealSpots(root.getX(),root.getY()));
    }

    public void setCard(CardClient newCard){
        ImageView card = findResourceOfCard(newCard);
        card.setFitHeight(100);
        card.setFitWidth(150);
        Pair<Double,Double> pos = loadPosition(newCard.getPosition().getX(),newCard.getPosition().getY());
        card.setTranslateX(pos.getKey());
        card.setTranslateY(pos.getValue());
        card.setOnMouseClicked(event -> {revealSpots(card.getTranslateX(),card.getTranslateY());});
        board.getChildren().add(card);
    }

    public void revealSpots(double x,double y){
        Button b1 = new Button("Altodx"), b2 = new Button("Altosx"), b3 = new Button("Bassosx"), b4 = new Button("Bassodx");
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
            for(Node node: board.getChildren()){
                ImageView card = (ImageView) node;
                if(card.getTranslateX() == pos.getKey() && card.getTranslateY() == pos.getValue()){
                    isOccupied = true;
                    break;
                }
            }
            if(!isOccupied){
                setupButton(b,pos.getKey(),pos.getValue());
                trueButtons.add(b);
            }
            i++;
        }
        for(Button b: trueButtons){
            b.setOnMouseClicked(event -> handlePositionRequest(b));
        }
        board.getChildren().addAll(trueButtons);
    }

    //UTILITY FUNCTIONS

    public void setPlayerBoards(HashMap<String, Parent> playerBoards) {
        this.playerBoards = playerBoards;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setModel(LittleModel model) {
        this.model = model;
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
            img = new Image("frontCard"+newCard.getId()+".png");
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
            }
        }
        return new ImageView(img);
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

    public void setupButton(Button b,double x,double y){
        b.setTranslateX(x);
        b.setTranslateY(y);
        b.setOpacity(0.2);
        b.setStyle("-fx-background-color: #FFD700;"); //gold color
        b.setPrefWidth(150);
        b.setPrefHeight(100);
    }

    public void handlePositionRequest(Button b){
        ArrayList<ImageView> hand = new ArrayList<>();
        hand.add(hand1);
        hand.add(hand2);
        hand.add(hand3);
        Point pos = inversePosition(b.getTranslateX(),b.getTranslateY());
        int i = 0;
        for(ImageView card: hand){
            int finalI = i;
            card.setOnMouseClicked(event -> {
                board.getChildren().removeIf(item -> item instanceof Button && !item.equals(b));
                lastPlayed = card;
                placeCardRequest(b,finalI,pos);
            });

            i++;
        }
    }

    public void placeCardRequest(Button b,int handIndex, Point position){
        Button b1 = new Button("Front"), b2 = new Button("Back");
        HBox container = new HBox(b1,b2);
        container.setSpacing(15);
        container.setAlignment(Pos.CENTER);
        statusMenu.getChildren().add(container);
        b1.setOnMouseClicked(event -> {
            board.getChildren().remove(b);
            statusMenu.getChildren().removeIf(node -> node instanceof HBox);
            ViewSubmissions.getInstance().placeCard(handIndex,position,true);
        });
        b2.setOnMouseClicked(event -> {
            board.getChildren().remove(b);
            statusMenu.getChildren().removeIf(node -> node instanceof HBox);
            ViewSubmissions.getInstance().placeCard(handIndex,position,false);

        });
    }
}
