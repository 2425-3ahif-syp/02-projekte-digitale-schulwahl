package at.htl.digitaleschulwahl.model;

public class Candidate {
    private String name;
    private String type; // "Schülervertreter" oder "Abteilungsvertreter"
    private String className;

    public Candidate(String name, String className, String type) {
        this.name = name;
        this.type = type;
        this.className = className;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String _class) {
        this.className = _class;
    }
}
