package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.StudentRepository;
import at.htl.digitaleschulwahl.view.MainView;
import at.htl.digitaleschulwahl.view.ToastNotification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
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
        attachEvents();
    }



    public static void show(Stage stage){
        MainView view = new MainView();
        MainPresenter controller = new MainPresenter(view);


        String css = MainPresenter.class.getResource("/mainPageStyle.css").toExternalForm();
        String css1 = MainPresenter.class.getResource("/votingPageStyle.css").toExternalForm();
        String css2 = MainPresenter.class.getResource("/toastNotification.css").toExternalForm();

        Scene scene = new Scene(view.getRoot(), 900,600);
        scene.getStylesheets().addAll(css2,css1, css);
        stage.setTitle("Digitale Schulwahl");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        MainPresenter.stage = stage;

    }

    public void bind(){
        view.getClassField().setItems(classList);
    }

    private void attachEvents(){
        view.getLoginButton().setOnAction(event -> tryLogin());
    }

    public void fillComboBox() {
        classList.clear();
        classList.addAll(studentRepository.getAllClasses());
        System.out.println(studentRepository.getAllClasses());
        bind();
    }

    public void navigateToVotingView(){
        VotingPresenter presenter = new VotingPresenter();
        presenter.show(stage);
    }

    public void navigateToPdfGenerationView(){

    }


    public void tryLogin(){
        if(view.getStudentToggle().isSelected()){
            String code = view.getStudentCodeField().getText().trim();
            System.out.println(code);
            if(code.isEmpty()){
                ToastNotification.show(stage, "Bitte gib deinen Code ein!", "error");
            } else{
                navigateToVotingView();
            }
        } else{
            ToastNotification.show(stage, "Lehrer login noch nicht in Arbeit", "warning");
        }
    }
}
