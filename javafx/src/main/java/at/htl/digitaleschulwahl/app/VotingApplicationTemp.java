package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.controller.VotingController;
import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.view.MainView;
import at.htl.digitaleschulwahl.controller.MainController;
import at.htl.digitaleschulwahl.view.VotingView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VotingApplicationTemp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            var databaseManager = DatabaseManager.getInstance();

            var votingController = new VotingController();

            var votingView = new VotingView(votingController);


            String css = getClass().getClassLoader().getResource("votingPageStyle.css").toExternalForm();

           /* Die scene wird erst durch einen button-click oder so angezeigt.
               Eine zweite votingScene wird dann noch benötigt, weil einmal für SV und
               einmal für Abteilungssprecher ... wird dann noch gehandelt!*/

            var votingScene = new Scene(votingView.getRoot(),900,600);
            votingScene.getStylesheets().add(css);

            primaryStage.setTitle("Digitale Schulwahl");
            primaryStage.setScene(votingScene);
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
