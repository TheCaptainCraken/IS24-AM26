package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.view.CardClient;
import it.polimi.ingsw.view.LittleModel;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
* Controller responsible for the handling of the GUI components that are use during the Playing Phase of the game*/
public class MatchController {
    //DATI PER SPOSTAMENTI CARTE
    //118 LUNGO X,60 LUNGO Y (+/-), sommati alla posizione della carta a cui deve essere piazzata la carta

    Controller controller;
    LittleModel model;
    Stage stage;


    @FXML
    ImageView root,hand1,hand2,hand3,common1,common2,secret1,secret2;

    public void showCommonTable(){
        //TODO setuppare hashmap di User/scene, capire quale scena è la propria, capire come inizializzare i vari elementi della view senza scrivere troppo codice

        //model.
    }

    public void setCard(CardClient newCard){

    }

    public void revealSpot(){

    }

    //UTILITY FUNCTIONS

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setModel(LittleModel model) {
        this.model = model;
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public ImageView findResourceOfCard(CardClient newCard){
        if(newCard.getId() < 57 && newCard.getId() >= 17){

        }
    }
}
