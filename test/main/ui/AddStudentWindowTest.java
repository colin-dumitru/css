package main.ui;

import edu.css.model.Exam;
import edu.css.model.Student;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;


/**
 * User: Sergiu Soltan
 */
public class AddStudentWindowTest {
    private AddStudentWindow addStudentWindow;
    private AddStudentWindow editStudentWindow;
    private Student mockStudent;
    private Exam mockExam;

    @Before
    public void setUp() throws Exception {
        stubMockStudent();
        stubMockExam();
        addStudentWindow = new AddStudentWindow();
        editStudentWindow = new AddStudentWindow(mockStudent);
        editStudentWindow.setExam(mockExam);
    }

    private void stubMockStudent(){
        mockStudent = mock(Student.class);
        stub(mockStudent.getAverage()).toReturn(5.5);
        stub(mockStudent.getId()).toReturn(1);
        stub(mockStudent.getName()).toReturn("John");
    }

    private void stubMockExam(){
        mockExam = mock(Exam.class);
        stub(mockExam.getId()).toReturn(10);
        stub(mockExam.getMark()).toReturn(5.5);
        stub(mockExam.getStudentId()).toReturn(1);
    }

    @Test
    public void testAddStudentWindowSetup() throws Exception {
        assertNotNull(addStudentWindow);
        assertNotNull(addStudentWindow.getExam());
        assertNotNull(addStudentWindow.getStudent());
        assertSame(addStudentWindow.isModal(), true);
    }

    @Test
    public void testEditStudentWindowSetup() throws Exception {
        assertNotNull(editStudentWindow);
        assertNotNull(editStudentWindow.getExam());
        assertNotNull(editStudentWindow.getStudent());
        assertSame(editStudentWindow.isModal(), true);

        Student currentStudent = editStudentWindow.getStudent();
        assertSame(currentStudent,mockStudent);

        Exam currentExam = editStudentWindow.getExam();
        assertSame(currentExam,mockExam);

        verify(mockStudent,atLeastOnce()).getId();
        verify(mockStudent,atMost(1)).getName();
        verify(mockStudent,atMost(1)).getAverage();

        verify(mockExam,never()).setStudentId(anyInt());
        verify(mockExam,atMost(1)).getMark();
    }

    @Test
    public void testOnOK() throws Exception {
        verify(mockStudent,atLeastOnce()).getId();

        verify(mockExam,atMost(1)).setStudentId(mockStudent.getId());
    }

    @Test
    public void testValidateInput() throws Exception {
        verify(mockStudent,atMost(1)).setAverage(anyDouble());
        verify(mockStudent,atMost(1)).setName(anyString());

        verify(mockExam,atMost(1)).setMark(anyDouble());

    }
}
