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
/**
 * Class responsible for the handling of the GUI components that are used to display
 * the final results of a finished game.
 *  */
public class EndgameHandler {
    /**
     * In the context of the EndgameHandler, it is used to display the winner of the game at the end of the game.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    Label label;
    /**
     * In the context of the EndgameHandler, it is used to display the ranking of players at the end of the game.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    TableView <Player> table;
    /**
     * Each TableColumn represents a column in the TableView that displays the ranking of players at the end of the game.
     * 'players' is used to display the names of the players.
     * 'gamePoints' is used to display the game points of the players.
     * 'objectivePoints' is used to display the objective points of the players.
     * 'total' is used to display the total points of the players, which is the sum of game points and objective points.
     * They are annotated with @FXML, which means they are injected by the JavaFX FXMLLoader.
     */
    @FXML
    TableColumn <Player,String> players,gamePoints,objectivePoints,total;
    /**
     * In the context of the EndgameHandler, it is used to trigger the exit operation of the game.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    Button exit;


    /**
     * This method is used to display the ranking of players at the end of the game.
     * It sets the data in the TableView to reflect the ranking of players.
     * It also sets the cell value factories for the 'players', 'gamePoints', 'objectivePoints', and 'total' columns.
     * The 'total' column is calculated as the sum of game points and objective points for each player.
     * Finally, it sets the text of the label to announce the winner of the game.
     *
     * @param ranking An ArrayList of Player objects, sorted in the order of their ranking.
     */
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
        // mi basta controllare i primi due, se ho pareggi successivi, questi sono già considerati
        if(ranking.get(0).getPoints() == ranking.get(1).getPoints() && ranking.get(0).getObjectivePoints() == ranking.get(1).getObjectivePoints()){
            label.setText("It's a tie!");
        } else {
            label.setText("The Winner is: " + ranking.get(0).getName() + "!");
        }

    }
    /**
     * This method is used to handle the exit operation of the game.
     * It terminates the currently running Java Virtual Machine.
     */
    public void handleExit(){
        System.exit(0);
    }

}
