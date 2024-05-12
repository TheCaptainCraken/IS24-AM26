package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.awt.*;

public abstract class NetworkPlug {

    protected abstract void finalizingNumberOfPlayers() throws NoNameException, SameNameException, LobbyCompleteException;//TODO

    protected abstract void gameIsStarting() throws NoNameException;

    protected abstract void refreshUsers();

    protected abstract void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId, boolean allWithRootCardPlaced) throws NoNameException;//TODO discuss if we have to modify

    protected abstract void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname, boolean allWithSecretObjectiveCardChosen) throws NoNameException;//TODO discuss if we have to modify

    protected abstract void sendPlacedCard(String nickname, int cardId, Point position, boolean side) throws NoNameException;

    protected abstract void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck) throws NoNameException;

    protected abstract void sendEndGame();
}
