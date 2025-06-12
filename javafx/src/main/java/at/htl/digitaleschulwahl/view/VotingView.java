package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.database.VoteRepository;
import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;
import at.htl.digitaleschulwahl.presenter.VotingPresenter;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.util.*;

public class VotingView {
    private final VotingPresenter controller;
    private final BorderPane root = new BorderPane();
    private final Button continueButton = new Button("Weiter");
    private final Button backButton = new Button("Zurück");
    private final Button submitButton = new Button("Stimmen abgeben");
    BaseStructureView baseStruct = new BaseStructureView(root);
    private Label secondHeading = new Label();
    private final HBox pointsHeader = new HBox(10);

    public HBox getPointsHeader() {
        return pointsHeader;
    }

    // temporär
    private final VoteRepository voteRepository = new VoteRepository();

    // Listen, um die aktuell angezeigten Kandidaten und deren zugehörige
    // ToggleGroups zu speichern.
    // Diese werden benötigt, um beim Absenden (Submit) die ausgewählten Rankings
    // den richtigen Kandidaten zuzuordnen.
    private List<ToggleGroup> currentToggleGroups = new ArrayList<>();
    private List<Candidate> currentCandidates = new ArrayList<>();

    public VotingView(VotingPresenter controller) {
        this.controller = controller;
        baseStruct.createNavBar();
        backButton.setVisible(false);

    }

    public void setSecondHeading(String text) {
        this.secondHeading.setText(text);
    }

    public void createUI() {

        VBox bottomBox = new VBox();
        bottomBox.setSpacing(10);
        bottomBox.getStyleClass().add("bottom-button-container");

        backButton.setVisible(false);
        backButton.setManaged(false);
        backButton.getStyleClass().add("bottom-button");

        continueButton.getStyleClass().add("bottom-button");

        bottomBox.getChildren().addAll(backButton, continueButton);

        submitButton.getStyleClass().add("submit-button");
        submitButton.setVisible(false);

        submitButton.setOnAction(event -> {
            Stage stage = (Stage) submitButton.getScene().getWindow();

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Wahl bestätigen");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Sind Sie sicher, dass Sie Ihre Stimmen abgeben möchten? " +
                    "Nach dem Abschicken kann Ihre Wahl nicht mehr geändert werden");

            Button okButton = (Button) confirmation.getDialogPane().lookupButton(ButtonType.OK);
            Button cancelButton = (Button) confirmation.getDialogPane().lookupButton(ButtonType.CANCEL);

            if (okButton != null) {
                okButton.setText("Ja");
            }
            if (cancelButton != null) {
                cancelButton.setText("Nein");
            }

            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (int i = 0; i < currentCandidates.size(); i++) {
                    var candidate = currentCandidates.get(i);
                    var group = currentToggleGroups.get(i);

                    if (group.getSelectedToggle() != null) {
                        int ranking = (int) group.getSelectedToggle().getUserData();
                        // TODO
                        // ACHTUNG
                        // später muss auf die Klasse des Wählenden zugegriffen werden, 111 ist nur ein
                        // Platzhalter
                        // wird erstmal so gelassen, bis es soweit ist

                        var vote = new Vote(candidate.getId(), ranking, 111);
                        // var vote = new Vote(candidate_id, ranking);

                        voteRepository.castVote(vote); // Vote wird in der DB gespeichert
                    } else {
                        // eigentlich muss eh nicht jeder eine Wahl bekommen
                        // ich denke, der else-Zweig bleibt fürs Erste leer ...
                    }
                }
                // Feedback an den Benutzer
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Stimmabgabe");
                alert.setHeaderText(null);
                alert.setContentText("Ihre Stimme(n) wurden erfolgreich gespeichert.");
                alert.showAndWait();
            } else {
                confirmation.close();
                stage.close();
            }

        });
        bottomBox.getChildren().add(submitButton);

        root.setBottom(bottomBox);
    }

    public VBox createVotingUI() {
        VBox mainContainer = new VBox();

        String secondHeading = controller.getSecondHeading();
        VBox headingContent = baseStruct.createHeadingSection(secondHeading);

        VBox votingSection = new VBox();
        votingSection.getStyleClass().add("voting-content");

        HBox candidatesAndVotingBox = new HBox();
        candidatesAndVotingBox.setSpacing(100);
        candidatesAndVotingBox.setAlignment(Pos.CENTER);

        VBox candidateInfoSection = new VBox(10);
        candidateInfoSection.setAlignment(Pos.CENTER_LEFT);
        candidateInfoSection.getChildren().addAll(createCandidateInfoSection());

        currentToggleGroups.clear();
        currentCandidates.clear();
        currentCandidates.addAll(controller.getCurrentCandidatesByType());
        VBox pointsSection;

        pointsSection = createPointsSection();
        pointsSection.setAlignment(Pos.CENTER);

        candidatesAndVotingBox.getChildren().addAll(candidateInfoSection, pointsSection);
        votingSection.getChildren().add(candidatesAndVotingBox);

        mainContainer.getChildren().addAll(headingContent, votingSection);
        return mainContainer;
    }

    private VBox createCandidateInfoSection() {
        var list = controller.getCurrentCandidatesByType();
        VBox candidateInfoSection = new VBox(15);
        candidateInfoSection.setAlignment(Pos.CENTER);

        for (Candidate candidate : list) {
            HBox currentBox = new HBox(15);
            currentBox.setAlignment(Pos.CENTER_LEFT);

            // TODO
            // Später werden die Bilder aus der DB geholt
            // benötigte Methode wird dann in der Candidate Klasse verfügbar sein

            ImageView img = new ImageView(new Image(getClass().getResource("/img/placeholder.jpg").toExternalForm()));
            img.setFitWidth(100);
            img.setFitHeight(100);
            img.setPreserveRatio(true);
            img.getStyleClass().add("img");

            Circle clip = new Circle(50, 50, 50);
            img.setClip(clip);

            Line verticalLine = new Line(0, 0, 0, 100);
            verticalLine.setStroke(Color.WHITE);
            verticalLine.setStrokeWidth(2);

            VBox textSection = new VBox(5);
            Label name = new Label(candidate.getName());

            name.getStyleClass().add("candidateLabel");
            name.setStyle("-fx-font-weight: bold");
            Label _class = new Label(candidate.getClassName().toUpperCase());
            _class.getStyleClass().add("candidateLabel");
            textSection.getChildren().addAll(name, _class);

            currentBox.getChildren().addAll(img, verticalLine, textSection);
            candidateInfoSection.getChildren().add(currentBox);
        }
        return candidateInfoSection;
    }

    private VBox createPointsSection() {
        var list = controller.getCurrentCandidatesByType();
        var maxPoints = controller.updateMaxPoints();
        VBox mainVBox = new VBox();
        mainVBox.setAlignment(Pos.CENTER);

        pointsHeader.setAlignment(Pos.CENTER);
        pointsHeader.getStyleClass().add("points-header");
        pointsHeader.setPrefHeight(2);

        for (int i = maxPoints; i >= 1; i--) {
            Label label = new Label("" + i);
            label.getStyleClass().add("label");
            pointsHeader.getChildren().add(label);
        }

        VBox pointsSection = new VBox(5);
        pointsSection.setAlignment(Pos.CENTER);
        pointsSection.setMinHeight(110 * list.size());

        List<List<ToggleButton>> columnButtons = new ArrayList<>();
        List<List<ToggleButton>> rowButtons = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            HBox pointsBox = new HBox(10);
            pointsBox.setMinHeight(110);
            pointsBox.setAlignment(Pos.CENTER);

            List<ToggleButton> currentRow = new ArrayList<>();

            for (int j = maxPoints; j >= 1; j--) {
                ToggleButton btn = new ToggleButton();
                btn.getStyleClass().add("point-button");
                btn.setUserData(j); // Punktewert

                // Spaltenliste initialisieren
                while (columnButtons.size() < maxPoints) {
                    columnButtons.add(new ArrayList<>());
                }

                columnButtons.get(maxPoints - j).add(btn);
                currentRow.add(btn);

                StackPane wrapper = new StackPane(btn);
                wrapper.setAlignment(Pos.CENTER);
                pointsBox.getChildren().add(wrapper);
            }
            rowButtons.add(currentRow);
            pointsSection.getChildren().add(pointsBox);
        }
        mainVBox.getChildren().addAll(pointsHeader, pointsSection);

        controller.registerPointButtonHandlers(columnButtons, rowButtons);
        controller.setRowButtons(rowButtons);
        return mainVBox;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public void setMaxPoints(int maxPoints) {
    }
}
