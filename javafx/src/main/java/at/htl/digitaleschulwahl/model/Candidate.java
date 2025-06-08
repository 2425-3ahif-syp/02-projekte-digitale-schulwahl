package at.htl.digitaleschulwahl.model;

public class Candidate {
    private int id;
    private String name;
    private String type; // "Schülervertreter" oder "Abteilungsvertreter"
    private String className;

    public Candidate(int id, String name, String className, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.className = className;
    }

    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Candidate candidate = (Candidate) obj;
        return id == candidate.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
