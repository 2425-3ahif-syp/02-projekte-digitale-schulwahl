package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.database.DiagrammRepository;
import at.htl.digitaleschulwahl.presenter.DiagrammPresenter;
import at.htl.digitaleschulwahl.view.DiagrammView;
import javafx.application.Application;
import javafx.stage.Stage;

import javafx.scene.Scene;

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
