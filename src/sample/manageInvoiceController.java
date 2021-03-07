package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class manageInvoiceController {
    public JSONObject userInfo;
    public JSONArray invoices;
    public Button previousButton;
    public Button deleteInvoiceButton;
    public Button printInvoiceButton;
    public Button modifyInvoiceButton;
    public TableView<InvoiceItem> tableView;
    public int invoiceId;
    public void setField (JSONObject userInfo){
        this.userInfo = userInfo;
        invoices = util.getInvoices(this.userInfo);
        for(int i=0;i<invoices.length();i++){
            JSONObject JSON = new JSONObject(invoices.getJSONObject(i).getString("JSON"));
            double totalPrice = 0;
            String customerName = "";
            try{
                totalPrice=JSON.getDouble("totalPrice");
            } catch (Exception e){
                totalPrice = 0;
            }
            try{
                JSONObject customer = util.getCustomer(JSON.getInt("customerId"), this.userInfo);
                customerName=customer.getString("customerName");
            } catch (Exception e){
                customerName="";
            }
            tableView.getItems().add(new InvoiceItem(

                    invoices.getJSONObject(i).getInt("invoiceId"),
                    totalPrice, customerName
            ));

        }
        modifyInvoiceButton.setDisable(true);
        deleteInvoiceButton.setDisable(true);
        printInvoiceButton.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                invoiceId=newSelection.getInvoiceId();
                modifyInvoiceButton.setDisable(false);
                deleteInvoiceButton.setDisable(false);
                printInvoiceButton.setDisable(false);
            } else {
                modifyInvoiceButton.setDisable(true);
                deleteInvoiceButton.setDisable(true);
                printInvoiceButton.setDisable(true);
            }
        });

    }
    public void modifyInvoiceAction(ActionEvent actionEvent){
        try {
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
    public void previousPress(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
            Parent mainMenuParent = (Parent)fxmlLoader.load();
            Scene mainMenuScene = new Scene(mainMenuParent);
            mainMenuController controller = fxmlLoader.<mainMenuController>getController();
            controller.setField(userInfo);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(mainMenuScene);
            window.show();
        } catch (IOException e){

        }
    }
    public void deleteInvoiceAction(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Invoice");
        alert.setContentText("Are you sure to delete invoice ID: "+invoiceId+"?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            util.deleteInvoice(invoiceId,this.userInfo);
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setTitle("Information");
            information.setHeaderText("Invoice Deleted!");
            information.setContentText("Changes has been saved successfully!");
            information.showAndWait();
            tableView.getItems().clear();
            invoices = util.getInvoices(this.userInfo);
            for(int i=0;i<invoices.length();i++){
                JSONObject JSON = new JSONObject(invoices.getJSONObject(i).getString("JSON"));
                double totalPrice = 0;
                String customerName = "";
                try{
                    totalPrice=JSON.getDouble("totalPrice");
                } catch (Exception e){
                    totalPrice = 0;
                }
                try{
                    JSONObject customer = util.getCustomer(JSON.getInt("customerId"), this.userInfo);
                    customerName=customer.getString("customerName");
                } catch (Exception e){
                    customerName="";
                }
                tableView.getItems().add(new InvoiceItem(

                        invoices.getJSONObject(i).getInt("invoiceId"),
                        totalPrice, customerName
                ));

            }


        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

}
