package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.controller.VotingController;
import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


import java.util.ArrayList;
import java.util.List;

// Java-FX Oberfläche fürs Abstimmen

public class VotingView {
    private final VotingController controller;
    private final BorderPane root = new BorderPane();
    private final List<Candidate> canidates = new ArrayList();
    private int numOfPoints;
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
        HBox main = createMainUI("Abteilungvertretung", canidates, numOfPoints);
        Button continueButton = new Button("Weiter");
        continueButton.setOnAction(e -> createUiForStudentCouncil());
        main.getChildren().add(continueButton);
        root.setTop(main);
    }

    public void createUiForStudentCouncil() {
        numOfPoints = 6;
        HBox main = createMainUI("Schülervertretung", canidates, numOfPoints);
        Button backButton = new Button("Zurück");
        backButton.setOnAction(e -> createUI());

        Button continueButton = new Button("Weiter");

        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10));
        main.getChildren().add(buttons);

        root.setCenter(main);
    }

    public HBox createMainUI(String title, List<Candidate> canidates, int maxPoints) {
        HBox main = new HBox();
        main.setSpacing(10);
        main.setPadding(new Insets(10));
        main.getChildren().add(new Label("Digitale Schulwahl - Wahl"));

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: black;");

        main.getChildren().add(new Label(title));

        ToggleGroup[] pointGroups = new ToggleGroup[canidates.size()];
        for (int i = 0; i < canidates.size(); i++) {
            pointGroups[i] = new ToggleGroup();
        }

        for (int i = 0; i < canidates.size(); i++) {
            Candidate candidate = canidates.get(i);
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


    private void saveVotes(ToggleGroup[] groups, String type) {
        int points = 0;
        for (int i = 0; i < canidates.size(); i++) {
            Candidate candidate = canidates.get(i);
            if (groups[i].getSelectedToggle() != null) {
                points = (int) groups[i].getSelectedToggle().getUserData();
            }

            Vote vote = new Vote(candidate, points);
            controller.castVote(vote);
        }
    }
}
