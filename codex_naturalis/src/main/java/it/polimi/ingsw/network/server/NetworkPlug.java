package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Kingdom;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NoNameException;
import it.polimi.ingsw.model.exception.SameNameException;

import java.awt.*;

public interface NetworkPlug {

      void finalizingNumberOfPlayers() throws NoNameException, SameNameException, LobbyCompleteException;

      void gameIsStarting() throws NoNameException;

      void refreshUsers();

      void sendingPlacedRootCardAndWhenCompleteObjectiveCards(String nickname, boolean side, int cardId,
                  boolean allWithRootCardPlaced) throws NoNameException;// TODO discuss if we have to modify

      void sendingHandsAndWhenSecretObjectiveCardsCompleteStartGameFlow(String nickname,
                  boolean allWithSecretObjectiveCardChosen) throws NoNameException;// TODO discuss if we have to modify

      void sendPlacedCard(String nickname, int cardId, Point position, boolean side) throws NoNameException;

      void sendDrawnCard(String nickname, int newCardId, Kingdom headDeck, boolean gold, int onTableOrDeck)
                  throws NoNameException;

      void sendEndGame();
}
