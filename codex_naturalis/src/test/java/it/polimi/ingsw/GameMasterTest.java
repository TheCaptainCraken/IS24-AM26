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
    static GameMaster game3;
    static GameMaster game4;
    static GameMaster game5;
    static Lobby lobby;
    static Lobby lobby2;
    static Lobby lobby3;
    static Lobby lobby4;
    static String basePath = "src/main/java/it/polimi/ingsw/model/decks/";
    static String alternatebasePath = "src/test/java/it/polimi/ingsw/test_decks/";

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
    public void PlayingPhaseTest() throws WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
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
        int CardId = game2.drawCard("pietro", true, -1);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[2].getId());

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", true, -1);
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
        int CardId = game2.drawCard("pietro", false, -1);
        assertEquals(CardId, lobby2.getPlayers()[0].getHand()[2].getId());

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", false, -1);
        assertEquals(CardId, lobby2.getPlayers()[1].getHand()[2].getId());
        //update correctly the deck
        assertNotEquals(CardId, game2.getResourceCardDeck().getId());
    }

    //Test for isPositionable
    @Test
    public void PlaceTopLeft() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {
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
    public void PlaceTopRight() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(1, 0);
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], position, true);
        assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
        assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getAttachmentCorners().get(Corner.BOTTOM_LEFT).getCard().getId());
    }
    @Test
    public void PlaceBottomLeft() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(-1, 0);
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], position, true);
        assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_LEFT).getCard().getId());
        assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_LEFT).getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
    }
    @Test
    public void PlaceBottomRight() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(0, -1);
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], position, true);
        assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());
        assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getAttachmentCorners().get(Corner.TOP_LEFT).getCard().getId());
    }

    @Test
    public void WrongPositionTest(){
        assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(4,3), true));
        assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(3,3), true));
        assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(10,-10), true));
    }

    @Test
    public void CardAlreadyThere() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException,
            NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {
        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), false);
        game2.drawCard("pietro", true, -1);

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[2], new Point(1, 0), false);
        game2.drawCard("marco", true, -1);

        assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(0,0), false));

        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(2, 0), false);
        game2.drawCard("pietro", true, -1);

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[0], new Point(2, 0), false);
        game2.drawCard("marco", true, -1);


        game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(3, 0), false);
        game2.drawCard("pietro", true, -1);

        game2.placeCard("marco", game2.getCurrentPlayer().getHand()[0], new Point(3, 0), false);
        game2.drawCard("marco", true, -1);

        assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", game2.getCurrentPlayer().getHand()[0], new Point(1,0), false));
   }
    @BeforeEach
    public void setUp3() throws SameNameException, LobbyCompleteException, IOException, ParseException,
            WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        //create player
        lobby3 = new Lobby();
        lobby3.addPlayer("pietro", Color.YELLOW);

        game3 = new GameMaster(lobby3,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        game3.placeRootCard("pietro", true);
        game3.chooseObjectiveCard("pietro", 0);

    }

    @Test
    @DisplayName("Card is Attached to 2 cards")
    public void TwoAttachments() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException,
            NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {

        int Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(1, 0), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(0, -1), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());
        assertDoesNotThrow(
                ()-> game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(1, -1), false)
        );

    }

    @Test
    @DisplayName("Card is Attached to 3 cards")
    public void ThreeAttachments() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException,
            NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {

        int Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(1, 0), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(0, -1), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(2, 0), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(2,-1), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        assertDoesNotThrow(
                ()-> game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(1, -1), false)
        );
    }

    @Test
    @DisplayName("Card is Attached to 4 cards")
    public void FourAttachments() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException,
            NotEnoughResourcesException, CardPositionException, NotExistsPlayerException {

        int Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(1, 0), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(0, -1), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(2, 0), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(2,-1), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(0,-2), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).
                getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(1,-2), false);
        game3.drawCard("pietro",false,0);
        assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).
                getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        assertDoesNotThrow(
                ()-> game3.placeCard("pietro", game3.getCurrentPlayer().getHand()[0], new Point(1, -1), false)
        );
    }

    @BeforeEach
    public void setUp4() throws SameNameException, LobbyCompleteException, IOException, ParseException,
            WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        //create player
        lobby4 = new Lobby();
        lobby4.addPlayer("pietro", Color.YELLOW);

        game4 = new GameMaster(lobby4,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        game4.placeRootCard("pietro", false);
        game4.chooseObjectiveCard("pietro", 0);

    }

    //TEST FOR RESOURCES
    @Test
    @DisplayName("Test for resources")
    public void notEnoughResourcesTest() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException,
            NotEnoughResourcesException, NotExistsPlayerException, CardPositionException {
        assertThrows(NotEnoughResourcesException.class,
                ()-> game4.placeCard("pietro", game4.getCurrentPlayer().getHand()[2], new Point(1, 0), true));
    }

    @Test
    @DisplayName("Test for updatingPoints")
    public void UpdatePoints() throws WrongGamePhaseException, NoTurnException, NoSuchFieldException,
            NotEnoughResourcesException, NotExistsPlayerException, CardPositionException {
        assertEquals(0 , lobby4.getPlayers()[0].getPoints());
        game4.placeCard("pietro", game4.getCurrentPlayer().getHand()[0], new Point(1, 0), true);


    }
    @Test
    @DisplayName("Test for Decks Emptying")
    public void deckEmpty() throws IOException, ParseException, WrongGamePhaseException, NoTurnException,
            NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game5 = new GameMaster(lobby4,
                alternatebasePath+"test_resourceDeck.json",
                alternatebasePath+"test_goldDeck.json",
                basePath+"objectiveCardsDeck.json",
                basePath+"startingCardsDeck.json");

        game5.placeRootCard("pietro",true);
        game5.chooseObjectiveCard("pietro",1);

        game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[2], new Point(1, 0), false);
        game5.drawCard("pietro",false,-1); // resource deck is now empty

        //testing if deck raises the exception, as it is empty
        game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[2], new Point(2, 0), false);
        assertThrows(IndexOutOfBoundsException.class,
                ()-> game5.drawCard("pietro",false,-1));

        //testing if deck raises the exception, as it is empty
        game5.drawCard("pietro",true,-1); //gold deck is now empty
        game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[2], new Point(3, 0), false);
        assertThrows(IndexOutOfBoundsException.class,
                ()-> game5.drawCard("pietro",true,-1));


        game5.drawCard("pietro",false,0); //slot[0] of resourceCard is empty
        game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[2], new Point(4, 0), false);

        assertThrows(IllegalArgumentException.class,
                ()-> game5.drawCard("pietro",false,0));

        game5.drawCard("pietro",false,1); //slot[1] of resourceCard is empty
        game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[2], new Point(5, 0), false);

        assertThrows(IllegalArgumentException.class,
                ()-> game5.drawCard("pietro",false,1));

        game5.drawCard("pietro",true,0); //slot[0] of goldCard is empty
        game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[2], new Point(6, 0), false);
        assertThrows(IllegalArgumentException.class,
                ()-> game5.drawCard("pietro",true,0));

        game5.drawCard("pietro",true,1); //slot[1] of goldCard is empty
        game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[2], new Point(7, 0), false);

        //ultimi check sono per valutare se effettivamente, dopo che non ci sono più carte da pescare, il gameMaster fa in modo che i giocatori non debbano più pescare
        //saltando quindi la DRAWING PHASE
        assertThrows(WrongGamePhaseException.class,
                ()-> game5.drawCard("pietro",true,0));
        assertDoesNotThrow(() -> game5.placeCard("pietro", game5.getCurrentPlayer().getHand()[0], new Point(8, 0), true));

    }
    @Test
    @DisplayName("Test for Decks Emptying")
    public void deckEmpty_FullLobby() throws IOException, ParseException, WrongGamePhaseException, NoTurnException,
            NotExistsPlayerException, NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        GameMaster game = new GameMaster(lobby,
                alternatebasePath + "test_resourceDeck2.json",
                alternatebasePath + "test_goldDeck2.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        for(int i = 0; i < lobby.getPlayers().length; i++){
            game.placeRootCard(game.getCurrentPlayer().getName(),true);
        }
        for(int i = 0; i < lobby.getPlayers().length; i++){
            game.chooseObjectiveCard(game.getCurrentPlayer().getName(),1);
        }
        game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(1, 0), false);
        game.drawCard(game.getCurrentPlayer().getName(),false,-1); // resource deck is now empty

        game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(1, 0), false);
        assertThrows(IndexOutOfBoundsException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),false,-1));

        game.drawCard(game.getCurrentPlayer().getName(),true,-1); // gold deck is now empty
        game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(1, 0), false);
        assertThrows(IndexOutOfBoundsException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),true,-1));

        game.drawCard(game.getCurrentPlayer().getName(),false,0); // resource slot[0] is now empty
        game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(1, 0), false);
        assertThrows(IllegalArgumentException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),false,0));

        game.drawCard(game.getCurrentPlayer().getName(),false,1); // resource slot[1] is now empty
        game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(2, 0), false);
        assertThrows(IllegalArgumentException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),false,1));

        game.drawCard(game.getCurrentPlayer().getName(),true,1); //gold slot[1] is now empty
        game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(2, 0), false);
        assertThrows(IllegalArgumentException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),true,1));

        game.drawCard(game.getCurrentPlayer().getName(),true,0); //gold slot[0] is now empty

        for(int i = 0; i < 6; i++){
            Player p = game.getCurrentPlayer();
            if(i < 2){
                game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(2, 0), false);
                assertNull((p.getHand()[2]));
            } else {
                game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[1], new Point(3, 0), false);
                assertNull((p.getHand()[1]));
            }
            assertThrows(WrongGamePhaseException.class,
                    ()->game.drawCard(game.getCurrentPlayer().getName(),true,1));
        }

        //ultima assert serve a confermare che GameMaster abbia fatto transizione verso GameState.END
        assertThrows(WrongGamePhaseException.class,
                ()->game.placeCard(game.getCurrentPlayer().getName(), game.getCurrentPlayer().getHand()[2], new Point(4, 0), false));
    }
}



