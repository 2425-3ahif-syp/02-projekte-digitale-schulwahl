package at.htl.digitaleschulwahl.model;
import at.htl.digitaleschulwahl.model.Candidate;

public class Vote {
    private int candidate_id;
    private int ranking;

    public Vote(int candidate_id, int ranking) {
     /*   if (!isValidRanking(candidate.getType(), ranking)) {
            throw new IllegalArgumentException("Ungültiges Ranking für " + candidate.getType());
        }
        this.candidate = candidate;*/
        this.candidate_id= candidate_id;
        this.ranking = ranking;
    }

  /*  private boolean isValidRanking(String candidateType, int ranking) {
        return switch (candidateType) {
            case "Schülervertreter" -> ranking >= 1 && ranking <= 6;
            case "Abteilungsvertreter" -> ranking == 1 || ranking == 2;
            default -> false;
        };
    }*/

    public int getCandidate() {
        return candidate_id;
    }

    public int getRanking() {
        return ranking;
    }
}
