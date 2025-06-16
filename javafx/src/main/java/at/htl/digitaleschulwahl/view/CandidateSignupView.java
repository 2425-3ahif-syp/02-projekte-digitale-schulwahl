package at.htl.digitaleschulwahl.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CandidateSignupView {
    private final BorderPane root = new BorderPane();
    private final BaseStructureView baseStruct = new BaseStructureView(root);

    private final TextField nameField = new TextField();
    private final TextField classField = new TextField();
    private final ComboBox<String> roleComboBox = new ComboBox<>();
    private final Button signupButton = new Button("Als Kandidat anmelden");
    private final Button cancelButton = new Button("Abbrechen");

    private Runnable onSignup;
    private Runnable onCancel;
    private Runnable onNavigateHome;
    private Stage stage;

    public CandidateSignupView() {
        createUI();
        baseStruct.createNavBar();
        baseStruct.showHomeButton();
    }

    public void setEventHandlers(Runnable onSignup, Runnable onCancel, Runnable onNavigateHome) {
        this.onSignup = onSignup;
        this.onCancel = onCancel;
        this.onNavigateHome = onNavigateHome;

        signupButton.setOnAction(e -> onSignup.run());
        cancelButton.setOnAction(e -> onCancel.run());
        baseStruct.setHomeButtonAction(e -> onNavigateHome.run());
    }

    private void createUI() {
        VBox headingContent = baseStruct.createHeadingSection("Kandidatur anmelden");

        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40, 40, 40, 40));

        VBox formSection = new VBox(20);
        formSection.setAlignment(Pos.CENTER);
        formSection.setMaxWidth(400);
        formSection.setPrefWidth(Double.MAX_VALUE);

        VBox nameContainer = new VBox(8);
        Label nameLabel = new Label("Vollständiger Name:");
        nameLabel.getStyleClass().add("label");
        nameField.setPromptText("Vor- und Nachname eingeben");
        nameField.getStyleClass().add("code-input");
        nameContainer.getChildren().addAll(nameLabel, nameField);

        VBox classContainer = new VBox(8);
        Label classLabel = new Label("Klasse:");
        classLabel.getStyleClass().add("label");
        classField.setPromptText("z.B. 5AHIF");
        classField.getStyleClass().add("code-input");
        classContainer.getChildren().addAll(classLabel, classField);

        VBox roleContainer = new VBox(8);
        Label roleLabel = new Label("Position:");
        roleLabel.getStyleClass().add("label");
        roleComboBox.getItems().addAll("Schülersprecher", "Abteilungsvertreter");
        roleComboBox.setPromptText("Position auswählen");
        roleComboBox.getStyleClass().add("combo-box");
        roleComboBox.setPrefWidth(Double.MAX_VALUE);
        roleContainer.getChildren().addAll(roleLabel, roleComboBox);

        formSection.getChildren().addAll(nameContainer, classContainer, roleContainer);

        VBox buttonSection = new VBox(20);
        buttonSection.setAlignment(Pos.BOTTOM_CENTER);
        buttonSection.setPadding(new Insets(20));

        cancelButton.getStyleClass().add("bottom-button");
        signupButton.getStyleClass().add("login-button");
        signupButton.setMaxWidth(400);
        cancelButton.setMaxWidth(400);

        buttonSection.getChildren().addAll(signupButton, cancelButton);

        // Content-Box unter der NavBar
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20, 20, 20, 20));
        contentBox.getChildren().addAll(headingContent, formSection);
        root.setCenter(contentBox);

        root.setBottom(buttonSection);
    }

    public BorderPane getRoot() {
        return root;
    }

    public String getName() {
        return nameField.getText().trim();
    }

    public String getClassName() {
        return classField.getText().trim();
    }

    public String getSelectedRole() {
        return roleComboBox.getSelectionModel().getSelectedItem();
    }

    public void clearForm() {
        nameField.clear();
        classField.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showError(String message) {
        if (stage != null) {
            ToastNotification.show(stage, message, "error");
        }
    }

    public void showSuccess(String message) {
        if (stage != null) {
            ToastNotification.show(stage, message, "success");
        }
    }
}
