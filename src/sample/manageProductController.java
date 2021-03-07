package sample;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class manageProductController {
    public Button previousButton;
    public JSONObject userInfo;
    public JSONArray products;
    public TableView<ProductItem> tableView;
    public Button modifyProductButton;
    public Button deleteProductButton;
    public void setField (JSONObject userInfo){
        this.userInfo = userInfo;
        products = util.getProducts(this.userInfo);
        for(int i=0;i<products.length();i++){
            tableView.getItems().add(new ProductItem(

                    products.getJSONObject(i).getInt("productId"),
                    products.getJSONObject(i).getString("productName"),
                    products.getJSONObject(i).getString("unit"),
                    products.getJSONObject(i).getString("description"),
                    products.getJSONObject(i).getDouble("price")
            ));

        }
        modifyProductButton.setDisable(true);
        deleteProductButton.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                //System.out.println(newSelection.userId);
                modifyProductButton.setDisable(false);
                deleteProductButton.setDisable(false);
            } else {
                modifyProductButton.setDisable(true);
                deleteProductButton.setDisable(true);
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
    public void createProductAction(ActionEvent actionEvent){
        JSONObject newProduct = new JSONObject();
        Dialog<JSONObject> newProductDialog = newProductDialog(newProduct);
        Optional<JSONObject> result = newProductDialog.showAndWait();
        if(result.toString().equals("Optional.empty")){

        } else {
            try {
                util.newProduct(newProduct,this.userInfo);
            } catch (Exception e){

            }
            tableView.getItems().clear();
            products = util.getProducts(this.userInfo);
            for(int i=0;i<products.length();i++){
                tableView.getItems().add(new ProductItem(

                        products.getJSONObject(i).getInt("productId"),
                        products.getJSONObject(i).getString("productName"),
                        products.getJSONObject(i).getString("unit"),
                        products.getJSONObject(i).getString("description"),
                        products.getJSONObject(i).getDouble("price")
                ));

            }
            if(newProduct.getInt("cancelled")==0) {
                Alert information = new Alert(Alert.AlertType.INFORMATION);
                information.setTitle("Information");
                information.setHeaderText("User Created!");
                information.setContentText("Changes has been saved successfully!");
                information.showAndWait();
            }
        }
    }
    public static Dialog<JSONObject> newProductDialog(JSONObject newProductInfo){

        //        System.out.println(user);
        // Create the custom dialog.
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("New Product");
        dialog.setHeaderText("Enter new product information.");


        // Set the button types.
        ButtonType OKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OKButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ToggleGroup priceToggleGroup = new ToggleGroup();
        TextField productName = new TextField();
        productName.setPromptText("Product Name");
        RadioButton withTax = new RadioButton();
        withTax.setToggleGroup(priceToggleGroup);
        withTax.setUserData("withTax");
        RadioButton withoutTax = new RadioButton();
        withoutTax.setToggleGroup(priceToggleGroup);
        withoutTax.setUserData("withoutTax");
        TextField price = new TextField();
        price.setPromptText("Sale Price");
        TextField unit = new TextField();
        price.setPromptText("Unit");
        TextArea description = new TextArea();
        description.setPromptText("Product Description");
//        try{
//            username.setText(newUserInfo.getString("username"));
//            fullname.setText(newUserInfo.getString("fullname"));
//        }catch (Exception e){
//
//        }

        grid.add(new Label("Product Name*:"), 0, 0);
        grid.add(productName, 1, 0);
        grid.add(new Label("Price with Tax*:"), 0, 1);
        grid.add(withTax, 1, 1);
        grid.add(new Label("Price without Tax*:"), 0, 2);
        grid.add(withoutTax, 1, 2);
        grid.add(new Label("Sale Price*:"), 0, 3);
        grid.add(price, 1, 3);
        grid.add(new Label("Unit:"), 0, 4);
        grid.add(unit, 1, 4);
        grid.add(new Label("Description:"), 0, 5);
        grid.add(description, 1, 5);

        // Enable/Disable login button depending on whether a username was entered.
        Node OKButton = dialog.getDialogPane().lookupButton(OKButtonType);
        OKButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        productName.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(productName.getText().trim().isEmpty()||price.getText().trim().isEmpty()||priceToggleGroup.getSelectedToggle() == null);
        });
        price.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(productName.getText().trim().isEmpty()||price.getText().trim().isEmpty()||priceToggleGroup.getSelectedToggle() == null);
        });
        price.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    price.setText(oldValue);
                }
            }
        });

        priceToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {

                if (priceToggleGroup.getSelectedToggle() != null) {

                    //System.out.println(priceToggleGroup.getSelectedToggle().getUserData().toString());
                    OKButton.setDisable(productName.getText().trim().isEmpty()||price.getText().trim().isEmpty()||priceToggleGroup.getSelectedToggle() == null);
                    // Do something here with the userData of newly selected radioButton

                }

            }
        });



        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> productName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OKButtonType) {
                newProductInfo.put("productName", productName.getText().trim());
                if (priceToggleGroup.getSelectedToggle().getUserData().toString().equals("withTax")){
                    newProductInfo.put("price",  Double.parseDouble(price.getText().trim()));
                } else if (priceToggleGroup.getSelectedToggle().getUserData().toString().equals("withoutTax")){
                    newProductInfo.put("price",  Double.parseDouble(price.getText().trim())*1.07);
                }

                newProductInfo.put("unit", unit.getText().trim());
                newProductInfo.put("description", description.getText().trim());
                newProductInfo.put("cancelled",0);
                //return newUserInfo;


                return newProductInfo;
            } else {
                newProductInfo.put("cancelled",1);
                return newProductInfo;
            }

        });
        return dialog;
    }
    public static Dialog<JSONObject> modifyProductDialog(JSONObject newProductInfo){

        //        System.out.println(user);
        // Create the custom dialog.
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("Modify Product");
        dialog.setHeaderText("Enter new product information.");


        // Set the button types.
        ButtonType OKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OKButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ToggleGroup priceToggleGroup = new ToggleGroup();
        TextField productName = new TextField();
        productName.setPromptText("Product Name");
        RadioButton withTax = new RadioButton();
        withTax.setToggleGroup(priceToggleGroup);
        withTax.setUserData("withTax");
        RadioButton withoutTax = new RadioButton();
        withoutTax.setToggleGroup(priceToggleGroup);
        withoutTax.setUserData("withoutTax");
        TextField price = new TextField();
        price.setPromptText("Sale Price");
        TextField unit = new TextField();
        price.setPromptText("Unit");
        TextArea description = new TextArea();
        description.setPromptText("Product Description");
        try{
            productName.setText(newProductInfo.getString("productName"));
            price.setText(String.valueOf(newProductInfo.getDouble("price")));
            description.setText(newProductInfo.getString("description"));
            unit.setText(newProductInfo.getString("unit"));
            priceToggleGroup.selectToggle(withTax);
        }catch (Exception e){

        }

        grid.add(new Label("Product Name*:"), 0, 0);
        grid.add(productName, 1, 0);
        grid.add(new Label("Price with Tax*:"), 0, 1);
        grid.add(withTax, 1, 1);
        grid.add(new Label("Price without Tax*:"), 0, 2);
        grid.add(withoutTax, 1, 2);
        grid.add(new Label("Sale Price*:"), 0, 3);
        grid.add(price, 1, 3);
        grid.add(new Label("Unit:"), 0, 4);
        grid.add(unit, 1, 4);
        grid.add(new Label("Description:"), 0, 5);
        grid.add(description, 1, 5);

        // Enable/Disable login button depending on whether a username was entered.
        Node OKButton = dialog.getDialogPane().lookupButton(OKButtonType);
        OKButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        productName.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(productName.getText().trim().isEmpty()||price.getText().trim().isEmpty()||priceToggleGroup.getSelectedToggle() == null);
        });
        price.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(productName.getText().trim().isEmpty()||price.getText().trim().isEmpty()||priceToggleGroup.getSelectedToggle() == null);
        });
        price.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    price.setText(oldValue);
                }
            }
        });

        priceToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {

                if (priceToggleGroup.getSelectedToggle() != null) {

                    //System.out.println(priceToggleGroup.getSelectedToggle().getUserData().toString());
                    OKButton.setDisable(productName.getText().trim().isEmpty()||price.getText().trim().isEmpty()||priceToggleGroup.getSelectedToggle() == null);
                    // Do something here with the userData of newly selected radioButton

                }

            }
        });



        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> productName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OKButtonType) {
                newProductInfo.put("productName", productName.getText().trim());
                if (priceToggleGroup.getSelectedToggle().getUserData().toString().equals("withTax")){
                    newProductInfo.put("price",  Double.parseDouble(price.getText().trim()));
                } else if (priceToggleGroup.getSelectedToggle().getUserData().toString().equals("withoutTax")){
                    newProductInfo.put("price",  Double.parseDouble(price.getText().trim())*1.07);
                }

                newProductInfo.put("unit", unit.getText().trim());
                newProductInfo.put("description", description.getText().trim());
                newProductInfo.put("cancelled",0);
                //return newUserInfo;


                return newProductInfo;
            } else {
                newProductInfo.put("cancelled",1);
                return newProductInfo;
            }

        });
        return dialog;
    }
    public void modifyCustomerAction(ActionEvent actionEvent){
        ProductItem productItem = tableView.getSelectionModel().getSelectedItem();
        JSONObject oldInfo = new JSONObject(util.getProduct(productItem.productId,this.userInfo).toString());
        Dialog<JSONObject> modifyProductDialog = modifyProductDialog(oldInfo);
        Optional<JSONObject> result = modifyProductDialog.showAndWait();
        //
        JSONObject newInfo = new JSONObject();
        if(result.toString().equals("Optional.empty")){

        } else {

            try {
                newInfo = new JSONObject(result.get().toString());
                util.modifyProduct(newInfo, this.userInfo);
            } catch (Exception e){

            }
            tableView.getItems().clear();
            products = util.getProducts(this.userInfo);
            for(int i=0;i<products.length();i++){
                tableView.getItems().add(new ProductItem(

                        products.getJSONObject(i).getInt("productId"),
                        products.getJSONObject(i).getString("productName"),
                        products.getJSONObject(i).getString("unit"),
                        products.getJSONObject(i).getString("description"),
                        products.getJSONObject(i).getDouble("price")
                ));

            }

        if(newInfo.getInt("cancelled")==0) {
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setTitle("Information");
            information.setHeaderText("Customer Created!");
            information.setContentText("Changes has been saved successfully!");
            information.showAndWait();
        }
    }

}
}
