package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

import java.util.ArrayList;
import java.util.HashMap;

public class EndgameHandler {
    @FXML
    Label label;
    @FXML
    TableColumn <String,String> players,gamePoints,objectivePoints,total;
    @FXML
    Button exit;

    private HashMap<String,Integer> extraPoints;

    public void setExtraPoints(HashMap<String, Integer> extraPoints) {
        this.extraPoints = extraPoints;
    }


    public void showRanking(ArrayList<Player> ranking){
        label.setText("The Winner is: " + ranking.get(0).getName() + "!");
        for(Player p: ranking){
            players.setText(p.getName());
            gamePoints.setText(Integer.valueOf(p.getPoints()).toString());
            objectivePoints.setText(Integer.valueOf(p.getObjectivePoints()).toString());
            total.setText(Integer.valueOf(p.getPoints() + p.getObjectivePoints()).toString());
        }
    }

    public void handleExit(){
        Platform.exit();
    }


}
