package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.presenter.VotingPresenter;
import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.view.VotingView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VotingApplicationTemp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            var databaseManager = DatabaseManager.getInstance();
            var votingController = new VotingPresenter();
        Scene votingScene = new Scene(votingController.getView().getRoot(),900,700);

            var votingView = new VotingView(votingController);
           // scene.getStylesheets().add(MainViewController.class.getResource("/syp/htlfragebogenapplication/Base.css").toExternalForm());
            String css = getClass().getResource("/votingPageStyle.css").toExternalForm();

           /* Die scene wird erst durch einen button-click oder so angezeigt.
               Eine zweite votingScene wird dann noch benötigt, weil einmal für SV und
               einmal für Abteilungssprecher ... wird dann noch gehandelt!*/

           // var votingScene = new Scene(votingView.getRoot(),900,700);

            votingScene.getStylesheets().add(css);

            primaryStage.setTitle("Digitale Schulwahl");
            primaryStage.setScene(votingScene);
            primaryStage.setResizable(true);
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
