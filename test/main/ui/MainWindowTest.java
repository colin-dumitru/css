package main.ui;

import edu.css.java.ReportGenerator;
import edu.css.model.Exam;
import edu.css.model.Student;
import edu.css.operations.ExamDAO;
import edu.css.operations.StudentDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;

/**
 * User: Sergiu Soltan
 */
public class MainWindowTest {
    private MainWindow mainWindow;
    private ExamDAO examDAOmock;
    private StudentDAO studentDAOmock;

    @Before
    public void setUp() throws Exception {
        mainWindow = new MainWindow();
        examDAOmock = mock(ExamDAO.class);
        studentDAOmock = mock(StudentDAO.class);

        stub(studentDAOmock.getStudents()).toReturn(Arrays.<Student>asList());
    }

    @Test
    public void testOnReport() throws Exception {
        File outputPath = new File(System.getProperty("java.io.tmpdir"), "report_" + System.currentTimeMillis() + ".pdf");
        assertNotNull(outputPath);

        ReportGenerator reportGenerator = new ReportGenerator(studentDAOmock.getStudents(),examDAOmock,outputPath.toString());
        reportGenerator.generate();

        File generatedFile = new File(reportGenerator.getOutputFileName());

        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.getTotalSpace() > 0);
        assertTrue(generatedFile.toString().endsWith(".pdf"));

        verify(examDAOmock,never()).addExam(Matchers.<Exam>any());
        verify(examDAOmock,never()).getExamForStudent(Matchers.<Student>any());

        verify(studentDAOmock,atLeastOnce()).getStudents();
        assertEquals(studentDAOmock.getStudents().isEmpty(), true);
    }

    @Test
    public void testMainWindow() throws Exception {
        assertNotNull(mainWindow);
        assertNotNull(examDAOmock);
        assertNotNull(studentDAOmock);

        assertEquals(mainWindow.isModal(),true);
    }
}
