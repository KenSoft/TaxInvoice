package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class modifyInvoiceController {
    public JSONObject userInfo;
    public JSONObject invoice;
    public JSONObject companyInfo;
    public JSONArray customers;
    public int invoiceId;
    public Button previousButton;
    public TableView tableView;
    public Label invoiceIdLabel;
    public Label companyInfoLabel;
    public Label customerInfoLabel;
    public ComboBox<CustomerItem> customerCombo = new ComboBox<CustomerItem>();
    public ComboBox<ProductItem> productCombo = new ComboBox<ProductItem>();
    public void setField (int invoiceId, JSONObject userInfo){
        this.userInfo=userInfo;
        this.invoiceId = invoiceId;
        invoiceIdLabel.setText("Invoice ID: "+this.invoiceId);
        companyInfo=util.getCompanyInfo(this.userInfo);
        companyInfoLabel.setText(companyInfo.getString("companyName")+" (Tax ID: "+
                companyInfo.getString("taxId")+")\n"+companyInfo.getString("address")+"\nPhone: "
                +companyInfo.getString("officePhone")+" Email: "+companyInfo.getString("email")+"\n"+
                companyInfo.getString("website"));
        customers = util.getCustomers(this.userInfo);
        customerCombo.setConverter(new StringConverter<CustomerItem>() {
            @Override
            public String toString(CustomerItem object) {
                return (object.taxId+" - "+object.getCustomerName());
            }

            @Override
            public CustomerItem fromString(String string) {
                return null;
            }
        });
        for(int i=0;i<customers.length();i++){
            customerCombo.getItems().add(
                    new CustomerItem(
                            customers.getJSONObject(i).getInt("customerId"),
                            customers.getJSONObject(i).getString("customerName"),
                            customers.getJSONObject(i).getString("address"),
                            customers.getJSONObject(i).getString("homePhone"),
                            customers.getJSONObject(i).getString("officePhone"),
                            customers.getJSONObject(i).getString("mobilePhone"),
                            customers.getJSONObject(i).getString("email"),
                            customers.getJSONObject(i).getString("taxId"))
            );
        }
        customerCombo.valueProperty().addListener((obs, oldVal, newVal) ->
            {
                JSONObject customer = util.getCustomer(newVal.customerId,this.userInfo);
                customerInfoLabel.setText(customer.getString("customerName")+" (Tax ID: "+
                        customer.getString("taxId")+")\n"+customer.getString("address")+"\nPhone: "
                        +customer.getString("officePhone")+"\nEmail: "+customer.getString("email"));
            });


    }

    public void previousPress(ActionEvent actionEvent){
        tableView.getItems().clear();
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

}
