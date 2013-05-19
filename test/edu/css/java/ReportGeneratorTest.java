package edu.css.java;

import edu.css.model.Exam;
import edu.css.model.Student;
import edu.css.operations.ExamDAO;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */
public class ReportGeneratorTest {

    @Test
    public void generateReportTest()
    {
        //random file in tmp directory
        String outputPath = new File(System.getProperty("java.io.tmpdir"), "report_" + System.currentTimeMillis() + ".pdf").toString() ;

        Student student1 = new Student("Dinu", 8.3);
        Student student2 = new Student("Andrew", 7.6);
        Exam exam1 = new Exam(6.5, 1);
        Exam exam2 = new Exam(6.5, 2);

        List<Student> studentList = new LinkedList<>();
        studentList.add(student1);
        studentList.add(student2);

        ExamDAO examDAO = mock(ExamDAO.class);
        when(examDAO.getExamForStudent(student1)).thenReturn(exam1);
        when(examDAO.getExamForStudent(student2)).thenReturn(exam2);

        ReportGenerator reportGenerator = new ReportGenerator(studentList, examDAO, outputPath);

        reportGenerator.generate();

        File generatedFile = new File(reportGenerator.getOutputFileName());

        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.getTotalSpace() > 0);
        assertTrue(generatedFile.toString().endsWith(".pdf"));
    }
}
