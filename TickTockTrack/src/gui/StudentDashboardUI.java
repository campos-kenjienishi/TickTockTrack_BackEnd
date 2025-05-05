package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import logic.SystemLogic;

public class StudentDashboardUI {

    public void initStudentDashboard(Stage primaryStage, SystemLogic systemLogic) {
        // Create buttons for each feature
        Button viewAttendanceButton = new Button("View My Attendance");
        Button attendanceStatusButton = new Button("Attendance Status");
        Button notificationsButton = new Button("Notifications");
        Button profileButton = new Button("Profile");
        Button logoutButton = new Button("Logout");

        // Event handlers for each button
        viewAttendanceButton.setOnAction(e -> {
            System.out.println("View My Attendance feature clicked.");
            // Add navigation to View Attendance UI here
        });

        attendanceStatusButton.setOnAction(e -> {
            System.out.println("Attendance Status feature clicked.");
            // Add navigation to Attendance Status UI here
        });

        notificationsButton.setOnAction(e -> {
            System.out.println("Notifications feature clicked.");
            // Add navigation to Notifications UI here
        });

        profileButton.setOnAction(e -> {
            System.out.println("Profile feature clicked.");
            // Add navigation to Profile UI here
        });

        logoutButton.setOnAction(e -> {
            System.out.println("Logout clicked.");
            UserInterface userInterface = new UserInterface(systemLogic);
            userInterface.initStudentLoginUI(primaryStage); // Navigate back to Student log in UI
        });

        // Arrange buttons in a vertical layout
        VBox layout = new VBox(10,
                new Label("Welcome to the Student Dashboard!"),
                viewAttendanceButton,
                attendanceStatusButton,
                notificationsButton,
                profileButton,
                logoutButton
        );
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Set the scene
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Dashboard");
        primaryStage.show();
    }
}
