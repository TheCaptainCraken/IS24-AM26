package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.view.CardClient;
import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.ViewSubmissions;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

/**
* Controller responsible for the handling of the GUI components that are use during the Playing Phase of the game*/
public class MatchController {
    //DATI PER SPOSTAMENTI CARTE
    //118 LUNGO X,60 LUNGO Y (+/-), sommati alla posizione della carta a cui deve essere piazzata la carta
    //Altezza carta 100, lunghezza 150

    private final int X = 118;
    private final int Y = 60;
    private boolean root_side = true;

    LittleModel model;
    Stage stage;


    @FXML
    ImageView root,hand1,hand2,hand3,common1,common2,secret1,secret2,res_deck,gold_deck,gold1,gold2,res1,res2;
    @FXML
    Label status;
    @FXML
    VBox playerContainer,statusMenu;
    @FXML
    HBox secretContainer;
    @FXML
    StackPane board;
    @FXML
    Button debug;

    public void showCommonTable(){
        //TODO setuppare hashmap di User/scene, capire quale scena è la propria, capire come inizializzare i vari elementi della view senza scrivere troppo codice
        int j = 3;

        for(int i = 4; i > model.getPlayersAndCardsNumber().size(); i--){
            playerContainer.getChildren().remove(j);
            j--;
        }
        ArrayList<Label> labels = new ArrayList<>();
        for(Node node: playerContainer.getChildren()){
            if(node instanceof VBox){
                for(Node n: ((VBox) node).getChildren()){
                    if(n instanceof HBox){
                        for(Node n1: ((HBox) n).getChildren()){
                            if(n1 instanceof Label){
                                labels.add((Label) n1);
                            }
                        }
                    }
                }
            }
        } //easily one of the worst things ive ever done
        int i = 0;
        for(String s: model.getPlayersAndCardsNumber().keySet()){
            labels.get(i).setText(s);
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
            root.setOnMouseClicked(null);
            root.setImage(loadStartingCardResource(id,true));
            ViewSubmissions.getInstance().chooseStartingCard(true);
        });
        b2.setOnMouseClicked(event -> {
            status.setText("Please wait for the other players...");
            statusMenu.getChildren().removeIf(node -> node instanceof HBox);
            root.setOnMouseClicked(null);
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
            secretContainer.getChildren().remove(secret2);
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(0);
        });
        secret2.setOnMouseClicked(event -> {
            status.setText("Please wait for the other players...");
            statusMenu.getChildren().removeIf(node -> node instanceof HBox);
            secretContainer.getChildren().remove(secret1);
            secret2.setOnMouseClicked(null);
            ViewSubmissions.getInstance().chooseSecretObjectiveCard(1);
        });
    }

    public void showIsFirst(String name){
        status.setText("The first player is " + name +".\nThe game is about to start!");
    }

    public void handleDebug(){
        //showStartingCard(99);
        Integer[] obj = new Integer[2];
        obj[0] = 1;
        obj[1] = 2;
        showCommonObjectives(obj);
        showSecretObjectiveCardsToChoose(obj);
    }

    public void setCard(CardClient newCard){
        ImageView card = findResourceOfCard(newCard);
        card.setFitHeight(100);
        card.setFitWidth(150);
        double x = 0;
        double y = 0;
        x = X * newCard.getPosition().getX();
        y = -Y * newCard.getPosition().getX();
        x = x + -X * newCard.getPosition().getY();
        y = y + -Y * newCard.getPosition().getY();
        card.setX(x);
        card.setY(y);
        card.setOnMouseClicked(event -> {revealSpots(card.getX(),card.getY());});
        board.getChildren().add(card);
    }

    public void revealSpots(double x,double y){
        Button b1 = new Button("Altodx"), b2 = new Button("Altosx"), b3 = new Button("Bassodx"), b4 = new Button("Bassosx");
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
    }

    //UTILITY FUNCTIONS

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setModel(LittleModel model) {
        this.model = model;
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
}
