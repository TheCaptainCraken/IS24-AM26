package it.polimi.ingsw.view.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import static it.polimi.ingsw.view.gui.GUI.scene;
/**
 * Controller Class responsible for showing the board of another player
 * during the PlayingPhase of the game.
 *  */

public class OtherPlayerBoardController {

    /**
     * These are the ImageViews for the cards in the other player's hand.
     * They are used to display the images of the cards that the other player has in their hand.
     * They are annotated with @FXML, which means they are injected by the JavaFX FXMLLoader.
     */
    @FXML
    ImageView hand1,hand2,hand3;
    /**
     * This is the StackPane for the other player's board.
     * This field is used to represent the other player's board in the GUI.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    StackPane board;
    /**
     * This is the Button for going back to the previous view.
     * This field is used to trigger the action of going back to the previous view in the GUI.
     * It is annotated with @FXML, which means it is injected by the JavaFX FXMLLoader.
     */
    @FXML
    Button goBack;
    /**
     * This is the Parent for the user's scene, used to go back to its board.
     */
    Parent playerScene;

    /**
     * This method is used to set the Parent for playerScene
     *
     * @param playerScene The Parent object representing the player's scene to be set.
     */

    public void setPlayerScene(Parent playerScene) {
        this.playerScene = playerScene;
    }

    /**
     * This method is used to handle the action of returning to the user's scene.
     * It first sets the user's scene to his board using the setPlayerScene method.
     * Then, it changes the scene of the GUI's stage to the user's scene.
     */
    public void handleReturn(){
        setPlayerScene(GUI.getMatch());
        GUI.getStage().setScene(playerScene.getScene());
    }
}

