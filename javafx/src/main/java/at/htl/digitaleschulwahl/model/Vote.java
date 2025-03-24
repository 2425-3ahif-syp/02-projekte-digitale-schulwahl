package at.htl.digitaleschulwahl.model;

public class Vote {
    private String voterId;  // ID (auto_increment, damit sie später in eine tabelle oder so gepeichert werden kann...irgendwie so...muss noch ünerlegt werden)
    private Candidate selectedCandidate;  // Der gewählte Kandidat
    private int points;  // Punkte, die dem Kandidaten gegeben wurden

    // Konstruktor
    public Vote(String voterId, Candidate selectedCandidate, int points) {
        this.voterId = voterId;
        this.selectedCandidate = selectedCandidate;
        this.points = points;
    }

    // Getter und Setter
    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public Candidate getSelectedCandidate() {
        return selectedCandidate;
    }

    public void setSelectedCandidate(Candidate selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }


    @Override
    public String toString() {

        // jaaa string format oder so...
        return "Vote{" +
                "voterId='" + voterId + '\'' +
                ", selectedCandidate=" + selectedCandidate +
                ", points=" + points +
                '}';
    }
}

