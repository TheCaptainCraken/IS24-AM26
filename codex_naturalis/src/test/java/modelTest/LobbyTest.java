package modelTest;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class LobbyTest {
    private static Lobby lobby;

    @Test
    @DisplayName("check correct function when lobby is complete")
    public void CheckCorrectFunctionLobbyComplete(){
        Assertions.assertThrows(SameNameException.class, () -> {
            Lobby lobby = new Lobby();
            lobby.addPlayer("pietro");
            lobby.addPlayer("marco");
            lobby.addPlayer("daniel");
            lobby.addPlayer("arturo");
            lobby.addPlayer("arturo");
        });
    }

    @BeforeAll
    public static void Setup() throws  SameNameException, LobbyCompleteException {
        lobby = new Lobby();
        lobby.addPlayer("pietro");
        lobby.addPlayer("marco");
        lobby.addPlayer("daniel");
        lobby.addPlayer("arturo");
    }

    @Test
    @DisplayName("Search player")
    public void SearchPlayerTest() throws  NoNameException {
        Player p1 = lobby.getPlayerFromName("pietro");
        Assertions.assertEquals("pietro", p1.getName());

        Player p2 = lobby.getPlayerFromName("marco");
        Assertions.assertEquals("marco", p2.getName());

        Player p3 = lobby.getPlayerFromName("daniel");
        Assertions.assertEquals("daniel", p3.getName());
        Assertions.assertThrows(NoNameException.class, () -> {
            lobby.getPlayerFromName("mario");
        });
        Assertions.assertTrue(lobby.isAdmitted("pietro"));
        Assertions.assertFalse(lobby.isAdmitted("mario"));

    }

    @Test
    @DisplayName("SetLock() is called")
    public void SetLockTestAndMaxSizeTest() throws LobbyCompleteException, SameNameException, ClosingLobbyException {
        Lobby lobby = new Lobby();
        Assertions.assertDoesNotThrow(() -> {lobby.setMaxSize(4);});
        Assertions.assertThrows(ClosingLobbyException.class, () -> {
            lobby.setMaxSize(5);
        });
        lobby.setMaxSize(2);
        lobby.addPlayer("pietro");
        lobby.addPlayer("marco");
        assertTrue(lobby.getLock());
       Assertions.assertThrows(LobbyCompleteException.class, () -> {
           lobby.addPlayer("daniel");});

    }
    @Test
    @DisplayName("Test PlayerColors")
    public void getPlayersAndPinsTest() throws SameNameException, LobbyCompleteException {
        Player p1 = new Player("pietro"), p2 = new Player("marco"), p3 = new Player("daniel"), p4 = new Player("arturo");
        p1.setColour(Color.BLUE);
        p2.setColour(Color.GREEN);
        p3.setColour(Color.RED);
        p4.setColour(Color.YELLOW);
        Lobby l = new Lobby();
        l.addPlayer(p1.getName());
        l.addPlayer(p2.getName());
        l.addPlayer(p3.getName());
        l.addPlayer(p4.getName());
        HashMap<String,Color> map = l.getPlayersAndPins();
        assertNotNull(map);
    }

    @Test
    @DisplayName("Test isReady")
    public void lobbyIsReadyTest() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
        Lobby lobby = new Lobby();
        lobby.setMaxSize(2);
        lobby.addPlayer("pietro");
        assertFalse(lobby.isReady());
        lobby.addPlayer("marco");
        assertTrue(lobby.isReady());
    }

    @Test
    @DisplayName("Test kickOut from lobby")
    public void kickOutTest() throws ClosingLobbyException, SameNameException, LobbyCompleteException {
        Lobby lobby = new Lobby();
        lobby.addPlayer("pietro");
        lobby.addPlayer("marco");
        lobby.setMaxSize(2);
        Assertions.assertTrue(lobby.isAdmitted("pietro"));
        Assertions.assertTrue(lobby.isAdmitted("marco"));
        Assertions.assertTrue(lobby.getLock());
    }

    @Test
    @DisplayName("test inizialise lobby")
    public void inzialiseLobby() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
        Lobby lobby = new Lobby();
       ;lobby.addPlayer("pietro");
        lobby.addPlayer("marco");
        lobby.addPlayer("daniel");
        lobby.addPlayer("arturo");
        Assertions.assertEquals(4, lobby.getPlayers().length);
        lobby.setMaxSize(2);
        Assertions.assertEquals(2, lobby.getPlayers().length);
        Assertions.assertThrows(LobbyCompleteException.class, () -> {
            lobby.addPlayer("daniel");
        });
        Assertions.assertThrows(ClosingLobbyException.class, () -> {
            lobby.setMaxSize(3);
        });
    }

    @Test
    public void normalSetLock() throws SameNameException, LobbyCompleteException, ClosingLobbyException {
        lobby = new Lobby();
        lobby.addPlayer("arco");
        lobby.addPlayer("marco");
        lobby.setMaxSize(2);
    }
}
