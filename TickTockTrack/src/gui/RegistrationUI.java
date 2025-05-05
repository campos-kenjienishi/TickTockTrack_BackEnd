package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.SystemLogic;
import gui.AdminDashboardUI;

public class RegistrationUI {

    public void showCreateUserScreen(Stage primaryStage, SystemLogic systemLogic) {
        Button adminButton = new Button("Register Admin");
        Button teacherButton = new Button("Register Teacher");
        Button studentButton = new Button("Register Student");
        Button backButton = new Button("Back");

        adminButton.setOnAction(e -> showAdminRegistrationForm(primaryStage, systemLogic));
        teacherButton.setOnAction(e -> showTeacherRegistrationForm(primaryStage, systemLogic));
        studentButton.setOnAction(e -> showStudentRegistrationForm(primaryStage, systemLogic));
        backButton.setOnAction(e -> {
            AdminDashboardUI adminDashboardUI = new AdminDashboardUI();  // Create an instance of AdminDashboardUI
            adminDashboardUI.initAdminDashboard(primaryStage, systemLogic);  // Show the Admin Dashboard
        });
        VBox layout = new VBox(10, adminButton, teacherButton, studentButton, backButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.setTitle("Create User");
    }

    public void showAdminRegistrationForm(Stage primaryStage, SystemLogic systemLogic) {
        // Create input fields for admin registration
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField();
        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");

        // Set prompt texts or labels
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        emailField.setPromptText("Email");

        // Handle register button click
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            // Call the registration method in SystemLogic
            boolean success = systemLogic.registerUser(username, "", "", "", email, password, "admin", "", "", "");

            // Show success or error alert based on the result
            if (success) {
                showAlert(AlertType.INFORMATION, "Registration Successful", "Admin registration successful.");
                // Clear input fields
                usernameField.clear();
                passwordField.clear();
                emailField.clear();
            } else {
                showAlert(AlertType.ERROR, "Registration Failed", "An error occurred during registration. Please try again.");
            }
        });

        // Handle back button click (go back to the create user screen)
        backButton.setOnAction(e -> showCreateUserScreen(primaryStage, systemLogic));

        // Layout
        VBox layout = new VBox(10, usernameField, passwordField, emailField, registerButton, backButton);
        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Registration");
    }

    public void showTeacherRegistrationForm(Stage primaryStage, SystemLogic systemLogic) {
        // Create input fields for teacher registration
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField();
        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");

        // Set prompt texts or labels
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        emailField.setPromptText("Email");

        // Handle register button click
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            // Call the registration method in SystemLogic
            boolean success = systemLogic.registerUser(username, "", "", "", email, password, "teacher", "", "", "");

            // Show success or error alert based on the result
            if (success) {
                showAlert(AlertType.INFORMATION, "Registration Successful", "Teacher registration successful.");
                // Clear input fields
                usernameField.clear();
                passwordField.clear();
                emailField.clear();
            } else {
                showAlert(AlertType.ERROR, "Registration Failed", "An error occurred during registration. Please try again.");
            }
        });

        // Handle back button click (go back to the create user screen)
        backButton.setOnAction(e -> showCreateUserScreen(primaryStage, systemLogic));

        // Layout
        VBox layout = new VBox(10, usernameField, passwordField, emailField, registerButton, backButton);
        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Teacher Registration");
    }

    public void showStudentRegistrationForm(Stage primaryStage, SystemLogic systemLogic) {
        // Create input fields for student registration
    	TextField usernameField = new TextField();
        TextField lastNameField = new TextField();
        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField gradeField = new TextField();
        TextField sectionField = new TextField();
        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");

        // Set prompt texts or labels
        usernameField.setPromptText("Username");
        lastNameField.setPromptText("Last Name");
        firstNameField.setPromptText("First Name");
        middleNameField.setPromptText("Middle Name");
        emailField.setPromptText("Email");
        passwordField.setPromptText("Password");
        gradeField.setPromptText("Year Level");
        sectionField.setPromptText("Section");

        // Handle register button click
        registerButton.setOnAction(e -> {
        	String username = usernameField.getText();
            String lastName = lastNameField.getText();
            String firstName = firstNameField.getText();
            String middleName = middleNameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String grade = gradeField.getText();
            String section = sectionField.getText();

            // Call the registration method in SystemLogic
            boolean success = systemLogic.registerUser(username, lastName, firstName, middleName, email, password, "student", grade, section, "0");

            // Show success or error alert based on the result
            if (success) {
                showAlert(AlertType.INFORMATION, "Registration Successful", "Student registration successful.");
                // Clear input fields
                usernameField.clear();
                lastNameField.clear();
                firstNameField.clear();
                middleNameField.clear();
                emailField.clear();
                passwordField.clear();
                gradeField.clear();
                sectionField.clear();
            } else {
                showAlert(AlertType.ERROR, "Registration Failed", "An error occurred during registration. Please try again.");
            }
        });

        // Handle back button click (go back to the create user screen)
        backButton.setOnAction(e -> showCreateUserScreen(primaryStage, systemLogic));

        // Layout
        VBox layout = new VBox(10, usernameField, lastNameField, firstNameField, middleNameField, emailField, passwordField, gradeField, sectionField, registerButton, backButton);
        Scene scene = new Scene(layout, 300, 350);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Registration");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.setContentText(message);
        alert.showAndWait();
    }
}   