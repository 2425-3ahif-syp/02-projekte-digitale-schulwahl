package at.htl.digitaleschulwahl.controller;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.model.Student;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
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

    public void getCodesIntoPDF(Integer classId) {
        String query = """
                SELECT first_name, last_name, login_code
                FROM STUDENT
                WHERE class_id = ?;
                """;
        StringBuilder pdfContent = new StringBuilder();

        pdfContent.append(getClass(classId)).append("\n\n");

        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, classId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String loginCode = resultSet.getString("login_code");
                    pdfContent.append(firstName).append(" ").append(lastName)
                            .append(" - Login Code: ").append(loginCode).append("\n");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Datenbankfehler: " + e.getMessage(), e);
        }

        savePDF(classId, pdfContent.toString());
    }

    private void savePDF(Integer classId, String content) {
        String PDF_DIRECTORY = "generated_pdfs";

        try {
            Path pdfDir = Paths.get(PDF_DIRECTORY);
            if (!Files.exists(pdfDir)) {
                Files.createDirectories(pdfDir);
            }

            String filePath = PDF_DIRECTORY + File.separator + getClass(classId) + "_codes.pdf";
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);
                    for (String line : content.split("\n")) {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -20);
                    }
                    contentStream.endText();
                }
                document.save(filePath);
                System.out.println("PDF gespeichert unter: " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern der PDF: " + e.getMessage(), e);
        }
    }

    private String getClass(Integer classId) {
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
}



