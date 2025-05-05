package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import logic.SystemLogic;

public class TeacherDashboardUI {

    public void initTeacherDashboard(Stage primaryStage, SystemLogic systemLogic) {
        // Create buttons for each feature
        Button markAttendanceButton = new Button("Mark Attendance");
        Button viewClassListButton = new Button("View Class List");
        Button addCourseButton = new Button("Add Course");
        Button viewAttendanceSummaryButton = new Button("View Attendance Summary");
        Button individualReportsButton = new Button("Individual Reports");
        Button searchStudentsButton = new Button("Search Students");
        Button logoutButton = new Button("Logout");

        // Event handlers for each button
        markAttendanceButton.setOnAction(e -> {
            System.out.println("Mark Attendance feature clicked.");
            // Add navigation to Mark Attendance UI here
        });

        viewClassListButton.setOnAction(e -> {
            System.out.println("View Class List feature clicked.");
            // Add navigation to View Class List UI here
        });

        addCourseButton.setOnAction(e -> {
            System.out.println("Add Course feature clicked.");
            // Add navigation to Add Course UI here
        });

        viewAttendanceSummaryButton.setOnAction(e -> {
            System.out.println("View Attendance Summary feature clicked.");
            // Add navigation to View Attendance Summary UI here
        });

        individualReportsButton.setOnAction(e -> {
            System.out.println("Individual Reports feature clicked.");
            // Add navigation to Individual Reports UI here
        });

        searchStudentsButton.setOnAction(e -> {
            System.out.println("Search Students feature clicked.");
            // Add navigation to Search Students UI here
        });

        logoutButton.setOnAction(e -> {
            System.out.println("Logout clicked.");
            UserInterface userInterface = new UserInterface(systemLogic);
            userInterface.initFacultyLoginUI(primaryStage); // Navigate back to Faculty Log in UI
        });

        // Arrange buttons in a vertical layout
        VBox layout = new VBox(10,
                new Label("Welcome to the Teacher Dashboard!"),
                markAttendanceButton,
                viewClassListButton,
                addCourseButton,
                viewAttendanceSummaryButton,
                individualReportsButton,
                searchStudentsButton,
                logoutButton
        );
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Set the scene
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Teacher Dashboard");
        primaryStage.show();
    }
}
