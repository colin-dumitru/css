package edu.css.java;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import edu.css.model.Exam;
import edu.css.model.Student;
import edu.css.operations.StudentManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZIG
 * Date: 23.04.2013
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */
public class ReportGenerator {

//    public static List<Student> staticList = new ArrayList<Student> (){{
//            add(new Student("Jora","Kardan",10d,10d,10d));
//            add(new Student("Jora","Kardan",10d,10d,10d));
//            add(new Student("Jora","Kardan",10d,10d,10d));
//            add(new Student("Jora","Kardan",10d,10d,10d));
//            add(new Student("Jora","Kardan",10d,10d,10d));
//            add(new Student("Jora","Kardan",10d,10d,10d));
//            add(new Student("Jora","Kardan",10d,10d,10d));
//    }};

    private List<Student> studentList;
    private String outputFileName;
    private final int Const = 50;

    public ReportGenerator(List<Student> studentList, String outputFileName) {
        this.studentList = studentList;
        this.outputFileName = getOutputFileName(outputFileName);
    }

    private String getOutputFileName(String outputFileName){
        if(outputFileName.endsWith(".pdf")){
            return outputFileName;
        }
        return outputFileName+".pdf";
    }

    public void generate() {
        try {
        Document document = new Document(PageSize.A4,Const,Const,Const,Const);
        PdfWriter.getInstance(document, new FileOutputStream(outputFileName,false));
        document.open();

        Paragraph p  = new Paragraph("Admission report");
        document.add(p);

        PdfPTable table = createTableHeader();
        addTableData(table);

        document.add(table);
        document.close();
        }
        catch (DocumentException e){
            e.printStackTrace();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private PdfPTable createTableHeader(){
        PdfPTable table = new PdfPTable(6);
        table.setSpacingBefore(15);
        table.setSpacingAfter(15);
        table.setLockedWidth(false);
        table.setWidthPercentage(100f);

//        PdfPCell c1 = new PdfPCell(new Phrase("Name"));
//        table.addCell(c1);
//
//        PdfPCell c2 = new PdfPCell(new Phrase("Forename"));
//        table.addCell(c2);
//
//        PdfPCell c3 = new PdfPCell(new Phrase("Bac average"));
//        table.addCell(c3);
//
//        PdfPCell c4 = new PdfPCell(new Phrase("Liceum average"));
//        table.addCell(c4);
//
//        PdfPCell c5 = new PdfPCell(new Phrase("Exam average"));
//        table.addCell(c5);
//
//        PdfPCell c6 = new PdfPCell(new Phrase("Admission average"));
//        table.addCell(c6);
//
//        PdfPCell c7 = new PdfPCell(new Phrase("Admission result"));
//        table.addCell(c7);

        PdfPCell c1 = new PdfPCell(new Phrase("Id"));
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("Name"));
        table.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase("BacAverage"));
        table.addCell(c3);

        PdfPCell c4 = new PdfPCell(new Phrase("ExamMark"));
        table.addCell(c4);

        PdfPCell c5 = new PdfPCell(new Phrase("Average"));
        table.addCell(c5);

        PdfPCell c6 = new PdfPCell(new Phrase("Passed"));
        table.addCell(c6);


        return table;
    }

    private void addTableData(PdfPTable table){
        for (Student student : studentList) {
            Exam exam = StudentManager.getExamForStudent(student);
            table.addCell(getCellValue(student.getId()));
            table.addCell(getCellValue(student.getName()));
            table.addCell(getCellValue(student.getAverage()));
            table.addCell(getCellValue(exam.getMark()));
            table.addCell(getCellValue(StudentManager.getPassingMark(student, exam)));
            table.addCell(getCellValue(student.getPassed()));

//            table.addCell(getCellValue(student.getNume()));
//            table.addCell(getCellValue(student.getPrenume()));
//            table.addCell(getCellValue(student.getMedieBac()));
//            table.addCell(getCellValue(student.getMedieLiceu()));
//            table.addCell(getCellValue(student.getMedieExamen()));
//            table.addCell(getCellValue(student.getMedieAdmitere()));
//            table.addCell(getCellValue(student.getStudentType()));
        }
    }

    private String getCellValue(Object value){
        if(value == null){
            return "undefined";
        }
        return value.toString();
    }

    public String getOutputFileName() {
        return outputFileName;
    }
}
