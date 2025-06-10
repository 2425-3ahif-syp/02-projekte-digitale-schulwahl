package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.presenter.MainPresenter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class MainView {
    private final MainPresenter controller;
    private final BorderPane root = new BorderPane();
    BaseStructureView baseStruct = new BaseStructureView(root);


    private final ToggleButton studentToggle = new ToggleButton("Schüler:in");
    private final ToggleButton teacherToggle = new ToggleButton("Lehrkraft");
    private final TextField codeField = new TextField();
    private final Button loginButton = new Button("Login");
    private final VBox formBox = new VBox(15);

    public MainView(MainPresenter controller) {
        this.controller = controller;
        createUI();
        baseStruct.createNavBar();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void createUI() {
        //Überschrift
        VBox headingBox = new VBox(10);
        headingBox.setAlignment(Pos.CENTER);
        headingBox.setPadding(new Insets(30, 10, 10, 10));

        Label heading = new Label("Digitale Schulwahl - Login");
        heading.getStyleClass().add("first-heading");

        Line line = new Line();
        line.getStyleClass().add("underline");

        heading.widthProperty().addListener((obs, oldVal, newVal) -> {
            line.setStartX(-40);
            line.setEndX(newVal.doubleValue() + 40);
        });

        headingBox.getChildren().addAll(heading, line);

        //Toggle & Eingabefeld
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

        centerBox.getChildren().addAll(toggleBox,spacer, formBox);



        //Content-Box unter der NavBar
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20, 20, 20, 20));
        contentBox.getChildren().addAll(headingBox, centerBox);

        root.setCenter(contentBox);

        //Login-Button ganz unten
        VBox bottomBox = new VBox(loginButton);
        bottomBox.setAlignment(Pos.BOTTOM_CENTER);
        bottomBox.setPadding(new Insets(20));
        loginButton.setMaxWidth(400);
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.getStyleClass().add("login-button");

        root.setBottom(bottomBox);
        root.getStyleClass().add("main-root");
    }


    private void updateForm(String userType) {
        formBox.getChildren().clear();

        if (userType.equals("teacher")) {
            TextField nameField = new TextField();
            nameField.setPromptText("Name");

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Passwort");

            ComboBox<String> classField = new ComboBox<>();
            classField.setPromptText("Klasse");

            formBox.getChildren().addAll(nameField, passwordField, classField);

            // Falls du die Felder an MainPresenter übergeben willst:
            controller.setTeacherFormFields(nameField, passwordField, classField);

        } else {
            TextField codeField = new TextField();
            codeField.setPromptText("Code");

            formBox.getChildren().add(codeField);

            controller.setStudentFormField(codeField);
        }
    }



}
