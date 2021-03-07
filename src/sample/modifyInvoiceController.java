package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

public class modifyInvoiceController {
    public JSONObject userInfo;
    public JSONObject invoice;
    public JSONObject companyInfo;
    public JSONArray customers;
    public JSONArray products;
    public JSONArray productsInInvoice = new JSONArray();
    public int invoiceId;
    public Button previousButton;
    public Button addToInvoiceButton;
    public Button removeFromInvoiceButton;
    public TableView<InvoiceProductItem> tableView;
    public Label invoiceIdLabel;
    public Label companyInfoLabel;
    public Label customerInfoLabel;
    public Label priceLabel;
    public TextField quantityText;
    public DatePicker datePicker;
    public int selectedProductId = 0;
    public int selectedInvoiceItem = 0;
    public ComboBox<CustomerItem> customerCombo = new ComboBox<CustomerItem>();
    public ComboBox<ProductItem> productCombo = new ComboBox<ProductItem>();
    public void setField (int invoiceId, JSONObject userInfo){
        this.userInfo=userInfo;
        this.invoiceId = invoiceId;
        this.invoice = util.getInvoice(invoiceId,this.userInfo);
        try{
            this.productsInInvoice = new JSONArray(new JSONObject(this.invoice.getString("JSON")).getJSONArray("productsArray"));
            for(int i=0;i<productsInInvoice.length();i++){
                tableView.getItems().add(new InvoiceProductItem(
                        productsInInvoice.getJSONObject(i).getInt("number"),
                        productsInInvoice.getJSONObject(i).getInt("productId"),
                        productsInInvoice.getJSONObject(i).getString("productName"),
                        productsInInvoice.getJSONObject(i).getString("unit"),
                        productsInInvoice.getJSONObject(i).getInt("quantity"),
                        productsInInvoice.getJSONObject(i).getDouble("price"),
                        productsInInvoice.getJSONObject(i).getDouble("totalPrice")
                ));
                priceLabel.setText("Price Before VAT: "+String.format("%.2f", new JSONObject(this.invoice.getString("JSON")).getDouble("totalPrice")/1.07)
                        +" THB | Total Price: "+String.format("%.2f", new JSONObject(this.invoice.getString("JSON")).getDouble("totalPrice")/1.00)+" THB");
            }
        }catch (Exception e){
            priceLabel.setText("Price Before VAT: 0.00 THB | Total Price: 0:00 THB ");
        }


        invoiceIdLabel.setText("Invoice ID: "+this.invoiceId);
        companyInfo=util.getCompanyInfo(this.userInfo);
        companyInfoLabel.setText(companyInfo.getString("companyName")+" (Tax ID: "+
                companyInfo.getString("taxId")+")\n"+companyInfo.getString("address")+"\nPhone: "
                +companyInfo.getString("officePhone")+" Email: "+companyInfo.getString("email")+"\n"+
                companyInfo.getString("website"));
        addToInvoiceButton.setDisable(true);
        quantityText.textProperty().addListener((observable, oldValue, newValue) -> {
            addToInvoiceButton.setDisable(quantityText.getText().trim().isEmpty()||selectedProductId==0);
        });

        removeFromInvoiceButton.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedInvoiceItem = newSelection.getNumber();
                removeFromInvoiceButton.setDisable(false);
            } else {
                removeFromInvoiceButton.setDisable(true);
            }
        });
        datePicker.valueProperty().addListener((obs, oldSelection, newSelection) ->{
            JSONObject newJSON = new JSONObject(invoice.getString("JSON"));
            newJSON.put("saleDate",newSelection.toString());
            this.invoice.put("JSON",newJSON.toString());
            util.updateInvoice(this.invoice,this.invoiceId,this.userInfo);
        });

        quantityText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    quantityText.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        products = util.getProducts(this.userInfo);
        productCombo.setConverter(new StringConverter<ProductItem>() {
            @Override
            public String toString(ProductItem object) {
                return (object.productId+" - "+object.getProductName());
            }

            @Override
            public ProductItem fromString(String string) {
                return null;
            }
        });
        for(int i=0;i<products.length();i++){
            productCombo.getItems().add(
                    new ProductItem(
                            products.getJSONObject(i).getInt("productId"),
                            products.getJSONObject(i).getString("productName"),
                            products.getJSONObject(i).getString("unit"),
                            products.getJSONObject(i).getString("description"),
                            products.getJSONObject(i).getDouble("price"))
            );
        }
        productCombo.valueProperty().addListener((obs, oldVal, newVal) ->
        {
            JSONObject product = util.getProduct(newVal.productId,this.userInfo);
            selectedProductId=newVal.productId;
            addToInvoiceButton.setDisable(quantityText.getText().trim().isEmpty());
        });

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
                JSONObject newJSON = new JSONObject(invoice.getString("JSON"));
                newJSON.put("customerId",customer.getInt("customerId"));
                this.invoice.put("JSON",newJSON.toString());
                util.updateInvoice(this.invoice,this.invoiceId,this.userInfo);
            });
        try{
            JSONObject JSON = new JSONObject(invoice.getString("JSON"));
            int customerId = JSON.getInt("customerId");
            JSONObject customer = util.getCustomer(customerId,this.userInfo);
            customerInfoLabel.setText(customer.getString("customerName")+" (Tax ID: "+
                    customer.getString("taxId")+")\n"+customer.getString("address")+"\nPhone: "
                    +customer.getString("officePhone")+"\nEmail: "+customer.getString("email"));
            JSONArray customers = util.getCustomers(this.userInfo);
            for(int i=0;i<customers.length();i++){
                if(customers.getJSONObject(i).getInt("customerId")==customer.getInt("customerId")){
                    customerCombo.getSelectionModel().select(i);
                }
            }


        }catch (Exception e){

        }
        try{
            JSONObject JSON = new JSONObject(invoice.getString("JSON"));
            datePicker.setValue(LocalDate.parse(JSON.getString("saleDate")));
        }catch (Exception e){

        }
    }

    public void addProductAction(ActionEvent actionEvent){
        double totalPrice = 0;
        int quantity = Integer.parseInt(quantityText.getText());
        JSONObject currentProduct = util.getProduct(this.selectedProductId,this.userInfo);
        JSONObject invoiceProduct = new JSONObject();
        int number = 1;
        try{
            JSONObject JSON = new JSONObject(invoice.getString("JSON"));
            totalPrice=JSON.getDouble("totalPrice");
        }catch (Exception e){

        }
        try{
            number = this.productsInInvoice.length()+1;
        }catch (Exception e){

        }
        invoiceProduct.put("number", number);
        invoiceProduct.put("productName", currentProduct.getString("productName"));
        invoiceProduct.put("productId", currentProduct.getInt("productId"));
        invoiceProduct.put("unit", currentProduct.getString("unit"));
        invoiceProduct.put("quantity", quantity);
        invoiceProduct.put("price", currentProduct.getDouble("price"));
        invoiceProduct.put("totalPrice", currentProduct.getDouble("price")*quantity);
        totalPrice = totalPrice+(currentProduct.getDouble("price")*quantity);
        productsInInvoice.put(invoiceProduct);
        JSONObject newJSON = new JSONObject(invoice.getString("JSON"));
        newJSON.put("productsArray", productsInInvoice);
        newJSON.put("totalPrice", totalPrice);
        this.invoice.put("JSON", newJSON.toString());
        util.updateInvoice(this.invoice,this.invoiceId,this.userInfo);

            tableView.getItems().clear();
            this.productsInInvoice = new JSONArray(new JSONObject(this.invoice.getString("JSON")).getJSONArray("productsArray"));
            for(int i=0;i<productsInInvoice.length();i++){
                tableView.getItems().add(new InvoiceProductItem(
                        productsInInvoice.getJSONObject(i).getInt("number"),
                        productsInInvoice.getJSONObject(i).getInt("productId"),
                        productsInInvoice.getJSONObject(i).getString("productName"),
                        productsInInvoice.getJSONObject(i).getString("unit"),
                        productsInInvoice.getJSONObject(i).getInt("quantity"),
                        productsInInvoice.getJSONObject(i).getDouble("price"),
                        productsInInvoice.getJSONObject(i).getDouble("totalPrice")
                ));
                priceLabel.setText("Price Before VAT: "+String.format("%.2f", new JSONObject(this.invoice.getString("JSON")).getDouble("totalPrice")/1.07)
                        +" THB | Total Price: "+String.format("%.2f", new JSONObject(this.invoice.getString("JSON")).getDouble("totalPrice")/1.00)+" THB");
            }

    }

    public void removeProductAction(ActionEvent actionEvent){
        double totalPrice = 0;
        try{
            JSONObject JSON = new JSONObject(invoice.getString("JSON"));
            totalPrice=JSON.getDouble("totalPrice");
        }catch (Exception e){

        }
        this.productsInInvoice = new JSONArray(new JSONObject(this.invoice.getString("JSON")).getJSONArray("productsArray"));
        for(int i=0;i<this.productsInInvoice.length();i++){
            if(this.productsInInvoice.getJSONObject(i).getInt("number")==selectedInvoiceItem){
                totalPrice = totalPrice-productsInInvoice.getJSONObject(i).getDouble("totalPrice");
                this.productsInInvoice.remove(i);
                break;
            }
        }
        for(int i=0;i<this.productsInInvoice.length();i++){
            this.productsInInvoice.getJSONObject(i).put("number",i+1);
        }
        JSONObject newJSON = new JSONObject(invoice.getString("JSON"));
        newJSON.put("productsArray", productsInInvoice);
        newJSON.put("totalPrice", totalPrice);
        this.invoice.put("JSON", newJSON.toString());
        util.updateInvoice(this.invoice,this.invoiceId,this.userInfo);
        tableView.getItems().clear();
        this.productsInInvoice = new JSONArray(new JSONObject(this.invoice.getString("JSON")).getJSONArray("productsArray"));
        for(int i=0;i<productsInInvoice.length();i++){
            tableView.getItems().add(new InvoiceProductItem(
                    productsInInvoice.getJSONObject(i).getInt("number"),
                    productsInInvoice.getJSONObject(i).getInt("productId"),
                    productsInInvoice.getJSONObject(i).getString("productName"),
                    productsInInvoice.getJSONObject(i).getString("unit"),
                    productsInInvoice.getJSONObject(i).getInt("quantity"),
                    productsInInvoice.getJSONObject(i).getDouble("price"),
                    productsInInvoice.getJSONObject(i).getDouble("totalPrice")
            ));
            priceLabel.setText("Price Before VAT: "+String.format("%.2f", new JSONObject(this.invoice.getString("JSON")).getDouble("totalPrice")/1.07)
                    +" THB | Total Price: "+String.format("%.2f", new JSONObject(this.invoice.getString("JSON")).getDouble("totalPrice")/1.00)+" THB");
        }
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
