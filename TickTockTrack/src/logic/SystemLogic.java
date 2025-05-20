package logic;

import database.DataBaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemLogic {

    // Verify login credentials for admins
	public boolean verifyLogin(String username, String password, String role) {
	    String query;

	    // Determine the query based on the role
	    if ("admin".equals(role)) {
	        query = "SELECT password_hash FROM Admins WHERE username = ?";
	    } else if ("teacher".equals(role)) {
	        query = "SELECT password_hash FROM Teachers WHERE username = ?";
	    } else if ("student".equals(role)) {
	        query = "SELECT password_hash FROM Students WHERE username = ?";
	    } else {
	        return false; // Invalid role
	    }

	    try (Connection connection = DataBaseConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	        preparedStatement.setString(1, username);

	        ResultSet resultSet = preparedStatement.executeQuery();
	        if (resultSet.next()) {
	            String storedHash = resultSet.getString("password_hash");
	            return validatePassword(password, storedHash);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // No match found
	}


	public boolean validatePassword(String inputPassword, String storedHash) {
	    String inputHash = hashPassword(inputPassword);
	    return storedHash.equals(inputHash);
	}
		
    // Hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean registerUser(String username, String lastName, String firstName, String middleName, String email, String password, String role, String yearLevel, String section, String absent_count) {
        String hashedPassword = hashPassword(password);  // Hash the password

        try (Connection connection = DataBaseConnection.getConnection()) {
            // Step 1: Insert into Users table (with dynamic role)
            String insertUserQuery = "INSERT INTO dbo.Users (username, role) VALUES (?, ?)";
            try (PreparedStatement userStatement = connection.prepareStatement(insertUserQuery)) {
                userStatement.setString(1, username);
                userStatement.setString(2, role);  // Set role dynamically (admin, teacher, student, etc.)
                userStatement.executeUpdate();
            }

            // Step 2: If the role is "Admin", insert into the Admins table
            if ("admin".equalsIgnoreCase(role)) {
                String insertAdminQuery = "INSERT INTO dbo.Admins (username, email, password, password_hash) VALUES (?, ?, ?, ?)";
                try (PreparedStatement adminStatement = connection.prepareStatement(insertAdminQuery)) {
                    adminStatement.setString(1, username);
                    adminStatement.setString(2, email);
                    adminStatement.setString(3, password);  // Store actual password (can be omitted if only hashed password is stored)
                    adminStatement.setString(4, hashedPassword);  // Store hashed password
                    adminStatement.executeUpdate();
                }
            }

            // Step 3: If the role is "Teacher", insert into Teachers table (assuming they have similar columns as Admins)
            if ("teacher".equalsIgnoreCase(role)) {
                String insertTeacherQuery = "INSERT INTO dbo.Teachers (username, email, password, password_hash) VALUES (?, ?, ?, ?)";
                try (PreparedStatement teacherStatement = connection.prepareStatement(insertTeacherQuery)) {
                    teacherStatement.setString(1, username);
                    teacherStatement.setString(2, email);
                    teacherStatement.setString(3, password);  // Store actual password (can be omitted if only hashed password is stored)
                    teacherStatement.setString(4, hashedPassword);  // Store hashed password
                    teacherStatement.executeUpdate();
                }
            }

            // Step 4: If the role is "Student", insert into Students table
            if ("student".equalsIgnoreCase(role)) {
                String insertStudentQuery = "INSERT INTO dbo.Students (username, last_name, first_name, middle_name, email, password, password_hash, year_level, section, absent_count, attendance_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement studentStatement = connection.prepareStatement(insertStudentQuery)) {
                    studentStatement.setString(1, username);
                    studentStatement.setString(2, lastName);
                    studentStatement.setString(3, firstName);
                    studentStatement.setString(4, middleName);
                    studentStatement.setString(5, email);
                    studentStatement.setString(6, password);  // Store actual password
                    studentStatement.setString(7, hashedPassword);  // Store hashed password
                    studentStatement.setString(8, yearLevel);
                    studentStatement.setString(9, section);
                    studentStatement.setInt(10, 0); // Default absent_count as 0
                    studentStatement.setString(11, "Good");  // Default attendance status, can be adjusted                    
                    studentStatement.executeUpdate();
                }
            }

            return true;  // Registration successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Registration failed
        }
    }


    // Fetch users by role and return their usernames
 // Fetch users by role and return their usernames and passwords
    public List<String> getUsersByRole(String role) {
        List<String> userList = new ArrayList<>();
        String tableName;

        // Map roles to corresponding database tables
        switch (role.toLowerCase()) {
            case "admin":
                tableName = "dbo.Admins";
                break;
            case "teacher":
                tableName = "dbo.Teachers";
                break;
            default:
                System.err.println("Invalid role provided: " + role);
                return userList; // Return an empty list for invalid roles
        }

        // Query to fetch both username and password
        String query = "SELECT username, password FROM " + tableName;

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                // Format as: Username: <username>, Password: <password>
                String userDetails = String.format("Username: %s, Password: %s", username, password);
                userList.add(userDetails);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users for role: " + role);
            e.printStackTrace();
        }

        return userList;
    }

    // Fetch and format student full names with their usernames and passwords
    public List<String> getFormattedStudentDetails() {
        List<String> studentDetails = new ArrayList<>();
        String query = "SELECT last_name, first_name, middle_name, username, password FROM dbo.Students";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String lastName = resultSet.getString("last_name");
                String firstName = resultSet.getString("first_name");
                String middleName = resultSet.getString("middle_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                // Format as: Name: <last_name>, <first_name> <middle_name>, Username: <username>, Password: <password>
                String formattedDetails = String.format(
                        "Name: %s, %s%s, Username: %s, Password: %s",
                        lastName, firstName, 
                        (middleName != null && !middleName.isEmpty() ? " " + middleName : ""),
                        username, password
                );
                studentDetails.add(formattedDetails);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student details.");
            e.printStackTrace();
        }

        return studentDetails;
    }

    // Fetch password for a given username
    public String getPasswordByUsername(String username) {
        String password = null;
        String query = "SELECT password FROM (" +
                       "    SELECT username, password FROM dbo.Admins " +
                       "    UNION ALL " +
                       "    SELECT username, password FROM dbo.Teachers " +
                       "    UNION ALL " +
                       "    SELECT username, password FROM dbo.Students " +
                       ") AS CombinedUsers WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            System.out.println("Executing query: " + preparedStatement);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    password = resultSet.getString("password");
                    System.out.println("Password found for username: " + username + " => " + password);
                } else {
                    System.err.println("No matching user found for username: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching password for username: " + username);
            e.printStackTrace();
        }

        return password;
    }

    // Helper method to display user list
    public void displayUsers(List<String> userList) {
        for (String user : userList) {
            System.out.println(user);
        }
    }

    // Helper method to display student details
    public void displayStudentDetails(List<String> studentDetails) {
        for (String detail : studentDetails) {
            System.out.println(detail);
        }
    }
    
    public List<String> getAdminsAndTeachersWithDetails() {
        List<String> userDetails = new ArrayList<>();
        String query = "SELECT username, role FROM dbo.Users WHERE role IN ('admin', 'teacher')";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String role = resultSet.getString("role");
                userDetails.add(username + " | " + role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userDetails;
    }

    public List<String> getAllStudentsWithDetails() {
        List<String> studentDetails = new ArrayList<>();
        String query = "SELECT username, last_name, first_name, middle_name, year_level, section FROM dbo.Students";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String lastName = resultSet.getString("last_name");
                String firstName = resultSet.getString("first_name");
                String middleName = resultSet.getString("middle_name");
                String yearLevel = resultSet.getString("year_level");
                String section = resultSet.getString("section");

                // Append role as "Student"
                studentDetails.add(username + " | " + lastName + " | " + firstName + " | " 
                                   + middleName + " | " + yearLevel + " | " + section + " | student");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentDetails;
    }


    public void updateUserUsername(String oldUsername, String newUsername, String role) {
        String tableName;
        switch (role.toLowerCase()) {
            case "admin":
                tableName = "dbo.Admins";
                break;
            case "teacher":
                tableName = "dbo.Teachers";
                break;
            default:
                System.err.println("Invalid role: " + role);
                return;
        }

        String updateUsersQuery = "UPDATE dbo.Users SET username = ? WHERE username = ?";
        String updateRoleQuery = "UPDATE " + tableName + " SET username = ? WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Update Users table
            try (PreparedStatement userStmt = connection.prepareStatement(updateUsersQuery)) {
                userStmt.setString(1, newUsername);
                userStmt.setString(2, oldUsername);
                int userRows = userStmt.executeUpdate();

                if (userRows == 0) {
                    System.err.println("No record found in dbo.Users for username: " + oldUsername);
                    connection.rollback();
                    return;
                }
            }

            // Update role-specific table
            try (PreparedStatement roleStmt = connection.prepareStatement(updateRoleQuery)) {
                roleStmt.setString(1, newUsername);
                roleStmt.setString(2, oldUsername);
                int roleRows = roleStmt.executeUpdate();

                if (roleRows == 0) {
                    System.err.println("No record found in " + tableName + " for username: " + oldUsername);
                    connection.rollback();
                    return;
                }
            }

            connection.commit();
        } catch (SQLException e) {
            System.err.println("Error during username update for: " + oldUsername);
            e.printStackTrace();
        }
    }

    public void updateUserPassword(String username, String newPassword, String role) {
        String tableName;
        switch (role.toLowerCase()) {
            case "admin":
                tableName = "dbo.Admins";
                break;
            case "teacher":
                tableName = "dbo.Teachers";
                break;
            default:
                System.err.println("Invalid role: " + role);
                return;
        }

        String updateRoleQuery = "UPDATE " + tableName + " SET password_hash = ?, password = ? WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection()) {
            try (PreparedStatement roleStmt = connection.prepareStatement(updateRoleQuery)) {
                roleStmt.setString(1, hashPassword(newPassword));
                roleStmt.setString(2, newPassword);
                roleStmt.setString(3, username);
                int roleRows = roleStmt.executeUpdate();

                if (roleRows == 0) {
                    System.err.println("No record found in " + tableName + " for username: " + username);
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during password update for: " + username);
            e.printStackTrace();
        }
    }

    public void updateStudentName(String username, String lastName, String firstName, String middleName) {
        String updateQuery = "UPDATE dbo.Students SET last_name = ?, first_name = ?, middle_name = ? WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, middleName);
            stmt.setString(4, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[SUCCESS] Updated name for username: " + username);
            } else {
                System.err.println("[ERROR] No record found for username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update name for username: " + username);
            e.printStackTrace();
        }
    }

    public void updateStudentYearLevel(String username, String yearLevel) {
        String updateQuery = "UPDATE dbo.Students SET year_level = ? WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setString(1, yearLevel);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[SUCCESS] Updated year level for username: " + username);
            } else {
                System.err.println("[ERROR] No record found for username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update year level for username: " + username);
            e.printStackTrace();
        }
    }

    public void updateStudentSection(String username, String section) {
        String updateQuery = "UPDATE dbo.Students SET section = ? WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setString(1, section);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[SUCCESS] Updated section for username: " + username);
            } else {
                System.err.println("[ERROR] No record found for username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update section for username: " + username);
            e.printStackTrace();
        }
    }
    
    public void updateStudentUsername(String oldUsername, String newUsername) {
        String updateUsersQuery = "UPDATE dbo.Users SET username = ? WHERE username = ?";
        String updateStudentsQuery = "UPDATE dbo.Students SET username = ? WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement userStmt = connection.prepareStatement(updateUsersQuery)) {
                userStmt.setString(1, newUsername);
                userStmt.setString(2, oldUsername);
                int userRows = userStmt.executeUpdate();

                if (userRows == 0) {
                    System.err.println("[ERROR] No record found in dbo.Users for username: " + oldUsername);
                    connection.rollback();
                    return;
                }
            }

            try (PreparedStatement studentStmt = connection.prepareStatement(updateStudentsQuery)) {
                studentStmt.setString(1, newUsername);
                studentStmt.setString(2, oldUsername);
                int studentRows = studentStmt.executeUpdate();

                if (studentRows == 0) {
                    System.err.println("[ERROR] No record found in dbo.Students for username: " + oldUsername);
                    connection.rollback();
                    return;
                }
            }

            connection.commit(); // Commit transaction
            System.out.println("[SUCCESS] Updated username from " + oldUsername + " to " + newUsername);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update username from " + oldUsername + " to " + newUsername);
            e.printStackTrace();
        }
    }

    public void updateStudentPassword(String username, String newPassword) {
        String updateQuery = "UPDATE dbo.Students SET password_hash = ?, password = ? WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setString(1, hashPassword(newPassword));
            stmt.setString(2, newPassword); // Avoid storing plain text passwords unless necessary
            stmt.setString(3, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[SUCCESS] Updated password for username: " + username);
            } else {
                System.err.println("[ERROR] No record found for username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update password for username: " + username);
            e.printStackTrace();
        }
    }

    public void deleteUser(String username, String role) {
        String tableName;

        // Determine the table based on role
        switch (role.toLowerCase()) {
            case "admin":
                tableName = "dbo.Admins";
                break;
            case "teacher":
                tableName = "dbo.Teachers";
                break;
            default:
                System.err.println("Invalid role for deleteUser: " + role);
                return; // Exit for unsupported roles
        }

        String deleteRoleQuery = "DELETE FROM " + tableName + " WHERE username = ?";
        String deleteUsersQuery = "DELETE FROM dbo.Users WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Delete from role-specific table
            try (PreparedStatement roleStmt = connection.prepareStatement(deleteRoleQuery)) {
                roleStmt.setString(1, username);
                int roleRows = roleStmt.executeUpdate();

                if (roleRows == 0) {
                    System.err.println("No record found in " + tableName + " for username: " + username);
                    connection.rollback();
                    return;
                }
            }

            // Delete from Users table
            try (PreparedStatement userStmt = connection.prepareStatement(deleteUsersQuery)) {
                userStmt.setString(1, username);
                int userRows = userStmt.executeUpdate();

                if (userRows == 0) {
                    System.err.println("No record found in dbo.Users for username: " + username);
                    connection.rollback();
                    return;
                }
            }

            connection.commit();
            System.out.println("Successfully deleted user: " + username);
        } catch (SQLException e) {
            System.err.println("Error during delete operation for username: " + username);
            e.printStackTrace();
        }
    }

    public void deleteStudent(String username) {
        String deleteStudentQuery = "DELETE FROM dbo.Students WHERE username = ?";
        String deleteUsersQuery = "DELETE FROM dbo.Users WHERE username = ?";

        try (Connection connection = DataBaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Delete from Students table
            try (PreparedStatement studentStmt = connection.prepareStatement(deleteStudentQuery)) {
                studentStmt.setString(1, username);
                int studentRows = studentStmt.executeUpdate();

                if (studentRows == 0) {
                    System.err.println("No record found in dbo.Students for username: " + username);
                    connection.rollback();
                    return;
                }
            }

            // Delete from Users table
            try (PreparedStatement userStmt = connection.prepareStatement(deleteUsersQuery)) {
                userStmt.setString(1, username);
                int userRows = userStmt.executeUpdate();

                if (userRows == 0) {
                    System.err.println("No record found in dbo.Users for username: " + username);
                    connection.rollback();
                    return;
                }
            }

            connection.commit();
            System.out.println("Successfully deleted student: " + username);
        } catch (SQLException e) {
            System.err.println("Error during delete operation for student: " + username);
            e.printStackTrace();
        }
    }
}