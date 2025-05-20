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

        // Button to Load Admins and Teachers
        Button loadUsersButton = new Button("Load Admins/Teachers");
        loadUsersButton.setOnAction(e -> {
            List<String> allUsers = systemLogic.getAdminsAndTeachersWithDetails();
            usersListView.getItems().clear();
            if (allUsers.isEmpty()) {
                usersListView.getItems().add("No Admins/Teachers found.");
            } else {
                usersListView.getItems().addAll(allUsers);
            }
        });

        // Button to Load Students
        Button loadStudentsButton = new Button("Load Students");
        loadStudentsButton.setOnAction(e -> {
            List<String> allStudents = systemLogic.getAllStudentsWithDetails();
            usersListView.getItems().clear();
            if (allStudents.isEmpty()) {
                usersListView.getItems().add("No Students found.");
            } else {
                usersListView.getItems().addAll(allStudents);
            }
        });

        // Edit Button
        Button editUserButton = new Button("Edit User");
        editUserButton.setOnAction(e -> {
            String selectedUser = usersListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String[] userFields = selectedUser.split("\\|");
                if (userFields.length < 2) { // Ensure at least username and role are present
                    showAlert("Error", "Invalid user details. Please check the data.");
                    return;
                }

                // Trim whitespace
                for (int i = 0; i < userFields.length; i++) {
                    userFields[i] = userFields[i].trim();
                }

                String role = userFields[userFields.length - 1]; // Role is the last field

                // Debugging
                System.out.println("Selected Role: " + role);

                if ("student".equalsIgnoreCase(role)) {
                    openEditStudentDialog(selectedUser, systemLogic); // Open student-specific dialog
                } else if ("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)) {
                    openEditUserDialog(selectedUser, systemLogic); // Open admin/teacher dialog
                } else {
                    showAlert("Error", "Unknown role selected. Cannot edit.");
                }
            } else {
                showAlert("Error", "No user selected for editing.");
            }
        });

        // Delete button
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setOnAction(e -> {
            String selectedUser = usersListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String[] userFields = selectedUser.split("\\|");
                if (userFields.length < 2) {
                    showAlert("Error", "Invalid user details. Please check the data.");
                    return;
                }

                String username = userFields[0].trim();
                String role = userFields[userFields.length - 1].trim();

                if ("student".equalsIgnoreCase(role)) {
                    systemLogic.deleteStudent(username);
                } else if ("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)) {
                    systemLogic.deleteUser(username, role);
                } else {
                    showAlert("Error", "Unknown role selected. Cannot delete.");
                }
            } else {
                showAlert("Error", "No user selected for deletion.");
            }
        });

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initAdminDashboard(primaryStage, systemLogic));

        layout.getChildren().addAll(loadUsersButton, loadStudentsButton, usersListView, editUserButton, deleteUserButton, backButton);
        layout.setStyle("-fx-padding: 20;");
        primaryStage.setScene(new Scene(layout, 600, 400));
    }


    private void openEditUserDialog(String userDetails, SystemLogic systemLogic) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Edit User");

        // Extract details from the selected user
        String[] userFields = userDetails.split(" \\| ");
        if (userFields.length < 2) {
            showAlert("Error", "Invalid user details.");
            return;
        }

        String username = userFields[0];
        String role = userFields[1].trim(); // Trim spaces

        // Default options for Admins/Teachers
        System.out.println("Opening admin/teacher edit dialog."); // Debugging
        Button editUsernameButton = new Button("Edit Username");
        Button editPasswordButton = new Button("Edit Password");

        // Edit Username Layout
        editUsernameButton.setOnAction(e -> {
            Stage usernameStage = new Stage();
            usernameStage.initModality(Modality.APPLICATION_MODAL);
            usernameStage.setTitle("Edit Username");

            TextField usernameField = new TextField(username);

            Button saveUsernameButton = new Button("Save");
            saveUsernameButton.setOnAction(ev -> {
                String newUsername = usernameField.getText();
                if (!newUsername.isEmpty()) {
                    systemLogic.updateUserUsername(username, newUsername, role);
                    usernameStage.close();
                    showAlert("Success", "Username updated successfully.");
                } else {
                    showAlert("Error", "Username cannot be empty.");
                }
            });

            VBox usernameLayout = new VBox(10, new Label("New Username:"), usernameField, saveUsernameButton);
            usernameLayout.setStyle("-fx-padding: 20;");
            usernameStage.setScene(new Scene(usernameLayout, 400, 200));
            usernameStage.showAndWait();
        });

        // Edit Password Layout
        editPasswordButton.setOnAction(e -> {
            Stage passwordStage = new Stage();
            passwordStage.initModality(Modality.APPLICATION_MODAL);
            passwordStage.setTitle("Edit Password");

            PasswordField passwordField = new PasswordField();

            Button savePasswordButton = new Button("Save");
            savePasswordButton.setOnAction(ev -> {
                String newPassword = passwordField.getText();
                if (!newPassword.isEmpty()) {
                    systemLogic.updateUserPassword(username, newPassword, role);
                    passwordStage.close();
                    showAlert("Success", "Password updated successfully.");
                } else {
                    showAlert("Error", "Password cannot be empty.");
                }
            });

            VBox passwordLayout = new VBox(10, new Label("New Password:"), passwordField, savePasswordButton);
            passwordLayout.setStyle("-fx-padding: 20;");
            passwordStage.setScene(new Scene(passwordLayout, 400, 200));
            passwordStage.showAndWait();
        });

        VBox dialogLayout = new VBox(10, new Label("Select an option to edit:"), editUsernameButton, editPasswordButton);
        dialogLayout.setStyle("-fx-padding: 20;");
        dialogStage.setScene(new Scene(dialogLayout, 300, 200));
        dialogStage.showAndWait();
    }

    public void openManageStudents(Stage primaryStage, SystemLogic systemLogic) {
        ListView<String> studentsListView = new ListView<>();
        VBox layout = new VBox(10);

        // Fetch all students
        Button loadStudentsButton = new Button("Load Students");
        loadStudentsButton.setOnAction(e -> {
            List<String> allStudents = systemLogic.getAllStudentsWithDetails();
            studentsListView.getItems().clear();
            if (allStudents.isEmpty()) {
                studentsListView.getItems().add("No Students found.");
            } else {
                studentsListView.getItems().addAll(allStudents);
            }
        });

        // Edit Student Button
        Button editStudentButton = new Button("Edit Student");
        editStudentButton.setOnAction(e -> {
            String selectedStudent = studentsListView.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                openEditStudentDialog(selectedStudent, systemLogic);
            } else {
                showAlert("Error", "No student selected for editing.");
            }
        });

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> initAdminDashboard(primaryStage, systemLogic));

        layout.getChildren().addAll(loadStudentsButton, studentsListView, editStudentButton, backButton);
        layout.setStyle("-fx-padding: 20;");
        primaryStage.setScene(new Scene(layout, 600, 400));
    }

    private void openEditStudentDialog(String studentDetails, SystemLogic systemLogic) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Edit Student");

        // Extract details from studentDetails
        String[] userFields = studentDetails.split("\\|");
        if (userFields.length < 7) { // Ensure all fields are present
            showAlert("Error", "Invalid student details. Please check the data.");
            return;
        }

        // Trim whitespace from each field
        for (int i = 0; i < userFields.length; i++) {
            userFields[i] = userFields[i].trim();
        }

        String username = userFields[0];
        String lastName = userFields[1];
        String firstName = userFields[2];
        String middleName = userFields[3];
        String yearLevel = userFields[4];
        String section = userFields[5];
        String role = userFields[6]; // Should be "student"

        // Buttons
        Button editNameButton = new Button("Edit Name");
        Button editYearLevelButton = new Button("Edit Year Level");
        Button editSectionButton = new Button("Edit Section");
        Button editUsernameButton = new Button("Edit Username");
        Button editPasswordButton = new Button("Edit Password");

        // Edit Name
        editNameButton.setOnAction(e -> {
            Stage nameStage = new Stage();
            nameStage.initModality(Modality.APPLICATION_MODAL);
            nameStage.setTitle("Edit Name");

            TextField lastNameField = new TextField(lastName);
            TextField firstNameField = new TextField(firstName);
            TextField middleNameField = new TextField(middleName);

            Button saveButton = new Button("Save");
            saveButton.setOnAction(ev -> {
                String newLastName = lastNameField.getText();
                String newFirstName = firstNameField.getText();
                String newMiddleName = middleNameField.getText();

                if (!newLastName.isEmpty() && !newFirstName.isEmpty()) {
                    systemLogic.updateStudentName(username, newLastName, newFirstName, newMiddleName);
                    showAlert("Success", "Name updated successfully.");
                    nameStage.close();
                } else {
                    showAlert("Error", "Last name and first name cannot be empty.");
                }
            });

            VBox layout = new VBox(10, 
                new Label("Last Name:"), lastNameField, 
                new Label("First Name:"), firstNameField, 
                new Label("Middle Name:"), middleNameField, 
                saveButton);
            layout.setStyle("-fx-padding: 20;");
            nameStage.setScene(new Scene(layout, 400, 300));
            nameStage.showAndWait();
        });

        // Edit Year Level
        editYearLevelButton.setOnAction(e -> {
            Stage yearStage = new Stage();
            yearStage.initModality(Modality.APPLICATION_MODAL);
            yearStage.setTitle("Edit Year Level");

            TextField yearField = new TextField(yearLevel);

            Button saveButton = new Button("Save");
            saveButton.setOnAction(ev -> {
                String newYearLevel = yearField.getText();
                if (!newYearLevel.isEmpty()) {
                    systemLogic.updateStudentYearLevel(username, newYearLevel);
                    showAlert("Success", "Year level updated successfully.");
                    yearStage.close();
                } else {
                    showAlert("Error", "Year level cannot be empty.");
                }
            });

            VBox layout = new VBox(10, new Label("New Year Level:"), yearField, saveButton);
            layout.setStyle("-fx-padding: 20;");
            yearStage.setScene(new Scene(layout, 300, 200));
            yearStage.showAndWait();
        });

        // Edit Section
        editSectionButton.setOnAction(e -> {
            Stage sectionStage = new Stage();
            sectionStage.initModality(Modality.APPLICATION_MODAL);
            sectionStage.setTitle("Edit Section");

            TextField sectionField = new TextField(section);

            Button saveButton = new Button("Save");
            saveButton.setOnAction(ev -> {
                String newSection = sectionField.getText();
                if (!newSection.isEmpty()) {
                    systemLogic.updateStudentSection(username, newSection);
                    showAlert("Success", "Section updated successfully.");
                    sectionStage.close();
                } else {
                    showAlert("Error", "Section cannot be empty.");
                }
            });

            VBox layout = new VBox(10, new Label("New Section:"), sectionField, saveButton);
            layout.setStyle("-fx-padding: 20;");
            sectionStage.setScene(new Scene(layout, 300, 200));
            sectionStage.showAndWait();
        });

        // Edit Username
        editUsernameButton.setOnAction(e -> {
            Stage usernameStage = new Stage();
            usernameStage.initModality(Modality.APPLICATION_MODAL);
            usernameStage.setTitle("Edit Username");

            TextField usernameField = new TextField(username);

            Button saveButton = new Button("Save");
            saveButton.setOnAction(ev -> {
                String newUsername = usernameField.getText().trim();
                if (!newUsername.isEmpty()) {
                    systemLogic.updateStudentUsername(username, newUsername);
                    showAlert("Success", "Username updated successfully.");
                    usernameStage.close();
                } else {
                    showAlert("Error", "Username cannot be empty.");
                }
            });

            VBox layout = new VBox(10, new Label("New Username:"), usernameField, saveButton);
            layout.setStyle("-fx-padding: 20;");
            usernameStage.setScene(new Scene(layout, 300, 200));
            usernameStage.showAndWait();
        });

        // Edit Password
        editPasswordButton.setOnAction(e -> {
            Stage passwordStage = new Stage();
            passwordStage.initModality(Modality.APPLICATION_MODAL);
            passwordStage.setTitle("Edit Password");

            PasswordField passwordField = new PasswordField();

            Button saveButton = new Button("Save");
            saveButton.setOnAction(ev -> {
                String newPassword = passwordField.getText().trim();
                if (!newPassword.isEmpty()) {
                    systemLogic.updateStudentPassword(username, newPassword);
                    showAlert("Success", "Password updated successfully.");
                    passwordStage.close();
                } else {
                    showAlert("Error", "Password cannot be empty.");
                }
            });

            VBox layout = new VBox(10, new Label("New Password:"), passwordField, saveButton);
            layout.setStyle("-fx-padding: 20;");
            passwordStage.setScene(new Scene(layout, 300, 200));
            passwordStage.showAndWait();
        });

        // Main Dialog Layout
        VBox dialogLayout = new VBox(10, 
            new Label("Select a detail to edit:"), 
            editNameButton, 
            editYearLevelButton, 
            editSectionButton, 
            editUsernameButton, 
            editPasswordButton);
        dialogLayout.setStyle("-fx-padding: 20;");
        dialogStage.setScene(new Scene(dialogLayout, 300, 250));
        dialogStage.showAndWait();
    }

    private String extractUsernameFromDetails(String details) {
        return details.split(" \\| ")[0].trim(); // Extracts the username
    }

    private String extractRoleFromDetails(String details) {
        return details.split(" \\| ")[1].trim(); // Extracts the role
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}