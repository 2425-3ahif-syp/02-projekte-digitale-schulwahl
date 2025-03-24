package at.htl.digitaleschulwahl.model;

public class Candidate {
    private String name;
    private String type; // "Schülervertreter" oder "Abteilungsvertreter"

    public Candidate(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
