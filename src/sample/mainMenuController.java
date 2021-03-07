package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class mainMenuController {
    public Button logoutButton;
    public Button exitButton;
    public Button createInvoiceButton;
    public Button manageInvoiceButton;
    public Button manageProductButton;
    public Button manageCustomerButton;
    public Label welcomeLabel;
    public JSONObject userInfo;
    //Logout Button Function
    public void setField(JSONObject userInfo){
        this.userInfo = userInfo;
        this.welcomeLabel.setText("Welcome back, "+userInfo.getString("fullname")+"!");
    }
    public void logoutAction (ActionEvent actionEvent){
        try {
            Parent tableViewParent = FXMLLoader.load(getClass().getResource("sample.fxml"));
            Scene tableViewScene = new Scene(tableViewParent);

            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(tableViewScene);
            window.show();
            JSONObject log = new JSONObject();
            log.put("withData",1);
            log.put("action","User logged out successfully");
            log.put("userId",userInfo.getInt("userId"));
            util.writeLog(log);
        } catch (IOException e){

        }
    }
    public void createInvoiceAction (ActionEvent actionEvent){
        try {
            int invoiceId = util.newInvoice(this.userInfo);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("modifyInvoice.fxml"));
            Parent modifyInvoiceParent = (Parent) fxmlLoader.load();
            Scene modifyInvoiceScene = new Scene(modifyInvoiceParent);
            modifyInvoiceController controller = fxmlLoader.<modifyInvoiceController>getController();
            controller.setField(invoiceId,this.userInfo);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(modifyInvoiceScene);
            window.show();
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }
    public void manageInvoiceAction (ActionEvent actionEvent){
        try{
            Parent tableViewParent = FXMLLoader.load(getClass().getResource("manageInvoice.fxml"));
            Scene tableViewScene = new Scene(tableViewParent);

            Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            window.setScene(tableViewScene);
            window.show();
        } catch (IOException e){

        }
    }
    public void manageProductAction (ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("manageProduct.fxml"));
            Parent mangeProductParent = (Parent)fxmlLoader.load();
            Scene manageProductScene = new Scene(mangeProductParent);
            manageProductController controller = fxmlLoader.<manageProductController>getController();
            controller.setField(userInfo);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(manageProductScene);
            window.show();
        } catch (IOException e){

        }
    }
    public void manageCustomerAction (ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("manageCustomer.fxml"));
            Parent mangeCustomerParent = (Parent)fxmlLoader.load();
            Scene manageCustomerScene = new Scene(mangeCustomerParent);
            manageCustomerController controller = fxmlLoader.<manageCustomerController>getController();
            controller.setField(userInfo);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(manageCustomerScene);
            window.show();
        } catch (IOException e){

        }
    }
    public void exitAction (ActionEvent actionEvent) {
        System.exit(0);
    }

}
