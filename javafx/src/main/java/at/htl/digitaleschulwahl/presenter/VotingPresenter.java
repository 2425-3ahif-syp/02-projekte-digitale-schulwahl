package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.VoteRepository;
import at.htl.digitaleschulwahl.database.CandidateRepository;
import at.htl.digitaleschulwahl.database.StudentRepository;
import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;
import at.htl.digitaleschulwahl.model.Student;
import at.htl.digitaleschulwahl.view.DiagrammView;
import at.htl.digitaleschulwahl.view.ToastNotification;
import at.htl.digitaleschulwahl.view.VotingView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VotingPresenter {

    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final StudentRepository studentRepository;
    private final VotingView view;
    private final Student authenticatedStudent;
    private boolean isCouncil = false; // false: Abteilungsvertretung, true: Schülervertretung.
    private static Stage stage;


    // private List<Candidate> currentCandidates;

    private static Map<Candidate, Integer> tempVotes1 = new HashMap<>();
    private static Map<Candidate, Integer> tempVotes2 = new HashMap<>();

    private List<List<ToggleButton>> rowButtons;
    private List<Candidate> currentCandidates;

    public VotingPresenter() {
        this(null);
    }

    public VotingPresenter(Student student) {
        this.authenticatedStudent = student;
        this.view = new VotingView(this);
        this.voteRepository = new VoteRepository();
        this.candidateRepository = new CandidateRepository();
        this.studentRepository = new StudentRepository();
        init();
    }

    public void init() {
        changeSpacingForPointsHeader();
        view.getRoot().setCenter(view.createVotingUI());
        view.createUI();
        getCurrentCandidatesByType();
        updateMaxPoints();
        attachListener();
    }

    public void toggleCouncilMode() {
        isCouncil = !isCouncil;
        getCurrentCandidatesByType();
        updateVotingUI();
    }

    public void attachListener() {
        view.getSubmitButton().setOnAction(event -> {
            handleSubmitButton();
        });
        view.getContinueButton().setOnAction(event -> {
            handleContinueButton();
        });

        view.getBackButton().setOnAction(event -> {
            handleBackButton();
        });
    }

    public void updateVotingUI() {
        view.getPointsHeader().getChildren().clear();
        view.getRoot().setCenter(view.createVotingUI());
        view.setSecondHeading(getSecondHeading());
        view.setMaxPoints(updateMaxPoints());
        changeSpacingForPointsHeader();
    }

    public void updateVotingUIAndRestore(Map<Candidate, Integer> votesToRestore) {
        updateVotingUI();
        // Restore votes after UI has been fully recreated
        if (votesToRestore != null && !votesToRestore.isEmpty()) {
            restoreSelectedVotes(votesToRestore);
        }
    }

    public List<Candidate> getCurrentCandidatesByType() {
        String typeToFilter = isCouncil ? "Schülersprecher" : "Abteilungsvertreter";
        return candidateRepository.getCandidates().stream()
                .filter(candidate -> candidate.getType() != null &&
                        candidate.getType().trim().equalsIgnoreCase(typeToFilter))
                .toList();
    }

    public String getSecondHeading() {
        return this.isCouncil ? "Schülervertretung" : "Abteilungsvertretung [Abteilung]";
    }

    public int updateMaxPoints() {
        return this.isCouncil ? 6 : 2;
    }

    public void registerPointButtonHandlers(List<List<ToggleButton>> columns, List<List<ToggleButton>> rows) {
        for (int row = 0; row < rows.size(); row++) {
            for (int col = 0; col < columns.size(); col++) {
                ToggleButton btn = rows.get(row).get(col);
                final int finalRow = row;
                final int finalCol = col;

                btn.setOnAction(e -> updateButtonStates(columns, rows, finalRow, finalCol));
            }
        }
    }

    public void changeSpacingForPointsHeader() {
        if (updateMaxPoints() == 6) {
            view.getPointsHeader().setSpacing(35);
        } else {
            view.getPointsHeader().setSpacing(20);
        }
    }

    public void handleBackButton(/* Button backButton, Button continueButton, Button submitButton */) {
        VotingPresenter.tempVotes2.clear();
        VotingPresenter.tempVotes2 = getSelectedVotes();
        view.getBackButton().setVisible(false);
        view.getContinueButton().setVisible(true);
        view.getSubmitButton().setVisible(false);
        toggleCouncilMode();
        updateVotingUIAndRestore(VotingPresenter.tempVotes1);
    }

    public void handleContinueButton() {
        VotingPresenter.tempVotes1.clear();
        VotingPresenter.tempVotes1 = getSelectedVotes();

        view.getBackButton().setVisible(true);
        view.getBackButton().setManaged(true);
        view.getContinueButton().setVisible(false);
        view.getContinueButton().setManaged(false);
        view.getSubmitButton().setVisible(true);
        toggleCouncilMode();
        updateVotingUIAndRestore(VotingPresenter.tempVotes2);
    }

    public void handleSubmitButton() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Wahl bestätigen");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Sind Sie sicher, dass Sie Ihre Stimmen abgeben möchten? Nach dem Abschicken kann Ihre Wahl nicht mehr geändert werden.");

        Button okButton = (Button) confirmation.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) confirmation.getDialogPane().lookupButton(ButtonType.CANCEL);

        if (okButton != null)
            okButton.setText("Ja");
        if (cancelButton != null)
            cancelButton.setText("Nein");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            VotingPresenter.tempVotes2.clear();
            VotingPresenter.tempVotes2 = getSelectedVotes();

            Integer voterClassId = null;
            if (authenticatedStudent != null) {
                voterClassId = studentRepository.getClassId(authenticatedStudent.getClassName(), authenticatedStudent.getGrade());
                System.out.println(authenticatedStudent.getClassName());
                System.out.println(voterClassId);
            }
            if (voterClassId == null) {

                ToastNotification.show(stage, "Class id not found", "error");

            }

            for (Map.Entry<Candidate, Integer> entry : VotingPresenter.tempVotes2.entrySet()) {
                Integer candidate_id = candidateRepository.getCandidateIdByName(entry.getKey().getName());
                voteRepository.castVote(new Vote(candidate_id, entry.getValue(), voterClassId));
            }
            for (Map.Entry<Candidate, Integer> entry : VotingPresenter.tempVotes1.entrySet()) {
                Integer candidate_id = candidateRepository.getCandidateIdByName(entry.getKey().getName());
                voteRepository.castVote(new Vote(candidate_id, entry.getValue(), voterClassId));
            }

            if (authenticatedStudent != null && authenticatedStudent.getLoginCode() != null) {
                studentRepository.deleteStudentCode(authenticatedStudent.getLoginCode());
            }

            DiagrammPresenter.show(VotingPresenter.stage);
            ToastNotification.show(stage, "Wahl erfolgreich gespeichert", "success");


            /*Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Stimmabgabe");
            info.setHeaderText(null);
            info.setContentText("Ihre Stimme(n) wurden erfolgreich gespeichert.");
            info.showAndWait();
            // stage.close();
            Stage stage = (Stage) view.getSubmitButton().getScene().getWindow();
            stage.close();*/

        } else {
            confirmation.close();
        }
    }

    public void setRowButtons(List<List<ToggleButton>> rowButtons) {
        this.rowButtons = rowButtons;
        this.currentCandidates = getCurrentCandidatesByType(); // Aktuelle Kandidaten zwischenspeichern
    }

    public Map<Candidate, Integer> getSelectedVotes() {
        Map<Candidate, Integer> selectedVotes = new HashMap<>();
        if (rowButtons == null || currentCandidates == null) {
            return selectedVotes;
        }

        for (int i = 0; i < currentCandidates.size(); i++) {
            Candidate candidate = currentCandidates.get(i);
            List<ToggleButton> buttonRow = rowButtons.get(i);

            for (ToggleButton btn : buttonRow) {
                if (btn.isSelected()) {
                    Object userData = btn.getUserData();
                    if (userData instanceof Integer) {
                        selectedVotes.put(candidate, (Integer) userData);
                    }
                    break;
                }
            }
        }

        return selectedVotes;
    }

    private void restoreSelectedVotes(Map<Candidate, Integer> savedVotes) {
        if (rowButtons == null || currentCandidates == null || savedVotes == null || savedVotes.isEmpty()) {
            return;
        }

        for (int i = 0; i < currentCandidates.size(); i++) {
            Candidate candidate = currentCandidates.get(i);
            Integer savedPoint = savedVotes.get(candidate);
            if (savedPoint == null) {
                continue;
            }

            List<ToggleButton> buttonRow = rowButtons.get(i);
            for (ToggleButton btn : buttonRow) {
                if (btn.getUserData() instanceof Integer point && point.equals(savedPoint)) {
                    btn.setSelected(true);
                    break;
                }
            }
        }
    }

    public void updateButtonStates(List<List<ToggleButton>> columnButtons, List<List<ToggleButton>> rowButtons,
            int rowIndex, int newColIndex) {
        ToggleButton clicked = rowButtons.get(rowIndex).get(newColIndex);
        boolean isSelectedNow = clicked.isSelected();

        if (isSelectedNow) {
            for (int i = 0; i < rowButtons.get(rowIndex).size(); i++) {
                ToggleButton btn = rowButtons.get(rowIndex).get(i);
                if (btn != clicked && btn.isSelected()) {
                    btn.setSelected(false);
                }
            }

            if (rowIndex > 0) {
                ToggleButton prevRowButton = rowButtons.get(rowIndex - 1).get(newColIndex);
                if (prevRowButton.isSelected()) {
                    prevRowButton.setSelected(false);
                }
            }

            if (rowIndex < rowButtons.size() - 1) {
                ToggleButton nextRowButton = rowButtons.get(rowIndex + 1).get(newColIndex);
                if (nextRowButton.isSelected()) {
                    nextRowButton.setSelected(false);
                }
            }
        } else {
            boolean anySelectedInSameCol = false;
            for (int i = 0; i < rowButtons.size(); i++) {
                if (i != rowIndex && rowButtons.get(i).get(newColIndex).isSelected()) {
                    anySelectedInSameCol = true;
                    break;
                }
            }

            if (!anySelectedInSameCol) {
                for (int i = 0; i < rowButtons.size(); i++) {
                    if (i != rowIndex) {
                        rowButtons.get(i).get(newColIndex).setDisable(false);
                    }
                }
            }
        }
    }

    public void show(Stage primaryStage) {
        Scene scene = new Scene(view.getRoot(), 1100, 800);
        String css = getClass().getResource("/votingPageStyle.css").toExternalForm();
        scene.getStylesheets().add(css);
        VotingPresenter.stage = primaryStage;

        primaryStage.setTitle("Digitale Schulwahl");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public VotingView getView() {
        return view;
    }

    public void navigateToHome() {
        Stage stage = (Stage) view.getRoot().getScene().getWindow();
        MainPresenter.show(stage);
    }

}
