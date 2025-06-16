package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.presenter.MainPresenter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class MainView {
    private final BorderPane root = new BorderPane();
    BaseStructureView baseStruct = new BaseStructureView(root);

    private final ToggleButton studentToggle = new ToggleButton("Schüler:in");
    private final ToggleButton teacherToggle = new ToggleButton("Lehrkraft");
    private final Button loginButton = new Button("Login");
    private final Button candidateSignupButton = new Button("Als Kandidat anmelden");
    private final VBox formBox = new VBox(15);

    private TextField studentCodeField = new TextField();
    private TextField teacherNameField = new TextField();
    private PasswordField teacherPasswordField = new PasswordField();
    private ComboBox<String> classField = new ComboBox<>();

    public MainView() {
        createUI();
        baseStruct.createNavBar();
        baseStruct.hideHomeButton();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void createUI() {

        // Überschrift
        VBox headingBox = baseStruct.createHeadingSection("Login");

        // Toggle & Eingabefeld
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(0, 40, 0, 40));

        ToggleGroup toggleGroup = new ToggleGroup();
        studentToggle.setToggleGroup(toggleGroup);
        teacherToggle.setToggleGroup(toggleGroup);
        studentToggle.setSelected(true);

        HBox toggleBox = new HBox(studentToggle, teacherToggle);
        toggleBox.setAlignment(Pos.CENTER);
        toggleBox.setSpacing(2);
        toggleBox.setMaxWidth(400);
        toggleBox.setPrefWidth(Double.MAX_VALUE);

        HBox.setHgrow(studentToggle, Priority.ALWAYS);
        HBox.setHgrow(teacherToggle, Priority.ALWAYS);

        studentToggle.setMaxWidth(Double.MAX_VALUE);
        teacherToggle.setMaxWidth(Double.MAX_VALUE);

        studentToggle.getStyleClass().add("user-toggle");
        teacherToggle.getStyleClass().add("user-toggle");

        studentToggle.setOnAction(e -> {
            if (studentToggle.isSelected()) {
                teacherToggle.setSelected(false);
                updateForm("student");
            } else {
                studentToggle.setSelected(true); // Immer eine Auswahl aktiv
            }
        });

        teacherToggle.setOnAction(e -> {
            if (teacherToggle.isSelected()) {
                studentToggle.setSelected(false);
                updateForm("teacher");
            } else {
                teacherToggle.setSelected(true);
            }
        });

        Region spacer = new Region();
        spacer.setMinHeight(10);

        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(400);
        formBox.setPrefWidth(Double.MAX_VALUE);
        updateForm("student"); // Start mit Schüler:in

        centerBox.getChildren().addAll(toggleBox, spacer, formBox);

        // Content-Box unter der NavBar
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20, 20, 20, 20));
        contentBox.getChildren().addAll(headingBox, centerBox);

        root.setCenter(contentBox);

        // Login-Button und Kandidatur-Button ganz unten
        VBox bottomBox = new VBox(15);
        bottomBox.setAlignment(Pos.BOTTOM_CENTER);
        bottomBox.setPadding(new Insets(20));

        loginButton.setMaxWidth(400);
        loginButton.getStyleClass().add("login-button");

        candidateSignupButton.setMaxWidth(400);
        candidateSignupButton.getStyleClass().add("bottom-button");

        bottomBox.getChildren().addAll(loginButton, candidateSignupButton);

        root.setBottom(bottomBox);
        root.getStyleClass().add("main-root");
    }

    private void updateForm(String userType) {
        formBox.getChildren().clear();

        if (userType.equals("teacher")) {

            VBox nameContainer = new VBox(8);
            Label nameLabel = new Label("Name:");
            nameLabel.getStyleClass().add("label");
            teacherNameField.setPromptText("Name eingeben");
            teacherNameField.getStyleClass().add("code-input");
            nameContainer.getChildren().addAll(nameLabel, teacherNameField);


            VBox pwContainer = new VBox(8);
            Label pwLabel = new Label("Passwort:");
            pwLabel.getStyleClass().add("label");
            teacherPasswordField.setPromptText("Passwort eingeben");
            teacherPasswordField.getStyleClass().add("code-input");
            pwContainer.getChildren().addAll(pwLabel, teacherPasswordField);

            VBox classContainer = new VBox(8);
            Label classLabel = new Label("Klasse:");
            pwLabel.getStyleClass().add("label");
            classField.setPromptText("Klasse");
            classField.getStyleClass().add("combo-box");
            classField.setPrefWidth(1000);
            pwContainer.getChildren().addAll(classLabel, classField);

            formBox.getChildren().addAll(nameContainer, pwContainer, classContainer);

            setTeacherFormFields(teacherNameField, teacherPasswordField, classField);

        } else {

            VBox codeContainer = new VBox(8);
            Label codeLabel = new Label("Code:");
            codeLabel.getStyleClass().add("label");
            studentCodeField.setPromptText("Code eingeben");
            studentCodeField.getStyleClass().add("code-input");
            codeContainer.getChildren().addAll(codeLabel, studentCodeField);

            formBox.getChildren().add(codeContainer);

            setStudentFormField(studentCodeField);
        }
    }

    public void setStudentFormField(TextField field) {
        this.studentCodeField = field;
    }

    public void setTeacherFormFields(TextField name, PasswordField pw, ComboBox<String> classComboBox) {
        this.teacherNameField = name;
        this.teacherPasswordField = pw;
        this.classField = classComboBox;
    }

    public ComboBox<String> getClassField() {
        return classField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public ToggleButton getTeacherToggle() {
        return teacherToggle;
    }

    public ToggleButton getStudentToggle() {
        return studentToggle;
    }

    public TextField getStudentCodeField() {
        return studentCodeField;
    }

    public TextField getTeacherNameField() {
        return teacherNameField;
    }

    public PasswordField getTeacherPasswordField() {
        return teacherPasswordField;
    }

    public Button getCandidateSignupButton() {
        return candidateSignupButton;
    }

}
