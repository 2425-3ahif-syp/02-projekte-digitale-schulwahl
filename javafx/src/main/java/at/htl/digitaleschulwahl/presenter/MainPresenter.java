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


    private final StudentRepository studentRepository = new StudentRepository();

    public MainPresenter(MainView view) {
        this.view = view;
        fillComboBox();
        bind();
    }



    public static void show(Stage stage){
        MainView view = new MainView();
        MainPresenter controller = new MainPresenter(view);


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

    public void bind(){
        view.getClassField().setItems(classList);
    }

    public void fillComboBox() {
        classList.clear();
        classList.addAll(studentRepository.getAllClasses());
        System.out.println(studentRepository.getAllClasses());
        bind();
    }

    public void tryLogin(){

    }
}
