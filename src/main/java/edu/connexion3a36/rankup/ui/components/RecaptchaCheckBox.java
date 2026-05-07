package edu.connexion3a36.rankup.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.*;

/**
 * Custom reCAPTCHA v2 Component for JavaFX
 * Displays quiz challenges with professional styling
 */
public class RecaptchaCheckBox extends VBox {
    private CheckBox captchaCheckBox;
    private String token;
    private VBox quizContainer;
    private boolean quizPassed = false;
    private RecaptchaQuiz currentQuiz;
    private Button verifyButton;

    // Color scheme
    private static final String PRIMARY_COLOR = "#667EEA";
    private static final String PRIMARY_DARK = "#5568D3";
    private static final String SUCCESS_COLOR = "#48BB78";
    private static final String ERROR_COLOR = "#F56565";
    private static final String WARNING_COLOR = "#ED8936";
    private static final String BACKGROUND_LIGHT = "#F7FAFC";
    private static final String BORDER_COLOR = "#E2E8F0";
    private static final String TEXT_DARK = "#1A202C";
    private static final String TEXT_GRAY = "#718096";

    public RecaptchaCheckBox() {
        // Main container styling
        this.setStyle(
            "-fx-border-color: " + BORDER_COLOR + "; " +
            "-fx-border-width: 2; " +
            "-fx-padding: 20; " +
            "-fx-border-radius: 12; " +
            "-fx-background-color: " + BACKGROUND_LIGHT + "; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        this.setSpacing(12);
        this.setPadding(new Insets(15));

        // Main checkbox container with better styling
        HBox checkboxContainer = new HBox();
        checkboxContainer.setSpacing(12);
        checkboxContainer.setStyle(
            "-fx-alignment: CENTER_LEFT; " +
            "-fx-padding: 12; " +
            "-fx-background-color: white; " +
            "-fx-border-color: " + BORDER_COLOR + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);"
        );

        // reCAPTCHA checkbox with enhanced styling
        captchaCheckBox = new CheckBox("I'm not a robot");
        captchaCheckBox.setStyle(
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + TEXT_DARK + "; " +
            "-fx-padding: 8;"
        );
        captchaCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                showQuizChallenge();
            } else {
                resetQuiz();
            }
        });

        checkboxContainer.getChildren().add(captchaCheckBox);

        // Logo/branding section with improved styling
        HBox logoBox = new HBox();
        logoBox.setSpacing(6);
        logoBox.setStyle(
            "-fx-alignment: CENTER_LEFT; " +
            "-fx-padding: 8; " +
            "-fx-text-fill: " + TEXT_GRAY + ";"
        );

        Text reCaptchaText = new Text("reCAPTCHA");
        reCaptchaText.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 10));
        reCaptchaText.setStyle("-fx-fill: " + TEXT_GRAY + ";");

        Text versionText = new Text("v2");
        versionText.setFont(Font.font("Segoe UI", 9));
        versionText.setStyle("-fx-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");

        Text privacyText = new Text(" • ");
        privacyText.setFont(Font.font("Segoe UI", 10));
        privacyText.setStyle("-fx-fill: " + TEXT_GRAY + ";");

        Hyperlink privacyLink = new Hyperlink("Privacy");
        privacyLink.setStyle(
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-padding: 0; " +
            "-fx-underline: true; " +
            "-fx-font-size: 10;"
        );
        privacyLink.setOnAction(e -> openUrl("https://www.google.com/policies/privacy/"));

        Text termsText = new Text(" • ");
        termsText.setFont(Font.font("Segoe UI", 10));
        termsText.setStyle("-fx-fill: " + TEXT_GRAY + ";");

        Hyperlink termsLink = new Hyperlink("Terms");
        termsLink.setStyle(
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-padding: 0; " +
            "-fx-underline: true; " +
            "-fx-font-size: 10;"
        );
        termsLink.setOnAction(e -> openUrl("https://www.google.com/policies/terms/"));

        logoBox.getChildren().addAll(reCaptchaText, versionText, privacyText, privacyLink, termsText, termsLink);

        // Quiz container with enhanced styling
        quizContainer = new VBox();
        quizContainer.setSpacing(12);
        quizContainer.setStyle(
            "-fx-padding: 0; " +
            "-fx-background-color: transparent;"
        );

        this.getChildren().addAll(checkboxContainer, logoBox, quizContainer);
    }

    /**
     * Show quiz challenge when checkbox is clicked
     */
    private void showQuizChallenge() {
        quizContainer.getChildren().clear();
        quizPassed = false;
        this.token = null;

        // Create a quiz
        currentQuiz = new RecaptchaQuiz();

        // Title with enhanced styling
        Label titleLabel = new Label("🔐 Complete the challenge");
        titleLabel.setStyle(
            "-fx-font-size: 13; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-padding: 5 0 5 0;"
        );

        Label challengeLabel = new Label(currentQuiz.getQuestion());
        challengeLabel.setStyle(
            "-fx-font-size: 13; " +
            "-fx-wrap-text: true; " +
            "-fx-text-fill: " + TEXT_DARK + "; " +
            "-fx-padding: 5 0 10 0;"
        );
        challengeLabel.setWrapText(true);
        challengeLabel.setMaxWidth(350);

        quizContainer.getChildren().addAll(titleLabel, challengeLabel);

        // Show quiz type
        if (currentQuiz.getType() == RecaptchaQuiz.QuizType.MULTIPLE_CHOICE) {
            showMultipleChoice(currentQuiz);
        } else if (currentQuiz.getType() == RecaptchaQuiz.QuizType.TEXT_INPUT) {
            showTextInput(currentQuiz);
        } else if (currentQuiz.getType() == RecaptchaQuiz.QuizType.GRID_SELECT) {
            showGridSelection(currentQuiz);
        }
    }

    /**
     * Show multiple choice quiz with enhanced styling
     */
    private void showMultipleChoice(RecaptchaQuiz quiz) {
        VBox optionsBox = new VBox();
        optionsBox.setSpacing(10);
        optionsBox.setStyle("-fx-padding: 12;");

        ToggleGroup group = new ToggleGroup();

        for (String option : quiz.getOptions()) {
            // Create styled radio button container
            HBox optionBox = new HBox();
            optionBox.setSpacing(10);
            optionBox.setStyle(
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-padding: 10; " +
                "-fx-background-color: white; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 3, 0, 0, 1);"
            );

            RadioButton rb = new RadioButton(option);
            rb.setToggleGroup(group);
            rb.setStyle(
                "-fx-font-size: 12; " +
                "-fx-text-fill: " + TEXT_DARK + "; " +
                "-fx-padding: 5;"
            );

            optionBox.getChildren().add(rb);
            optionsBox.getChildren().add(optionBox);

            // Hover effect
            optionBox.setOnMouseEntered(e -> optionBox.setStyle(
                "-fx-border-color: " + PRIMARY_COLOR + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 6; " +
                "-fx-padding: 10; " +
                "-fx-background-color: #F7FAFC; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.15), 5, 0, 0, 2);"
            ));

            optionBox.setOnMouseExited(e -> optionBox.setStyle(
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-padding: 10; " +
                "-fx-background-color: white; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 3, 0, 0, 1);"
            ));
        }

        quizContainer.getChildren().add(optionsBox);

        // Verify button with enhanced styling
        verifyButton = new Button("✓ Verify");
        verifyButton.setStyle(
            "-fx-padding: 10 40 10 40; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0, 0, 2);"
        );

        verifyButton.setOnMouseEntered(e -> verifyButton.setStyle(
            "-fx-padding: 10 40 10 40; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_DARK + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 10, 0, 0, 3);"
        ));

        verifyButton.setOnMouseExited(e -> verifyButton.setStyle(
            "-fx-padding: 10 40 10 40; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0, 0, 2);"
        ));

        verifyButton.setOnAction(e -> {
            RadioButton selected = (RadioButton) group.getSelectedToggle();
            if (selected != null && quiz.validateAnswer(selected.getText())) {
                quizPassed = true;
                this.token = generateMockToken();
                showSuccessMessage();
            } else {
                showErrorMessage("❌ Incorrect answer. Please try again.");
            }
        });

        HBox buttonBox = new HBox();
        buttonBox.setStyle("-fx-alignment: CENTER; -fx-padding: 10 0 0 0;");
        buttonBox.getChildren().add(verifyButton);
        quizContainer.getChildren().add(buttonBox);
    }

    /**
     * Show text input quiz with enhanced styling
     */
    private void showTextInput(RecaptchaQuiz quiz) {
        HBox inputBox = new HBox();
        inputBox.setSpacing(10);
        inputBox.setStyle(
            "-fx-padding: 12; " +
            "-fx-alignment: CENTER; " +
            "-fx-spacing: 10;"
        );

        TextField textField = new TextField();
        textField.setPromptText("Type your answer here...");
        textField.setPrefWidth(200);
        textField.setStyle(
            "-fx-padding: 10; " +
            "-fx-font-size: 12; " +
            "-fx-border-color: " + BORDER_COLOR + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6; " +
            "-fx-text-fill: " + TEXT_DARK + "; " +
            "-fx-control-inner-background: white; " +
            "-fx-focus-color: " + PRIMARY_COLOR + "; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);"
        );

        verifyButton = new Button("✓ Verify");
        verifyButton.setStyle(
            "-fx-padding: 10 30 10 30; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0, 0, 2);"
        );

        verifyButton.setOnMouseEntered(e -> verifyButton.setStyle(
            "-fx-padding: 10 30 10 30; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_DARK + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 10, 0, 0, 3);"
        ));

        verifyButton.setOnMouseExited(e -> verifyButton.setStyle(
            "-fx-padding: 10 30 10 30; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0, 0, 2);"
        ));

        verifyButton.setOnAction(e -> {
            if (quiz.validateAnswer(textField.getText())) {
                quizPassed = true;
                this.token = generateMockToken();
                showSuccessMessage();
            } else {
                showErrorMessage("❌ Incorrect answer. Please try again.");
                textField.clear();
                textField.requestFocus();
            }
        });

        inputBox.getChildren().addAll(textField, verifyButton);
        quizContainer.getChildren().add(inputBox);
    }

    /**
     * Show grid selection quiz with enhanced styling
     */
    private void showGridSelection(RecaptchaQuiz quiz) {
        Label instructionLabel = new Label("📸 Click on the matching images below:");
        instructionLabel.setStyle(
            "-fx-font-size: 11; " +
            "-fx-text-fill: " + TEXT_GRAY + "; " +
            "-fx-padding: 5 0 10 0;"
        );
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(8);
        gridPane.setVgap(8);
        gridPane.setStyle("-fx-padding: 12; -fx-alignment: CENTER;");

        List<String> images = quiz.getImageLabels();
        List<CheckBox> checkboxes = new ArrayList<>();
        
        int col = 0;
        int row = 0;
        for (String imageLabel : images) {
            VBox imageBox = new VBox();
            imageBox.setStyle(
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 2; " +
                "-fx-padding: 8; " +
                "-fx-alignment: CENTER; " +
                "-fx-background-color: white; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);"
            );
            imageBox.setPrefSize(90, 90);
            imageBox.setSpacing(5);
            
            Label emoji = new Label(imageLabel);
            emoji.setFont(Font.font(40));
            
            CheckBox cb = new CheckBox();
            cb.setStyle(
                "-fx-font-size: 12; " +
                "-fx-padding: 5;"
            );
            
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    imageBox.setStyle(
                        "-fx-border-color: " + SUCCESS_COLOR + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 8; " +
                        "-fx-alignment: CENTER; " +
                        "-fx-background-color: #F0FDF4; " +
                        "-fx-border-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(72,187,120,0.3), 6, 0, 0, 2);"
                    );
                } else {
                    imageBox.setStyle(
                        "-fx-border-color: " + BORDER_COLOR + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 8; " +
                        "-fx-alignment: CENTER; " +
                        "-fx-background-color: white; " +
                        "-fx-border-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);"
                    );
                }
            });
            
            imageBox.getChildren().addAll(emoji, cb);
            gridPane.add(imageBox, col, row);
            
            // Hover effect
            imageBox.setOnMouseEntered(e -> {
                if (!cb.isSelected()) {
                    imageBox.setStyle(
                        "-fx-border-color: " + PRIMARY_COLOR + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 8; " +
                        "-fx-alignment: CENTER; " +
                        "-fx-background-color: #F7FAFC; " +
                        "-fx-border-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.15), 6, 0, 0, 2);"
                    );
                }
            });
            
            imageBox.setOnMouseExited(e -> {
                if (!cb.isSelected()) {
                    imageBox.setStyle(
                        "-fx-border-color: " + BORDER_COLOR + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 8; " +
                        "-fx-alignment: CENTER; " +
                        "-fx-background-color: white; " +
                        "-fx-border-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);"
                    );
                }
            });
            
            checkboxes.add(cb);
            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }

        quizContainer.getChildren().addAll(instructionLabel, gridPane);

        // Verify button
        verifyButton = new Button("✓ Verify");
        verifyButton.setStyle(
            "-fx-padding: 10 40 10 40; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0, 0, 2);"
        );
        
        verifyButton.setOnMouseEntered(e -> verifyButton.setStyle(
            "-fx-padding: 10 40 10 40; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_DARK + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 10, 0, 0, 3);"
        ));
        
        verifyButton.setOnMouseExited(e -> verifyButton.setStyle(
            "-fx-padding: 10 40 10 40; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 8, 0, 0, 2);"
        ));
        
        verifyButton.setOnAction(e -> {
            List<String> selected = new ArrayList<>();
            for (int i = 0; i < checkboxes.size(); i++) {
                if (checkboxes.get(i).isSelected()) {
                    selected.add(images.get(i));
                }
            }

            if (quiz.validateImageSelection(selected)) {
                quizPassed = true;
                this.token = generateMockToken();
                showSuccessMessage();
            } else {
                showErrorMessage("❌ Incorrect selection. Please try again.");
            }
        });

        HBox buttonBox = new HBox();
        buttonBox.setStyle("-fx-alignment: CENTER; -fx-padding: 10 0 0 0;");
        buttonBox.getChildren().add(verifyButton);
        quizContainer.getChildren().add(buttonBox);
    }

    /**
     * Show success message with enhanced styling
     */
    private void showSuccessMessage() {
        quizContainer.getChildren().clear();
        VBox successBox = new VBox();
        successBox.setStyle(
            "-fx-alignment: CENTER; " +
            "-fx-padding: 20; " +
            "-fx-background-color: #F0FDF4; " +
            "-fx-border-color: " + SUCCESS_COLOR + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(72,187,120,0.2), 8, 0, 0, 2);"
        );
        successBox.setSpacing(8);

        Label successLabel = new Label("✅ Verified Successfully!");
        successLabel.setStyle(
            "-fx-font-size: 15; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + SUCCESS_COLOR + ";"
        );

        Label messageLabel = new Label("You have successfully verified that you are not a robot.");
        messageLabel.setStyle(
            "-fx-font-size: 12; " +
            "-fx-text-fill: #22863A; " +
            "-fx-wrap-text: true;"
        );
        messageLabel.setWrapText(true);

        successBox.getChildren().addAll(successLabel, messageLabel);
        quizContainer.getChildren().add(successBox);
    }

    /**
     * Show error message with enhanced styling
     */
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Verification Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 15;"
        );
        alert.showAndWait();
    }

    /**
     * Check if the reCAPTCHA has been validated
     */
    public boolean isVerified() {
        return quizPassed && token != null;
    }

    /**
     * Get the reCAPTCHA token
     */
    public String getToken() {
        return token;
    }

    /**
     * Reset the reCAPTCHA
     */
    public void reset() {
        captchaCheckBox.setSelected(false);
        resetQuiz();
    }

    /**
     * Reset quiz state
     */
    private void resetQuiz() {
        quizContainer.getChildren().clear();
        quizPassed = false;
        this.token = null;
        currentQuiz = null;
    }

    /**
     * Generate a mock token for demonstration
     */
    private String generateMockToken() {
        return "quiz_token_" + System.currentTimeMillis();
    }

    /**
     * Open URL in default browser
     */
    private void openUrl(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            System.err.println("Could not open URL: " + e.getMessage());
        }
    }
}

