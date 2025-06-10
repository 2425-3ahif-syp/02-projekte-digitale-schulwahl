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
        DiagrammRepository repo = new DiagrammRepository(DatabaseManager.getInstance().getConnection());
        DiagrammView view = new DiagrammView();

        String css = getClass().getResource("/votingPageStyle.css").toExternalForm();

        new DiagrammPresenter(repo, view);

        Scene scene = new Scene(view.getRoot(), 1000, 600);
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Digitale Schulwahl – Auswertung");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
