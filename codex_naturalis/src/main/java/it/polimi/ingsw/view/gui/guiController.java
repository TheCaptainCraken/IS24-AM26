package it.polimi.ingsw.view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class guiController {

    @FXML
    Label label1;
    @FXML
    Button button1;
    @FXML

    public void handleSubmit(){
        label1.setText("Un Cane Bastardo");
    }
}
