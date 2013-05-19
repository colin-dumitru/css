package main.ui;

import edu.css.model.Exam;
import edu.css.operations.ExamDAO;
import edu.css.operations.StudentDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Sergiu Soltan
 */
public class SelectStudentWindowTest {
    private SelectStudentWindow selectStudentWindow;
    private ExamDAO examDAOmock;
    private StudentDAO studentDAOmock;

    @Before
    public void setUp() throws Exception {
        selectStudentWindow = new SelectStudentWindow();
        examDAOmock = mock(ExamDAO.class);
        studentDAOmock = mock(StudentDAO.class);
    }

    @Test
    public void testSelectStudentWindow() throws Exception {
        assertNotNull(selectStudentWindow);
        assertSame(selectStudentWindow.isModal(), true);

        assertNotNull(studentDAOmock);
        assertNotNull(examDAOmock);
        verify(studentDAOmock, never()).getStudents();
        verify(examDAOmock,never()).addExam(Matchers.<Exam>any());
    }
}
