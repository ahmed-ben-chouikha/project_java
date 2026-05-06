package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;


    @FXML
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

            return;
        }

        if (!password.equals(confirmPassword)) {
            return;
        }

        try {
                RankUpApp.showLogin();
            } else {
            }
        } catch (Exception e) {
        }
    }

    @FXML
        RankUpApp.showLogin();
    }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
