package edu.connexion3a36.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class MyConnection {

    private final String url="jdbc:mysql://localhost:3306/esportdevvvvvv?useSSL=false&serverTimezone=UTC";
    private final String login="root";
    private final String pwd="";

    private Connection cnx;

    public static MyConnection instance;

    private MyConnection(){
        try {
            cnx = DriverManager.getConnection(url,login,pwd);
            initializeSchema();
            System.out.println("Connection établie!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initializeSchema() throws SQLException {
        try (Statement statement = cnx.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS personne ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "nom VARCHAR(255) NOT NULL, "
                    + "prenom VARCHAR(255) NOT NULL"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS tournaments ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "tournament_name VARCHAR(255) NOT NULL, "
                    + "game_type VARCHAR(100) NOT NULL, "
                    + "start_date DATE NOT NULL, "
                    + "end_date DATE NOT NULL, "
                    + "max_teams INT NOT NULL, "
                    + "status ENUM('planned', 'upcoming', 'ongoing', 'completed', 'open', 'closed', 'finished') DEFAULT 'planned', "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS tournament_registrations ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "player_name VARCHAR(255) NOT NULL, "
                    + "team_name VARCHAR(255) NOT NULL, "
                    + "team_members TEXT, "
                    + "contact_info VARCHAR(500), "
                    + "tournament_id INT NOT NULL, "
                    + "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "status ENUM('pending', 'confirmed', 'rejected') DEFAULT 'pending', "
                    + "rejection_reason VARCHAR(500), "
                    + "FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE"
                    + ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS reviews ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "player_name VARCHAR(255) NOT NULL, "
                    + "tournament_id INT NOT NULL, "
                    + "tournament_name VARCHAR(255) NOT NULL, "
                    + "rating INT NOT NULL, "
                    + "comment TEXT NOT NULL, "
                    + "review_date DATE NOT NULL, "
                    + "status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending', "
                    + "rejection_reason VARCHAR(500), "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE"
                    + ")");

            statement.executeUpdate("ALTER TABLE reviews ADD COLUMN IF NOT EXISTS tournament_name VARCHAR(255) NOT NULL DEFAULT ''");
            statement.executeUpdate("ALTER TABLE tournaments MODIFY COLUMN status ENUM('planned', 'upcoming', 'ongoing', 'completed', 'open', 'closed', 'finished') DEFAULT 'planned'");
            statement.executeUpdate("ALTER TABLE tournament_registrations ADD COLUMN IF NOT EXISTS team_members TEXT");
            statement.executeUpdate("ALTER TABLE tournament_registrations ADD COLUMN IF NOT EXISTS contact_info VARCHAR(500)");
        }
    }

    public Connection getCnx() {
        return cnx;
    }

    public static MyConnection getInstance(){
        if(instance == null){
            instance = new MyConnection();
        }
        return instance;
    }
}
