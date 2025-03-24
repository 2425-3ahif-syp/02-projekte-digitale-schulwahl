package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.controller.VotingController;
import javafx.scene.layout.BorderPane;

// Java-FX Oberfläche fürs Abstimmen

public class VotingView {
   private final VotingController controller;
   private final BorderPane root= new BorderPane();

    public VotingView(VotingController controller) {
        this.controller = controller;
        createUI();
    }
    public BorderPane getRoot() {
        return root;
    }

    public void createUI() {

    }
}
