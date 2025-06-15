package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.StudentRepository;
import at.htl.digitaleschulwahl.model.Student;
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

    public static void show(Stage stage) {
        MainView view = new MainView();
        MainPresenter controller = new MainPresenter(view);

        Scene scene = new Scene(view.getRoot(), 900, 600);

        try {
            var mainPageCss = MainPresenter.class.getResource("/mainPageStyle.css");
            var votingPageCss = MainPresenter.class.getResource("/votingPageStyle.css");
            var toastCss = MainPresenter.class.getResource("/toastNotification.css");

            if (mainPageCss != null) {
                scene.getStylesheets().add(mainPageCss.toExternalForm());
            } else {
                System.err.println("Warning: mainPageStyle.css not found");
            }

            if (votingPageCss != null) {
                scene.getStylesheets().add(votingPageCss.toExternalForm());
            } else {
                System.err.println("Warning: votingPageStyle.css not found");
            }

            if (toastCss != null) {
                scene.getStylesheets().add(toastCss.toExternalForm());
            } else {
                System.err.println("Warning: toastNotification.css not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS files: " + e.getMessage());
        }

        stage.setTitle("Digitale Schulwahl");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        MainPresenter.stage = stage;

    }

    public void bind() {
        view.getClassField().setItems(classList);
    }

    private void attachEvents() {
        view.getLoginButton().setOnAction(event -> tryLogin());
        view.getCandidateSignupButton().setOnAction(event -> navigateToCandidateSignup());
    }

    public void fillComboBox() {
        classList.clear();
        classList.addAll(studentRepository.getAllClasses());
        bind();
    }

    public void navigateToVotingView(Student authenticatedStudent) {
        VotingPresenter presenter = new VotingPresenter(authenticatedStudent);
        presenter.show(stage);
    }

    public void navigateToPdfGenerationView() {
        PdfPresenter presenter = new PdfPresenter();
        presenter.show(stage);
    }

    public void navigateToCandidateSignup() {
        CandidateSignupPresenter presenter = new CandidateSignupPresenter();
        presenter.show(stage);
    }

    public void tryLogin() {
        if (view.getStudentToggle().isSelected()) {
            String code = view.getStudentCodeField().getText().trim();
            System.out.println(code);
            if (code.isEmpty()) {
                ToastNotification.show(stage, "Bitte gib deinen Code ein!", "error");
            } else {
                if (studentRepository.verifyStudentCode(code)) {
                    Student authenticatedStudent = studentRepository.getStudentByCode(code);
                    navigateToVotingView(authenticatedStudent);
                } else {
                    ToastNotification.show(stage, "Ungültiger Code! Bitte überprüfe deinen Code.", "error");
                }
            }
        } else {
            navigateToPdfGenerationView();
        }
    }
}
