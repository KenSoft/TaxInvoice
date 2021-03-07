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
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class manageUserController {
    public Button previousButton;
    public Button modifyUserButton;
    public Button deleteUserButton;
    public TableView<UserItem> tableView;
    public JSONObject userInfo;
    public JSONArray users;
    public void setField(JSONObject userInfo){
        this.userInfo=userInfo;
        users = util.getUsers(this.userInfo);
        for(int i=0;i<users.length();i++){
            //System.out.println(users.getJSONObject(i).getString("username"));
            tableView.getItems().add(new UserItem(users.getJSONObject(i).getInt("userId"),
                    users.getJSONObject(i).getString("username"),users.getJSONObject(i).getString("fullname")));
        }
        modifyUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                //System.out.println(newSelection.userId);
                modifyUserButton.setDisable(false);
                deleteUserButton.setDisable(false);
            } else {
                modifyUserButton.setDisable(true);
                deleteUserButton.setDisable(true);
            }
        });
    }
    public void previousPress(ActionEvent actionEvent){
        tableView.getItems().clear();
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("superMenu.fxml"));
            Parent superMenuParent = (Parent)fxmlLoader.load();
            Scene superMenuScene = new Scene(superMenuParent);
            superMenuController controller = fxmlLoader.<superMenuController>getController();
            controller.setField(userInfo);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(superMenuScene);
            window.show();
        } catch (IOException e){

        }
    }
    public void modifyUserAction(ActionEvent actionEvent){
        UserItem userItem = tableView.getSelectionModel().getSelectedItem();
        JSONObject oldInfo = util.getUser(userItem.userId,this.userInfo);
        Dialog<JSONObject> modifyDialog = modifyDialog(oldInfo);
        Optional<JSONObject> result = modifyDialog.showAndWait();
        if(result.toString().equals("Optional.empty")){

        } else {
            JSONObject newInfo = new JSONObject();
            try {
                newInfo = new JSONObject(result.get().toString());
                while(!newInfo.getString("password").equals(newInfo.getString("confirmPassword"))||util.getUserByUsername(newInfo.getString("username"), this.userInfo)==1){
                    if(!newInfo.getString("password").equals(newInfo.getString("confirmPassword"))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Password Mismatched!");
                        alert.setContentText("Please retype the password.");
                        alert.showAndWait();
                    } else if (util.getUserByUsername(newInfo.getString("username"), this.userInfo)==1){
                        if(newInfo.getString("username").equals(oldInfo.getString("username"))){
                            break;
                        }
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("User already existed!");
                        alert.setContentText("Please choose another username.");
                        alert.showAndWait();
                    }
                    modifyDialog = modifyDialog(newInfo);
                    result = modifyDialog.showAndWait();
                    newInfo = new JSONObject(result.get().toString());
                }
                util.modifyUser(newInfo,this.userInfo);
            } catch (Exception e){
                System.out.println(e);
            }
            users = util.getUsers(this.userInfo);
            tableView.getItems().clear();
            for(int i=0;i<users.length();i++){
                //System.out.println(users.getJSONObject(i).getString("username"));
                tableView.getItems().add(new UserItem(users.getJSONObject(i).getInt("userId"),
                        users.getJSONObject(i).getString("username"),users.getJSONObject(i).getString("fullname")));
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

    public void createUserAction(ActionEvent actionEvent){
        JSONObject newUser = new JSONObject();
        Dialog<JSONObject> newUserDialog = newUserDialog(newUser);
        Optional<JSONObject> result = newUserDialog.showAndWait();
        if(result.toString().equals("Optional.empty")){

        } else {
            try {
                while(!newUser.getString("password").equals(newUser.getString("confirmPassword"))||util.getUserByUsername(newUser.getString("username"), this.userInfo)>0){
                    if(!newUser.getString("password").equals(newUser.getString("confirmPassword"))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Password Mismatched!");
                        alert.setContentText("Please re-type the password.");
                        alert.showAndWait();
                    } else if (util.getUserByUsername(newUser.getString("username"), this.userInfo)>0){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("User already existed!");
                        alert.setContentText("Please choose another username.");
                        alert.showAndWait();
                    }
                    newUserDialog = newUserDialog(newUser);
                    result = newUserDialog.showAndWait();
                    newUser = new JSONObject(result.get().toString());
                }
                util.newUser(newUser,this.userInfo);
            } catch (Exception e){

            }
            users = util.getUsers(this.userInfo);
            tableView.getItems().clear();
            for(int i=0;i<users.length();i++){
                //System.out.println(users.getJSONObject(i).getString("username"));
                tableView.getItems().add(new UserItem(users.getJSONObject(i).getInt("userId"),
                        users.getJSONObject(i).getString("username"),users.getJSONObject(i).getString("fullname")));
            }
            if(newUser.getInt("cancelled")==0) {
                Alert information = new Alert(Alert.AlertType.INFORMATION);
                information.setTitle("Information");
                information.setHeaderText("User Created!");
                information.setContentText("Changes has been saved successfully!");
                information.showAndWait();
            }
        }
    }
    public void deleteUserEvent(ActionEvent actionEvent){
        UserItem userItem = tableView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure to delete user: "+userItem.username+"?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            util.deleteUser(userItem.userId,this.userInfo);
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setTitle("Information");
            information.setHeaderText("User Deleted!");
            information.setContentText("Changes has been saved successfully!");
            information.showAndWait();
            users = util.getUsers(this.userInfo);
            tableView.getItems().clear();
            for(int i=0;i<users.length();i++){
                //System.out.println(users.getJSONObject(i).getString("username"));
                tableView.getItems().add(new UserItem(users.getJSONObject(i).getInt("userId"),
                        users.getJSONObject(i).getString("username"),users.getJSONObject(i).getString("fullname")));
            }


        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
    public static Dialog<JSONObject> modifyDialog(JSONObject user){
        JSONObject newUserInfo = new JSONObject();
        //        System.out.println(user);
        // Create the custom dialog.
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("Modify User");
        dialog.setHeaderText("Enter new user information.");


        // Set the button types.
        ButtonType OKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OKButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setText(user.getString("username"));
        TextField fullname = new TextField();
        fullname.setPromptText("Full Name");
        fullname.setText(user.getString("fullname"));
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Retype the password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(fullname, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(new Label("Confirm Password:"), 0, 3);
        grid.add(confirmPassword, 1, 3);

        // Enable/Disable login button depending on whether a username was entered.
        Node OKButton = dialog.getDialogPane().lookupButton(OKButtonType);
        OKButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });
        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });
        fullname.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OKButtonType) {
                newUserInfo.put("userId", user.getInt("userId"));
                newUserInfo.put("username", username.getText().trim());
                newUserInfo.put("fullname", fullname.getText().trim());
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
    public static Dialog<JSONObject> newUserDialog(JSONObject newUserInfo){

        //        System.out.println(user);
        // Create the custom dialog.
        Dialog<JSONObject> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Enter new user information.");


        // Set the button types.
        ButtonType OKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(OKButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        TextField fullname = new TextField();
        fullname.setPromptText("Full Name");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Retype the password");
        try{
            username.setText(newUserInfo.getString("username"));
            fullname.setText(newUserInfo.getString("fullname"));
        }catch (Exception e){

        }

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(fullname, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(new Label("Confirm Password:"), 0, 3);
        grid.add(confirmPassword, 1, 3);

        // Enable/Disable login button depending on whether a username was entered.
        Node OKButton = dialog.getDialogPane().lookupButton(OKButtonType);
        OKButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });
        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });
        fullname.textProperty().addListener((observable, oldValue, newValue) -> {
            OKButton.setDisable(username.getText().trim().isEmpty()||fullname.getText().trim().isEmpty()
                    ||password.getText().trim().isEmpty()||confirmPassword.getText().trim().isEmpty());
        });


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == OKButtonType) {
                newUserInfo.put("username", username.getText().trim());
                newUserInfo.put("fullname", fullname.getText().trim());
                newUserInfo.put("password", password.getText().trim());
                newUserInfo.put("confirmPassword", confirmPassword.getText().trim());
                newUserInfo.put("role", "User");
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

