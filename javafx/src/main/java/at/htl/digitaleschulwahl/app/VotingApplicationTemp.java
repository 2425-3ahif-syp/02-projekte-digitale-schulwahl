package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.presenter.VotingPresenter;
import javafx.application.Application;
import javafx.stage.Stage;

public class VotingApplicationTemp extends Application {

    @Override
    public void start(Stage primaryStage) {
      try {
          var databaseManager = DatabaseManager.getInstance();
          VotingPresenter presenter = new VotingPresenter();
          presenter.show(primaryStage);

      } catch (Exception e) {
          e.printStackTrace();
      }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void stop() {
        DatabaseManager.getInstance().closeConnection();
    }

}
