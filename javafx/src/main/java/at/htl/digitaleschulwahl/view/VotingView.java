package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.controller.VotingController;
import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;
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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class VotingView {
    private final VotingController controller;
    private final BorderPane root = new BorderPane();
    private boolean isCouncil = false; //true = Schülervertretung, false= Abteilungsvertretung
    private final Button backButton = new Button("Zurück");
    private final Button continueButton = new Button("Weiter");
    BaseStructureView baseStruct = new BaseStructureView(root);

    // Listen, um die aktuell angezeigten Kandidaten und deren zugehörige ToggleGroups zu speichern.
    // Diese werden benötigt, um beim Absenden (Submit) die ausgewählten Rankings den richtigen Kandidaten zuzuordnen.
    private List<ToggleGroup> currentToggleGroups = new ArrayList<>();
    private List<Candidate> currentCandidates = new ArrayList<>();

    public VotingView(VotingController controller) {
        this.controller = controller;
        baseStruct.createNavBar();
        backButton.setVisible(false);
        createUI();
    }


    public BorderPane getRoot() {
        return root;
    }

    public void createUI() {

        var main = new VBox();

        // Zunächst wird die Ansicht für den ersten "Wahlmodus" erstellt (Abteilungsvertretung)
        isCouncil = false;

        //main = createVotingUI(true);
        main = createVotingUI(isCouncil);
        //      main = createVotingUI(false);

        root.setCenter(main);

        VBox bottomBox = new VBox();
        bottomBox.setSpacing(10);
        bottomBox.getStyleClass().add("bottom-button-container");

        Button continueButton = new Button("Weiter");
        continueButton.getStyleClass().add("bottom-button");

        bottomBox.getChildren().addAll(continueButton, backButton);


        var submitButton = new Button("Stimmen abgeben");
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
                        //TODO
                        // ACHTUNG
                        // später muss auf die Klasse des Wählenden zugegriffen werden, 111 ist nur ein Platzhalter
                        // wird erstmal so gelassen, bis es soweit ist

                        var vote = new Vote(candidate.getId(), ranking,111);
                        // var vote = new Vote(candidate_id, ranking);

                        controller.castVote(vote); // Vote wird in der DB gespeichert
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

        continueButton.setOnAction(event -> {

            isCouncil = true;
            root.setCenter(createVotingUI(isCouncil));
            //    root.setCenter(createVotingUI(false));
            backButton.setVisible(true);
            backButton.getStyleClass().add("bottom-button");

            continueButton.setVisible(false);

            submitButton.setVisible(true);
            bottomBox.getChildren().add(backButton);
        });

        backButton.setOnAction(event -> {
            isCouncil = false;
            root.setCenter(createVotingUI(isCouncil));
            continueButton.setVisible(true);
            submitButton.setVisible(false);
            //  root.setCenter(createVotingUI(true));
            backButton.setVisible(false);
        });

        root.setBottom(bottomBox);
    }

    private VBox createVotingUI(boolean isCouncil) {
        VBox mainContainer = new VBox();

        List<Candidate> allCandidates = controller.getCandidates();

        String typeToFilter = isCouncil ? "Schülersprecher" : "Abteilungsvertreter";

        currentCandidates = allCandidates.stream()
                .filter(candidate -> candidate.getType() != null
                        && candidate.getType().trim().equalsIgnoreCase(typeToFilter))
                .toList();

        VBox headingContent = new VBox();
        headingContent.getStyleClass().add("content");

        HBox headingTitle = new HBox();
        headingTitle.setAlignment(Pos.CENTER);

        Label firstHeading = new Label("Digitale Schulwahl - Wahl");
        firstHeading.getStyleClass().add("first-heading");

        Line line = new Line();
        line.getStyleClass().add("underline");

        VBox headingBox = new VBox();
        headingBox.setAlignment(Pos.CENTER);
        headingBox.getChildren().addAll(firstHeading, line);

        firstHeading.widthProperty().addListener((_, _, newWidth) -> {
            line.setStartX(-40);
            line.setEndX(newWidth.doubleValue() + 40);
        });

        Label secondHeading = new Label(getSecondHeading(isCouncil));
        secondHeading.getStyleClass().add("second-heading");
        headingBox.getChildren().add(secondHeading);

        headingContent.getChildren().add(headingBox);

        VBox votingSection = new VBox();
        votingSection.getStyleClass().add("voting-content");

        HBox candidatesAndVotingBox = new HBox();
        candidatesAndVotingBox.setSpacing(100);
        candidatesAndVotingBox.setAlignment(Pos.CENTER);

        VBox candidateInfoSection = new VBox(10);
        candidateInfoSection.setAlignment(Pos.CENTER_LEFT);
        candidateInfoSection.getChildren().addAll(createCandidateInfoSection(currentCandidates));
        //candidateInfoSection.getChildren().addAll(createCandidateInfoSection(testCandidates));

        currentToggleGroups.clear();
        VBox pointsSection;

      /*  if (isCouncil) {
            pointsSection = createPointsSection(2, testCandidates);

        } else {
            pointsSection = createPointsSection(6, testCandidates);
        }*/

        pointsSection = createPointsSection(isCouncil ? 6 : 2, currentCandidates);
        pointsSection.setAlignment(Pos.CENTER);

        candidatesAndVotingBox.getChildren().addAll(candidateInfoSection, pointsSection);
        votingSection.getChildren().add(candidatesAndVotingBox);

        mainContainer.getChildren().addAll(headingContent, votingSection);

        return mainContainer;
    }

    private String getSecondHeading(boolean isCouncil) {
        return isCouncil ? "Schülervertretung" : "Abteilungsvertretung [Abteilung]";
    }


    private VBox createCandidateInfoSection(List<Candidate> candidates) {
        VBox candidateInfoSection = new VBox(15);
        candidateInfoSection.setAlignment(Pos.CENTER);

        for (int i = 0; i < candidates.size(); i++) {
            HBox currentBox = new HBox(15);
            currentBox.setAlignment(Pos.CENTER_LEFT);

            //TODO
            // Später werden die Bilder aus der DB geholt
            // benötigte Methode wird dann in der Candidate Klasse verfügbar sein

            ImageView img = new ImageView(new Image("file:src/resources/img/placeholder.jpg"));
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
            Label name = new Label(candidates.get(i).getName());

            name.getStyleClass().add("candidateLabel");
            name.setStyle("-fx-font-weight: bold");
            Label _class = new Label(candidates.get(i).getClassName().toUpperCase());
            _class.getStyleClass().add("candidateLabel");
            textSection.getChildren().addAll(name, _class);

            currentBox.getChildren().addAll(img, verticalLine, textSection);
            candidateInfoSection.getChildren().add(currentBox);
        }
        return candidateInfoSection;
    }


    private VBox createPointsSection(int maxPoints, List<Candidate> candidates) {
        VBox mainVBox = new VBox();
        mainVBox.setAlignment(Pos.CENTER);

        HBox pointsHeader = new HBox(10);
        pointsHeader.setAlignment(Pos.CENTER);
        pointsHeader.getStyleClass().add("points-header");
        pointsHeader.setPrefHeight(2);

        if (maxPoints == 6) {
            pointsHeader.setSpacing(35);
        } else {
            pointsHeader.setSpacing(20);
        }

        for (int i = maxPoints; i >= 1; i--) {
            Label label = new Label("" + i);
            label.getStyleClass().add("label");
            pointsHeader.getChildren().add(label);
        }

        VBox pointsSection = new VBox(5);
        pointsSection.setAlignment(Pos.CENTER);
        pointsSection.setMinHeight(110 * candidates.size());

        List<List<ToggleButton>> columnButtons = new ArrayList<>();
        List<List<ToggleButton>> rowButtons = new ArrayList<>();

        for (int i = 0; i < candidates.size(); i++) {
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

                int finalRowIndex = i;
                int finalColIndex = maxPoints - j;

                btn.setOnAction(e -> updateButtonStates(columnButtons, rowButtons, finalRowIndex, finalColIndex));

                StackPane wrapper = new StackPane(btn);
                wrapper.setAlignment(Pos.CENTER);
                pointsBox.getChildren().add(wrapper);
            }

            rowButtons.add(currentRow);
            pointsSection.getChildren().add(pointsBox);
        }

        mainVBox.getChildren().addAll(pointsHeader, pointsSection);
        return mainVBox;
    }



    private void updateButtonStates(
            List<List<ToggleButton>> columnButtons,
            List<List<ToggleButton>> rowButtons,
            int rowIndex,
            int newColIndex) {

        // Der angeklickte Button
        ToggleButton clicked = rowButtons.get(rowIndex).get(newColIndex);
        boolean isSelectedNow = clicked.isSelected();

        // Wenn der Button jetzt selektiert ist
        if (isSelectedNow) {
            // Alle Buttons in der gleichen Reihe abwählen
            for (int i = 0; i < rowButtons.get(rowIndex).size(); i++) {
                ToggleButton btn = rowButtons.get(rowIndex).get(i);
                if (btn != clicked && btn.isSelected()) {
                    btn.setSelected(false);  // Alle anderen in der Reihe abwählen
                }
            }

            // Wenn der Button in der gleichen Spalte in der vorherigen Reihe selektiert ist, abwählen
            if (rowIndex > 0) {
                ToggleButton prevRowButton = rowButtons.get(rowIndex - 1).get(newColIndex);
                if (prevRowButton.isSelected()) {
                    prevRowButton.setSelected(false); // Den Button in der vorherigen Reihe abwählen
                }
            }

            // Wenn der Button in der gleichen Spalte in der nächsten Reihe selektiert ist, abwählen
            if (rowIndex < rowButtons.size() - 1) {
                ToggleButton nextRowButton = rowButtons.get(rowIndex + 1).get(newColIndex);
                if (nextRowButton.isSelected()) {
                    nextRowButton.setSelected(false); // Den Button in der nächsten Reihe abwählen
                }
            }
        } else {
            // Wenn der Button abgewählt wird, überprüfe, ob in der gleichen Spalte in einer anderen Reihe noch einer selektiert ist
            boolean anySelectedInSameCol = false;
            for (int i = 0; i < rowButtons.size(); i++) {
                if (i != rowIndex && rowButtons.get(i).get(newColIndex).isSelected()) {
                    anySelectedInSameCol = true;
                    break;
                }
            }

            // Wenn kein Button in dieser Spalte mehr selektiert ist, alle Buttons in dieser Spalte wieder aktivieren
            if (!anySelectedInSameCol) {
                for (int i = 0; i < rowButtons.size(); i++) {
                    if (i != rowIndex) {
                        rowButtons.get(i).get(newColIndex).setDisable(false);
                    }
                }
            }
        }
    }





    /*private void saveVotes(ToggleGroup[] groups, String type) {
        int points = 0;
        File file = new File("votes.txt");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            for (int i = 0; i < candidates.size(); i++) {
                Candidate candidate = candidates.get(i);

                if (groups[i].getSelectedToggle() != null) {
                    points = (int) groups[i].getSelectedToggle().getUserData();
                }

                writer.write(candidate.getName() + ": " + points + " Punkte (" + type + ")\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Fehler beim Speichern der Votes!");
        }
    }*/


}
