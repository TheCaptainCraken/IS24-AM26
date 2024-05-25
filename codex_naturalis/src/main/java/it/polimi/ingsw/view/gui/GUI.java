package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.LittleModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends Application implements InterfaceForView{

    private  Controller controller; //TODO va final una volta finita implementazione (capire come usarlo come singleton)
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


    @Override
    public void start(Stage stage) throws IOException {
        //loginController = new LoginController();
        root = loadFXML("loginView");
        loginController =(LoginController) fxmlLoader.getController();
        this.primaryStage = stage;
        loginController.setStage(stage);
        loginController.setController(controller);
        scene = new Scene(root, 1920, 1080);
        loginController.setup();
        stage.setScene(scene);
        stage.show();
    }

    public void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private  Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(it.polimi.ingsw.App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }

    @Override
    public void showInsertNumberOfPlayers(){
         loginController.showInsertNumberOfPlayers();
    }


    @Override
    public void stopWaiting() {
        loginController.stopWaiting();
    }

    @Override
    public void disconnect() {
        loginController.disconnect();
    }

    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        loginController.refreshUsers(playersAndPins);
    }

    @Override
    public void showCommonTable() {
        try{
            setRoot("matchView");
            matchController = (MatchController) fxmlLoader.getController();
            matchController.setController(controller);
            matchController.setStage(primaryStage);
            matchController.setModel(model);
            matchController.showCommonTable();
        } catch(IOException e){
            System.out.println("Could not load fxml file");
        }
    }


    @Override
    public void showStartingCard(int startingCardId) {

    }

    @Override
    public void showCommonObjectives(Integer[] objectiveCardIds) {

    }

    @Override
    public void showSecretObjectiveCard(int indexCard) {

    }

    @Override
    public void showSecretObjectiveCardsToChoose(Integer[] objectiveCardIds) {

    }

    @Override
    public void showTurnInfo(String currentPlayer, GameState gameState) {

    }

    @Override
    public void showExtraPoints(HashMap<String, Integer> extraPoints) {

    }

    @Override
    public void showRanking(ArrayList<Player> ranking) {

    }

    @Override
    public void showHiddenHand(String nickname) {

    }

    @Override
    public void showHand() {

    }

    @Override
    public void showResourcesPlayer(String name) {

    }

    @Override
    public void showResourcesAllPlayers() {

    }

    @Override
    public void showPoints(HashMap<String, Integer> points) {

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
        loginController.showIsFirst(firstPlayer);
    }


    public void waitLobby(){
        loginController.waitLobby();
    }

    @Override
    public void correctNumberOfPlayers(int numberOfPlayers) {
        loginController.correctNumberOfPlayers(numberOfPlayers);
    }


}