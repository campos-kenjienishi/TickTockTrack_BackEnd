package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import logic.SystemLogic;

import java.util.List;

public class AdminDashboardUI {

    public void initAdminDashboard(Stage primaryStage, SystemLogic systemLogic) {
        // Create buttons for each function
        Button createUserButton = new Button("Create User");
        Button viewUsersButton = new Button("View All Users");
        Button attendanceReportsButton = new Button("Attendance Reports");
        Button manageAccountsButton = new Button("Manage Accounts");
        Button searchFilterButton = new Button("Search & Filter");
        Button logOutButton = new Button("Log Out");

        // Button actions
        createUserButton.setOnAction(e -> showCreateUserScreen(primaryStage, systemLogic));
        viewUsersButton.setOnAction(e -> showAllUsers(primaryStage, systemLogic));
        attendanceReportsButton.setOnAction(e -> System.out.println("Attendance Reports button clicked!"));
        manageAccountsButton.setOnAction(e -> openManageAccounts(primaryStage, systemLogic));
        searchFilterButton.setOnAction(e -> System.out.println("Search & Filter button clicked!"));

        logOutButton.setOnAction(e -> {
            primaryStage.close();
            UserInterface userInterface = new UserInterface(systemLogic);
            Stage loginStage = new Stage();
            userInterface.initFacultyLoginUI(loginStage);
        });

        VBox layout = new VBox(10, createUserButton, viewUsersButton, attendanceReportsButton, manageAccountsButton, searchFilterButton, logOutButton);
        layout.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(layout, 300, 350));
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }

    private void showCreateUserScreen(Stage primaryStage, SystemLogic systemLogic) {
        Button adminButton = new Button("Register Admin");
        Button teacherButton = new Button("Register Teacher");
        Button studentButton = new Button("Register Student");
        Button backButton = new Button("Back");

        adminButton.setOnAction(e -> {
            RegistrationUI registrationUI = new RegistrationUI();
            registrationUI.showAdminRegistrationForm(primaryStage, systemLogic);
        });

        teacherButton.setOnAction(e -> {
            RegistrationUI registrationUI = new RegistrationUI();
            registrationUI.showTeacherRegistrationForm(primaryStage, systemLogic);
        });

        studentButton.setOnAction(e -> {
            RegistrationUI registrationUI = new RegistrationUI();
            registrationUI.showStudentRegistrationForm(primaryStage, systemLogic);
        });

        backButton.setOnAction(e -> initAdminDashboard(primaryStage, systemLogic));

        VBox layout = new VBox(10, adminButton, teacherButton, studentButton, backButton);
        layout.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.setTitle("Create User");
    }

    private String currentRole;
    
    public void showAllUsers(Stage primaryStage, SystemLogic systemLogic) {
        VBox roleSelectionLayout = new VBox(10);
        Button viewAdminsButton = new Button("View Admins");
        Button viewTeachersButton = new Button("View Teachers");
        Button viewStudentsButton = new Button("View Students");

        ListView<String> usersListView = new ListView<>();

        viewAdminsButton.setOnAction(e -> {
            currentRole = "admin";
            List<String> adminsList = systemLogic.getUsersByRole(currentRole);
            usersListView.getItems().clear();
            usersListView.getItems().addAll(adminsList);
        });

        viewTeachersButton.setOnAction(e -> {
            currentRole = "teacher";
            List<String> teachersList = systemLogic.getUsersByRole(currentRole);
            usersListView.getItems().clear();
            usersListView.getItems().addAll(teachersList);
        });

        viewStudentsButton.setOnAction(e -> {
            currentRole = "student";
            List<String> studentsList = systemLogic.getFormattedStudentDetails();
            usersListView.getItems().clear();
            usersListView.getItems().addAll(studentsList);
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initAdminDashboard(primaryStage, systemLogic));

        VBox layout = new VBox(10, new Label("Select Role to View Users:"), roleSelectionLayout, usersListView, backButton);
        layout.setStyle("-fx-padding: 20;");

        roleSelectionLayout.getChildren().addAll(viewAdminsButton, viewTeachersButton, viewStudentsButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
        primaryStage.setTitle("View Users by Role");
    }
    
    public void openManageAccounts(Stage primaryStage, SystemLogic systemLogic) {
        ListView<String> usersListView = new ListView<>();
        VBox layout = new VBox(10);

        // Fetch all users and populate list
        Button loadUsersButton = new Button("Load Users");
        loadUsersButton.setOnAction(e -> {
            List<String> allUsers = systemLogic.getAllUsersWithDetails();
            usersListView.getItems().clear();
            usersListView.getItems().addAll(allUsers);
        });

        // Edit button
        Button editUserButton = new Button("Edit User");
        editUserButton.setOnAction(e -> {
            String selectedUser = usersListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                openEditUserDialog(selectedUser, systemLogic);
            } else {
                showAlert("Error", "No user selected for editing.");
            }
        });

        // Delete button
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setOnAction(e -> {
            String selectedUser = usersListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null && currentRole != null) {
                systemLogic.deleteUser(selectedUser, currentRole);
                usersListView.getItems().remove(selectedUser);
                showAlert("Success", "User deleted successfully.");
            } else {
                showAlert("Error", "No user selected for deletion or role is not set.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initAdminDashboard(primaryStage, systemLogic));

        layout.getChildren().addAll(loadUsersButton, usersListView, editUserButton, deleteUserButton, backButton);
        layout.setStyle("-fx-padding: 20;");
        primaryStage.setScene(new Scene(layout, 600, 400));
    }

    private void openEditUserDialog(String userDetails, SystemLogic systemLogic) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Edit User");

        String[] userFields = userDetails.split(" \\| ");
        String username = userFields[0];
        String role = userFields[1];

        TextField usernameField = new TextField(username);
        PasswordField passwordField = new PasswordField();

        VBox fieldsLayout = new VBox(10, new Label("Username:"), usernameField, new Label("Password:"), passwordField);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String newUsername = usernameField.getText();
            String newPassword = passwordField.getText();

            if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                systemLogic.updateUserDetails(username, newUsername, newPassword, role);
                dialogStage.close();
                showAlert("Success", "User details updated successfully.");
            } else {
                showAlert("Error", "Username and password cannot be empty.");
            }
        });

        VBox dialogLayout = new VBox(10, fieldsLayout, saveButton);
        dialogLayout.setStyle("-fx-padding: 20;");
        dialogStage.setScene(new Scene(dialogLayout, 400, 300));
        dialogStage.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}