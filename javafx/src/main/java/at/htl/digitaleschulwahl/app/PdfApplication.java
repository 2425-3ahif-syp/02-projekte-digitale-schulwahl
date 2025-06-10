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

            var databaseManager = DatabaseManager.getInstance();
            var mainController = new PdfPresenter();
           // var votingController = new VotingPresenter();

            var mainView = new PdfView(mainController);
            mainView.setPrimaryStage(primaryStage);
         //   var votingView = new VotingView(votingController);
            String css = getClass().getResource("/votingPageStyle.css").toExternalForm();

            var mainScene = new Scene(mainView.getRoot(), 900, 600);
            mainScene.getStylesheets().add(css);

            /*
             * Die scene wird erst durch einen button-click oder so angezeigt.
             * Eine zweite votingScene wird dann noch benötigt, weil einmal für SV und
             * einmal für Abteilungssprecher ... wird dann noch gehandelt!
             */
            //   var votingScene = new Scene(votingView.getRoot(), 900, 600);

            primaryStage.setTitle("Digitale Schulwahl");
            primaryStage.setScene(mainScene);
            primaryStage.show();
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
