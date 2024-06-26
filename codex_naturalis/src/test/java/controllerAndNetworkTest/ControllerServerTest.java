package controllerAndNetworkTest;

import it.polimi.ingsw.controller.server.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;


public class ControllerServerTest {
        //il base path è differente per entrambi(test e non test) quindi non c'è bisogno di fare il controllo
        static String basePath = "src/main/java/it/polimi/ingsw/model/decks/";

        @BeforeEach
        public void setUp() {
            Controller.getInstance().reset();
        }

        @Test
        @DisplayName("Test that two equals names cannot be in the same lobby.")
        public void equalNames() throws SameNameException, LobbyCompleteException {
            Controller.getInstance().addPlayer("pippo");
            Assertions.assertThrows(SameNameException.class, () -> {
                Controller.getInstance().addPlayer("pippo");
            });
        }

        @Test
        @DisplayName("Test that two people cannot choose the same color.")
        public void equalColor() throws SameNameException, LobbyCompleteException, ClosingLobbyException, ColorAlreadyTakenException, NoNameException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Controller.getInstance().initializeLobby(2);

            Controller.getInstance().setColourAndGameIsReadyToStart("pippo", Color.BLUE);
            Assertions.assertThrows(ColorAlreadyTakenException.class, () -> {
                Controller.getInstance().setColourAndGameIsReadyToStart("pluto", Color.BLUE);
            });
        }

        @Test
        public void setColor() throws ColorAlreadyTakenException, NoNameException, ClosingLobbyException, SameNameException, LobbyCompleteException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Controller.getInstance().initializeLobby(2);

            Controller.getInstance().setColourAndGameIsReadyToStart("pippo", Color.BLUE);
            Controller.getInstance().setColourAndGameIsReadyToStart("pluto", Color.RED);
            Assertions.assertEquals(Color.RED, Controller.getInstance().getLobby().getPlayers()[1].getColor());
            Assertions.assertEquals(Color.BLUE, Controller.getInstance().getLobby().getPlayers()[0].getColor());
        }

        @Test
        @DisplayName("Test that the lobby is closed when it is full.")
        public void lobbyComplete() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Assertions.assertFalse(Controller.getInstance().lobbyIsReady());
            Controller.getInstance().initializeLobby(2);
            Assertions.assertThrows(LobbyCompleteException.class, () -> {
                Controller.getInstance().addPlayer("paperino");
            });
        }

        @Test
        @DisplayName("Test that the lobby is closed when it is full. Different order.")
        public void lobbyComplete1() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Controller.getInstance().initializeLobby(3);
            //lobby not ready yet, one player is missing
            Assertions.assertFalse(Controller.getInstance().lobbyIsReady());
            Controller.getInstance().addPlayer("paperino");

            Assertions.assertTrue(Controller.getInstance().lobbyIsReady());
            Assertions.assertThrows(LobbyCompleteException.class, () -> {
                Controller.getInstance().addPlayer("topolino");
            });
        }

        @Test
        @DisplayName("Test that the lobby cancel the players nota admitted.")
        public void lobbyDisconnect() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Controller.getInstance().addPlayer("paperino");
            Controller.getInstance().initializeLobby(2);

            Assertions.assertThrows(NoNameException.class, () -> {
                Controller.getInstance().getLobby().getPlayerFromName("paperino");
            });
            Assertions.assertEquals(2, Controller.getInstance().getLobby().getPlayers().length);
        }

        @Test
        @DisplayName("Test that only one person can choose the number of players in the lobby.")
        public void cheatIsFirst() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Controller.getInstance().initializeLobby(2);

            //time error
            Assertions.assertThrows(ClosingLobbyException.class, () -> {
                Controller.getInstance().initializeLobby(3);
            });

            //input error
            Assertions.assertThrows(ClosingLobbyException.class, () -> {
                Controller.getInstance().initializeLobby(10);
            });

            Assertions.assertThrows(ClosingLobbyException.class, () -> {
                Controller.getInstance().initializeLobby(-4);
            });
        }

        @Test
        @DisplayName("Test that the lobby is ready.")
        public void isLobbyReady() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Controller.getInstance().initializeLobby(2);
            Assertions.assertTrue(Controller.getInstance().lobbyIsReady());
        }

        @Test
        @DisplayName("Test that the gameMaster is not ready.")
        public void isGameMasterReady() throws SameNameException, LobbyCompleteException, ClosingLobbyException, ColorAlreadyTakenException, NoNameException {
            Controller.getInstance().addPlayer("pippo");
            Controller.getInstance().addPlayer("pluto");
            Controller.getInstance().initializeLobby(2);

            Assertions.assertFalse(Controller.getInstance().setColourAndGameIsReadyToStart("pippo", Color.BLUE));
            Assertions.assertTrue(Controller.getInstance().setColourAndGameIsReadyToStart("pluto", Color.RED));

        }

}
