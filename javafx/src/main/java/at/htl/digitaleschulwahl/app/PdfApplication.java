package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.presenter.VotingPresenter;
import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.view.PdfView;
import at.htl.digitaleschulwahl.presenter.PdfPresenter;
import at.htl.digitaleschulwahl.view.VotingView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PdfApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            var pdfController = new PdfPresenter();
            pdfController.show(primaryStage);
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
