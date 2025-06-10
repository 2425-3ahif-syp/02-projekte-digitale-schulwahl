package at.htl.digitaleschulwahl.presenter;


import at.htl.digitaleschulwahl.database.StudentRepository;
import at.htl.digitaleschulwahl.model.Student;
import at.htl.digitaleschulwahl.view.MainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MainPresenter {
    private ObservableList<String> classList = FXCollections.observableArrayList();
    private MainView view;

    private TextField studentCodeField;
    private TextField teacherNameField;
    private PasswordField teacherPasswordField;
    private ComboBox<String> classComboBox;
    private final StudentRepository studentRepository = new StudentRepository();

    public void MainPresenter() {
        fillComboBox();

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
