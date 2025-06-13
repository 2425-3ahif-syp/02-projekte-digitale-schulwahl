package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.presenter.CandidateInputPresenter;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CandidateInputView {
    private final CandidateInputPresenter controller;

    private final BorderPane root = new BorderPane();

    BaseStructureView baseStructureView = new BaseStructureView(root);

    public CandidateInputView(CandidateInputPresenter controller) {
        this.controller = controller;
        createUI();
        baseStructureView.createNavBar();
        baseStructureView.showHomeButton();
        baseStructureView.setHomeButtonAction(e -> controller.navigateToHome());
    }

    private void createUI() {
        VBox main = new VBox();
        main.setSpacing(55);
        main.setAlignment(Pos.TOP_CENTER);
        VBox headingContent = baseStructureView.createHeadingSection("Neue Kandidaten hinzufügen");

        VBox formContent = new VBox();
        formContent.setSpacing(10);
        formContent.getStyleClass().add("form-content");

        HBox nameContent = new HBox();
        nameContent.setSpacing(50);
        VBox firstNameVbox = new VBox();
        VBox lastNameVbox = new VBox();

        firstNameVbox.setMinHeight(40);
        lastNameVbox.setMinHeight(40);


       // formContent.getStyleClass().add("form-content");
        Label firstNameLabel = new Label("Vorname:");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        firstNameField.getStyleClass().add("textfield");
        firstNameVbox.getChildren().addAll(firstNameLabel,firstNameField);

        Label lastNameLabel = new Label("Nachname:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");
        lastNameField.getStyleClass().add("textfield");
        lastNameVbox.getChildren().addAll(lastNameLabel,lastNameField);

        nameContent.getChildren().addAll(firstNameVbox, lastNameVbox);

        Label classLabel = new Label("Klasse:");
        ComboBox<String> classComboBox = new ComboBox<>();

        Label typeLabel = new Label("Kandidatentyp:");
        ComboBox<String> typeComboBox = new ComboBox<>();

        HBox bottomContent = new HBox();
        Button addButton = new Button("Hinzufügen");
        bottomContent.getChildren().addAll(addButton);
        bottomContent.getStyleClass().add("form-bottom-button-container");

        formContent.getChildren().addAll(nameContent,classLabel,classComboBox,typeLabel,typeComboBox);

        main.getChildren().addAll(headingContent, formContent);
        root.setCenter(main);
        root.setBottom(bottomContent);
    }

    public BorderPane getRoot() {
        return root;
    }
}
