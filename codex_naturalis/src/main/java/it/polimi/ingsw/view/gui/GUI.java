package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.model.CardClient;
import it.polimi.ingsw.view.model.LittleModel;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.ViewSubmissions;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
/**
 * This is the main GUI class for the application.
 * It extends the Application class from JavaFX and implements the ViewInterface.
 * It manages the GUI's lifecycle, including the start of the application, and the loading of various FXML files.
 * It also handles the communication between the GUI and the game logic by implementing various methods from the ViewInterface.
 *
 * The class uses the Singleton pattern to ensure that only one instance of the GUI is created during the application's lifecycle.
 *
 * The class contains references to various controllers (LoginController, MatchController, EndgameHandler) which manage different parts of the GUI.
 *
 * The class also contains a method for each action that can be performed in the GUI, such as showing the common table, showing the starting card, showing the common objectives, etc.
 * These methods are typically called from the game logic to update the GUI.
 */

public class GUI extends Application implements ViewInterface {
    /**
     * This is a static instance of the ViewInterface in the GUI class.
     * It is used to implement the Singleton pattern, ensuring that only one instance of the GUI class is created during the application's lifecycle.
     * This instance is used to access the methods of the GUI class from other classes.
     */
    private static ViewInterface instance;
    /**
     * This is a static final Object used as a lock for thread synchronization in the GUI class.
     * It is used in conjunction with the 'synchronized' keyword to control access to shared resources, in this case, the 'instance' field.
     * This ensures that only one thread can execute the code block guarded by this lock at a time, preventing race conditions.
     */
    private static final Object lock = new Object();
    /**
     * This is an instance of the LittleModel class in the GUI class.
     * The LittleModel class is a simplified representation of the game model, suitable for GUI rendering.
     * This instance is used to store and manage the game state information that is necessary for the GUI to display.
     */
    private LittleModel model;
    /**
     * This is an instance of the FXMLLoader class in the GUI class.
     * FXMLLoader is a class in the JavaFX library that loads an object hierarchy from an XML document.
     * This instance is used to load the various FXML files that define the GUI layout and controls.
     */
    private FXMLLoader fxmlLoader;
    /**
     * This is a static instance of the LoginController class in the GUI class.
     * LoginController is a class that manages the login view of the application.
     * This instance is used to control the login view, including handling user inputs and updating the view based on changes in the application state.
     */
    public static LoginController loginController;
    /**
     * This is a static instance of the MatchController class in the GUI class.
     * MatchController is a class that manages the match view of the application.
     * This instance is used to control the match view, including handling user inputs, updating the view based on changes in the game state, and managing the game flow.
     */
    public static MatchController matchController;
    /**
     * This is a static instance of the EndgameHandler class in the GUI class.
     * EndgameHandler is a class that manages the endgame view of the application.
     * This instance is used to control the endgame view, including displaying the final results and rankings of the game.
     */
    public static EndgameHandler endgameHandler;
    /**
     * This is a static instance of the Scene class in the GUI class.
     * Scene is a class in the JavaFX library that holds all the graphical elements in a JavaFX application.
     * This instance is used to hold and manage the graphical elements that make up the GUI.
     */
    static Scene scene;
    /**
     * This is a static instance of the Stage class in the GUI class.
     * Stage is a top-level JavaFX container that hosts the scene graph of UI controls.
     * This instance is used as the primary stage where other scenes (views) are set and displayed.
     */
    private static Stage primaryStage;
    /**
     * This is a boolean field in the GUI class.
     * It is used to track whether the game has already started or not.
     * When the game starts, this field is set to true.
     */
    private boolean gameAlreadyStarted = false;
    /**
     * This is a HashMap field in the GUI class.
     * It is used to store the colors of the players in the game.
     * The key is the player's nickname, and the value is the player's color.
     */
    private HashMap<String,Color> playerColors;
    /**
     * This is a static Parent field in the GUI class.
     * Parent is a class in the JavaFX library that represents the base class for all nodes that have children in the scene graph.
     * This field is used to store the match view, which is the main view of the application where the game is played.
     */
    private static Parent match;
    /**
     * This is a static Parent field in the GUI class.
     * This field is used to store the endgame view, which is displayed at the end of the game to show the final results and rankings.
     */
    private Parent end;
    /**
     * This is a static Parent field in the GUI class.
     * This field is used to store the shutdown view, which is displayed when the game is stopped or closed by a disconnection.
     */
    private Parent shutdown;

    /**
     * This is the start method of the Application class in the GUI class.
     * It is overridden from the Application class in JavaFX.
     * This method is called when the application is launched.
     *
     * The method initializes the GUI by loading the various FXML files that define the GUI layout and controls.
     * It also sets up the primary stage of the application, including setting the scene, and showing the stage.
     * The method also sets up the singleton instance of the GUI class.
     *
     * @param stage The primary stage for this application, onto which the application scene can be set.
     * @throws IOException If an input or output exception occurred
     */
    @Override
    public void start(Stage stage) throws IOException {

        synchronized (lock) {
            instance = this;
            lock.notify();
        }

        Parent root = loadFXML("loginView");
        loginController =(LoginController) fxmlLoader.getController();
        match = loadFXML("matchView");
        matchController = (MatchController) fxmlLoader.getController();
        end = loadFXML("endgameView");
        endgameHandler = (EndgameHandler) fxmlLoader.getController();
        shutdown = loadFXML("shutdownView");

        stage.setOnCloseRequest(we -> System.exit(0));
        primaryStage = stage;
        loginController.setStage(stage);
        scene = new Scene(root, 1920, 1080);
        Platform.runLater(() -> loginController.setup());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This is a static method in the GUI class.
     * It returns the primary stage of the application.
     * This method is used to retrieve the primary stage from other classes.
     *
     * @return The primary stage of the application.
     */
    public static Stage getStage() {
        return primaryStage;
    }

    /**
     * This is a static method in the GUI class.
     * It returns the match view of the application.
     * The match view is the main view of the application where the game is played.
     * This method is used to retrieve the match view from other classes.
     *
     * @return The match view of the application.
     */
    public static Parent getMatch(){
        return match;
    } //serve per otherPlayerController

    /**
     * This is a method in the GUI class.
     * It is used to load an FXML file that defines the layout and controls of a view in the GUI.
     * The name of the FXML file to load is passed as a parameter to the method.
     * If the FXML file is successfully loaded, the method returns the root of the object hierarchy defined in the FXML file.
     * If an IOException occurs during the loading of the FXML file, the method prints an error message and returns null.
     *
     * @param fxml The name of the FXML file to load.
     * @return The root of the object hierarchy defined in the FXML file, or null if an IOException occurred.
     */
    private Parent loadFXML(String fxml) {
        System.out.println("Loading the file: " + fxml );
        try{
            fxmlLoader = new FXMLLoader(it.polimi.ingsw.App.class.getResource(fxml + ".fxml"));
            return fxmlLoader.load();

        } catch (IOException e){
            System.out.println("Error loading FXML file");
        }
        return null;
    }

    /**
     * This is a static method in the GUI class.
     * It returns the singleton instance of the GUI class.
     * If the instance is not yet created, it waits until the instance is created.
     * This method is used to retrieve the singleton instance of the GUI class from other classes.
     *
     * @return The singleton instance of the GUI class.
     * @throws InterruptedException If any thread has interrupted the current thread. The interrupted status of the current thread is cleared when this exception is thrown.
     */
    public static ViewInterface getInstance() throws InterruptedException {
        synchronized (lock) {
            while (instance == null) {
                lock.wait();
            }
            return instance;
        }
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to prompt the user to input the number of players for the game.
     * The actual task is delegated to the LoginController's showInsertNumberOfPlayers method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void askNumberOfPlayers() {
        Platform.runLater(() -> loginController.showInsertNumberOfPlayers());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to stop the waiting process in the login view.
     * The actual task is delegated to the LoginController's stopWaiting method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void stopWaiting() {
        Platform.runLater(() -> loginController.stopWaiting());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the disconnection process in the application when the player has been kicked out of a full lobby.
     * The actual task is delegated to the LoginController's disconnect method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void disconnect() {
        Platform.runLater(() -> loginController.disconnect());
    }

    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to update the player colors in the GUI.
     * The method receives a HashMap where the keys are player nicknames and the values are their corresponding colors.
     * The HashMap is then stored in the 'playerColors' field and used to refresh the users in the LoginController.
     * The actual task is delegated to the LoginController's refreshUsers method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param playersAndPins A HashMap where the keys are player nicknames and the values are their corresponding colors.
     */
    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        playerColors = playersAndPins;
        Platform.runLater(() -> loginController.refreshUsers(playersAndPins));
    }
    /**
     * This method is part of the GUI class.
     * It is used to set the LittleModel instance for the GUI.
     * The LittleModel class is a simplified representation of the game model, suitable for GUI rendering.
     * This instance is used to store and manage the game state information that is necessary for the GUI to display.
     *
     * @param model The LittleModel instance to be set for the GUI.
     */
    public void setModel(LittleModel model) {
        this.model = model;
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the common table/user's play area in the GUI.
     * If the game has not started yet, it sets up the match view, including setting player colors, player boards, stage, model, and scene.
     * It also sets the gameAlreadyStarted field to true.
     * If the game has already started, it simply displays the updated common table.
     * The actual tasks are delegated to the MatchController's setup and showCommonTable methods.
     * The tasks are wrapped inside Platform.runLater to ensure that they run on the JavaFX Application Thread,
     * as they involve GUI operations which need to be thread-safe.
     */
    @Override
    public void showCommonTable() {
        if(!gameAlreadyStarted){
            gameAlreadyStarted = true;
            ArrayList<String> players = new ArrayList<>(model.getTable().keySet());
            HashMap<String,Scene> playerBoards = new HashMap<>();
            for (String player : players) {
                if(!player.equals(ViewSubmissions.getInstance().getNickname())){
                    Scene scene = new Scene(Objects.requireNonNull(loadFXML("otherPlayerView")),1920,1080);
                    playerBoards.put(player,scene);
                }
            }
            scene.setRoot(match);
            matchController.setPlayerColors(playerColors);
            matchController.setPlayerBoards(playerBoards);
            matchController.setStage(primaryStage);
            matchController.setModel(model);
            matchController.setScene(scene);
            Platform.runLater(() -> matchController.setup());
            Platform.runLater(()-> matchController.showCommonTable());
        } else {
            Platform.runLater(() -> matchController.showCommonTable());
        }
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the starting card in the GUI.
     * The method receives an integer representing the ID of the starting card.
     * The actual task is delegated to the MatchController's showStartingCard method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param startingCardId An integer representing the ID of the starting card.
     */
    @Override
    public void showStartingCard(int startingCardId) {
        Platform.runLater(() -> matchController.showStartingCard(startingCardId));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the common objectives in the GUI.
     * The method receives an array of integers representing the IDs of the common objective cards.
     * The actual task is delegated to the MatchController's showCommonObjectives method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param objectiveCardIds An array of integers representing the IDs of the common objective cards.
     */
    @Override
    public void showCommonObjectives(Integer[] objectiveCardIds) {
        Platform.runLater(() -> matchController.showCommonObjectives(objectiveCardIds));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the secret objective card chosen by the user in the GUI.
     * The method receives an integer representing the ID of the secret objective card.
     * The actual task is delegated to the MatchController's showSecretObjectiveCard method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param indexCard An integer representing the ID of the secret objective card.
     */
    @Override
    public void showSecretObjectiveCard(int indexCard) {
        Platform.runLater(() -> matchController.showSecretObjectiveCard(indexCard));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the secret objective cards for the user to choose in the GUI.
     * The method receives an array of integers representing the IDs of the secret objective cards.
     * The actual task is delegated to the MatchController's showSecretObjectiveCardsToChoose method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param objectiveCardIds An array of integers representing the IDs of the secret objective cards.
     */
    @Override
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        Platform.runLater(() -> matchController.showSecretObjectiveCardsToChoose(objectiveCardIds));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the current turn information in the GUI.
     * The method receives a string representing the current player and a GameState object representing the current game state.
     * The actual task is delegated to the MatchController's showTurnInfo method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param currentPlayer A string representing the current player.
     * @param gameState A GameState object representing the current game state.
     */
    @Override
    public void showTurnInfo(String currentPlayer, GameState gameState) {
        Platform.runLater(() -> matchController.showTurnInfo(currentPlayer, gameState));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the resources of the players in the GUI.
     * The actual task is delegated to the MatchController's showResourcesPlayer method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void showResourcesPlayer() {
        Platform.runLater(() -> matchController.showResourcesPlayer());
    }

    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to signal the end of the game in the GUI.
     * The method receives a HashMap where the keys are player nicknames and the values are their extra points.
     * However, the extra points are not used by the GUI, it was done this way to respect the signature found in the interface .
     * The actual task is delegated to the MatchController's gameIsEnding method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param extraPoints A HashMap where the keys are player nicknames and the values are their extra points.
     */
    @Override
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        //extra points is not used by the GUI
        Platform.runLater(() -> matchController.gameIsEnding());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the final ranking of players at the end of the game in the GUI.
     * The method receives an ArrayList of Player objects representing the final ranking of players.
     * The actual task is delegated to the EndgameHandler's showRanking method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param ranking An ArrayList of Player objects representing the final ranking of players.
     */
    @Override
    public void showRanking(ArrayList<Player> ranking) {
        Platform.runLater(() -> {
            Scene scene1 = new Scene(end,500,400);
            endgameHandler.showRanking(ranking);
            primaryStage.setScene(scene1);
        });
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to update the table (game board) of a specific player in the GUI.
     * The method receives a string representing the nickname of the player whose table is to be updated.
     * The actual task is delegated to the MatchController's showTableOfPlayer method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param nickname A string representing the nickname of the player whose table is to be updated.
     */
    @Override
    public void showTableOfPlayer(String nickname) {
        Platform.runLater(() -> matchController.showTableOfPlayer(nickname));
    }

    /**
     * This method is used to rebuild the game board view in the GUI.
     * It is called when the game state changes and the board view needs to be updated.
     * The method receives a string representing the name of the player whose board is to be updated,
     * and a CardClient object representing the card to be placed on the board.
     * The actual task of updating the board view is delegated to the MatchController's rebuildView method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param name A string representing the name of the player whose board is to be updated.
     * @param card A CardClient object representing the card to be placed on the board.
     */
    public void rebuildBoard(String name, CardClient card){
        Platform.runLater(() -> matchController.rebuildView(card, name));
    }

    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to update the hidden hand of a specific player in the GUI.
     * The method receives a string representing the nickname of the player whose hidden hand is to be updated.
     * The actual task is delegated to the MatchController's showHiddenHand method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param nickname A string representing the nickname of the player whose hidden hand is to be updated.
     */
    @Override
    public void showHiddenHand(String nickname) {
        Platform.runLater(() -> matchController.showHiddenHand(nickname));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to update the hand of the user in the GUI.
     * The actual task is delegated to the MatchController's showHand method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void showHand() {
        Platform.runLater(() -> matchController.showHand());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the updated points of all the players in the GUI.
     * The actual task is delegated to the MatchController's showPoints method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void showPoints() {
        Platform.runLater(() -> matchController.showPoints());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that the chosen color is already taken by another player.
     * The actual task is delegated to the LoginController's colorAlreadyTaken method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void colorAlreadyTaken() {
        Platform.runLater(() -> loginController.colorAlreadyTaken());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that the chosen nickname is already taken by another player.
     * The actual task is delegated to the LoginController's sameName method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void sameName(String nickname) {
        Platform.runLater(() -> loginController.sameName(nickname));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that they cannot perform an action because it's not their turn.
     * The actual task is delegated to the MatchController's noTurn method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void noTurn() {
        Platform.runLater(() -> matchController.noTurn() );
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that they cannot perform an action because they don't have enough resources.
     * The actual task is delegated to the MatchController's notEnoughResources method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void notEnoughResources() {
        Platform.runLater(() -> matchController.notEnoughResources());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user about a connection issue.
     * If the game has already started, the notification is handled by the MatchController's noConnection method.
     * If the game has not started yet, the notification is handled by the LoginController's noConnection method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void noConnection() {
        if(gameAlreadyStarted){
            Platform.runLater(() -> matchController.noConnection());
        } else{
            Platform.runLater(() -> loginController.noConnection());
        }
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that they cannot perform an action because the card position is invalid.
     * The actual task is delegated to the MatchController's cardPositionError method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void cardPositionError() {
        Platform.runLater(() -> matchController.cardPositionError());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that the game lobby is complete and the game can start.
     * The actual task is delegated to the LoginController's lobbyComplete method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void lobbyComplete() {
        Platform.runLater(() ->loginController.lobbyComplete());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that they cannot perform an action because the game is in the wrong phase.
     * The actual task is delegated to the MatchController's wrongGamePhase method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void wrongGamePhase() {
        Platform.runLater(() -> matchController.wrongGamePhase());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that there is no player with the given name.
     * If the game has already started, the notification is handled by the MatchController's noPlayer method.
     * If the game has not started yet, the notification is handled by the LoginController's noPlayer method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void noPlayer() {
        if(gameAlreadyStarted){
            Platform.runLater(() -> matchController.noPlayer());
        } else{
            Platform.runLater(() -> loginController.noPlayer());
        }
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user that the game lobby cannot be closed due to an error.
     * The actual task is delegated to the LoginController's closingLobbyError method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void closingLobbyError() {
        Platform.runLater(() ->loginController.closingLobbyError());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display the starting card chosen by the user in the GUI.
     * The actual task is delegated to the MatchController's showStartingCard method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void showStartingCardChosen() {
        Platform.runLater(() -> matchController.showStartingCard());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to handle the situation when the game is stopped, for example due to a player disconnection.
     * The method changes the current scene to a shutdown scene, indicating the end of the game.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void stopGaming() {
        Platform.runLater(()->{
            Scene scene1 = new Scene(shutdown,600,400);
            primaryStage.setScene(scene1);
        });
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to display who plays first in the GUI.
     * The method receives a string representing the nickname of the first player.
     * The actual task is delegated to the MatchController's showIsFirst method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param firstPlayer A string representing the nickname of the first player.
     */
    @Override
    public void showIsFirst(String firstPlayer) {
        Platform.runLater(() -> matchController.showIsFirst(firstPlayer));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user to wait in the game lobby until he can choose his color.
     * The actual task is delegated to the LoginController's waitLobby method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     */
    @Override
    public void waitLobby(){
       Platform.runLater(() -> loginController.waitLobby());
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to notify the user about the correct number of players in the game.
     * The method receives an integer representing the number of players.
     * The actual task is delegated to the LoginController's correctNumberOfPlayers method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param numberOfPlayers An integer representing the number of players in the game.
     */
    @Override
    public void correctNumberOfPlayers(int numberOfPlayers) {
        Platform.runLater(() -> loginController.correctNumberOfPlayers(numberOfPlayers));
    }
    /**
     * This method is part of the ViewInterface implemented by the GUI class.
     * It is used to handle the reception of chat messages in the GUI.
     * The method receives a string representing the sender of the message, the message itself, and a boolean indicating if the message is broadcasted to all players.
     * The actual task is delegated to the MatchController's receiveChatMessage method.
     * The task is wrapped inside Platform.runLater to ensure that it runs on the JavaFX Application Thread,
     * as it involves a GUI operation which needs to be thread-safe.
     *
     * @param sender A string representing the sender of the message.
     * @param message A string representing the message.
     * @param broadcast A boolean indicating if the message is broadcasted to all players.
     */
    @Override
    public void receiveChatMessage(String sender, String message, boolean broadcast) {
        Platform.runLater(() -> matchController.receiveChatMessage(sender, message, broadcast));
    }
}