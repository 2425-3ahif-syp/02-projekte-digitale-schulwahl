package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.controller.MainController;
import at.htl.digitaleschulwahl.model.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
        /*var toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));

        var generateButton = new Button("Generate Codes");
        generateButton.setOnAction(e -> {
            controller.generateAndSaveCodesForAllStudents();
            loadData();
            controller.getCodesIntoPDF(1);
        });

        toolbar.getChildren().add(generateButton);

        TableColumn<Student, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName())
        );
        nameCol.setMinWidth(80);

        TableColumn<Student, String> classCol = new TableColumn<>("Class");
        classCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        "%d%s".formatted(
                                cellData.getValue().getGrade(),
                                cellData.getValue().getClassName()
                        )
                )
        );
        classCol.setMinWidth(80);

        TableColumn<Student, String> codeCol = new TableColumn<>("Login Code");
        codeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLoginCode())
        );
        codeCol.setMinWidth(80);

        tableView.getColumns().addAll(nameCol, classCol, codeCol);

        root.setTop(toolbar);
        root.setCenter(tableView);*/
    }



    private void loadData() {
        tableView.setItems(FXCollections.observableArrayList(controller.getAllStudents()));
    }
}
