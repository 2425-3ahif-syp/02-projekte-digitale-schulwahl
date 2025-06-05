package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.controller.DiagrammController;
import javafx.scene.layout.BorderPane;

public class DiagrammView {
    private final DiagrammController diagrammController;
    private final BorderPane root = new BorderPane();
    BaseStructureView baseStruct = new BaseStructureView(root);

    public DiagrammView(final DiagrammController diagrammController) {
        this.diagrammController = diagrammController;
        baseStruct.createNavBar();
    }



}
