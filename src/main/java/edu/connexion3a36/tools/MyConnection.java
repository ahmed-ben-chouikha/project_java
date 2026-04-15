package edu.connexion3a36.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class MyConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "esportdevvvvvv";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private final String url;
    private final String login;
    private final String pwd;

    private Connection cnx;

    public static MyConnection instance;

    private MyConnection(){
        String host = resolve("DB_HOST", "db.host", DEFAULT_HOST);
        String port = resolve("DB_PORT", "db.port", DEFAULT_PORT);
        String database = resolve("DB_NAME", "db.name", DEFAULT_DB);

        this.login = resolve("DB_USER", "db.user", DEFAULT_USER);
        this.pwd = resolve("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";

        try {
            cnx = DriverManager.getConnection(url,login,pwd);
            initializeSchema();
            System.out.println("Connection établie!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private String resolve(String envKey, String sysKey, String fallback) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        String sysValue = System.getProperty(sysKey);
        if (sysValue != null && !sysValue.trim().isEmpty()) {
            return sysValue.trim();
        }

        return fallback;
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
