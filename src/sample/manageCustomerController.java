package sample;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class manageCustomerController {
    public JSONObject userInfo;
    public Button previousButton;
    public TableView<CustomerItem> tableView;
    public JSONArray customers;
    public Button modifyCustomerButton;
    public Button deleteCustomerButton;
    static EmailValidator validator = EmailValidator.getInstance();
    public void setField(JSONObject userInfo){
        this.userInfo = userInfo;
        customers = util.getCustomers(this.userInfo);
        for(int i=0;i<customers.length();i++){
            //System.out.println(users.getJSONObject(i).getString("username"));
            tableView.getItems().add(new CustomerItem(
                    customers.getJSONObject(i).getInt("customerId"),
                    customers.getJSONObject(i).getString("customerName"),
                    customers.getJSONObject(i).getString("address"),
                    customers.getJSONObject(i).getString("homePhone"),
                    customers.getJSONObject(i).getString("officePhone"),
                    customers.getJSONObject(i).getString("mobilePhone"),
                    customers.getJSONObject(i).getString("email"),
                    customers.getJSONObject(i).getString("taxId")));
        }
        modifyCustomerButton.setDisable(true);
        deleteCustomerButton.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                //System.out.println(newSelection.userId);
                modifyCustomerButton.setDisable(false);
                deleteCustomerButton.setDisable(false);
            } else {
                modifyCustomerButton.setDisable(true);
                deleteCustomerButton.setDisable(true);
            }
        });
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
    public void createCustomer(ActionEvent actionEvent){
        JSONObject newUser = new JSONObject();
        Dialog<JSONObject> newCustomerDialog = newCustomerDialog(newUser);
        Optional<JSONObject> result = newCustomerDialog.showAndWait();
        if(result.toString().equals("Optional.empty")){

        } else {
            try {
                while(util.getCustomerByTaxId(newUser.getString("taxId"), this.userInfo)>0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Customer already existed!");
                    alert.setContentText("Please confirm your information.");
                    alert.showAndWait();

                    newCustomerDialog = newCustomerDialog(newUser);
                    result = newCustomerDialog.showAndWait();
                    newUser = new JSONObject(result.get().toString());
                }
                util.newCustomer(newUser,this.userInfo);
            } catch (Exception e){

            }
            customers = util.getCustomers(this.userInfo);
            tableView.getItems().clear();
            for(int i=0;i<customers.length();i++){

                tableView.getItems().add(new CustomerItem(customers.getJSONObject(i).getInt("customerId"),
                        customers.getJSONObject(i).getString("customerName"),
                        customers.getJSONObject(i).getString("address"),
                        customers.getJSONObject(i).getString("homePhone"),
                        customers.getJSONObject(i).getString("officePhone"),
                        customers.getJSONObject(i).getString("mobilePhone"),
                        customers.getJSONObject(i).getString("email"),
                        customers.getJSONObject(i).getString("taxId")));
            }}

            if(newUser.getInt("cancelled")==0) {
                Alert information = new Alert(Alert.AlertType.INFORMATION);
                information.setTitle("Information");
                information.setHeaderText("Customer Created!");
                information.setContentText("Changes has been saved successfully!");
                information.showAndWait();
            }
        }
    public void modifyCustomerAction(ActionEvent actionEvent){
        CustomerItem customerItem = tableView.getSelectionModel().getSelectedItem();
        JSONObject oldInfo = new JSONObject(util.getCustomer(customerItem.customerId,this.userInfo).toString());
        Dialog<JSONObject> modifyCustomerDialog = modifyCustomerDialog(oldInfo);
        Optional<JSONObject> result = modifyCustomerDialog.showAndWait();
        //
        JSONObject newInfo = new JSONObject();
        if(result.toString().equals("Optional.empty")){

        } else {

            try {
                newInfo = new JSONObject(result.get().toString());
                while(util.getCustomerByTaxId(newInfo.getString("taxId"), this.userInfo)==1){
                    if(newInfo.getString("taxId").equals(oldInfo.getString("taxId"))){
                        break;
                    }
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Customer already existed!");
                    alert.setContentText("Please confirm your information.");
                    alert.showAndWait();

                    modifyCustomerDialog = modifyCustomerDialog(newInfo);
                    result = modifyCustomerDialog.showAndWait();
                    newInfo = new JSONObject(result.get().toString());
                }
                util.modifyCustomer(newInfo, this.userInfo);
            } catch (Exception e){

            }
            customers = util.getCustomers(this.userInfo);
            tableView.getItems().clear();
            for(int i=0;i<customers.length();i++){

                tableView.getItems().add(new CustomerItem(customers.getJSONObject(i).getInt("customerId"),
                        customers.getJSONObject(i).getString("customerName"),
                        customers.getJSONObject(i).getString("address"),
                        customers.getJSONObject(i).getString("homePhone"),
                        customers.getJSONObject(i).getString("officePhone"),
                        customers.getJSONObject(i).getString("mobilePhone"),
                        customers.getJSONObject(i).getString("email"),
                        customers.getJSONObject(i).getString("taxId")));
            }}

        if(newInfo.getInt("cancelled")==0) {
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setTitle("Information");
            information.setHeaderText("Customer Created!");
            information.setContentText("Changes has been saved successfully!");
            information.showAndWait();
        }
    }

    public static Dialog<JSONObject> newCustomerDialog(JSONObject newCustomerInfo){

        //        System.out.println(user);
        // Create the custom dialog.
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("New Customer");
        dialog.setHeaderText("Enter new customer information.");


        // Set the button types.
        ButtonType OKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OKButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField customerName = new TextField();
        customerName.setPromptText("(*Required*)");
        TextField taxId = new TextField();
        taxId.setPromptText("(*Required*)");
        TextArea address = new TextArea();
        address.setPromptText("(*Required*)");
        TextField officePhone = new TextField();
        officePhone.setPromptText("(*Required*)");
        TextField homePhone = new TextField();
        homePhone.setPromptText("(Optional)");
        TextField mobilePhone = new TextField();
        mobilePhone.setPromptText("(Optional)");
        TextField email = new TextField();
        email.setPromptText("(Optional)");

        try{
            customerName.setText(newCustomerInfo.getString("customerName"));
            taxId.setText(newCustomerInfo.getString("taxId"));
            address.setText(newCustomerInfo.getString("address"));
            officePhone.setText(newCustomerInfo.getString("officePhone"));
            homePhone.setText(newCustomerInfo.getString("homePhone"));
            mobilePhone.setText(newCustomerInfo.getString("mobilePhone"));
            email.setText(newCustomerInfo.getString("email"));

        }catch (Exception e){

        }

        grid.add(new Label("Customer Name*:"), 0, 0);
        grid.add(customerName, 1, 0);
        grid.add(new Label("Tax ID*:"), 0, 1);
        grid.add(taxId, 1, 1);
        grid.add(new Label("Address*:"), 0, 2);
        grid.add(address, 1, 2);
        grid.add(new Label("Office Phone*:"), 0, 3);
        grid.add(officePhone, 1, 3);
        grid.add(new Label("Home Phone:"), 0, 4);
        grid.add(homePhone, 1, 4);
        grid.add(new Label("Mobile Phone:"), 0, 5);
        grid.add(mobilePhone, 1, 5);
        grid.add(new Label("Email Address:"), 0, 6);
        grid.add(email, 1, 6);

        // Enable/Disable login button depending on whether a username was entered.
        Node OKButton = dialog.getDialogPane().lookupButton(OKButtonType);
        OKButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        officePhone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    officePhone.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        mobilePhone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    mobilePhone.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        homePhone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    homePhone.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        taxId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    taxId.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        officePhone.textProperty().addListener((observable, oldValue, newValue) -> {

        OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        address.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                    ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                    !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        customerName.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                    ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                    !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        taxId.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                    ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                    !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        homePhone.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(!homePhone.getText().trim().isEmpty()&&!validatePhone(homePhone.getText()));

        });
        mobilePhone.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(!mobilePhone.getText().trim().isEmpty()&&!validatePhone(mobilePhone.getText()));

        });
        email.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(!email.getText().trim().isEmpty()&&!validator.isValid(email.getText()));

        });


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> customerName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OKButtonType) {
                newCustomerInfo.put("customerName", customerName.getText().trim());
                newCustomerInfo.put("taxId", taxId.getText().trim());
                newCustomerInfo.put("address", address.getText().trim());
                newCustomerInfo.put("officePhone", officePhone.getText().trim());
                newCustomerInfo.put("homePhone", homePhone.getText().trim());
                newCustomerInfo.put("mobilePhone", mobilePhone.getText().trim());
                newCustomerInfo.put("email", email.getText().trim());
                newCustomerInfo.put("cancelled",0);
                //return newUserInfo;


                return newCustomerInfo;
            } else {
                newCustomerInfo.put("cancelled",1);
                return newCustomerInfo;
            }

        });
        return dialog;
    }
    public static boolean validatePhone(String phoneNumber){
        if((phoneNumber.length()==9 || phoneNumber.length()==10)){
            return true;
        }
        else return false;
    }
    public static boolean validateTaxId(String taxId){
        if(taxId.length()==13){
            return true;
        }
        else return false;
    }
    public static Dialog<JSONObject> modifyCustomerDialog(JSONObject customerInfo){
        JSONObject newCustomerInfo = new JSONObject();

        //        System.out.println(user);
        // Create the custom dialog.
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("Modify Customer");
        dialog.setHeaderText("Enter new customer information.");


        // Set the button types.
        ButtonType OKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OKButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField customerName = new TextField();
        customerName.setPromptText("(*Required*)");
        TextField taxId = new TextField();
        taxId.setPromptText("(*Required*)");
        TextArea address = new TextArea();
        address.setPromptText("(*Required*)");
        TextField officePhone = new TextField();
        officePhone.setPromptText("(*Required*)");
        TextField homePhone = new TextField();
        homePhone.setPromptText("(Optional)");
        TextField mobilePhone = new TextField();
        mobilePhone.setPromptText("(Optional)");
        TextField email = new TextField();
        email.setPromptText("(Optional)");

        try{
            customerName.setText(customerInfo.getString("customerName"));
            taxId.setText(customerInfo.getString("taxId"));
            address.setText(customerInfo.getString("address"));
            officePhone.setText(customerInfo.getString("officePhone"));
            homePhone.setText(customerInfo.getString("homePhone"));
            mobilePhone.setText(customerInfo.getString("mobilePhone"));
            email.setText(customerInfo.getString("email"));

        }catch (Exception e){

        }

        grid.add(new Label("Customer Name*:"), 0, 0);
        grid.add(customerName, 1, 0);
        grid.add(new Label("Tax ID*:"), 0, 1);
        grid.add(taxId, 1, 1);
        grid.add(new Label("Address*:"), 0, 2);
        grid.add(address, 1, 2);
        grid.add(new Label("Office Phone*:"), 0, 3);
        grid.add(officePhone, 1, 3);
        grid.add(new Label("Home Phone:"), 0, 4);
        grid.add(homePhone, 1, 4);
        grid.add(new Label("Mobile Phone:"), 0, 5);
        grid.add(mobilePhone, 1, 5);
        grid.add(new Label("Email Address:"), 0, 6);
        grid.add(email, 1, 6);

        // Enable/Disable login button depending on whether a username was entered.
        Node OKButton = dialog.getDialogPane().lookupButton(OKButtonType);
        OKButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        officePhone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    officePhone.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        mobilePhone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    mobilePhone.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        homePhone.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    homePhone.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        taxId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    taxId.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        officePhone.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                    ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                    !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        address.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                    ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                    !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        customerName.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                    ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                    !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        taxId.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(customerName.getText().trim().isEmpty()||taxId.getText().trim().isEmpty()
                    ||address.getText().trim().isEmpty()||officePhone.getText().trim().isEmpty()||
                    !validatePhone(officePhone.getText())||!validateTaxId(taxId.getText()));


        });
        homePhone.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(!homePhone.getText().trim().isEmpty()&&!validatePhone(homePhone.getText()));

        });
        mobilePhone.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(!mobilePhone.getText().trim().isEmpty()&&!validatePhone(mobilePhone.getText()));

        });
        email.textProperty().addListener((observable, oldValue, newValue) -> {

            OKButton.setDisable(!email.getText().trim().isEmpty()&&!validator.isValid(email.getText()));

        });


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> customerName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OKButtonType) {
                newCustomerInfo.put("customerId", customerInfo.getInt("customerId"));
                newCustomerInfo.put("customerName", customerName.getText().trim());
                newCustomerInfo.put("taxId", taxId.getText().trim());
                newCustomerInfo.put("address", address.getText().trim());
                newCustomerInfo.put("officePhone", officePhone.getText().trim());
                newCustomerInfo.put("homePhone", homePhone.getText().trim());
                newCustomerInfo.put("mobilePhone", mobilePhone.getText().trim());
                newCustomerInfo.put("email", email.getText().trim());
                newCustomerInfo.put("cancelled",0);
                //return newUserInfo;
                return newCustomerInfo;
            } else {
                newCustomerInfo.put("cancelled",1);
                return newCustomerInfo;
            }

        });
        return dialog;
    }
    public void deleteCustomerEvent(ActionEvent actionEvent){
        CustomerItem customerItem = tableView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure to delete customer: "+customerItem.customerName+"?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            util.deleteCustomer(customerItem.customerId,this.userInfo);
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setTitle("Information");
            information.setHeaderText("Customer Deleted!");
            information.setContentText("Changes has been saved successfully!");
            information.showAndWait();
            tableView.getItems().clear();
            customers = util.getCustomers(this.userInfo);
            for(int i=0;i<customers.length();i++){
                //System.out.println(users.getJSONObject(i).getString("username"));
                tableView.getItems().add(new CustomerItem(
                        customers.getJSONObject(i).getInt("customerId"),
                        customers.getJSONObject(i).getString("customerName"),
                        customers.getJSONObject(i).getString("address"),
                        customers.getJSONObject(i).getString("homePhone"),
                        customers.getJSONObject(i).getString("officePhone"),
                        customers.getJSONObject(i).getString("mobilePhone"),
                        customers.getJSONObject(i).getString("email"),
                        customers.getJSONObject(i).getString("taxId")));
            }


        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
}
