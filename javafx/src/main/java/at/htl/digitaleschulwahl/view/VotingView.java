package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.controller.VotingController;
import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Java-FX Oberfläche fürs Abstimmen

public class VotingView {
    private final VotingController controller;
    private final BorderPane root = new BorderPane();
    private int numOfPoints;
    private List<Candidate> candidates;
    private ToggleGroup[] departmentGroups;
    private ToggleGroup[] studentCouncilGroups;

    public VotingView(VotingController controller) {
        this.controller = controller;
        createUI();
    }

    public BorderPane getRoot() {
        return root;
    }

    public void createUI() {
        numOfPoints = 2;
        List<Candidate> sortedCandidates = controller.getCandidates().stream()
                .filter(c -> c.getType() == "Abteilungsvertreter")  // Normaler String-Vergleich
                .collect(Collectors.toList());;

        int numOfDepartments = sortedCandidates.size();
        System.out.println(numOfDepartments);

        var toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        var main = createMainUI("Abteilungvertretung", sortedCandidates, numOfPoints);
        Button continueButton = new Button("Weiter");
        continueButton.setOnAction(e -> createUiForStudentCouncil());
        main.getChildren().add(continueButton);
        continueButton.getStyleClass().add("button");
        root.setCenter(main);
    }

    public void createUiForStudentCouncil() {
        numOfPoints = 6;
        List<Candidate> studentCouncilCandidate = controller.getCandidates().stream().
                filter(c -> Objects.equals(c.getType(), "Schülersprecher"))
              .collect(Collectors.toList());

        var main = createMainUI("Schülervertretung", studentCouncilCandidate, numOfPoints);
        Button backButton = new Button("Zurück");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> createUI());

        Button continueButton = new Button("Weiter");

        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        main.getChildren().add(buttons);

        root.setCenter(main);
    }

    private VBox createMainUI(String title, List<Candidate> candidates, int maxPoints) {
        VBox main = new VBox(10);
        main.setPadding(new Insets(20));
//        main.getStyleClass().add("main-container");

        Label titleLabel = new Label("Digitale Schulwahl - Wahl");
        titleLabel.getStyleClass().add("title-label");

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: white;");

        Label sectionLabel = new Label(title);
        sectionLabel.getStyleClass().add("section-label");

        main.getChildren().addAll(titleLabel, sectionLabel);

        ToggleGroup[] pointGroups = new ToggleGroup[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            pointGroups[i] = new ToggleGroup();
        }

        for (int i = 0; i < candidates.size(); i++) {
            Candidate candidate = candidates.get(i);
            HBox candidateBox = new HBox(25);
            candidateBox.getStyleClass().add("candidate-box");

           // System.out.println(getClass().getClassLoader().getResource("img/placeholder.jpg").toExternalForm());
           // ImageView profileImage = new ImageView(new Image(getClass().getClassLoader().getResource("placeholder.jpg").toExternalForm()));

           // profileImage.setFitWidth(50);
            // profileImage.setFitHeight(50);

            VBox infoBox = new VBox(new Label(candidate.getName()), new Label(candidate.getClassName()));

            ToggleGroup group = pointGroups[i];
            HBox radioButtons = new HBox(5);

            for (int j = maxPoints; j > 0; j--) {
                RadioButton radioButton = new RadioButton();
                radioButton.setToggleGroup(group);
                radioButton.setUserData(j);
                radioButtons.getChildren().add(radioButton);
            }

            candidateBox.getChildren().addAll( infoBox, radioButtons);
            main.getChildren().add(candidateBox);
        }

        if (maxPoints == 2) {
            this.departmentGroups = pointGroups;
        } else {
            this.studentCouncilGroups = pointGroups;
        }

        return main;
    }

   /* public HBox createMainUI(String title, List<Candidate> candidates, int maxPoints) {
        HBox main = new HBox();
        main.setSpacing(10);
        main.setPadding(new Insets(10));
        main.getChildren().add(new Label("Digitale Schulwahl - Wahl"));

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: black;");

        main.getChildren().add(new Label(title));

        ToggleGroup[] pointGroups = new ToggleGroup[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            pointGroups[i] = new ToggleGroup();
        }

        for (int i = 0; i < candidates.size(); i++) {
            Candidate candidate = candidates.get(i);
            HBox candidateBox = new HBox(new Label(candidate.getName()));
            ToggleGroup group = pointGroups[i];

            for (int j = maxPoints; j > 0; j--) {
                RadioButton radioButton = new RadioButton(String.valueOf(j));
                radioButton.setToggleGroup(group);
                radioButton.setUserData(j);
                candidateBox.getChildren().add(radioButton);
            }
            main.getChildren().add(candidateBox);
        }

        if (maxPoints == 2) {
            this.departmentGroups = pointGroups;
        } else {
            this.studentCouncilGroups = pointGroups;
        }

        return main;
    }
*/


    private void saveVotes(ToggleGroup[] groups, String type) {
        int points = 0;
        for (int i = 0; i < candidates.size(); i++) {
            Candidate candidate = candidates.get(i);
            if (groups[i].getSelectedToggle() != null) {
                points = (int) groups[i].getSelectedToggle().getUserData();
            }

            Vote vote = new Vote(candidate, points);
            controller.castVote(vote);
        }
    }
}
