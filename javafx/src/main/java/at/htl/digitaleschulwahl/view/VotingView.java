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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VotingView {
    private final VotingController controller;
    private final BorderPane root = new BorderPane();
    private List<Candidate> candidates;
    /* private ToggleGroup[] departmentGroups;
     private ToggleGroup[] studentCouncilGroups;*/
    private boolean isCouncil = false; //true = Schülervertretung
    private Button backButton = new Button("Zurück");

    public VotingView(VotingController controller) {
        this.controller = controller;
        backButton.setVisible(false);
        createUI();
    }

    public BorderPane getRoot() {
        return root;
    }

    public void createUI() {

        VBox main = new VBox();
        HBox toolBar = new HBox();

        toolBar.getStyleClass().add("tool-bar");

        ImageView imageView = new ImageView(new Image("file:htl_leonding_logo.png"));

        imageView.setFitHeight(60);

        imageView.setFitWidth(260);

        toolBar.getChildren().add(imageView);

        root.setTop(toolBar);

        isCouncil = false;
        main = createVotingUI(true);

        root.setCenter(main);

        VBox bottomBox = new VBox();
        bottomBox.setSpacing(10);
        bottomBox.getStyleClass().add("bottom-button-container");

        Button continueButton = new Button("Weiter");
        continueButton.getStyleClass().add("bottom-button");

        bottomBox.getChildren().addAll(continueButton, backButton);
        continueButton.setOnAction(event -> {

            isCouncil = true;
            root.setCenter(createVotingUI(false));
            backButton.setVisible(true);
            backButton.getStyleClass().add("bottom-button");
            bottomBox.getChildren().add(backButton);
        });

        backButton.setOnAction(event -> {
            root.setCenter(createVotingUI(true));
            backButton.setVisible(false);
        });

        root.setBottom(bottomBox);
    }

    private VBox createVotingUI(boolean isCouncil) {
        VBox mainContainer = new VBox();

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

      /*  var candidates = controller.getCandidates();

        if (!isCouncil) {
            candidates = candidates.stream()
                    .filter(candidate -> candidate.getType() != null && candidate.getType().trim().equalsIgnoreCase("Schülervertreter"))
                    .toList();
        } else {
            candidates = candidates.stream()
                    .filter(candidate -> candidate.getType() != null && candidate.getType().trim().equalsIgnoreCase("Abteilungsvertreter"))
                    .toList();
        }
        */

        var testListOfCandidates = new ArrayList<Candidate>();
        testListOfCandidates.add(new Candidate(1, "Anna Schmidt", "4AHITM", "Schülersprecher"));
        testListOfCandidates.add(new Candidate(2, "Felix Bauer", "3BHIF", "Abteilungsvertreter"));
        testListOfCandidates.add(new Candidate(3, "Julia Fischer", "5BHITM", "Abteilungsvertreter"));
        testListOfCandidates.add(new Candidate(4, "Lukas Meier", "5AHIF", "Schülersprecher"));

        List<Candidate> testCandidates;
        if (isCouncil) {
            testCandidates = testListOfCandidates.stream().filter(candidate -> candidate.getType().equals("Schülersprecher")).toList();
        } else {
            testCandidates = testListOfCandidates.stream().filter(candidate -> candidate.getType().equals("Abteilungsvertreter")).toList();
        }


        VBox votingSection = new VBox();
        votingSection.getStyleClass().add("voting-content");

        HBox candidatesAndVotingBox = new HBox();
        candidatesAndVotingBox.setSpacing(200);
        candidatesAndVotingBox.setAlignment(Pos.CENTER);

        VBox candidateInfoSection = new VBox(10);
        candidateInfoSection.setAlignment(Pos.CENTER_LEFT);
        candidateInfoSection.getChildren().addAll(createCandidateInfoSection(testCandidates));

        VBox pointsSection;

        if (isCouncil) {
            pointsSection = createPointsSection(2, testCandidates);

        } else {
            pointsSection = createPointsSection(6, testCandidates);
        }
        pointsSection.setAlignment(Pos.CENTER);

        candidatesAndVotingBox.getChildren().addAll(candidateInfoSection, pointsSection);
        votingSection.getChildren().add(candidatesAndVotingBox);

        mainContainer.getChildren().addAll(headingContent, votingSection);

        return mainContainer;
    }

    private String getSecondHeading(boolean isCouncil) {
        String heading = "";
        if (!isCouncil) {
            heading = "Schülervertretung";
        } else {
            heading = "Abteilungsvertretung [Abteilung]";
        }
        return heading;
    }

    private VBox createCandidateInfoSection(List<Candidate> candidates) {
        VBox candidateInfoSection = new VBox(10);

        for (int i = 0; i < candidates.size(); i++) {
            HBox currentBox = new HBox(15);
            currentBox.setAlignment(Pos.CENTER);

            ImageView img = new ImageView(new Image("file:src/resources/img/placeholder.jpg")); //
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
            Label _class = new Label(candidates.get(i).getClassName());
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

        if (maxPoints == 6) {
            pointsHeader.setSpacing(38);
        } else {
            pointsHeader.setSpacing(20);
        }

        for (int i = maxPoints; i >= 1; i--) {
            Label label = new Label("" + i);
            label.getStyleClass().add("label");
            pointsHeader.getChildren().add(label);
        }

        VBox pointsSection = new VBox(10);
        pointsSection.setAlignment(Pos.CENTER);
        pointsSection.setMinHeight(110 * candidates.size());

        for (Candidate candidate : candidates) {
            HBox pointsBox = new HBox(10);
            pointsBox.setMinHeight(110); // Genauso hoch wie candidateBox
            pointsBox.setAlignment(Pos.CENTER);

            ToggleGroup group = new ToggleGroup();

            for (int i = maxPoints; i >= 1; i--) {
                RadioButton radioButton = new RadioButton();
                radioButton.getStyleClass().add("radio-button");
                radioButton.setToggleGroup(group);
                radioButton.setUserData(i);

                StackPane radioButtonWrapper = new StackPane();
                radioButtonWrapper.setAlignment(Pos.CENTER);
                radioButtonWrapper.getChildren().add(radioButton);


                pointsBox.getChildren().add(radioButtonWrapper);
            }
            pointsSection.getChildren().add(pointsBox);
        }
        mainVBox.getChildren().addAll(pointsHeader, pointsSection);

        return mainVBox;
    }


    private void saveVotes(ToggleGroup[] groups, String type) {
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
    }
}
