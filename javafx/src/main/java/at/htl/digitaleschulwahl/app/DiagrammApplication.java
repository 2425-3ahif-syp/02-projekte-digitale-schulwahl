package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.presenter.DiagrammPresenter;
import javafx.application.Application;
import javafx.stage.Stage;


public class DiagrammApplication extends Application
{
    @Override
    public void start(Stage primaryStage) {

        DiagrammPresenter.show(primaryStage);
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
