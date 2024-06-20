package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Player;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;

public class EndgameHandler {
    @FXML
    Label label;
    @FXML
    TableView <Player> table;
    @FXML
    TableColumn <Player,String> players,gamePoints,objectivePoints,total;
    @FXML
    Button exit;

    private HashMap<String,Integer> extraPoints;

    public void setExtraPoints(HashMap<String, Integer> extraPoints) {
        this.extraPoints = extraPoints;
    }


    public void showRanking(ArrayList<Player> ranking){
        ObservableList<Player> data = FXCollections.observableArrayList(ranking);
        table.setItems(data);
        players.setCellValueFactory(new PropertyValueFactory<>("name"));
        gamePoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        objectivePoints.setCellValueFactory(new PropertyValueFactory<>("objectivePoints"));
        total.setCellValueFactory(param -> {
            Player player = param.getValue();
            Integer totalPoints = player.getPoints() + player.getObjectivePoints();
            return new SimpleIntegerProperty(totalPoints).asString();
        });
        label.setText("The Winner is: " + ranking.get(0).getName() + "!");

    }

    public void handleExit(){
        Platform.exit();
    }

}
