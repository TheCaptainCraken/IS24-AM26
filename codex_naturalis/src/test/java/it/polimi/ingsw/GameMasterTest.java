package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.LobbyCompleteException;
import it.polimi.ingsw.model.exception.SameNameException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.model.Lobby;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameMasterTest {
    GameMaster game;
    static Lobby lobby;

    @BeforeAll
    public static void setUp() throws SameNameException, LobbyCompleteException {
        //create player
        lobby = new Lobby();
        lobby.addPlayer("pietro", Color.YELLOW);
        lobby.addPlayer("marco", Color.GREEN);
        lobby.addPlayer("giovanni", Color.RED);
        lobby.addPlayer("francesco", Color.BLUE);
        GameMaster game = new GameMaster(lobby, null, null, null, null );
    }

    @Test
    public void Test1(){
        assertEquals("pietro", game.getCurrentPlayer().getName());
    }
}
