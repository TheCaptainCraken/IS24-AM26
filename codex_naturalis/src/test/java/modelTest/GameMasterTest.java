package modelTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exception.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
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
    static String alternatebasePath = "src/test/java/modelTest/test_decks/";

    @BeforeEach
    public void setUp() throws SameNameException, LobbyCompleteException {
        //create player
        lobby = new Lobby();
        lobby.addPlayer("pietro");
        lobby.addPlayer("marco");
        lobby.addPlayer("giovanni");
        lobby.addPlayer("francesco");

        try {
            game = new GameMaster(lobby,
                    basePath + "resourceCardsDeck.json",
                    "src/main/java/it/polimi/ingsw/model/decks/goldCardsDeck.json",
                    basePath + "objectiveCardsDeck.json",
                    basePath + "startingCardsDeck.json");
        } catch (IOException e) {
            System.out.println("File not found");
        } catch (ParseException e) {
            System.out.println("Error in parsing");
        }
    }

    @Test
    @DisplayName("Test of constructor")
    public void BasicGettersTest() throws IOException{
        Assertions.assertEquals("pietro", game.getCurrentPlayer().getName());
        Assertions.assertEquals(lobby.getPlayers().length,4);

        HashMap<Sign, Integer> test = game.getPlayerResources(game.getCurrentPlayer().getName());
        assert (!test.isEmpty()); //testing getPlayerResources()..

       Assertions.assertEquals(-1, game.getTurn());

        int i;
        for(i = 0; i < lobby.getPlayers().length; i++){
            Assertions.assertEquals(lobby.getPlayers()[i].getPoints(), 0);
        }
        Assertions.assertEquals(0, game.getPlayerPoints(game.getCurrentPlayer().getName()));
    }

    //testing end phase:
    @Test
    public void CorrectTransitionWhenMoreThan20points() throws WrongGamePhaseException, NoTurnException, NoNameException, NotEnoughResourcesException, CardPositionException {
        int i;
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.placeRootCard(lobby.getPlayers()[i].getName(),false);
        }
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.chooseObjectiveCard(lobby.getPlayers()[i].getName(),0);
        }
        lobby.getPlayerFromName("pietro").addPoints(20);
        Assertions.assertEquals(20, game.getPlayerPoints("pietro"));
        game.placeCard("pietro", 1, new Point (1,0 ), false);
        assert (game.getTurnType() == TurnType.PLAYING);
        game.drawCard("pietro", true, 0);
        assert(game.getTurnType() == TurnType.SECOND_LAST_TURN);

        game.placeCard("marco", 1, new Point (1,0 ), false);
        game.drawCard("marco", true, 0);
        assert (game.getTurnType() == TurnType.SECOND_LAST_TURN);
        game.placeCard("giovanni", 1, new Point (1,0 ), false);
        game.drawCard("giovanni", true, 0);

        game.placeCard("francesco", 1, new Point (1,0 ), false);
        game.drawCard("francesco", true, 0);

        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("pietro", 1, new Point (2,0 ), false);
        game.drawCard("pietro", true, 0);
        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("marco", 1, new Point (2,0 ), false);
        game.drawCard("marco", true, 0);
        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("giovanni", 1, new Point (2,0 ), false);
        game.drawCard("giovanni", true, 0);
        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("francesco", 1, new Point (2,0 ), false);
        game.drawCard("francesco", true, 0);

        assert (game.getGameState() == GameState.END);
        Assertions.assertNotNull(game.getRanking());
    }
    @Test
    public void transitionAtEndGameForLastPlayer() throws WrongGamePhaseException, NoTurnException, NoNameException, NotEnoughResourcesException, CardPositionException {
        int i;
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.placeRootCard(lobby.getPlayers()[i].getName(),false);
        }
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.chooseObjectiveCard(lobby.getPlayers()[i].getName(),0);
        }
        game.placeCard("pietro", 1, new Point (1,0 ), false);
        assert (game.getTurnType() == TurnType.PLAYING);
        game.drawCard("pietro", true, 0);

        game.placeCard("marco", 1, new Point (1,0 ), false);
        game.drawCard("marco", true, 0);
        game.placeCard("giovanni", 1, new Point (1,0 ), false);
        game.drawCard("giovanni", true, 0);

        lobby.getPlayerFromName("francesco").addPoints(20);
        game.placeCard("francesco", 1, new Point (1,0 ), false);
        game.drawCard("francesco", true, 0);
        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("pietro", 1, new Point (2,0 ), false);
        game.drawCard("pietro", true, 0);
        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("marco", 1, new Point (2,0 ), false);
        game.drawCard("marco", true, 0);
        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("giovanni", 1, new Point (2,0 ), false);
        game.drawCard("giovanni", true, 0);
        assert (game.getTurnType() == TurnType.LAST_TURN);
        game.placeCard("francesco", 1, new Point (2,0 ), false);
        game.drawCard("francesco", true, 0);

        assert (game.getGameState() == GameState.END);
    }

    @Test
    @DisplayName("Exception not current player for placeRootCard")
    public void NotCurrentPlayerTest() {
        Assertions.assertThrows(NoTurnException.class, ()->game.placeRootCard("marco", false));
    }

    @Test
    public void PlayingPhaseTest() throws WrongGamePhaseException, NoTurnException, NoNameException {
        int i;
        Player startingPlayer = game.getCurrentPlayer();
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.placeRootCard(lobby.getPlayers()[i].getName(),false);
        }
        Assertions.assertEquals(startingPlayer, game.getCurrentPlayer());

        for(i = 0; i < lobby.getPlayers().length; i++){
            game.chooseObjectiveCard(lobby.getPlayers()[i].getName(),0);
        }
        Assertions.assertEquals(startingPlayer,game.getCurrentPlayer());

    }

    //These are test for draw card. I use just one player to easily test the methods
    @BeforeEach
    public void setUp2() throws  IOException, ParseException{
        //create player
        lobby2 = new Lobby();
        lobby2.addPlayer("pietro");
        lobby2.addPlayer("marco");

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
    public void drawNoTurnTest() {
        Assertions.assertThrows(
                NoTurnException.class,
                () -> game2.drawCard("marco", true, 1)
        );
    }

    @Test
    @DisplayName("Not right phase exception for draw card test")
    public void drawNotRightPhaseTest() {
        Assertions.assertThrows(
                WrongGamePhaseException.class,
                () -> game2.drawCard("pietro", true, 1)
        );
    }

    @Test
    @DisplayName("Correct position of drawn card test")
    public void drawCardTest() throws WrongGamePhaseException, NoTurnException, NoNameException,
             NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", 0, new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, 1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[0].getHand()[0].getId());

        int CardId2;
        game2.placeCard("marco",0, new Point(1, 0), true);
        CardId2 = game2.getGoldCardOnTable(1).getId();
        CardId = game2.drawCard("marco", true, 1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[1].getHand()[0].getId());
        Assertions.assertNotEquals(CardId2, game2.getGoldCardOnTable(1).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 0")
    public void drawCardPositionZeroTest() throws WrongGamePhaseException, NoTurnException, NoNameException,
             NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", 0, new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, 0);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[0].getHand()[0].getId());

        int CardId2;
        game2.placeCard("marco", 1, new Point(1, 0), true);
        CardId2 = game2.getGoldCardOnTable(0).getId();
        CardId = game2.drawCard("marco", true, 0);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[1].getHand()[1].getId());
        //update correctly the deck
        Assertions.assertNotEquals(CardId2, game2.getGoldCardOnTable(0).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 1")
    public void drawCardPositionOneTest() throws WrongGamePhaseException, NoTurnException, NoNameException,
             NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", 1, new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, 1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[0].getHand()[1].getId());

        game2.placeCard("marco", 1, new Point(1, 0), true);
        CardId = game2.drawCard("marco", true, 1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[1].getHand()[1].getId());
        //update correctly the deck
        Assertions.assertNotEquals(CardId, game2.getGoldCardOnTable(1).getId());

    }

    @Test
    @DisplayName("Test of drawCard position 2")
    public void drawCardTestPositionTwo() throws WrongGamePhaseException, NoTurnException, NoNameException,
            NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro",2, new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", true, -1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[0].getHand()[2].getId());

        game2.placeCard("marco", 2, new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", true, -1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[1].getHand()[2].getId());
        //update correctly the deck
        Assertions.assertNotEquals(CardId, game2.getGoldCardDeckTestOnly().getId());
    }

    @Test
    @DisplayName("Test of drawCard position 0 resource card")
    public void drawResourceCardTestPositionZeroTest() throws WrongGamePhaseException, NoTurnException, NoNameException,
            NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", 0, new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", false, 0);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[0].getHand()[0].getId());

        game2.placeCard("marco", 0, new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", false, 0);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[1].getHand()[0].getId());
        //update correctly the deck
        Assertions.assertNotEquals(CardId, game2.getResourceCardOnTable(0).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 1 resource card")
    public void drawResourceCardTestPositionOneTest() throws WrongGamePhaseException, NoTurnException, NoNameException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", 1, new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", false, 1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[0].getHand()[1].getId());

        game2.placeCard("marco", 1, new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", false, 1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[1].getHand()[1].getId());
        //update correctly the deck
        Assertions.assertNotEquals(CardId, game2.getResourceCardOnTable(1).getId());
    }

    @Test
    @DisplayName("Test of drawCard position 2 resource card")
    public void drawResourceCardTestPositionTwoTest() throws WrongGamePhaseException, NoTurnException, NoNameException,
            NoSuchFieldException, NotEnoughResourcesException, CardPositionException {
        game2.placeCard("pietro", 2, new Point(1, 0), true);
        int CardId = game2.drawCard("pietro", false, -1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[0].getHand()[2].getId());

        game2.placeCard("marco",2, new Point(1, 0), true);
        //add resources to player
        CardId = game2.drawCard("marco", false, -1);
        Assertions.assertEquals(CardId, lobby2.getPlayers()[1].getHand()[2].getId());
        //update correctly the deck
        Assertions.assertNotEquals(CardId, game2.getResourceCardDeckTestOnly().getId());
    }

    //Test for isPositionable
    @Test
    public void PlaceTopLeft() throws WrongGamePhaseException, NoTurnException, NoNameException, NotEnoughResourcesException, CardPositionException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(0, 1);
        game2.placeCard("pietro", 0, position, true);
        //test if the pointer of starting card points to the right card
        Assertions.assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_LEFT).getCard().getId());
        //test if the card attached to the starting card is the starting card
        Assertions.assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_LEFT).getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());
    }
    @Test
    public void PlaceTopRight() throws WrongGamePhaseException, NoTurnException,  NotEnoughResourcesException, CardPositionException, NoNameException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(1, 0);
        game2.placeCard("pietro", 0, position, true);
        Assertions.assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
        Assertions.assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getAttachmentCorners().get(Corner.BOTTOM_LEFT).getCard().getId());
    }
    @Test
    public void PlaceBottomLeft() throws WrongGamePhaseException, NoTurnException, NoNameException, NotEnoughResourcesException, CardPositionException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(-1, 0);
        game2.placeCard("pietro", 0, position, true);
        Assertions.assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_LEFT).getCard().getId());
        Assertions.assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_LEFT).getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
    }
    @Test
    public void PlaceBottomRight() throws WrongGamePhaseException, NoTurnException,  NotEnoughResourcesException, CardPositionException, NoNameException {
        int Card = lobby2.getPlayers()[0].getHand()[0].getId();
        Point position = new Point(0, -1);
        game2.placeCard("pietro", 0, position, true);
        Assertions.assertEquals(Card , lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());
        Assertions.assertEquals(lobby2.getPlayers()[0].getRootCard().getCard().getId(),
                lobby2.getPlayers()[0].getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getAttachmentCorners().get(Corner.TOP_LEFT).getCard().getId());
    }

    @Test
    public void WrongPositionTest(){
        Assertions.assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", 0, new Point(4,3), true));
        Assertions.assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", 0, new Point(3,3), true));
        Assertions.assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", 0, new Point(10,-10), true));
    }

    @Test
    public void CardAlreadyThere() throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, CardPositionException, NoNameException {
        game2.placeCard("pietro", 2, new Point(1, 0), false);
        game2.drawCard("pietro", true, -1);

        game2.placeCard("marco", 2, new Point(1, 0), false);
        game2.drawCard("marco", true, -1);

        Assertions.assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", 0, new Point(0,0), false));

        game2.placeCard("pietro", 0, new Point(2, 0), false);
        game2.drawCard("pietro", true, -1);

        game2.placeCard("marco", 0, new Point(2, 0), false);
        game2.drawCard("marco", true, -1);


        game2.placeCard("pietro", 0, new Point(3, 0), false);
        game2.drawCard("pietro", true, -1);

        game2.placeCard("marco", 0, new Point(3, 0), false);
        game2.drawCard("marco", true, -1);

        Assertions.assertThrows(CardPositionException.class, ()->
                game2.placeCard("pietro", 0, new Point(1,0), false));
   }
    @BeforeEach
    public void setUp3() throws IOException, ParseException{
        //create player
        lobby3 = new Lobby();
        lobby3.addPlayer("pietro");

        game3 = new GameMaster(lobby3,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        game3.placeRootCard("pietro", true);
        game3.chooseObjectiveCard("pietro", 0);

    }

    @Test
    @DisplayName("NoCardException")
    public void noCardException() throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, CardPositionException, NoNameException {
        int Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(-1, 0), false);
        game3.drawCard("pietro",false,0);

        game3.placeCard("pietro", 0, new Point(-2, 0), false);
        game3.drawCard("pietro",false,0);

        Assertions.assertThrows(CardPositionException.class, ()->
                game3.placeCard("pietro", 0, new Point(-1,0), false));

    }

    @Test
    @DisplayName("Card is Attached to 2 cards")
    public void TwoAttachments() throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, CardPositionException, NoNameException {

        int Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(1, 0), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());
        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(0, -1), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());
        Assertions.assertDoesNotThrow(
                ()-> game3.placeCard("pietro", 0, new Point(1, -1), false)
        );

    }

    @Test
    @DisplayName("Card is Attached to 3 cards")
    public void ThreeAttachments() throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, CardPositionException, NoNameException {

        int Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro",0, new Point(1, 0), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(0, -1), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(2, 0), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(2,-1), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Assertions.assertDoesNotThrow(
                ()-> game3.placeCard("pietro",0, new Point(1, -1), false)
        );
    }

    @Test
    @DisplayName("Card is Attached to 4 cards")
    public void FourAttachments() throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, CardPositionException, NoNameException {

        int Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(1, 0), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(0, -1), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(2, 0), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro", 0, new Point(2,-1), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.TOP_RIGHT).
                getAttachmentCorners().get(Corner.TOP_RIGHT).getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro",0, new Point(0,-2), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).
                getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getCard().getId());

        Card = game3.getCurrentPlayer().getHand()[0].getId();
        game3.placeCard("pietro",0, new Point(1,-2), false);
        game3.drawCard("pietro",false,0);
        Assertions.assertEquals(Card, game3.getCurrentPlayer().getRootCard().getAttachmentCorners().get(Corner.BOTTOM_RIGHT).
                getAttachmentCorners().get(Corner.BOTTOM_RIGHT).getAttachmentCorners().get(Corner.TOP_RIGHT).getCard().getId());

        Assertions.assertDoesNotThrow(
                ()-> game3.placeCard("pietro", 0, new Point(1, -1), false)
        );
    }

    @BeforeEach
    public void setUp4() throws  IOException, ParseException{
        //create player
        lobby4 = new Lobby();
        lobby4.addPlayer("pietro");

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
    public void notEnoughResourcesTest() {
        Assertions.assertThrows(NotEnoughResourcesException.class,
                ()-> game4.placeCard("pietro",2, new Point(1, 0), true));
    }

    @Test
    @DisplayName("Test for updatingPoints")
    public void UpdatePoints() throws WrongGamePhaseException, NoTurnException,
            NotEnoughResourcesException, NoNameException, CardPositionException {
        Assertions.assertEquals(0 , lobby4.getPlayers()[0].getPoints());
        game4.placeCard("pietro", 0, new Point(1, 0), true);


    }
    @Test
    @DisplayName("Test for Decks Emptying")
    public void deckEmpty() throws IOException, ParseException{
        game5 = new GameMaster(lobby4,
                "src/test/java/modelTest/test_decks/test_resourceDeck.json",
                "src/test/java/modelTest/test_decks/test_goldDeck.json",
                basePath+"objectiveCardsDeck.json",
                basePath+"startingCardsDeck.json");

        game5.placeRootCard("pietro",true);
        game5.chooseObjectiveCard("pietro",1);

        game5.placeCard("pietro", 2, new Point(1, 0), false);
        // resource deck is now empty
        game5.drawCard("pietro",false,-1);

        game5.placeCard("pietro",2, new Point(2, 0), false);
        //testing if deck raises the exception, as it is empty
        assertThrows(CardPositionException.class,
                ()-> game5.drawCard("pietro",false,-1));

        //gold deck is now empty
        game5.drawCard("pietro",true,-1);
        game5.placeCard("pietro", 2, new Point(3, 0), false);
        //testing if deck raises the exception, as it is empty
        assertThrows(CardPositionException.class,
                ()-> game5.drawCard("pietro",true,-1));

        //slot[0] of resourceCard is empty
        game5.drawCard("pietro",false,0);
        game5.placeCard("pietro", 2, new Point(4, 0), false);

        assertThrows(CardPositionException.class,
                ()-> game5.drawCard("pietro",false,0));

        //slot[1] of resourceCard is empty
        game5.drawCard("pietro",false,1);
        game5.placeCard("pietro", 2, new Point(5, 0), false);

        assertThrows(CardPositionException.class,
                ()-> game5.drawCard("pietro",false,1));

        //slot[0] of goldCard is empty
        game5.drawCard("pietro",true,0);
        game5.placeCard("pietro", 2 , new Point(6, 0), false);
        assertThrows(CardPositionException.class,
                ()-> game5.drawCard("pietro",true,0));

        //slot[1] of goldCard is empty
        game5.drawCard("pietro",true,1);
        game5.placeCard("pietro", 2, new Point(7, 0), false);

        assertThrows(WrongGamePhaseException.class,
                ()-> game5.drawCard("pietro",true,0));
    }
    @Test
    @DisplayName("Test for Decks Emptying")
    public void deckEmpty_FullLobby() throws IOException, ParseException {
        GameMaster game = new GameMaster(lobby,
                "src/test/java/modelTest/test_decks/test_resourceDeck2.json",
                "src/test/java/modelTest/test_decks/test_goldDeck2.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        int i;
        //set the lobby
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.placeRootCard(game.getCurrentPlayer().getName(),true);
        }
        for(i = 0; i < lobby.getPlayers().length; i++){
            game.chooseObjectiveCard(game.getCurrentPlayer().getName(),1);
        }

        // resource deck is now empty, player 1
        game.placeCard(game.getCurrentPlayer().getName(),2, new Point(1, 0), false);
        game.drawCard(game.getCurrentPlayer().getName(),false,-1);

        // player 2 tries to get a null card
        game.placeCard(game.getCurrentPlayer().getName(), 2, new Point(1, 0), false);
        assertThrows(CardPositionException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),false,-1));
        // gold deck is now empty, player 2
        game.drawCard(game.getCurrentPlayer().getName(),true,-1);

        // player 3 tries to get null card
        game.placeCard(game.getCurrentPlayer().getName(),2, new Point(1, 0), false);
        assertThrows(CardPositionException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),true,-1));
        // resource slot[0] is now empty, player 3
        game.drawCard(game.getCurrentPlayer().getName(),false,0);

        // player 4 tries to get null card
        game.placeCard(game.getCurrentPlayer().getName(), 2, new Point(1, 0), false);
        assertThrows(CardPositionException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),false,0));
        // resource slot[1] is now empty, player 4
        game.drawCard(game.getCurrentPlayer().getName(),false,1);

        //gold slot[1] is now empty, player 1
        game.placeCard(game.getCurrentPlayer().getName(), 2, new Point(2, 0), false);
        assertThrows(CardPositionException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),false,1));
        //gold slot[1] is now empty, player 1
        game.drawCard(game.getCurrentPlayer().getName(),true,1);

        //player 2
        game.placeCard(game.getCurrentPlayer().getName(),2, new Point(2, 0), false);
        assertThrows(CardPositionException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),true,1));
        //gold slot[0] is now empty, player 2
        game.drawCard(game.getCurrentPlayer().getName(),true,0);

        Kingdom test = game.getHeadDeck(true);
        assertNull(test);
        test = game.getHeadDeck(false);
        assertNull(test);

        assertNull(game.getCard(true,0));
        assertNull(game.getCard(false,0));

        assertNull(game.getCard(true, 1));
        assertNull(game.getCard(false, 1));
        //TABLE EMPTY NOW, we are now in second_last_turn phase.
        //starting from player 3
        for(i = 0; i < 6; i++){
            Player p = game.getCurrentPlayer();
            if(i < 2){
                // for player 3 and 4
                game.placeCard(game.getCurrentPlayer().getName(), 2, new Point(2, 0), false);
                assertNull((p.getHand()[2]));
            } else {
                //for player 1 and 2, 3 and 4
                game.placeCard(game.getCurrentPlayer().getName(),1, new Point(3, 0), false);
                assertNull((p.getHand()[1]));
            }

        }

        // we should be in end phase now
        assertThrows(WrongGamePhaseException.class,
                ()->game.drawCard(game.getCurrentPlayer().getName(),true,1));
        assertThrows(WrongGamePhaseException.class,
                ()->game.placeCard(game.getCurrentPlayer().getName(), 2, new Point(4, 0), false));
        assertEquals(GameState.END, game.getGameState());
    }

    @Test
    @DisplayName(("Tests for basic getters"))
    public void gettersTest() throws NoNameException {
        Assertions.assertNotNull(game2.getCard(false,0));
        Assertions.assertNotNull(game2.getCard(true,0));
        Assertions.assertNotNull(game2.getHeadDeck(true));
        Assertions.assertNotNull(game2.getHeadDeck(false));
        Assertions.assertNotNull(game2.getResourceCard(0));
        Assertions.assertNotNull(game2.getGoldCard(0));
        Assertions.assertNotNull(game2.getStartingCardToPosition("pietro"));
    }

    @Test
    @DisplayName("Getters for objective phase")
    public void getObjectiveToChoose() throws IOException, ParseException {
        Lobby lobby = new Lobby();
        lobby.addPlayer("pietro");
        GameMaster game = new GameMaster(lobby,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");
        game.placeRootCard("pietro",true);
        Assertions.assertNotNull(game.getObjectiveCard(0));
        Assertions.assertNotNull(game.getObjectiveCardToChoose(0,0));
    }

    @Test
    @DisplayName("WrongPhase")
    public void wrongPhaseException() throws IOException, ParseException {
        Lobby lobby = new Lobby();
        lobby.addPlayer("pietro");
        lobby.addPlayer("marco");
        GameMaster game = new GameMaster(lobby,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");
        game.placeRootCard("pietro",true);
        Assertions.assertThrows(WrongGamePhaseException.class, ()-> game.chooseObjectiveCard("marco",0));
        game.placeRootCard("marco",true);
        game.chooseObjectiveCard("pietro",0);
        Assertions.assertThrows(WrongGamePhaseException.class, ()-> game.placeCard("marco",0,new Point(1,0),true));
        game.chooseObjectiveCard("marco",0);

        game.placeCard("pietro",0,new Point(1,0),true);
        game.drawCard("pietro",true,0);
        Assertions.assertThrows(NoTurnException.class, ()-> game.placeCard("pietro",0,new Point(1,0),true));

        Assertions.assertThrows(WrongGamePhaseException.class, game::endGame);
    }

    @Test
    @DisplayName("Test for notEnoughResources")
     public void notEnoughResources() throws IOException, ParseException {
        Lobby lobby = new Lobby();
        lobby.addPlayer("pietro");
        GameMaster game = new GameMaster(lobby,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");
        game.placeRootCard("pietro",true);
        game.chooseObjectiveCard("pietro",0);
        Assertions.assertThrows(NotEnoughResourcesException.class, ()-> game.placeCard("pietro",2,new Point(1,0),true));
    }

    @Test
    public void fromCountableToSign(){
        Assertions.assertEquals(Sign.QUILL, game.fromCountableToSign(Countable.QUILL));
        Assertions.assertEquals(Sign.INKWELL, game.fromCountableToSign(Countable.INKWELL));
        Assertions.assertEquals(Sign.SCROLL, game.fromCountableToSign(Countable.SCROLL));
        Assertions.assertThrows(IllegalArgumentException.class, ()-> game.fromCountableToSign(Countable.CORNER));
    }

    @Test
    @DisplayName("player not in lobby")
    public void NoPlayerException() throws SameNameException, LobbyCompleteException, NoNameException {
       lobby = new Lobby();
       lobby.addPlayer("pietro");
       lobby.addPlayer("marco");
       Assertions.assertThrows(NoNameException.class, () -> lobby.getPlayerFromName("giovanni"));
    }

    @Test
    public void simulateTwoPlayerGame() throws Exception {
        // Crea due giocatori
        Player player1 = new Player("Player1");
        Player player2 = new Player("Player2");

        // Crea un nuovo lobby e aggiungi i giocatori
        Lobby lobby = new Lobby();
        lobby.addPlayer(player1.getName());
        lobby.addPlayer(player2.getName());

        // Crea un nuovo GameMaster con il lobby
        GameMaster gameMaster = new GameMaster(lobby, basePath + "resourceCardsDeck.json", basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json", basePath + "startingCardsDeck.json");

        // Simula alcune mosse
        gameMaster.placeRootCard(player1.getName(), true);
        gameMaster.placeRootCard(player2.getName(), false);

        gameMaster.chooseObjectiveCard(player1.getName(), 0);
        gameMaster.chooseObjectiveCard(player2.getName(), 1);

        gameMaster.placeCard(player1.getName(), 0, new Point(0, 1), true);
        gameMaster.drawCard(player1.getName(), true, 0);

        gameMaster.placeCard(player2.getName(), 0, new Point(1, 0), true);
        gameMaster.drawCard(player2.getName(), false, 1);

        // Verifica che il gioco si comporti come previsto
        assertEquals(GameState.PLACING_PHASE, gameMaster.getGameState(), "The game state should be PLACING_PHASE");
        assertNotNull(gameMaster.getResourceCard(0), "The resource card at position 0 should not be null");
        assertNotNull(gameMaster.getGoldCard(1), "The gold card at position 1 should not be null");

        Assertions.assertThrows(NoNameException.class, () -> game.getOrderPlayer("giacomo"));
    }

    @Test
    public void endPointsGame() throws IOException, ParseException {
        // Crea due giocatori
        Player player1 = new Player("Player1");
        Player player2 = new Player("Player2");

        // Crea un nuovo lobby e aggiungi i giocatori
        Lobby lobby = new Lobby();
        lobby.addPlayer(player1.getName());
        lobby.addPlayer(player2.getName());

        // Crea un nuovo GameMaster con il lobby
        GameMaster gameMaster = new GameMaster(lobby, basePath + "resourceCardsDeck.json", basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json", basePath + "startingCardsDeck.json");

        // Simula alcune mosse
        gameMaster.placeRootCard(player1.getName(), false);
        gameMaster.placeRootCard(player2.getName(), false);

        gameMaster.chooseObjectiveCard(player1.getName(), 0);
        gameMaster.chooseObjectiveCard(player2.getName(), 1);

        gameMaster.placeCard(player1.getName(), 0, new Point(0, 1), false);
        gameMaster.drawCard(player1.getName(), true, 0);

        gameMaster.placeCard(player2.getName(), 0, new Point(0, 1), false);
        gameMaster.drawCard(player2.getName(), false, 1);

        gameMaster.placeCard(player1.getName(), 0, new Point(0, 2), false);
        gameMaster.drawCard(player1.getName(), true, 0);

        gameMaster.placeCard(player2.getName(), 0, new Point(0, 2), false);
        gameMaster.drawCard(player2.getName(), false, 1);

        gameMaster.placeCard(player1.getName(), 0, new Point(0, 3), false);
        gameMaster.drawCard(player1.getName(), true, 0);

        gameMaster.placeCard(player2.getName(), 0, new Point(0, 3), false);
        gameMaster.drawCard(player2.getName(), false, 1);

        gameMaster.placeCard(player1.getName(), 0, new Point(0, 4), false);
        gameMaster.drawCard(player1.getName(), true, 0);

        gameMaster.placeCard(player2.getName(), 0, new Point(0, 4), false);
        gameMaster.drawCard(player2.getName(), false, 1);

        gameMaster.placeCard(player1.getName(), 0, new Point(0, 5), false);

        lobby.getPlayerFromName(player1.getName()).addPoints(20);
        lobby.getPlayerFromName(player2.getName()).addPoints(20);

        gameMaster.drawCard(player1.getName(), true, 0);

        gameMaster.placeCard(player2.getName(), 0, new Point(0, 5), false);
        gameMaster.drawCard(player2.getName(), false, 1);

        gameMaster.placeCard(player1.getName(), 0, new Point(0, 6), false);
        gameMaster.drawCard(player1.getName(), true, 0);

        gameMaster.placeCard(player2.getName(), 0, new Point(0, 6), false);
        gameMaster.drawCard(player2.getName(), true, 0);

        assertEquals(GameState.END, gameMaster.getGameState(), "The game state should be END");

        for(int i = 0; i < lobby.getPlayers().length; i++) {
            for (int j = 0; j < 2; j++) {
                ObjectiveCard card = gameMaster.getObjectiveCardToChoose(i, j);
                gameMaster.calculateEndGamePoints(card.getType(), card.getMultiplier(), lobby.getPlayerFromName("Player1"), card.getKingdom());
            }
        }
    }

    @Test
    public void removeResourcesToBacksideTest() throws IOException, ParseException {
        Player p1 = new Player("a");
        Lobby lobby = new Lobby();
        lobby.addPlayer(p1.getName());
        GameMaster game = new GameMaster(lobby, basePath + "resourceCardsDeck.json", alternatebasePath + "test_goldDeck3.json",
                basePath + "objectiveCardsDeck.json", alternatebasePath + "TestCoverageStart.json");
        game.placeRootCard(p1.getName(), false);
        game.chooseObjectiveCard(p1.getName(), 0);
        Assertions.assertThrows(NotEnoughResourcesException.class, () -> game.placeCard(p1.getName(), 2, new Point(0, 1), true));
        game.placeCard(p1.getName(), 0, new Point(0, -1), false);
        game.drawCard(p1.getName(), false, 0);
        game.placeCard(p1.getName(), 0, new Point(-1, 0), false);
        game.drawCard(p1.getName(), false, 0);
        for(Sign s: Sign.values()){
            game.getCurrentPlayer().addResource(s, 10);
        }
        game.placeCard(p1.getName(), 2, new Point(0, 1), true);

    }
}



