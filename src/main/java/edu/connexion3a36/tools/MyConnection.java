package edu.connexion3a36.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    // Keep short DB timeouts to avoid long UI hangs during synchronous page loads.
    private String url="jdbc:mysql://localhost:3306/esportdevvvvvv?useSSL=false&serverTimezone=UTC&connectTimeout=5000&socketTimeout=5000";
    private String login="root";
    private String pwd="";

    private Connection cnx;

    public static MyConnection instance;

    private MyConnection(){
        try {
            cnx = DriverManager.getConnection(url,login,pwd);
            System.out.println("Connection établie!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
