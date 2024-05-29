package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.ViewSubmissions;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class GUI extends Application implements ViewInterface {
    private static ViewInterface instance;
    private static final Object lock = new Object();

    private ViewSubmissions viewSubmissions; //TODO va final una volta finita implementazione
    private  LittleModel model; //TODO va final una volta finita implementazione
    private FXMLLoader fxmlLoader;
    public static LoginController loginController;
    public static MatchController matchController;
    private static Scene scene;
    private Stage primaryStage;
    private Parent root;

    /*public GUI(Controller controller, LittleModel model) {
        this.controller = controller;
        this.model = model;
    }*/ //TODO costruttore va usato, metodo launch sarà chiamato DOPO che controller ha creato oggetto GUI

    public static void main(String[] args) {
        Application.launch(GUI.class, args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        //loginController = new LoginController();
        synchronized (lock) {
            instance = this;
            lock.notify();
        }
        //root = loadFXML("loginView");
        root = loadFXML("matchView");
        matchController = (MatchController) fxmlLoader.getController();
        //loginController =(LoginController) fxmlLoader.getController();
        this.primaryStage = stage;
        //loginController.setStage(stage);
        scene = new Scene(root, 1920, 1080);
        //loginController.setup();
        stage.setScene(scene);
        stage.show();
    }

    public void setRoot(String fxml) {
        scene.setRoot(loadFXML(fxml));

    }

    private  Parent loadFXML(String fxml) {
        try{
            fxmlLoader = new FXMLLoader(it.polimi.ingsw.App.class.getResource(fxml + ".fxml"));
            return fxmlLoader.load();

        } catch (IOException e){
            System.out.println("Could not load fxml file");
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
        Platform.runLater(() -> loginController.refreshUsers(playersAndPins));
    }

    @Override
    public void showCommonTable() {
        setRoot("matchView");
        matchController = (MatchController) fxmlLoader.getController();
        matchController.setStage(primaryStage);
        matchController.setModel(model);
        Platform.runLater(()->matchController.showCommonTable());
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

    }

    @Override
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {
        Platform.runLater(() -> matchController.showSecretObjectiveCardsToChoose(objectiveCardIds));
    }

    @Override
    public void showTurnInfo(String currentPlayer, GameState gameState) {

    }

    @Override
    public void showResourcesPlayer() {

    }

    @Override
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {

    }

    @Override
    public void showRanking(ArrayList<Player> ranking) {

    }

    @Override
    public void showTableOfPlayer(String nickname) {

    }

    @Override
    public void showHiddenHand(String nickname) {

    }

    @Override
    public void showHand() {

    }

    @Override
    public void showResourcesAllPlayers() {

    }

    @Override
    public void showPoints() {

    }


    @Override
    public void colorAlreadyTaken() {
        loginController.colorAlreadyTaken();
    }

    @Override
    public void sameName(String nickname) {
        loginController.sameName(nickname);
    }

    @Override
    public void noTurn() {

    }

    @Override
    public void notEnoughResources() {

    }

    @Override
    public void noConnection() {

    }

    @Override
    public void cardPositionError() {

    }

    @Override
    public void lobbyComplete() {

    }

    @Override
    public void wrongGamePhase() {

    }

    @Override
    public void noPlayer() {

    }

    @Override
    public void closingLobbyError() {

    }

    @Override
    public void showIsFirst(String firstPlayer) {
        Platform.runLater(() -> matchController.showIsFirst(firstPlayer));
    }


    @Override
    public void start() {

    }

    public void waitLobby(){
       Platform.runLater(() -> loginController.waitLobby());
    }

    @Override
    public void correctNumberOfPlayers(int numberOfPlayers) {
        Platform.runLater(() -> loginController.correctNumberOfPlayers(numberOfPlayers));
    }


}