// TODO
package at.htl.digitaleschulwahl.app;

import at.htl.digitaleschulwahl.presenter.MainPresenter;
import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {

            var mainController = new MainPresenter();

            var mainView = new MainView(mainController);
            String css = getClass().getResource("/mainPageStyle.css").toExternalForm();
            String css1 = getClass().getResource("/votingPageStyle.css").toExternalForm();

            var mainScene = new Scene(mainView.getRoot(), 900, 600);
            mainScene.getStylesheets().addAll(css1, css);

            primaryStage.setTitle("Digitale Schulwahl");
            primaryStage.setScene(mainScene);
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
