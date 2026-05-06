package edu.connexion3a36.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class MyConnection {

    private final String url="jdbc:mysql://localhost:3306/esportdevvvvvv-2?useSSL=false&serverTimezone=UTC";
    private final String login="root";
    private final String pwd="";

    private Connection cnx;

    public static MyConnection instance;

    private MyConnection(){
        try {
            cnx = DriverManager.getConnection(url,login,pwd);
            initializeSchema();
            ensureColumnExists("punition", "is_automatic", "TINYINT(1) DEFAULT 0");
            System.out.println("Connection établie!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initializeSchema() throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS personne ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nom VARCHAR(255) NOT NULL, "
                + "prenom VARCHAR(255) NOT NULL"
                + ")";

        try (Statement statement = cnx.createStatement()) {
            statement.executeUpdate(createTable);
        }
    }

    private void ensureColumnExists(String table, String column, String definition) {
        try (Statement statement = cnx.createStatement()) {
            // Check if column exists
            ResultSet rs = statement.executeQuery("SHOW COLUMNS FROM " + table + " LIKE '" + column + "'");
            if (!rs.next()) {
                System.out.println("Adding missing column '" + column + "' to table '" + table + "'...");
                statement.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            }
        } catch (SQLException e) {
            System.err.println("Warning: Could not check/add column " + column + ": " + e.getMessage());
        }
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(url, login, pwd);
            }
        } catch (SQLException e) {
            System.out.println("Error reconnecting: " + e.getMessage());
        }
        return cnx;
    }

    public static MyConnection getInstance(){
        if(instance == null){
            instance = new MyConnection();
        }
        return instance;
    }
}
