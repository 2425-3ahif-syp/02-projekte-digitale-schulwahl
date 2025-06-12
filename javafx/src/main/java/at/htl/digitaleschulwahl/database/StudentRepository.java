package at.htl.digitaleschulwahl.database;

import at.htl.digitaleschulwahl.model.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {
    private final Connection connection;

    public StudentRepository() {
        connection = DatabaseManager.getInstance().getConnection();

    }

    public List<Student> getAllStudents() {

        var students = new ArrayList<Student>();
        var query = """
                    SELECT
                        student.id,
                        first_name,
                        last_name,
                        login_code,
                        class_name,
                        EXTRACT(YEAR FROM CURRENT_DATE) - start_year + (EXTRACT(MONTH FROM CURRENT_DATE)::int >= 9)::int AS grade
                    FROM student
                    JOIN class ON student.class_id = class.id;
                """;

        try (
                var preparedStatement = connection.prepareStatement(query);
                var resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                students.add(new Student(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("class_name"),
                        resultSet.getString("login_code"),
                        resultSet.getInt("grade")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public void saveCodesForAllStudents(java.util.function.Supplier<String> codeGenerator) {
        try {
            var query = "UPDATE student SET login_code = ? WHERE id = ?";

            try (var statement = connection.prepareStatement(query)) {
                for (var student : getAllStudents()) {
                    String uniqueCode = codeGenerator.get();
                    statement.setString(1, uniqueCode);
                    statement.setInt(2, student.getId());
                    statement.addBatch();
                    student.setLoginCode(uniqueCode);
                }

                statement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getClass(Integer classId) {
        var query = """
                SELECT
                    (EXTRACT(YEAR FROM CURRENT_DATE) - start_year +
                    (EXTRACT(MONTH FROM CURRENT_DATE)::int >= 9)::int) || '' || class_name AS grade
                FROM class
                WHERE id = ?;
                """;

        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, classId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("grade");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Datenbankfehler: " + e.getMessage(), e);
        }

        return null;
    }

    public Integer getClassId(String className) {
        var query = """
                SELECT id
                FROM class
                WHERE ((EXTRACT(YEAR FROM CURRENT_DATE) - start_year + (EXTRACT(MONTH FROM CURRENT_DATE)::int >= 9)::int) || '' || class_name) = ?;
                """;

        try (var statement = connection.prepareStatement(query)) {
            statement.setString(1, className);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    return null; // oder -1, oder Fehler werfen, falls keine Klasse gefunden
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Datenbankfehler: " + e.getMessage(), e);
        }
    }

    public String[] getAllClasses() {
        String query = """
                SELECT distinct
                    (EXTRACT(YEAR FROM CURRENT_DATE) - start_year +
                    (EXTRACT(MONTH FROM CURRENT_DATE)::int >= 9)::int) || '' || class_name AS full_class
                FROM class
                ORDER BY full_class;
                """;

        List<String> classList = new ArrayList<>();

        try (var statement = connection.prepareStatement(query);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                classList.add(resultSet.getString("full_class"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen der Klassen: " + e.getMessage(), e);
        }

        return classList.toArray(new String[0]);
    }

    public boolean verifyStudentCode(String code) {
        String query = """
                SELECT COUNT(*) as count
                FROM student
                WHERE login_code = ?;
                """;

        try (var statement = connection.prepareStatement(query)) {
            statement.setString(1, code);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Überprüfen des Codes: " + e.getMessage(), e);
        }

        return false;
    }

    public Student getStudentByCode(String code) {
        String query = """
                SELECT
                    student.id,
                    first_name,
                    last_name,
                    login_code,
                    class_name,
                    EXTRACT(YEAR FROM CURRENT_DATE) - start_year + (EXTRACT(MONTH FROM CURRENT_DATE)::int >= 9)::int AS grade
                FROM student
                JOIN class ON student.class_id = class.id
                WHERE login_code = ?;
                """;

        try (var statement = connection.prepareStatement(query)) {
            statement.setString(1, code);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Student(
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("class_name"),
                            resultSet.getString("login_code"),
                            resultSet.getInt("grade"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen des Students: " + e.getMessage(), e);
        }

        return null;
    }

    public void deleteStudentCode(String code) {
        String query = """
                UPDATE student
                SET login_code = NULL
                WHERE login_code = ?;
                """;

        try (var statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Codes: " + e.getMessage(), e);
        }
    }

    public ArrayList<Student> getStudentsByClass(Integer classId) {
        String query = """
                SELECT s.id, first_name, last_name, login_code, (EXTRACT(YEAR FROM CURRENT_DATE) - start_year +
                (EXTRACT(MONTH FROM CURRENT_DATE)::int >= 9)::int) as grade, class_name
                FROM STUDENT s
                JOIN CLASS c ON s.class_id = c.id
                WHERE s.class_id = ?;
                """;
        ArrayList<Student> students = new ArrayList<>();

        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, classId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Integer id = resultSet.getInt("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String loginCode = resultSet.getString("login_code");
                    Integer grade = resultSet.getInt("grade");
                    String className = resultSet.getString("class_name");
                    students.add(new Student(id, firstName, lastName, className, loginCode, grade));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen des Students: " + e.getMessage(), e);
        }

        return students;
    }
}
