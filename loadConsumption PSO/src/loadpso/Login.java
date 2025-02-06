

package loadpso;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;
    
    public Login() {
        initializeDatabase();
        createLoginPanel();
    }
    
//    private void initializeDatabase() {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection(
//                "jdbc:mysql://localhost:3306/cloud_app",
//                "root",
//                "Joy_boy_611"
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    private void createLoginPanel() {
        setTitle("Cloud VM Allocation System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");
        
        // Add components to panel
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
        
        gbc.gridy = 3;
        panel.add(signupButton, gbc);
        
        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> showSignupDialog());
        
        add(panel);
        setLocationRelativeTo(null);
    }
    
private void handleLogin() {
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());
    
    try {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT * FROM users WHERE username = ? AND password = ?"
        );
        stmt.setString(1, username);
        stmt.setString(2, password);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            this.dispose(); // Close login window
            Main.launchMainFrame(username); // Pass username to main frame
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    private void showSignupDialog() {
        JDialog dialog = new JDialog(this, "Sign Up", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JTextField newUsername = new JTextField(20);
        JPasswordField newPassword = new JPasswordField(20);
        JButton submitButton = new JButton("Create Account");
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(newUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(newPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(submitButton, gbc);
        
        submitButton.addActionListener(e -> {
            createNewUser(newUsername.getText(), new String(newPassword.getPassword()));
            dialog.dispose();
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    

    
    private void openVMAllocationSystem(String username) {
        this.dispose();
        VMAllocation vmSystem = new VMAllocation();
        vmSystem.setCurrentUser(username);
        vmSystem.setVisible(true);
    }
    public void showLoginFrame() {
    setVisible(true);
}
    private void initializeDatabase() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/cloud_app",
            "root",
            "Joy_boy_611"
        );
        System.out.println("Database connected successfully");
        
        Statement stmt = connection.createStatement();
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "username VARCHAR(50) UNIQUE NOT NULL," +
            "password VARCHAR(50) NOT NULL" +
            ")";
        stmt.execute(createTableSQL);
        System.out.println("Users table created/verified");
        
    } catch (Exception e) {
        System.out.println("Database connection failed: " + e.getMessage());
        e.printStackTrace();
    }
}
    private void createNewUser(String username, String password) {
    try {
        connection.setAutoCommit(false);
        PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO users (username, password) VALUES (?, ?)"
        );
        stmt.setString(1, username);
        stmt.setString(2, password);
        
        int rowsAffected = stmt.executeUpdate();
        connection.commit();
        
        if(rowsAffected > 0) {
            System.out.println("User " + username + " inserted into database");
            JOptionPane.showMessageDialog(this, "Account created successfully!");
        } else {
            connection.rollback();
            JOptionPane.showMessageDialog(this, "Failed to create account");
        }
    } catch (SQLException e) {
        try {
            connection.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("Database error: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "Error creating user: " + e.getMessage());
    }
}


}


