package headadmin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;

public class AdminRegistration {

    // Database connection details
	private static final String DB_URL = "jdbc:sqlserver://localhost;databaseName=AttendanceDB;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "LMS_Admin";
    private static final String DB_PASSWORD = "enikin0322";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Prompt for Head Admin details
        System.out.println("Enter Head Admin Username: ");
        String username = scanner.nextLine();
        
        System.out.println("Enter Head Admin Password: ");
        String password = scanner.nextLine();
        
        System.out.println("Enter Head Admin Role (Put Admin): ");
        String role = scanner.nextLine();

        System.out.println("Enter Head Admin Email: ");
        String email = scanner.nextLine();

        // Call the method to register the head admin
        registerHeadAdmin(username, password, role, email);

        scanner.close();
    }

    private static void registerHeadAdmin(String username, String password, String role, String email) {
        // Hash the password using SHA-256
        String passwordHash = hashPassword(password);
        
        // SQL insert statements to add the new head admin
        String insertUserSQL = "INSERT INTO dbo.Users (username, role) VALUES (?, ?)";
        String insertAdminSQL = "INSERT INTO dbo.Admins (username, email, password, password_hash) VALUES (?, ?, ?, ?)";

        // Connect to the database and execute the queries
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmtUser = conn.prepareStatement(insertUserSQL);
             PreparedStatement stmtAdmin = conn.prepareStatement(insertAdminSQL)) {
            
            // Set parameters for the Users table
            stmtUser.setString(1, username);
            stmtUser.setString(2, role);
            
            // Set parameters for the Admins table
            stmtAdmin.setString(1, username);
            stmtAdmin.setString(2, email);
            stmtAdmin.setString(3, password);  // Actual password stored in Admins table
            stmtAdmin.setString(4, passwordHash); // Password hash stored in Admins table
            
            // Execute the insert queries
            int rowsAffectedUser = stmtUser.executeUpdate();
            int rowsAffectedAdmin = stmtAdmin.executeUpdate();

            // Check if the insertion was successful
            if (rowsAffectedUser > 0 && rowsAffectedAdmin > 0) {
                System.out.println("Head Admin registration successful!");
            } else {
                System.out.println("Failed to register Head Admin.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while registering the Head Admin: " + e.getMessage());
        }
    }

    // Method to hash the password using SHA-256
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
}