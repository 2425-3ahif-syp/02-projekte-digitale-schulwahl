package at.htl.digitaleschulwahl.controller;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.model.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainController {
    private final Connection connection;
    private final Random random = new Random();

    public MainController() {
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
                    var code = generateRandomCode();
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

    private String generateRandomCode() {
        var characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        var codeLength = 8;

        return random.ints(codeLength, 0, characters.length())
                .mapToObj(characters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
