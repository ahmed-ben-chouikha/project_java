package edu.connexion3a36.Controller;

import edu.connexion3a36.services.EsportsCatalogService;
import edu.connexion3a36.services.EsportsCatalogService.MatchCard;
import edu.connexion3a36.services.EsportsCatalogService.StatCard;
import edu.connexion3a36.services.EsportsCatalogService.TeamCard;
import edu.connexion3a36.services.EsportsCatalogService.TournamentCard;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AjouterPersonneController {

    @FXML
    private StackPane contentHost;

    @FXML
    private ScrollPane contentScroll;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button matchesButton;

    @FXML
    private Button tournamentsButton;

    @FXML
    private Button teamsButton;

    @FXML
    private Button userButton;

    @FXML
    private Button adminButton;

    @FXML
    private Button openTournamentButton;

    @FXML
    private ToggleButton userModeButton;

    @FXML
    private ToggleButton adminModeButton;

    @FXML
    private Label pageTitle;

    @FXML
    private Label pageSubtitle;

    private final EsportsCatalogService catalog = new EsportsCatalogService();
    private List<Button> navButtons;
    private boolean adminMode;

    @FXML
    void initialize() {
        navButtons = List.of(dashboardButton, matchesButton, tournamentsButton, teamsButton, userButton, adminButton);
        navButtons.forEach(button -> button.getStyleClass().add("nav-button"));
        openTournamentButton.getStyleClass().add("primary-action");
        userModeButton.getStyleClass().add("role-toggle");
        adminModeButton.getStyleClass().add("role-toggle");
        userModeButton.setSelected(true);
        adminMode = false;
        showDashboard(null);
    }

    @FXML
    void showDashboard(ActionEvent event) {
        pageTitle.setText("Esports Dashboard");
        pageSubtitle.setText("Live matches, upcoming tournaments, and team spotlights in one modern hub");
        highlightNav(dashboardButton);
        displayPage(createDashboardPage());
        userModeButton.setSelected(!adminMode);
        adminModeButton.setSelected(adminMode);
    }

    @FXML
    void showMatches(ActionEvent event) {
        pageTitle.setText("Match Center");
        pageSubtitle.setText("Track live battles, upcoming showdowns, and quick broadcast actions");
        highlightNav(matchesButton);
        displayPage(createMatchesPage());
    }

    @FXML
    void showTeams(ActionEvent event) {
        pageTitle.setText("Featured Teams");
        pageSubtitle.setText("Explore rosters, regions, and competitive records");
        highlightNav(teamsButton);
        displayPage(createTeamsPage());
    }

    @FXML
    void showUserArea(ActionEvent event) {
        adminMode = false;
        userModeButton.setSelected(true);
        adminModeButton.setSelected(false);
        pageTitle.setText("User Area");
        pageSubtitle.setText("Your profile, rewards, and tournament registration shortcuts");
        highlightNav(userButton);
        displayPage(createUserPage());
    }

    @FXML
    void showAdminArea(ActionEvent event) {
        adminMode = true;
        adminModeButton.setSelected(true);
        userModeButton.setSelected(false);
        pageTitle.setText("Admin Console");
        pageSubtitle.setText("Approve teams, schedule brackets, and manage the broadcast queue");
        highlightNav(adminButton);
        displayPage(createAdminPage());
    }

    @FXML
    void openTournamentHub(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPersonne2.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showAlert("Unable to open tournament hub", e.getMessage());
        }
    }

    private void displayPage(Node page) {
        contentHost.getChildren().setAll(page);
        StackPane.setAlignment(page, Pos.TOP_CENTER);
        if (contentScroll != null) {
            contentScroll.setVvalue(0);
        }
    }

    private void highlightNav(Button activeButton) {
        navButtons.forEach(button -> button.getStyleClass().remove("nav-active"));
        activeButton.getStyleClass().add("nav-active");
    }

    private VBox createDashboardPage() {
        VBox page = createPageContainer();
        page.getChildren().add(createHeroCard());
        page.getChildren().add(createStatsRow());
        page.getChildren().add(createPreviewSection("Live Matches", "Follow the action across the biggest brackets", createMatchCards(catalog.getFeaturedMatches().subList(0, 2))));
        page.getChildren().add(createPreviewSection("Upcoming Tournaments", "Open slots and prize pools for teams ready to join", createTournamentCards(catalog.getUpcomingTournaments().subList(0, 2))));
        page.getChildren().add(createPreviewSection("Featured Teams", "Top rosters ready for the next season", createTeamCards(catalog.getFeaturedTeams())));
        page.getChildren().add(createRolePanel());
        return page;
    }

    private VBox createMatchesPage() {
        VBox page = createPageContainer();
        page.getChildren().add(createSectionHeader("Match Center", "See live and upcoming esports matchups with broadcast-ready actions"));
        FlowPane cards = createFlowPane();
        for (MatchCard match : catalog.getFeaturedMatches()) {
            cards.getChildren().add(createMatchCard(match));
        }
        page.getChildren().add(cards);
        page.getChildren().add(createActionStrip("Broadcast queue", "Pin the next match, publish highlights, or switch to admin review.", "Open tournament hub", e -> openTournamentHub(e)));
        return page;
    }

    private VBox createTeamsPage() {
        VBox page = createPageContainer();
        page.getChildren().add(createSectionHeader("Teams", "Competitive rosters, regions, and recent records"));
        FlowPane cards = createFlowPane();
        for (TeamCard team : catalog.getFeaturedTeams()) {
            cards.getChildren().add(createTeamCard(team));
        }
        page.getChildren().add(cards);
        return page;
    }

    private VBox createUserPage() {
        VBox page = createPageContainer();
        page.getChildren().add(createSectionHeader("User Area", "A clean player dashboard for brackets, rewards, and registrations"));
        HBox row = new HBox(18);
        row.getChildren().addAll(createUserProfileCard(), createUserActionsCard());
        page.getChildren().add(row);
        page.getChildren().add(createPreviewSection("Recommended Tournaments", "Join one of the open events below", createTournamentCards(catalog.getUpcomingTournaments())));
        return page;
    }

    private VBox createAdminPage() {
        VBox page = createPageContainer();
        page.getChildren().add(createSectionHeader("Admin Console", "Control brackets, approve entries, and keep the site polished"));
        HBox row = new HBox(18);
        row.getChildren().addAll(createAdminStatsCard(), createAdminTasksCard());
        page.getChildren().add(row);
        page.getChildren().add(createPreviewSection("Moderation Queue", "Suggested actions for the next admin shift", createAdminQueueCards()));
        return page;
    }

    private VBox createHeroCard() {
        VBox card = createCard();
        Label eyebrow = new Label("Season 12 live now");
        eyebrow.getStyleClass().add("eyebrow");

        Label title = new Label("Compete, watch, and join the bracket in one sleek esports hub");
        title.getStyleClass().add("hero-title");
        title.setWrapText(true);

        Label copy = new Label("A modern dashboard for matches, tournaments, teams, and role-based admin tools built with JavaFX.");
        copy.getStyleClass().add("hero-copy");
        copy.setWrapText(true);

        HBox actions = new HBox(12);
        Button joinNow = createPrimaryButton("Join Tournament Hub", e -> openTournamentHub(e));
        Button exploreMatches = createSecondaryButton("View Matches", this::showMatches);
        actions.getChildren().addAll(joinNow, exploreMatches);

        card.getChildren().addAll(eyebrow, title, copy, actions);
        return card;
    }

    private HBox createStatsRow() {
        HBox row = new HBox(18);
        row.getChildren().addAll(catalog.getOverviewStats().stream().map(this::createStatCard).toList());
        return row;
    }

    private VBox createPreviewSection(String title, String subtitle, FlowPane cards) {
        VBox section = createCard();
        section.getChildren().addAll(createSectionHeader(title, subtitle), cards);
        return section;
    }

    private VBox createRolePanel() {
        VBox card = createCard();
        card.getChildren().add(createSectionHeader(adminMode ? "Admin Snapshot" : "User Snapshot", adminMode ? "Tools for moderation and scheduling" : "Your tournament shortcuts and highlights"));
        card.getChildren().add(adminMode ? createAdminMiniGrid() : createUserMiniGrid());
        return card;
    }

    private VBox createUserProfileCard() {
        VBox card = createCard();
        card.getChildren().add(createSectionHeader("Player profile", "Join events, keep your streak alive, and track wins"));
        card.getChildren().addAll(
                createKeyValueRow("Gamertag", "NebulaRush"),
                createKeyValueRow("Rank", "Diamond III"),
                createKeyValueRow("Status", "Eligible for 3 open tournaments")
        );
        return card;
    }

    private VBox createUserActionsCard() {
        VBox card = createCard();
        card.getChildren().add(createSectionHeader("Fast actions", "Everything a player needs in one place"));
        card.getChildren().addAll(
                createActionLine("Reserve a spot in the next bracket"),
                createActionLine("Check your match reminders"),
                createActionLine("Open tournament hub for registration")
        );
        card.getChildren().add(createPrimaryButton("Open Join Page", e -> openTournamentHub(e)));
        return card;
    }

    private VBox createAdminStatsCard() {
        VBox card = createCard();
        card.getChildren().add(createSectionHeader("Operations", "The admin room at a glance"));
        card.getChildren().addAll(
                createKeyValueRow("Pending approvals", "4 rosters"),
                createKeyValueRow("Live brackets", "2 in progress"),
                createKeyValueRow("Moderation queue", "6 tickets")
        );
        return card;
    }

    private VBox createAdminTasksCard() {
        VBox card = createCard();
        card.getChildren().add(createSectionHeader("Admin tools", "Quick tasks for a clean event flow"));
        card.getChildren().addAll(
                createActionLine("Approve joining teams"),
                createActionLine("Schedule stream overlays"),
                createActionLine("Publish next tournament update")
        );
        card.getChildren().add(createSecondaryButton("Open tournaments", e -> openTournamentHub(e)));
        return card;
    }

    private FlowPane createAdminQueueCards() {
        FlowPane cards = createFlowPane();
        cards.getChildren().add(createMiniQueueCard("Roster review", "Nova Crew", "Approve new squad details"));
        cards.getChildren().add(createMiniQueueCard("Bracket sync", "Quarterfinal 2", "Update the match result"));
        cards.getChildren().add(createMiniQueueCard("Community notice", "Discord banner", "Publish the next live date"));
        return cards;
    }

    private VBox createMiniQueueCard(String title, String subject, String detail) {
        VBox card = createCard();
        Label head = new Label(title);
        head.getStyleClass().add("card-title");
        Label subjectLabel = new Label(subject);
        subjectLabel.getStyleClass().add("card-subtitle");
        Label detailLabel = new Label(detail);
        detailLabel.setWrapText(true);
        card.getChildren().addAll(head, subjectLabel, detailLabel);
        return card;
    }

    private VBox createUserMiniGrid() {
        VBox box = new VBox(10);
        box.getChildren().addAll(
                createMiniActionCard("Join streak", "Keep your weekly attendance bonus active"),
                createMiniActionCard("Personal rewards", "Redeem player drops and badges"),
                createMiniActionCard("Open tournaments", "See slots that still accept teams")
        );
        return box;
    }

    private VBox createAdminMiniGrid() {
        VBox box = new VBox(10);
        box.getChildren().addAll(
                createMiniActionCard("Approve teams", "Validate roster names and regions"),
                createMiniActionCard("Moderate chat", "Keep the event stream clean"),
                createMiniActionCard("Schedule finals", "Lock the next broadcast window")
        );
        return box;
    }

    private VBox createMiniActionCard(String title, String subtitle) {
        VBox card = new VBox(6);
        card.getStyleClass().add("mini-card");
        card.getChildren().addAll(labelWithStyle(title, "card-title"), labelWithStyle(subtitle, "card-subtitle"));
        return card;
    }

    private VBox createCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("glass-card");
        card.setPadding(new Insets(22));
        return card;
    }

    private VBox createPageContainer() {
        VBox page = new VBox(18);
        page.setFillWidth(true);
        page.setPadding(new Insets(8, 8, 24, 8));
        return page;
    }

    private VBox createSectionHeader(String title, String subtitle) {
        VBox header = new VBox(4);
        header.getChildren().addAll(labelWithStyle(title, "section-title"), labelWithStyle(subtitle, "section-subtitle"));
        return header;
    }

    private FlowPane createFlowPane() {
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(16);
        flowPane.setVgap(16);
        flowPane.setPrefWrapLength(1200);
        return flowPane;
    }

    private VBox createStatCard(StatCard stat) {
        VBox card = createCard();
        card.getStyleClass().add("stat-card");
        Label value = labelWithStyle(stat.value(), "stat-value");
        Label label = labelWithStyle(stat.label(), "stat-label");
        Label detail = labelWithStyle(stat.detail(), "stat-detail");
        detail.setWrapText(true);
        card.getChildren().addAll(value, label, detail);
        return card;
    }

    private VBox createMatchCard(MatchCard match) {
        VBox card = createCard();
        HBox topLine = new HBox(10);
        topLine.setAlignment(Pos.CENTER_LEFT);
        Label badge = createBadge(match.status(), match.status().equalsIgnoreCase("LIVE") ? "badge-live" : "badge-open");
        Label game = labelWithStyle(match.game(), "card-title");
        topLine.getChildren().addAll(game, badge);

        Label teams = labelWithStyle(match.teams(), "card-subtitle");
        Label stage = labelWithStyle(match.stage(), "card-detail");
        Label time = labelWithStyle(match.time(), "card-detail");
        Button action = createSecondaryButton(match.status().equalsIgnoreCase("LIVE") ? "Watch now" : "View bracket", e -> showAlert("Match hub", match.teams() + " • " + match.time()));
        card.getChildren().addAll(topLine, teams, stage, time, action);
        return card;
    }

    private VBox createTournamentCard(TournamentCard tournament) {
        VBox card = createCard();
        Label head = labelWithStyle(tournament.name(), "card-title");
        Label subtitle = labelWithStyle(tournament.game(), "card-subtitle");
        Label date = labelWithStyle("Date: " + tournament.date(), "card-detail");
        Label prize = labelWithStyle("Prize: " + tournament.prizePool(), "card-detail");
        Label slots = labelWithStyle("Slots: " + tournament.slots(), "card-detail");
        Label badge = createBadge(tournament.openForJoin() ? "OPEN" : "FULL", tournament.openForJoin() ? "badge-open" : "badge-muted");
        Button join = createPrimaryButton(tournament.openForJoin() ? "Join now" : "Waitlist", e -> showAlert("Tournament registration", tournament.name() + " is ready for signup on the join page."));
        card.getChildren().addAll(head, subtitle, badge, date, prize, slots, join);
        return card;
    }

    private VBox createTeamCard(TeamCard team) {
        VBox card = createCard();
        Label head = labelWithStyle(team.name(), "card-title");
        Label roster = labelWithStyle(team.roster(), "card-subtitle");
        Label region = labelWithStyle(team.region(), "card-detail");
        Label record = labelWithStyle(team.record(), "card-detail");
        Button action = createSecondaryButton("View roster", e -> showAlert("Team spotlight", team.name() + " • " + team.record()));
        card.getChildren().addAll(head, roster, region, record, action);
        return card;
    }

    private FlowPane createMatchCards(List<MatchCard> matches) {
        FlowPane flowPane = createFlowPane();
        matches.forEach(match -> flowPane.getChildren().add(createMatchCard(match)));
        return flowPane;
    }

    private FlowPane createTournamentCards(List<TournamentCard> tournaments) {
        FlowPane flowPane = createFlowPane();
        tournaments.forEach(tournament -> flowPane.getChildren().add(createTournamentCard(tournament)));
        return flowPane;
    }

    private FlowPane createTeamCards(List<TeamCard> teams) {
        FlowPane flowPane = createFlowPane();
        teams.forEach(team -> flowPane.getChildren().add(createTeamCard(team)));
        return flowPane;
    }

    private HBox createActionStrip(String title, String subtitle, String actionText, javafx.event.EventHandler<ActionEvent> handler) {
        HBox strip = new HBox(16);
        strip.setAlignment(Pos.CENTER_LEFT);
        strip.getStyleClass().add("glass-card");
        strip.setPadding(new Insets(18));
        VBox textBox = new VBox(4);
        textBox.getChildren().addAll(labelWithStyle(title, "card-title"), labelWithStyle(subtitle, "card-subtitle"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Button button = createPrimaryButton(actionText, handler);
        strip.getChildren().addAll(textBox, spacer, button);
        return strip;
    }

    private Label createBadge(String text, String styleClass) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge", styleClass);
        return badge;
    }

    private Label createKeyValueRow(String key, String value) {
        Label label = new Label(key + ": " + value);
        label.getStyleClass().add("key-value");
        return label;
    }

    private Label createActionLine(String text) {
        Label label = new Label("• " + text);
        label.getStyleClass().add("action-line");
        label.setWrapText(true);
        return label;
    }

    private Label labelWithStyle(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private Button createPrimaryButton(String text, javafx.event.EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-action");
        button.setOnAction(handler);
        return button;
    }

    private Button createSecondaryButton(String text, javafx.event.EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-action");
        button.setOnAction(handler);
        return button;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
