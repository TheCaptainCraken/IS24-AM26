package controllerAndNetworkTest;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.view.LittleModel;
import it.polimi.ingsw.view.TUI;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.ViewSubmissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControllerClientTest {
    String path = "src/main/java/it/polimi/ingsw/model/decks/";
    public static ViewSubmissions viewSubmissions = ViewSubmissions.getInstance();
    public static Controller controller;
    public static LittleModel littleModel;
    public ViewInterface viewInterface;

    @Test
    @BeforeEach()
    public void setUp(){
        viewSubmissions = ViewSubmissions.getInstance();
        controller = new Controller();
        littleModel = new LittleModel(path);
        viewInterface = new TUI(littleModel, controller);
    }

    @Test
    public void testChooseNumberOfPlayers(){
        viewInterface.start();
        viewInterface.askNumberOfPlayers();
        controller.insertNumberOfPlayers(2);

        controller.stopWaiting();
    }

}
