package at.htl.digitaleschulwahl.presenter;

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

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;


public class PdfPresenter {
    private final StudentRepository studentRepository;
    private final Random random = new Random();


    public PdfPresenter() {
        studentRepository = new StudentRepository();
    }

    public void generateAndSaveCodesForAllStudents(){
        String code = generateRandomCode();
        studentRepository.saveCodesForAllStudents(code);
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
        ArrayList<Student> students = studentRepository.getStudentsByClass(classId);

        StringBuilder pdfContent = new StringBuilder();

        pdfContent.append(studentRepository.getClass(classId)).append("\n\n");


        for(Student student : students) {
            pdfContent.append(student.getFirstName()).append(" ").append(student.getLastName())
                    .append(" - Login Code: ").append(student.getLoginCode()).append("\n");
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



