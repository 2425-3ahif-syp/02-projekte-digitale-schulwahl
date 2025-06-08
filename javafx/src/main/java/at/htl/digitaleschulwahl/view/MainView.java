package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.database.StudentRepository;
import at.htl.digitaleschulwahl.presenter.PdfPresenter;
import at.htl.digitaleschulwahl.model.Student;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;

public class MainView {
    private final PdfPresenter controller;
    private final BorderPane root = new BorderPane();
    private final TableView<Student> tableView = new TableView<>();
    private final StudentRepository studentRepository = new StudentRepository();
    BaseStructureView baseStruct = new BaseStructureView(root);
    private Integer classId;
    private String[] allClasses;
    private Stage primaryStage;

    public MainView(PdfPresenter controller) {
        this.controller = controller;
        createUI();
        baseStruct.createNavBar();
        loadData();

        controller.setPdfSaveCallback(this::showPdfSavedToast);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void showPdfSavedToast(String filePath) {
        if (primaryStage != null) {
            String fileName = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
            ToastNotification.show(primaryStage, "PDF gespeichert: " + fileName);
        }
    }

    public BorderPane getRoot() {
        return root;
    }

    private void createUI() {

        VBox main = new VBox();
        main.setAlignment(Pos.CENTER);
        main.setSpacing(10);
        allClasses = studentRepository.getAllClasses();

        ComboBox<String> classDropdown = new ComboBox<>();
        classDropdown.getStyleClass().add("class-dropdown");
        classDropdown.getItems().addAll(allClasses);
        classDropdown.setPromptText("Klasse auswählen");
        classDropdown.setMaxWidth(300);

        classDropdown.setOnAction(event -> {
            String selectedClass = classDropdown.getValue();
            classId = studentRepository.getClassId(selectedClass);
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
        tableView.setItems(FXCollections.observableArrayList(studentRepository.getAllStudents()));
    }
}
