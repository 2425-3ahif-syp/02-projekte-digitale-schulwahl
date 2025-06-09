package at.htl.digitaleschulwahl.presenter;


import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MainPresenter {
    private TextField studentCodeField;
    private TextField teacherNameField;
    private PasswordField teacherPasswordField;
    private TextField teacherClassField;


    public void setStudentFormField(TextField field) {
        this.studentCodeField = field;
    }

    public void setTeacherFormFields(TextField name, PasswordField pw, TextField clazz) {
        this.teacherNameField = name;
        this.teacherPasswordField = pw;
        this.teacherClassField = clazz;
    }
}
