package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.database.DiagrammRepository;
import at.htl.digitaleschulwahl.database.DiagrammRepository.ClassInfo;
import at.htl.digitaleschulwahl.database.DiagrammRepository.VoteCount;
import at.htl.digitaleschulwahl.view.DiagrammView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DiagrammPresenter {
    private final DiagrammView view;
    private final DiagrammRepository repository;
    private static Stage stage;

    private final String[] roles = { "Schülersprecher", "Abteilungsvertreter" };
    private int currentRoleIndex = 0; // 0 = Schülersprecher, 1 = Abteilungsvertreter
    private boolean showingOverall = false;

    public DiagrammPresenter(DiagrammRepository repository, DiagrammView view) {
        this.repository = repository;
        this.view = view;

        loadAllClasses();

        // Initiales Rollen‐Label setzen
        view.setRoleLabel(roles[currentRoleIndex]);

        // Eventhandler
        view.addPrevRoleListener(this::onPrevRoleClicked);
        view.addNextRoleListener(this::onNextRoleClicked);
        view.addClassSelectionListener(this::onClassSelectionChanged);
        view.addShowOverallListener(this::onShowOverallClicked);

        view.getBaseStruct().setHomeButtonAction(e -> navigateToHome());

        refreshChart();
        refreshCandidateList();

        // Timeline, die alle 15 Sekunden Chart und Liste aktualisiert
        var autoRefresh = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> {
                    // Klassen neu laden
                    loadAllClasses();

                    // Immer Gesamt-Chart und Klassen‑Liste neu laden
                    refreshChart();
                    refreshCandidateList();
                })
        );

        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    public static void show(Stage stage) {
        DiagrammRepository repo = new DiagrammRepository(DatabaseManager.getInstance().getConnection());
        DiagrammView view = new DiagrammView();

        String css = DiagrammPresenter.class.getResource("/votingPageStyle.css").toExternalForm();

        new DiagrammPresenter(repo, view);

        Scene scene = new Scene(view.getRoot(), 1000, 600);
        scene.getStylesheets().add(css);

        stage.setTitle("Digitale Schulwahl – Auswertung");
        stage.setScene(scene);
        stage.show();
        DiagrammPresenter.stage = stage;

    }

    /**
     * Holt alle Klassen aus dem Repository und füllt die ComboBox in der View.
     */
    private void loadAllClasses() {
        try {
            List<ClassInfo> classes = repository.getAllClasses();
            view.setClassList(classes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onPrevRoleClicked(ActionEvent event) {
        currentRoleIndex = (currentRoleIndex + roles.length - 1) % roles.length;
        view.setRoleLabel(roles[currentRoleIndex]);
        refreshChart();
        refreshCandidateList();
    }

    private void onNextRoleClicked(ActionEvent event) {
        currentRoleIndex = (currentRoleIndex + 1) % roles.length;
        view.setRoleLabel(roles[currentRoleIndex]);
        refreshChart();
        refreshCandidateList();
    }

    private void onClassSelectionChanged(ObservableValue<? extends ClassInfo> obs,
            ClassInfo oldClass, ClassInfo newClass) {
        refreshCandidateList();
    }

    /**
     * Holt aus dem Repository die VoteCounts aller Klassen
     * für die gewählte Rolle, und View wird dann halt neu geladen
     */
    private void refreshChart() {
        String currentRole = roles[currentRoleIndex];
        try {
            List<VoteCount> allCounts = repository.getVoteCountsByRole(currentRole);
            String title = currentRole + " – Gesamt";
            view.updateChart(title, allCounts);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCandidateList() {
        ClassInfo selectedClass = view.getSelectedClass();
        if (selectedClass == null) {
            view.updateCandidateList(Collections.emptyList());
            return;
        }

        String currentRole = roles[currentRoleIndex];
        int classId = selectedClass.getId();

        System.out.println("Selected class: "+classId);

        try {
            List<VoteCount> classCounts = repository.getVoteCountsByRoleAndClass(currentRole, classId);
            view.updateCandidateList(classCounts);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private void onShowOverallClicked(ActionEvent event) {
        String currentRole = roles[currentRoleIndex];
        try {
            List<VoteCount> allCounts = repository.getVoteCountsByRole(currentRole);
            double total = allCounts.stream().mapToDouble(VoteCount::getCount).sum();

            List<String> overallList = allCounts.stream()
                    .map(vc -> {
                        double pct = total > 0 ? vc.getCount() / total * 100 : 0;
                        return vc.getCandidateName() + ": " + String.format("%.1f", pct) + " %";
                    })
                    .toList();

            view.updateCandidateListWithPercentages(overallList); // ← NEU
            showingOverall = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void navigateToHome() {
        MainPresenter.show(stage);
    }

}
