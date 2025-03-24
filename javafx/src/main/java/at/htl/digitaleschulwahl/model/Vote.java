package at.htl.digitaleschulwahl.model;
import at.htl.digitaleschulwahl.model.Candidate;

public class Vote {
    private Candidate candidate;
    private int ranking;

    public Vote(Candidate candidate, int ranking) {
        if (!isValidRanking(candidate.getType(), ranking)) {
            throw new IllegalArgumentException("Ungültiges Ranking für " + candidate.getType());
        }
        this.candidate = candidate;
        this.ranking = ranking;
    }

    private boolean isValidRanking(String candidateType, int ranking) {
        return switch (candidateType) {
            case "Schülervertreter" -> ranking >= 1 && ranking <= 6;
            case "Abteilungsvertreter" -> ranking == 1 || ranking == 2;
            default -> false;
        };
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public int getRanking() {
        return ranking;
    }
}
