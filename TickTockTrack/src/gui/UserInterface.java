package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.SystemLogic;

public class UserInterface {
    private final SystemLogic systemLogic;

    public UserInterface(SystemLogic systemLogic) {
        this.systemLogic = systemLogic;
    }

    /**
     * Entry point for the application: Pre-Login screen
     */
    public void initPreLoginUI(Stage primaryStage) {
        // Create buttons for Faculty and Student login
        Button facultyButton = new Button("Faculty");
        Button studentButton = new Button("Student");

        // Set button actions
        facultyButton.setOnAction(e -> initFacultyLoginUI(primaryStage));
        studentButton.setOnAction(e -> initStudentLoginUI(primaryStage));

        // Layout
        VBox layout = new VBox(20, facultyButton, studentButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.setTitle("Select Login Type");
        primaryStage.show();
    }

    /**
     * Faculty login UI (for Admins and Teachers)
     */
    public void initFacultyLoginUI(Stage primaryStage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");
        Label messageLabel = new Label();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // First, check if the credentials belong to an admin
            if (systemLogic.verifyLogin(username, password, "admin")) {
                messageLabel.setText("Admin login successful!");

                AdminDashboardUI adminDashboard = new AdminDashboardUI();
                adminDashboard.initAdminDashboard(primaryStage, systemLogic);

            // If not an admin, check if the credentials belong to a teacher
            } else if (systemLogic.verifyLogin(username, password, "teacher")) {
                messageLabel.setText("Teacher login successful!");

                TeacherDashboardUI teacherDashboard = new TeacherDashboardUI();
                teacherDashboard.initTeacherDashboard(primaryStage, systemLogic);

            // If neither, display an error message
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        backButton.setOnAction(e -> initPreLoginUI(primaryStage)); // Return to pre-login screen

        VBox layout = new VBox(10, usernameField, passwordField, loginButton, backButton, messageLabel);
        layout.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(layout, 300, 250));
        primaryStage.setTitle("Faculty Login");
        primaryStage.show();
    }

    /**
     * Student login UI
     */
    public void initStudentLoginUI(Stage primaryStage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Student ID");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");
        Label messageLabel = new Label();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (systemLogic.verifyLogin(username, password, "student")) { // Use a role parameter
                messageLabel.setText("Login successful!");

                StudentDashboardUI studentDashboard = new StudentDashboardUI();
                studentDashboard.initStudentDashboard(primaryStage, systemLogic); // Redirect to student dashboard
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        backButton.setOnAction(e -> initPreLoginUI(primaryStage)); // Go back to pre-login screen

        VBox layout = new VBox(10, usernameField, passwordField, loginButton, backButton, messageLabel);
        layout.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.setTitle("Student Login");
        primaryStage.show();
    }
}
