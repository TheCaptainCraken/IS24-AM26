package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.FullLobbyException;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.NotExistsPlayerException;
import it.polimi.ingsw.model.exception.SameNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class LobbyTest {
    private static Lobby lobby;

    @Test
    @DisplayName("check correct function when lobby is complete")
    public void CheckCorrectFunctionLobbyComplete(){
        assertThrows(LobbyCompleteException.class, () -> {
           Lobby lobby = new Lobby(4);
           lobby.addPlayer("pietro", Color.BLUE);
           lobby.addPlayer("marco", Color.RED);
           lobby.addPlayer("daniel", Color.GREEN);
           lobby.addPlayer("arturo", Color.YELLOW);
           lobby.addPlayer("arturo", Color.YELLOW);
        });
    }

    @BeforeAll
    public static void Setup() throws FullLobbyException, SameNameException, LobbyCompleteException {
        lobby = new Lobby(4);
        lobby.addPlayer("pietro", Color.BLUE);
        lobby.addPlayer("marco", Color.RED);
        lobby.addPlayer("daniel", Color.GREEN);
        lobby.addPlayer("arturo", Color.YELLOW);
    }
    @Test
    @DisplayName("Search player")
    public void SearchPlayerTest() throws NotExistsPlayerException, NoSuchFieldException {
        Player p1 = lobby.getPlayerFromName("pietro");
        assertEquals("pietro", p1.getName());

        Player p2 = lobby.getPlayerFromName("marco");
        assertEquals("marco", p2.getName());

        Player p3 = lobby.getPlayerFromName("daniel");
        assertEquals("daniel", p3.getName());

    }
}
