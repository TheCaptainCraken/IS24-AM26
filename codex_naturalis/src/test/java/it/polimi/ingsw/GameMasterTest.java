package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GameMasterTest {
    static GameMaster game;
    static GameMaster game2;
    static Lobby lobby;
    static Lobby lobby2;
    static String basePath = "src/main/java/it/polimi/ingsw/model/decks/";

    @BeforeEach
    public void setUp() throws SameNameException, LobbyCompleteException, IOException, ParseException {
        //create player
        lobby = new Lobby();
        lobby.addPlayer("pietro", Color.YELLOW);
        lobby.addPlayer("marco", Color.GREEN);
        lobby.addPlayer("giovanni", Color.RED);
        lobby.addPlayer("francesco", Color.BLUE);

        game = new GameMaster(lobby,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");
    }

    @Test
    @DisplayName("Test of constructor")
    public void BasicGettersTest() throws IOException, ParseException, NoSuchFieldException {
        assertEquals("pietro", game.getCurrentPlayer().getName());
        assertEquals(lobby.getPlayers().length,4);

        HashMap<Sign, Integer> test = game.getPlayerResources(game.getCurrentPlayer().getName());
        assert (!test.isEmpty()); //testing getPlayerResources()..

       assertEquals(-1, game.getTurn());

        int i;
        for(i = 0; i < lobby.getPlayers().length; i++){
            assertEquals(lobby.getPlayers()[i].getPoints(), 0);
        }
        assertEquals(0, game.getPlayerPoints(game.getCurrentPlayer().getName()));
    }

    @Test
    @DisplayName("Exception not current player for placeRootCard")
    public void NotCurrentPlayerTest() throws IOException, ParseException, WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        assertThrows(NoTurnException.class, ()->game.placeRootCard("marco", false));
    }

    @Test
    public void PlayingPhaseTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException, CardPositionException, NotEnoughResourcesException {
        int i;
        Player startingPlayer = game.getCurrentPlayer();
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.placeRootCard(lobby.getPlayers()[i].getName(),false);
        }
        assertEquals(startingPlayer, game.getCurrentPlayer());

        for(i = 0; i < lobby.getPlayers().length; i++){
            game.chooseObjectiveCard(lobby.getPlayers()[i].getName(),0);
        }
        assertEquals(startingPlayer,game.getCurrentPlayer());

        Point p = new Point(1,0);
        for(i = 0; i < lobby.getPlayers().length; i++){
                game.placeCard(game.getCurrentPlayer().getName(),game.getCurrentPlayer().getHand()[0],p,true );
                game.drawCard(game.getCurrentPlayer().getName(),false,1);
        }

    }

    //These are test for draw card. I use just one player to easily test the methods
    @BeforeEach
    public void setUp2() throws SameNameException, LobbyCompleteException, IOException, ParseException,
            WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        //create player
        lobby2 = new Lobby();
        lobby2.addPlayer("pietro", Color.YELLOW);
        lobby2.addPlayer("marco", Color.GREEN);

        game2 = new GameMaster(lobby2,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        game2.placeRootCard("pietro", true);
        game2.placeRootCard("marco", true);
        game2.chooseObjectiveCard("pietro", 0);
        game2.chooseObjectiveCard("marco", 0);

        for(Sign sign : Sign.values()){
            lobby2.getPlayers()[0].addResource(sign, 5);
            lobby2.getPlayers()[1].addResource(sign, 5);
        }
    }
    //Test of drawCard
    @Test
    @DisplayName("No turn exception for draw card test")
    public void drawNoTurnTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException {
        assertThrows(
                NoTurnException.class,
                () -> game2.drawCard("marco", true, 1)
        );
    }

    @Test
    @DisplayName("Not right phase exception for draw card test")
    public void drawNotRightPhaseTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException {
        assertThrows(
                WrongGamePhaseException.class,
                () -> game2.drawCard("pietro", true, 1)
        );
    }

    @Test
    @DisplayName("Correct position of drawn card test")
    public void drawCardTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, 1);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[0].getId());

        int CardId2;
        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[0], new Point(1, 0), true);
        CardId2 = game2.getGoldCardOnTable(1).getId();
        CardId = game2.drawCard("marco", true, 1);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[0].getId());
        assertNotEquals(CardId2, game2.getGoldCardOnTable(1).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 0")
    public void drawCardPositionZeroTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, 0);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[0].getId());

        int CardId2;
        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[1], new Point(1, 0), true);
        CardId2 = game2.getGoldCardOnTable(0).getId();
        CardId = game2.drawCard("marco", true, 0);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[1].getId());
        //update correctly the deck
        assertNotEquals(CardId2, game2.getGoldCardOnTable(0).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 1")
    public void drawCardPositionOneTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[1], new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, 1);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[1].getId());

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[1], new Point(1, 0), true);
        CardId = game2.drawCard("marco", true, 1);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[1].getId());
        //update correctly the deck
        assertNotEquals(CardId, game2.getGoldCardOnTable(1).getId());

    }

    @Test
    @DisplayName("Test of drawCard position 2")
    public void drawCardTestPositionTwo() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, 2);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[2].getId());

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", true, 2);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[2].getId());
        //update correctly the deck
        assertNotEquals(CardId, game2.getGoldCardDeck().getId());
    }

    @Test
    @DisplayName("Test of drawCard position 0 resource card")
    public void drawResourceCardTestPositionZeroTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", false, 0);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[0].getId());

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[0], new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", false, 0);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[0].getId());
        //update correctly the deck
        assertNotEquals(CardId, game2.getResourceCardOnTable(0).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 1 resource card")
    public void drawResourceCardTestPositionOneTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[1], new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", false, 1);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[1].getId());

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[1], new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", false, 1);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[1].getId());
        //update correctly the deck
        assertNotEquals(CardId, game2.getResourceCardOnTable(1).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 2 resource card")
    public void drawResourceCardTestPositionTwoTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", false, 2);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[2].getId());

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", false, 2);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[2].getId());
        //update correctly the deck
        assertNotEquals(CardId, game2.getResourceCardDeck().getId());
    }

    //Test for isPositionable
    @Test
    public void PlaceTopLeft() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(0, 1);
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], position, true);
        //test if the pointer of starting card points to the right card
        assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_LEFT).getCard().getId());
        //test if the card attached to the starting card is the starting card
        assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_LEFT).getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());
    }
    @Test
    public void PlaceTopRight() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(1, 0);
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], position, true);
        assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
        assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getAttachmentCorners().get(Corner.BOTTOM_LEFT).getCard().getId());
    }
    @Test
    public void PlaceBottomLeft() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(-1, 0);
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], position, true);
        assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_LEFT).getCard().getId());
        assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_LEFT).getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
    }
    @Test
    public void PlaceBottomRight() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(0, -1);
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], position, true);
        assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());
        assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getAttachmentCorners().get(Corner.TOP_LEFT).getCard().getId());
    }
}



