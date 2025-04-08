package at.htl.digitaleschulwahl.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BaseStructureView {
    private BorderPane root;
    public BaseStructureView(BorderPane root){
        this.root = root;
    }

    public void createNavBar() {

        HBox toolBar = new HBox();

        toolBar.getStyleClass().add("tool-bar");

        ImageView imageView = new ImageView(new Image(getClass().getResource("/img/htl_leonding_logo.png").toExternalForm()));

        imageView.setFitHeight(60);

        imageView.setFitWidth(260);

        toolBar.getChildren().add(imageView);

        root.setTop(toolBar);

    }
}
