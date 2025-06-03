package at.htl.digitaleschulwahl.database;

import at.htl.digitaleschulwahl.model.Student;
import at.htl.digitaleschulwahl.presenter.PdfPresenter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentRepository {
    private final Connection connection;
    private final Random random = new Random();
    private final PdfPresenter pdfPresenter = new PdfPresenter();

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
                var resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                students.add(new Student(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("class_name"),
                        resultSet.getString("login_code"),
                        resultSet.getInt("grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public void generateAndSaveCodesForAllStudents() {
        try {
            var query = "UPDATE student SET login_code = ? WHERE id = ?";

            try (var statement = connection.prepareStatement(query)) {
                for (var student : getAllStudents()) {
                    var code = pdfPresenter.generateRandomCode();
                    statement.setString(1, code);
                    statement.setInt(2, student.getId());
                    statement.addBatch();
                    student.setLoginCode(code);
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
}
