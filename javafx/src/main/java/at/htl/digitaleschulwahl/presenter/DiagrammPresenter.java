package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.DiagrammRepository;
import at.htl.digitaleschulwahl.database.DiagrammRepository.ClassInfo;
import at.htl.digitaleschulwahl.database.DiagrammRepository.VoteCount;
import at.htl.digitaleschulwahl.view.DiagrammView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Presenter – kümmert sich um alle Datenbankaufrufe und Geschäftslogik.
 * Bindet das Repository an die View, ohne dass die View selbst SQL‐Zugriffe kennt.
 */
public class DiagrammPresenter {
    private final DiagrammView view;
    private final DiagrammRepository repository;

    // Wir behalten zwei Rollen in einem Array, um zyklisch zwischen ihnen umzuschalten.
    private final String[] roles = { "Schülersprecher", "Abteilungsvertreter" };
    private int currentRoleIndex = 0; // 0 = Schülersprecher, 1 = Abteilungsvertreter
    private boolean showingOverall = false;


    public DiagrammPresenter(DiagrammRepository repository, DiagrammView view) {
        this.repository = repository;
        this.view = view;

        // 1) Klassenliste laden und in die View schreiben:
        loadAllClasses();

        // 2) Initiales Rollen‐Label setzen
        view.setRoleLabel(roles[currentRoleIndex]);

        // 3) Event‐Handler registrieren:
        view.addPrevRoleListener(this::onPrevRoleClicked);
        view.addNextRoleListener(this::onNextRoleClicked);
        view.addClassSelectionListener(this::onClassSelectionChanged);
        view.addShowOverallListener(this::onShowOverallClicked);



        // 4) Erstmaliges Zeichnen: globales Diagramm + Kandidatenliste für erste Klasse
        refreshChart();
        refreshCandidateList();
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
            // Optional: zeige einen Alert oder Log‐Eintrag, falls das Laden der Klassen fehlschlägt.
        }
    }

    /**
     * Wird aufgerufen, wenn der Benutzer auf “←” klickt:
     * Rollen‐Index dekrementieren, Label aktualisieren,
     * dann sowohl Chart als auch Kandidatenliste erneuern.
     */
    private void onPrevRoleClicked(ActionEvent event) {
        currentRoleIndex = (currentRoleIndex + roles.length - 1) % roles.length;
        view.setRoleLabel(roles[currentRoleIndex]);
        refreshChart();
        refreshCandidateList();
    }

    /**
     * Wird aufgerufen, wenn der Benutzer auf “→” klickt:
     * Rollen‐Index inkrementieren, Label aktualisieren,
     * dann sowohl Chart als auch Kandidatenliste erneuern.
     */
    private void onNextRoleClicked(ActionEvent event) {
        currentRoleIndex = (currentRoleIndex + 1) % roles.length;
        view.setRoleLabel(roles[currentRoleIndex]);
        refreshChart();
        refreshCandidateList();
    }

    /**
     * Wird aufgerufen, wenn der Benutzer in der ComboBox die Klasse wechselt.
     * Hier aktualisieren wir **nur** die rechte Kandidatenliste,
     * weil das Tortendiagramm global alle Klassen anzeigt.
     */
    private void onClassSelectionChanged(ObservableValue<? extends ClassInfo> obs,
                                         ClassInfo oldClass, ClassInfo newClass) {
        refreshCandidateList();
    }

    /**
     * Holt aus dem Repository die VoteCounts über alle Klassen hinweg
     * für die aktuell gewählte Rolle, und sagt der View, dass sie das PieChart
     * mit diesen Werten neu zeichnen soll.
     */
    private void refreshChart() {
        String currentRole = roles[currentRoleIndex];
        try {
            // Hier die neue Methode: summiert über alle Klassen
            List<VoteCount> allCounts = repository.getVoteCountsByRole(currentRole);
            String title = currentRole + " – Gesamt";
            view.updateChart(title, allCounts);
        } catch (SQLException e) {
            e.printStackTrace();
            // Optional: hier einen Fehler in der View anzeigen lassen
        }
    }

    /**
     * Holt die aktuell ausgewählte Klasse aus der View, und ruft
     * getVoteCountsByRoleAndClass(...) auf, damit die View rechts
     * die Kandidatenliste entsprechend aktualisieren kann.
     * Falls noch keine Klasse ausgewählt ist, setzen wir eine leere Liste.
     */
    private void refreshCandidateList() {
        ClassInfo selectedClass = view.getSelectedClass();
        if (selectedClass == null) {
            view.updateCandidateList(Collections.emptyList());
            return;
        }

        String currentRole = roles[currentRoleIndex];
        int classId = selectedClass.getId();

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


}
