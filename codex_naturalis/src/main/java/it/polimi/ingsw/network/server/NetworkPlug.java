package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Kingdom;

import java.awt.*;

public abstract class NetworkPlug {

    protected abstract void finalizingNumberOfPlayers();//TODO

    protected abstract void gameIsStarting();

    protected abstract void refreshUsers();

    protected abstract void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId, boolean allWithRootCardPlaced);//TODO discuss if we have to modify

    protected abstract void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname, boolean allWithSecretObjectiveCardChosen);//TODO discuss if we have to modify

    protected abstract void sendPlacedCard(String nickname, int cardId, Point position, boolean side);

    protected abstract void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck);

    protected abstract void sendEndGame();
}
