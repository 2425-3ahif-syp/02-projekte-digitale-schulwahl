package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.database.StudentRepository;
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


public class PdfPresenter {
    private final StudentRepository studentRepository;
    private final Random random = new Random();
    private final Connection connection;


    public PdfPresenter() {
        studentRepository = new StudentRepository();
        connection = DatabaseManager.getInstance().getConnection();
    }

    public String generateRandomCode() {
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

        pdfContent.append(studentRepository.getClass(classId)).append("\n\n");

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

            String filePath = PDF_DIRECTORY + File.separator + studentRepository.getClass(classId) + "_codes.pdf";
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



}



