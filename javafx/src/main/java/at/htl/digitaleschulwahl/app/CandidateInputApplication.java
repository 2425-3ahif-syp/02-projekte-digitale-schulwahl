package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.presenter.CandidateInputPresenter;
import javafx.application.Application;
import javafx.stage.Stage;

public class CandidateInputApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            CandidateInputPresenter presenter = new CandidateInputPresenter();
            presenter.show(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
