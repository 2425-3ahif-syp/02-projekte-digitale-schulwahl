package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.controller.MainController;
import at.htl.digitaleschulwahl.model.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainView {
    private final MainController controller;
    private final BorderPane root = new BorderPane();
    private final TableView<Student> tableView = new TableView<>();
    BaseStructureView baseStruct = new BaseStructureView(root);
    private Integer classId;
    private String[] allClasses;

    public MainView(MainController controller) {
        this.controller = controller;
        createUI();
        baseStruct.createNavBar();
        loadData();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void createUI() {

        VBox main = new VBox();
        main.setAlignment(Pos.CENTER);
        main.setSpacing(10);
        allClasses = controller.getAllClasses();

        ComboBox<String> classDropdown = new ComboBox<>();
        classDropdown.getStyleClass().add("class-dropdown");
        classDropdown.getItems().addAll(allClasses);
        classDropdown.setPromptText("Klasse auswählen");
        classDropdown.setMaxWidth(300);

        classDropdown.setOnAction(event -> {
            String selectedClass = classDropdown.getValue();
            classId = controller.getClassId(selectedClass);
        });

        root.setCenter(classDropdown);

        VBox bottomBox = new VBox();
        bottomBox.setSpacing(10);
        bottomBox.getStyleClass().add("bottom-button-container");

        Button generateButton = new Button("Codes generieren");
        generateButton.getStyleClass().add("bottom-button");

        bottomBox.getChildren().addAll(generateButton);
        generateButton.setOnAction(event -> {
            controller.generateAndSaveCodesForAllStudents();
            loadData();
            controller.getCodesIntoPDF(classId);
        });

        root.setBottom(bottomBox);

    }



    private void loadData() {
        tableView.setItems(FXCollections.observableArrayList(controller.getAllStudents()));
    }
}
