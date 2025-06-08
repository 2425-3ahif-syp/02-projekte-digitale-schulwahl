package at.htl.digitaleschulwahl.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastNotification {
    public static void show(Stage owner, String message) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        popup.getScene().setFill(null);
        Label label = new Label(message);
        label.getStylesheets().add(ToastNotification.class.getResource("/toastNotification.css").toExternalForm());
        label.getStyleClass().add("toast-label");
        label.setWrapText(true);
        label.setMaxWidth(250);

        popup.getContent().add(label);

        double toastWidth = 250;
        double margin = 15;
        double titleBarHeight = 130;

        double x = owner.getX() + owner.getWidth() - toastWidth - margin;
        double y = owner.getY() + titleBarHeight + margin;

        if (x < owner.getX()) {
            x = owner.getX() + margin;
        }
        if (y < owner.getY()) {
            y = owner.getY() + titleBarHeight + margin;
        }

        popup.show(owner, x, y);

        label.setOpacity(0);
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(label.opacityProperty(), 1.0)));

        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(2700),
                        new KeyValue(label.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(3000),
                        new KeyValue(label.opacityProperty(), 0.0)));

        fadeOut.setOnFinished(e -> popup.hide());

        fadeIn.play();
        fadeIn.setOnFinished(e -> fadeOut.play());
    }
}
