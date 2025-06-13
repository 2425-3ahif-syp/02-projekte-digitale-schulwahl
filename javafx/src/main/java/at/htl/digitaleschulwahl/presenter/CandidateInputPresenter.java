package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.view.CandidateInputView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CandidateInputPresenter {
    CandidateInputView view;

    public CandidateInputPresenter() {
        this.view = new CandidateInputView(this);
    }

    public void navigateToHome() {
        Stage stage = (Stage) view.getRoot().getScene().getWindow();
        MainPresenter.show(stage);
    }

    public  void show(Stage primaryStage) {
        Scene scene = new Scene(view.getRoot(), 900, 700);
        String css = getClass().getResource("/votingPageStyle.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Digitale Schulwahl");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
