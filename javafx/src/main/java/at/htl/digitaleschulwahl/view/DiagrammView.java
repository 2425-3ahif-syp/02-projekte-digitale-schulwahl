package at.htl.digitaleschulwahl.view;

import at.htl.digitaleschulwahl.database.DiagrammRepository.ClassInfo;
import at.htl.digitaleschulwahl.database.DiagrammRepository.VoteCount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

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
    private final Button showOverallButton;
    private final ComboBox<ClassInfo> classComboBox;
    private final ListView<String> candidateListView;
    private final ListView<String> percentageListView;

    public DiagrammView() {
        this.baseStruct = new BaseStructureView(root);
        baseStruct.createNavBar(); // htl leonding logo
        baseStruct.showHomeButton();

        // ---- UI‐Initialisierung ----
        pieChart = new PieChart();
        pieChart.setTitle("Gesamtverteilung");
        pieChart.setStyle("-fx-background-color: #424242; -fx-text-fill: white;");
        pieChart.getStyleClass().add("chart");

        pieChart.setScaleX(1);
        pieChart.setScaleY(1);

        prevRoleButton = new Button("←");
        prevRoleButton.getStyleClass().add("button");
        nextRoleButton = new Button("→");
        nextRoleButton.getStyleClass().add("button");

        roleLabel = new Label("Schülersprecher");
        roleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox carouselBox = new HBox(10);
        carouselBox.setPadding(new Insets(10, 0, 10, 0));
        carouselBox.getChildren().addAll(prevRoleButton,roleLabel, nextRoleButton);
        carouselBox.setStyle("-fx-alignment: center;");

        classComboBox = new ComboBox<>();
        classComboBox.setPromptText("Wähle eine Klasse");
        classComboBox.setStyle(
                "-fx-background-color: #555555; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #cccccc; " +
                        "-fx-font-size: 14px;");

        showOverallButton = new Button("Gesamt");
        showOverallButton.getStyleClass().add("button");

        HBox classSelectionBox = new HBox(10, classComboBox, showOverallButton);
        classSelectionBox.setStyle("-fx-alignment: center-left;");

        candidateListView = new ListView<>();
        candidateListView.setPlaceholder(new Label("Kein Ergebnis"));
        candidateListView.setStyle(
                "-fx-control-inner-background: #424242; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px;");

        percentageListView = new ListView<>();
        percentageListView.setPlaceholder(new Label("Noch keine Daten"));
        percentageListView.setStyle(
                "-fx-control-inner-background: #333333; " +
                        "-fx-text-fill: #00ff99; " +
                        "-fx-font-size: 14px;");
        ((Label) percentageListView.getPlaceholder()).setStyle("-fx-text-fill: #888888;");

        ((Label) candidateListView.getPlaceholder()).setStyle("-fx-text-fill: #bbbbbb;");

        // Layout: PieChart links, rechts VBox mit Carousel / ComboBox / ListView
        BorderPane.setMargin(pieChart, new Insets(10));
        root.setLeft(pieChart);

        VBox rightVBox = new VBox(15);

        rightVBox.setPrefWidth(400);
        rightVBox.setMinWidth(400);
        rightVBox.setMaxWidth(400);

        rightVBox.setPadding(new Insets(10));
        rightVBox.getChildren().addAll(carouselBox, classSelectionBox, candidateListView);
        rightVBox.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(candidateListView, Priority.ALWAYS);
        root.setRight(rightVBox);
    }

    public BorderPane getRoot() {
        return root;
    }

    public BaseStructureView getBaseStruct() {
        return baseStruct;
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

    public void setClassList(List<ClassInfo> classes) {
        ObservableList<ClassInfo> obs = FXCollections.observableArrayList(classes);
        classComboBox.setItems(obs);
        if (!obs.isEmpty()) {
            classComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Aktualisiert nur den Text “Rolle” (z.B. “Schülersprecher” oder
     * “Abteilungsvertreter”)
     * im Label im Carousel.
     */
    public void setRoleLabel(String roleText) {
        roleLabel.setText(roleText);
    }

    public void updateChart(String title, List<VoteCount> data) {
        pieChart.setTitle(title);

        double total = data.stream().mapToDouble(VoteCount::getCount).sum();

        ObservableList<PieChart.Data> slices = FXCollections.observableArrayList();
        for (VoteCount vc : data) {
            double pct = total > 0 ? vc.getCount() / total * 100 : 0;

            PieChart.Data slice = new PieChart.Data(vc.getCandidateName(), vc.getCount());
            slices.add(slice);
        }
        pieChart.setData(slices);

    }

    public void updateCandidateList(List<VoteCount> data) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (VoteCount vc : data) {
            String row = vc.getCandidateName() + " – " + vc.getCount() + " Punkte";
            items.add(row);
        }
        candidateListView.setItems(items);
    }

    public ClassInfo getSelectedClass() {
        return classComboBox.getSelectionModel().getSelectedItem();
    }

    public void addShowOverallListener(EventHandler<ActionEvent> listener) {
        showOverallButton.setOnAction(listener);
    }

    public void updateCandidateListWithPercentages(List<String> percentageRows) {
        ObservableList<String> items = FXCollections.observableArrayList(percentageRows);
        candidateListView.setItems(items);
    }

}
