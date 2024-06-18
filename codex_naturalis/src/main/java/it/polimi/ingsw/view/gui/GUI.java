package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Player;
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

public class GUI extends Application implements ViewInterface {
    private static ViewInterface instance;
    private static final Object lock = new Object();


    private LittleModel model;
    private FXMLLoader fxmlLoader;
    public static LoginController loginController;
    public static MatchController matchController;
    public static EndgameHandler endgameHandler;
    static Scene scene;
    private static Stage primaryStage;
    private boolean gameAlreadyStarted = false;
    private Parent root;
    private HashMap<String,Color> playerColors;
    private static Parent match;
    private Parent end;

    @Override
    public void start(Stage stage) throws IOException {

        synchronized (lock) {
            instance = this;
            lock.notify();
        }

        root = loadFXML("loginView");
        loginController =(LoginController) fxmlLoader.getController();
        match = loadFXML("matchView");
        matchController = (MatchController) fxmlLoader.getController();
        end = loadFXML("endgameView");
        endgameHandler = (EndgameHandler) fxmlLoader.getController();
        primaryStage = stage;
        loginController.setStage(stage);
        scene = new Scene(root, 1920, 1080);
        Platform.runLater(() -> loginController.setup());
        stage.setScene(scene);
        stage.show();
    }

    public void setRoot(Parent parent) {
        scene.setRoot(parent);
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static Parent getMatch(){
        return match;
    }

    private Parent loadFXML(String fxml) {
        try{
            fxmlLoader = new FXMLLoader(it.polimi.ingsw.App.class.getResource(fxml + ".fxml"));
            return fxmlLoader.load();

        } catch (IOException e){
            System.out.println("Error loading FXML file");
        }
        return null;
    }

    // Method to get the singleton instance
    public static ViewInterface getInstance() throws InterruptedException {
        synchronized (lock) {
            while (instance == null) {
                lock.wait();
            }
            return instance;
        }
    }
    @Override
    public void askNumberOfPlayers() {
        Platform.runLater(() -> loginController.showInsertNumberOfPlayers());
    }

    @Override
    public void stopWaiting() {
        Platform.runLater(() -> loginController.stopWaiting());
    }

    @Override
    public void disconnect() {
        Platform.runLater(() -> loginController.disconnect());
    }

    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        playerColors = playersAndPins;
        Platform.runLater(() -> loginController.refreshUsers(playersAndPins));
    }

    public void setModel(LittleModel model) {
        this.model = model;
    }

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
            Platform.runLater(()->matchController.showCommonTable());
        } else {
            Platform.runLater(() -> matchController.showCommonTable());
        }
    }


    @Override
    public void showStartingCard(int startingCardId) {
        Platform.runLater(() -> matchController.showStartingCard(startingCardId));
    }

    @Override
    public void showCommonObjectives(Integer[] objectiveCardIds) {
        Platform.runLater(() -> matchController.showCommonObjectives(objectiveCardIds));
    }

    @Override
    public void showSecretObjectiveCard(int indexCard) {
        Platform.runLater(() -> matchController.showSecretObjectiveCard(indexCard));
    }

    @Override
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        Platform.runLater(() -> matchController.showSecretObjectiveCardsToChoose(objectiveCardIds));
    }

    @Override
    public void showTurnInfo(String currentPlayer, GameState gameState) {
        Platform.runLater(() -> matchController.showTurnInfo(currentPlayer, gameState));
    }

    @Override
    public void showResourcesPlayer() {
        Platform.runLater(() -> matchController.showResourcesPlayer());
    }

    @Override
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {
        Platform.runLater(() -> endgameHandler.setExtraPoints(extraPoints));
        Platform.runLater(() -> matchController.gameIsEnding());
    }

    @Override
    public void showRanking(ArrayList<Player> ranking) {
        Platform.runLater(() -> {
            Scene scene1 = new Scene(end,500,400);
            endgameHandler.showRanking(ranking);
            primaryStage.setScene(scene1);
        });
    }

    @Override
    public void showTableOfPlayer(String nickname) {
        Platform.runLater(() -> matchController.showTableOfPlayer(nickname));
    }

    @Override
    public void showHiddenHand(String nickname) {
        Platform.runLater(() -> matchController.showHiddenHand(nickname));
    }

    @Override
    public void showHand() {
        Platform.runLater(() -> matchController.showHand());
    }

    @Override
    public void showPoints() {
        Platform.runLater(() -> matchController.showPoints());
    }


    @Override
    public void colorAlreadyTaken() {
        Platform.runLater(() -> loginController.colorAlreadyTaken());
    }

    @Override
    public void sameName(String nickname) {
        Platform.runLater(() -> loginController.sameName(nickname));
    }

    @Override
    public void noTurn() {
        Platform.runLater(() -> matchController.noTurn() );
    }

    @Override
    public void notEnoughResources() {
        Platform.runLater(() -> matchController.notEnoughResources());
    }

    @Override
    public void noConnection() {
        if(gameAlreadyStarted){
            Platform.runLater(() -> matchController.noConnection());
        } else{
            Platform.runLater(() -> loginController.noConnection());
        }
    }

    @Override
    public void cardPositionError() {
        Platform.runLater(() -> matchController.cardPositionError());
    }

    @Override
    public void lobbyComplete() {
        Platform.runLater(() ->loginController.lobbyComplete());
    }

    @Override
    public void wrongGamePhase() {
        Platform.runLater(() -> matchController.wrongGamePhase());
    }

    @Override
    public void noPlayer() {
        //TODO
        //???? errore che non dovrebbe mai verificarsi. Però da alucne parti è chiamato.
        //stampa un messagggio semplice. Tipo non conosco il giocatore
    }

    @Override
    public void closingLobbyError() {
        Platform.runLater(() ->loginController.closingLobbyError());
    }

    @Override
    public void showStartingCardChosen() {
        Platform.runLater(() -> matchController.showStartingCard());
    }

    @Override
    public void stopGaming() {
        //TODO
    }

    @Override
    public void showIsFirst(String firstPlayer) {
        Platform.runLater(() -> matchController.showIsFirst(firstPlayer));
    }

    public void waitLobby(){
       Platform.runLater(() -> loginController.waitLobby());
    }

    @Override
    public void correctNumberOfPlayers(int numberOfPlayers) {
        Platform.runLater(() -> loginController.correctNumberOfPlayers(numberOfPlayers));
    }

    @Override
    public void receiveMessage(String sender, String message) {
        //TODO chat
    }
}