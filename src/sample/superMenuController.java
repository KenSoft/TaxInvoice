package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.*;

import java.io.IOException;
import java.util.Optional;

public class superMenuController {
    public Button logoutButton;
    public JSONObject userInfo;
    public Label welcomeLabel;
    //Logout Button Function
    public void setField(JSONObject userInfo){
        this.userInfo=userInfo;
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
            log.put("action","Superuser logged out successfully");
            log.put("userId",userInfo.getInt("userId"));
            util.writeLog(log);
        } catch (IOException e){

        }
    }
    public void manageUsersAction (ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("manageUsers.fxml"));
            Parent tableViewParent = (Parent)fxmlLoader.load();
            Scene tableViewScene = new Scene(tableViewParent);
            manageUserController controller = fxmlLoader.<manageUserController>getController();
            controller.setField(userInfo);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(tableViewScene);
            window.show();
        } catch (IOException e){

        }
    }
    public void changePasswordAction(ActionEvent actionEvent){
        JSONObject oldInfo = util.getUser(this.userInfo.getInt("userId"),this.userInfo);
        Dialog<JSONObject> modifyDialog = modifyDialog(oldInfo);
        Optional<JSONObject> result = modifyDialog.showAndWait();
        if(result.toString().equals("Optional.empty")){

        } else {
            JSONObject newInfo = new JSONObject();
            try {
                newInfo = new JSONObject(result.get().toString());
                while(!newInfo.getString("password").equals(newInfo.getString("confirmPassword"))){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Password Mismatched!");
                    alert.setContentText("Please retype the password.");
                    alert.showAndWait();
                    modifyDialog = modifyDialog(newInfo);
                    result = modifyDialog.showAndWait();
                    newInfo = new JSONObject(result.get().toString());
                }
                util.modifyUser(newInfo,this.userInfo);
            } catch (Exception e){
                System.out.println(e);
            }
            if(newInfo.getInt("cancelled")==0) {
                Alert information = new Alert(Alert.AlertType.INFORMATION);
                information.setTitle("Information");
                information.setHeaderText("User Information Updated!");
                information.setContentText("Changes has been saved successfully!");
                information.showAndWait();
            }
        }


    }
    public static Dialog<JSONObject> modifyDialog(JSONObject user){
        JSONObject newUserInfo = new JSONObject();
        //        System.out.println(user);
        // Create the custom dialog.
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter a new password.");


        // Set the button types.
        ButtonType OKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OKButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Retype the password");

        grid.add(new Label("New Password:"), 0, 0);
        grid.add(password, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPassword, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node OKButton = dialog.getDialogPane().lookupButton(OKButtonType);
        OKButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });
        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> password.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OKButtonType) {
                newUserInfo.put("userId", user.getInt("userId"));
                newUserInfo.put("username", user.getString("username"));
                newUserInfo.put("fullname", user.getString("fullname"));
                newUserInfo.put("password", password.getText().trim());
                newUserInfo.put("confirmPassword", confirmPassword.getText().trim());
                newUserInfo.put("role", user.getString("role"));
                newUserInfo.put("cancelled",0);
                //return newUserInfo;
                return newUserInfo;
            } else {
                newUserInfo.put("cancelled",1);
                return newUserInfo;
            }

        });
        return dialog;
    }

}
