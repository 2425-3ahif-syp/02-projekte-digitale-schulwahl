package at.htl.digitaleschulwahl.model;

public class Student {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String className;
    private String loginCode;
    private final int grade;

    public Student(int id, String firstName, String lastName, String className, String loginCode, int grade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.className = className;
        this.loginCode = loginCode;
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getClassName() {
        return className;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public int getGrade() {return grade;}

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}
