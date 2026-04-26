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
        String createTable = "CREATE TABLE IF NOT EXISTS personne ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nom VARCHAR(255) NOT NULL, "
                + "prenom VARCHAR(255) NOT NULL"
                + ")";

        try (Statement statement = cnx.createStatement()) {
            statement.executeUpdate(createTable);
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
