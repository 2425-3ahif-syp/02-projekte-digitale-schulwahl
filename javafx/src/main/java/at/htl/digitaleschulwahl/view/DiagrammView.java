package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.database.DiagrammRepository.ClassInfo;
import at.htl.digitaleschulwahl.database.DiagrammRepository.VoteCount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.List;

/**
 * DiagrammView: zeigt das Tortendiagramm (links) und rechts
 * Dropdown + Kandidatenliste. Die View kennt kein Repository,
 * sie bietet nur Methoden, um Daten hereinzuschreiben und Listener
 * für Button‐/ComboBox‐Events zu registrieren.
 */
public class DiagrammView {
    private final BorderPane root = new BorderPane();
    private final BaseStructureView baseStruct;

    // UI‐Elemente (gleiche wie zuvor):
    private final PieChart pieChart;
    private final Button prevRoleButton;
    private final Button nextRoleButton;
    private final Label roleLabel;
    private final ComboBox<ClassInfo> classComboBox;
    private final ListView<String> candidateListView;

    public DiagrammView() {
        this.baseStruct = new BaseStructureView(root);
        baseStruct.createNavBar(); // obere Navigationsleiste / Überschrift

        // ---- UI‐Initialisierung ----
        pieChart = new PieChart();
        pieChart.setTitle("Gesamtverteilung");
        // Hinweis: Hier könnte noch „– bitte Rolle wählen“ stehen, bis der Presenter initialisiert

        prevRoleButton = new Button("←");
        nextRoleButton = new Button("→");
        roleLabel = new Label("Schülersprecher");
        roleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox carouselBox = new HBox(5);
        carouselBox.getChildren().addAll(prevRoleButton, roleLabel, nextRoleButton);

        classComboBox = new ComboBox<>();
        classComboBox.setPromptText("Wähle eine Klasse");

        candidateListView = new ListView<>();
        candidateListView.setPlaceholder(new Label("Kein Ergebnis"));

        // Layout: PieChart links, rechts VBox mit Carousel / ComboBox / ListView
        BorderPane.setMargin(pieChart, new Insets(10));
        root.setLeft(pieChart);

        VBox rightVBox = new VBox(10);
        rightVBox.setPadding(new Insets(10));
        rightVBox.getChildren().addAll(carouselBox, classComboBox, candidateListView);
        VBox.setVgrow(candidateListView, Priority.ALWAYS);
        root.setRight(rightVBox);
    }

    public BorderPane getRoot() {
        return root;
    }

    // =========================================
    // 1) Listener‐Registrierung durch Presenter
    // =========================================

    public void addPrevRoleListener(EventHandler<ActionEvent> listener) {
        prevRoleButton.setOnAction(listener);
    }

    public void addNextRoleListener(EventHandler<ActionEvent> listener) {
        nextRoleButton.setOnAction(listener);
    }

    public void addClassSelectionListener(ChangeListener<ClassInfo> listener) {
        classComboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    // =========================================
    // 2) Öffentliche Methods, um Daten zu setzen
    // =========================================

    /**
     * Der Presenter ruft das auf, um der ComboBox alle Klassen zur Verfügung zu stellen.
     */
    public void setClassList(List<ClassInfo> classes) {
        ObservableList<ClassInfo> obs = FXCollections.observableArrayList(classes);
        classComboBox.setItems(obs);
        if (!obs.isEmpty()) {
            classComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Aktualisiert nur den Text “Rolle” (z.B. “Schülersprecher” oder “Abteilungsvertreter”)
     * im Label im Carousel.
     */
    public void setRoleLabel(String roleText) {
        roleLabel.setText(roleText);
    }

    /**
     * Aktualisiert nur das Tortendiagramm (PieChart),
     * basierend auf den insgesamt über alle Klassen aggregierten Daten (data).
     * Der title ist z.B. “Schülersprecher – Gesamt”.
     */
    public void updateChart(String title, List<VoteCount> data) {
        pieChart.setTitle(title);

        ObservableList<PieChart.Data> slices = FXCollections.observableArrayList();
        for (VoteCount vc : data) {
            PieChart.Data slice = new PieChart.Data(vc.getCandidateName(), vc.getCount());
            slices.add(slice);
        }
        pieChart.setData(slices);
    }

    /**
     * Aktualisiert nur die rechte ListView (Kandidaten + Punkte) für die aktuell
     * ausgewählte Klasse UND Rolle. data sind VoteCounts, die vorher
     * à la getVoteCountsByRoleAndClass(...) abgefragt wurden.
     */
    public void updateCandidateList(List<VoteCount> data) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (VoteCount vc : data) {
            String row = vc.getCandidateName() + " – " + vc.getCount() + " Punkte";
            items.add(row);
        }
        candidateListView.setItems(items);
    }

    /**
     * Hilfsmethode für den Presenter, um abzufragen, welche Klasse gerade ausgewählt ist.
     */
    public ClassInfo getSelectedClass() {
        return classComboBox.getSelectionModel().getSelectedItem();
    }
}
