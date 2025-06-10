package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.StudentRepository;
import at.htl.digitaleschulwahl.view.MainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainPresenter {
    private ObservableList<String> classList = FXCollections.observableArrayList();
    private MainView view;

    private static Stage stage;

    private TextField studentCodeField;
    private TextField teacherNameField;
    private PasswordField teacherPasswordField;
    private ComboBox<String> classComboBox;
    private final StudentRepository studentRepository = new StudentRepository();

    public void MainPresenter(MainView view) {
        this.view = view;
        fillComboBox();
    }

    public static void show(Stage stage){
        MainView view = new MainView();

        String css = MainPresenter.class.getResource("/mainPageStyle.css").toExternalForm();
        String css1 = MainPresenter.class.getResource("/votingPageStyle.css").toExternalForm();

        Scene scene = new Scene(view.getRoot(), 900,600);
        scene.getStylesheets().addAll(css1, css);
        stage.setTitle("Digitale Schulwahl");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        MainPresenter.stage = stage;



    }

    public void setStudentFormField(TextField field) {
        this.studentCodeField = field;
    }

    public void setTeacherFormFields(TextField name, PasswordField pw, ComboBox<String> classComboBox) {
        this.teacherNameField = name;
        this.teacherPasswordField = pw;
        this.classComboBox = classComboBox;
    }

    public void bind(){

    }

    public void fillComboBox() {
        classList.clear();
        classList.addAll(studentRepository.getAllClasses());
    }
}
