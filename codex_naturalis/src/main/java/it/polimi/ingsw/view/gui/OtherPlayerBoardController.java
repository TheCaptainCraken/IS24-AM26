package it.polimi.ingsw.view.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import static it.polimi.ingsw.view.gui.GUI.scene;

public class OtherPlayerBoardController {

    @FXML
    Label playerName;
    @FXML
    ImageView hand1,hand2,hand3,root;
    @FXML
    StackPane board;
    @FXML
    Button goBack;
    Parent playerScene;

    public void setPlayerScene(Parent playerScene) {
        this.playerScene = playerScene;
    }

    public void handleReturn(){
        setPlayerScene(GUI.getMatch());
        GUI.getStage().setScene(playerScene.getScene());
    }
}

