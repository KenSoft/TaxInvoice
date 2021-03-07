package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class manageProductController {
    public Button previousButton;
    public JSONObject userInfo;
    public JSONArray products;
    public TableView tableView;
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
}
