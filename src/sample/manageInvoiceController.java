package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class manageInvoiceController {
    public Button previousButton;
    public void previousPress(ActionEvent actionEvent){
        try{
        Parent tableViewParent = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
        Scene tableViewScene = new Scene(tableViewParent);

        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(tableViewScene);
        window.show();
        } catch (IOException e){

        }
    }

}
