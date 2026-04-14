package edu.connexion3a36.Controller;

import edu.connexion3a36.services.EsportsCatalogService;
import edu.connexion3a36.services.EsportsCatalogService.TournamentCard;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AfficherPersonne2Controller {

    @FXML
    private FlowPane tournamentCardsHost;

    @FXML
    private VBox joinCardHost;

    @FXML
    private VBox roleCardHost;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Button backButton;

    @FXML
    private ToggleButton userModeButton;

    @FXML
    private ToggleButton adminModeButton;

    private final ChoiceBox<String> tournamentChoice = new ChoiceBox<>();

    private final javafx.scene.control.TextField gamerTagField = new javafx.scene.control.TextField();

    private final javafx.scene.control.TextField teamField = new javafx.scene.control.TextField();

    private final EsportsCatalogService catalog = new EsportsCatalogService();
    private boolean adminMode;

    @FXML
    void initialize() {
        adminMode = false;
        userModeButton.setSelected(true);
        tournamentCardsHost.setHgap(16);
        tournamentCardsHost.setVgap(16);
        tournamentCardsHost.getChildren().clear();
        catalog.getUpcomingTournaments().forEach(tournament -> tournamentCardsHost.getChildren().add(createTournamentCard(tournament)));

        java.util.List<String> tournamentNames = catalog.getUpcomingTournaments().stream()
                .map(t -> t.name)
                .collect(java.util.stream.Collectors.toList());
        tournamentChoice.setItems(FXCollections.observableArrayList(tournamentNames));
        if (!tournamentChoice.getItems().isEmpty()) {
            tournamentChoice.setValue(tournamentChoice.getItems().get(0));
        }
        gamerTagField.setPromptText("Your gamer tag");
        teamField.setPromptText("Your team name");

        joinCardHost.getChildren().setAll(createJoinCard());
        roleCardHost.getChildren().setAll(createRoleCard());
    }

    @FXML
    void backToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPersonne.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showMessage("Unable to return to dashboard", e.getMessage());
        }
    }

    @FXML
    void showUserMode(ActionEvent event) {
        adminMode = false;
        userModeButton.setSelected(true);
        adminModeButton.setSelected(false);
        roleCardHost.getChildren().setAll(createRoleCard());
    }

    @FXML
    void showAdminMode(ActionEvent event) {
        adminMode = true;
        adminModeButton.setSelected(true);
        userModeButton.setSelected(false);
        roleCardHost.getChildren().setAll(createRoleCard());
    }

    @FXML
    void joinTournament(ActionEvent event) {
        String tournament = tournamentChoice.getValue();
        String gamerTag = gamerTagField.getText().trim();
        String team = teamField.getText().trim();

        if (tournament == null || tournament.trim().isEmpty()) {
            showMessage("Join tournament", "Please choose a tournament first.");
            return;
        }
        if (gamerTag.isEmpty() || team.isEmpty()) {
            showMessage("Join tournament", "Please enter your gamer tag and team name.");
            return;
        }

        feedbackLabel.setText("Registered " + gamerTag + " from " + team + " for " + tournament + ".");
        showMessage("Tournament registration", feedbackLabel.getText());
    }

    private VBox createJoinCard() {
        VBox card = createCard();
        card.getChildren().addAll(
                createTitle("Register your squad"),
                createSubtitle("Fill in the form and secure a spot in an open tournament."),
                labeledField("Tournament", tournamentChoice),
                labeledField("Gamer tag", gamerTagField),
                labeledField("Team name", teamField),
                createPrimaryButton("Join tournament", this::joinTournament)
        );
        return card;
    }

    private VBox createRoleCard() {
        VBox card = createCard();
        if (adminMode) {
            card.getChildren().addAll(
                    createTitle("Admin focus"),
                    createSubtitle("Monitor registrations, bracket updates, and community notifications."),
                    createBullet("Approve new rosters"),
                    createBullet("Update match results"),
                    createBullet("Publish event announcements")
            );
        } else {
            card.getChildren().addAll(
                    createTitle("Player focus"),
                    createSubtitle("See the best tournaments, open slots, and team-friendly events."),
                    createBullet("Reserve your tournament slot"),
                    createBullet("Check match start times"),
                    createBullet("Track prize pools and rules")
            );
        }
        return card;
    }

    private VBox createTournamentCard(TournamentCard tournament) {
        VBox card = createCard();
        Label title = createTitle(tournament.name);
        Label subtitle = createSubtitle(tournament.game);
        Label details = new Label(tournament.date + " • " + tournament.prizePool + " • " + tournament.slots);
        details.getStyleClass().add("card-detail");
        Button action = createPrimaryButton(tournament.openForJoin ? "Join" : "Waitlist", e -> {
            tournamentChoice.setValue(tournament.name);
            showMessage("Tournament hub", tournament.name + " selected. Complete the form to join.");
        });
        if (!tournament.openForJoin) {
            action.getStyleClass().add("secondary-action");
        }
        card.getChildren().addAll(title, subtitle, details, action);
        return card;
    }

    private VBox createCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("glass-card");
        card.setPadding(new Insets(20));
        return card;
    }

    private Label createTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("card-title");
        label.setWrapText(true);
        return label;
    }

    private Label createSubtitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("card-subtitle");
        label.setWrapText(true);
        return label;
    }

    private Label createBullet(String text) {
        Label label = new Label("• " + text);
        label.getStyleClass().add("action-line");
        label.setWrapText(true);
        return label;
    }

    private HBox labeledField(String labelText, javafx.scene.Node field) {
        VBox box = new VBox(6);
        box.getChildren().addAll(createSubtitle(labelText), field);
        HBox wrapper = new HBox(box);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(box, javafx.scene.layout.Priority.ALWAYS);
        if (field instanceof Region) {
            ((Region) field).setPrefWidth(360);
        }
        return wrapper;
    }

    private Button createPrimaryButton(String text, javafx.event.EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-action");
        button.setOnAction(handler);
        return button;
    }

    private void showMessage(String title, String message) {
        feedbackLabel.setText(message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
