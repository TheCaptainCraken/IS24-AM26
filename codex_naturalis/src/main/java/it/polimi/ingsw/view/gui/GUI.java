package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends Application implements InterfaceForView{
    private Controller controller;
    public static LoginController loginController;
    private static Scene scene;
    private Parent root;


    @Override
    public void start(Stage stage) throws IOException {
        loginController = new LoginController();
        root = loadFXML("gui");
        scene = new Scene(root, 1920, 1080);
        loginController.setStage(stage);
        loginController.setController(controller);
        stage.setScene(scene);
        stage.show();
    }

    public  void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private  Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(it.polimi.ingsw.App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }

    public void showInsertNumberOfPlayer(){
         loginController.showInsertNumberOfPlayer();
    }

    @Override
    public void start() {

    }

    @Override
    public void stopWaiting() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void refreshUsers(HashMap<String, Color> playersAndPins) {

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

    }

    @Override
    public void sameName(String nickname) {

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

    }

    @Override
    public void showInsertNumberOfPlayers() {

    }

    public void waitLobby(){
        loginController.waitLobby();
    }

    @Override
    public void correctNumberOfPlayers(int numberOfPlayers) {

    }


}