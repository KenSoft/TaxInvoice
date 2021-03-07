package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.JSONObject;


import java.io.IOException;
import java.util.Optional;

public class Controller {
    public Button cancelButton;
    public Button loginButton;
    public Button adminButton;
    public TextField username;
    public PasswordField password;
    //Cancel Button Function
    public void cancelAction (ActionEvent actionEvent){
        System.exit(0);
    }
    //Login Button Function
    public void loginAction (ActionEvent actionEvent){
        JSONObject loginResult;
        loginResult = util.loginSQL(username.getText(), password.getText());
        if (loginResult.getString("role").equals("Admin")||loginResult.getString("role").equals("User")){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
                Parent mainMenuParent = (Parent)fxmlLoader.load();
                Scene mainMenuScene = new Scene(mainMenuParent);
                mainMenuController controller = fxmlLoader.<mainMenuController>getController();
                controller.setField(loginResult);
                Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                window.setScene(mainMenuScene);
                window.show();
            } catch (IOException e) {

            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            JSONObject log = new JSONObject();
            log.put("withData",0);
            log.put("action","User login failed");
            util.writeLog(log);
            alert.setTitle("Error");
            alert.setHeaderText("Incorrect Username or Password");
            alert.setContentText("Please enter a valid credential or contact system administrator!");
            alert.showAndWait();
        }
    }
    //Admin Menu Button Function
    public void adminMenuAction (ActionEvent actionEvent) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Superuser Login");
        dialog.setHeaderText("Please enter your credential.");


        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            //System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
            JSONObject userInfo = util.superuserLoginSQL(usernamePassword.getKey(),usernamePassword.getValue());
            if (userInfo.getString("role").equals("Admin")){
                try {
                    //Parent tableViewParent = FXMLLoader.load(getClass().getResource("superMenu.fxml"));
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("superMenu.fxml"));
                    Parent superMenuParent = (Parent)fxmlLoader.load();
                    Scene superMenuScene = new Scene(superMenuParent);
                    superMenuController controller = fxmlLoader.<superMenuController>getController();
                    controller.setField(userInfo);
                    Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    window.setScene(superMenuScene);
                    window.show();
                } catch (IOException e) {

                }
            }
            else{
                JSONObject log = new JSONObject();
                log.put("withData",0);
                log.put("action","Superuser login failed");
                util.writeLog(log);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Incorrect Username or Password");
                alert.setContentText("Please enter a valid credential!");
                alert.showAndWait();
            }
        });

    }
}


