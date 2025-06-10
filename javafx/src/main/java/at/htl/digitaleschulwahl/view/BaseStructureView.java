package at.htl.digitaleschulwahl.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

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

    public VBox createHeadingSection(String secondTitle){
        VBox headingContent = new VBox();
        headingContent.getStyleClass().add("content");

        Label firstHeading = new Label("Digitale Schulwahl");
        firstHeading.getStyleClass().add("first-heading");

        Line line = new Line();
        line.getStyleClass().add("underline");

        VBox headingBox = new VBox();
        headingBox.setAlignment(Pos.CENTER);
        headingBox.getChildren().addAll(firstHeading, line);

        firstHeading.widthProperty().addListener((a, b, newWidth) -> {
            line.setStartX(-40);
            line.setEndX(newWidth.doubleValue() + 40);
        });

        Label secondHeading = new Label(secondTitle);
        secondHeading.getStyleClass().add("second-heading");
        headingBox.getChildren().add(secondHeading);
        headingContent.getChildren().add(headingBox);
        return headingContent;
    }
}
