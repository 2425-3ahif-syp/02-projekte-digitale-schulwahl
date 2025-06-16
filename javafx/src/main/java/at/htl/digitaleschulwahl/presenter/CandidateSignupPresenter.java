package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.CandidateRepository;
import at.htl.digitaleschulwahl.view.CandidateSignupView;
import at.htl.digitaleschulwahl.view.ToastNotification;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CandidateSignupPresenter {
    private final CandidateSignupView view;
    private final CandidateRepository candidateRepository;
    private static Stage stage;

    public CandidateSignupPresenter() {
        this.candidateRepository = new CandidateRepository();
        this.view = new CandidateSignupView();
        this.view.setEventHandlers(
                this::handleSignup,
                this::navigateToHome,
                this::navigateToHome);
    }

    public void handleSignup() {
        String name = view.getName();
        String className = view.getClassName();
        String role = view.getSelectedRole();

        if (name.isEmpty()) {
            view.showError("Bitte geben Sie Ihren vollständigen Namen ein.");
            return;
        }

        if (className.isEmpty()) {
            view.showError("Bitte geben Sie Ihre Klasse ein.");
            return;
        }

        if (role == null) {
            view.showError("Bitte wählen Sie eine Position aus.");
            return;
        }

        if (candidateRepository.candidateExists(name, role)) {
            view.showError("Sie kandidieren bereits für diese Position.");
            return;
        }

        if (!isValidClassFormat(className)) {
            view.showError("Bitte geben Sie eine gültige Klasse ein (z.B. 5AHIF).");
            return;
        }

        boolean success = candidateRepository.addCandidate(name, className.toUpperCase(), role);

        if (success) {
            view.showSuccess("Ihre Kandidatur wurde erfolgreich angemeldet!");
            view.clearForm();
        } else {
            view.showError("Fehler beim Anmelden der Kandidatur. Bitte versuchen Sie es erneut.");
        }
    }

    private boolean isValidClassFormat(String className) {
        return className.matches("\\d[A-Z]{4}");
    }

    public void navigateToHome() {
        MainPresenter.show(stage);
    }

    public void show(Stage primaryStage) {
        CandidateSignupPresenter.stage = primaryStage;
        view.setStage(primaryStage);

        Scene scene = new Scene(view.getRoot(), 1100, 800);

        try {
            var mainPageCss = getClass().getResource("/mainPageStyle.css");
            var votingPageCss = getClass().getResource("/votingPageStyle.css");
            var toastCss = getClass().getResource("/toastNotification.css");

            if (mainPageCss != null) {
                scene.getStylesheets().add(mainPageCss.toExternalForm());
            }
            if (votingPageCss != null) {
                scene.getStylesheets().add(votingPageCss.toExternalForm());
            }
            if (toastCss != null) {
                scene.getStylesheets().add(toastCss.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS files: " + e.getMessage());
        }

        primaryStage.setTitle("Digitale Schulwahl - Kandidatur anmelden");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
