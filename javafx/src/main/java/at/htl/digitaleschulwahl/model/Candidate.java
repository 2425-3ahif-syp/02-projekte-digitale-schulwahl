package at.htl.digitaleschulwahl.model;

public class Candidate {

    private String name;  // Name des Kandidaten
    private String position;  // Position (Schülervertretung oder Abteilungssprecher)
    private int points;  // Punkteanzahl des Kandidaten

    // Konstruktor
    public Candidate(String name, String position) {
        this.name = name;
        this.position = position;
        this.points = 0;  // Initialpunkte setzen (könnte auch aus einer Datei kommen)
    }

    // Getter und Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;  // Punkte hinzufügen
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", points=" + points +
                '}';
    }

}
