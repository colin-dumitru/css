package edu.css.operations;

import edu.css.db.JsonDB;
import edu.css.model.Exam;
import edu.css.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class ExamDAOTest {
    private JsonDB jsonDBMock;
    private List<Student> studentList;
    private List<Exam> examList;
    private Student student;
    private Exam exam;

    @Before
    public void setUp()
    {

        jsonDBMock = mock(JsonDB.class);

        student = mock(Student.class);
        when(student.getId()).thenReturn(1);
        when(student.getName()).thenReturn("Dinu");
        when(student.getAverage()).thenReturn(7.5);

        studentList = new LinkedList<>();
        studentList.add(student);

        exam = new Exam();
        exam.setMark(7.3);
        exam.setStudentId(student.getId());

        examList = new LinkedList<>();
        examList.add(exam);

        when(jsonDBMock.getAll(Student.class)).thenReturn(studentList);
        when(jsonDBMock.getAll(Exam.class)).thenReturn(examList);
    }

    @Test
    public void simpleMockingTest() {
        ExamDAO examDAO = mock(ExamDAO.class);
        List<Student> studentList = new LinkedList<>();

        when(examDAO.getExamForStudent(student)).thenReturn(exam);

        Exam foundExam = examDAO.getExamForStudent(student);

        assertEquals(exam, foundExam);
    }

    @Test
    public void getExamForStudentTest() {
        ExamDAO examDAO = new ExamDAO(jsonDBMock);

        Exam foundExam = examDAO.getExamForStudent(student);

        assertEquals("Different exam", exam, foundExam);
        verify(jsonDBMock).begin();
        verify(jsonDBMock).getAll(Exam.class);
        verify(jsonDBMock).end(false); // no changes should be
    }

    @Test
    public void newExamTest() {
        ExamDAO examDAO = new ExamDAO(jsonDBMock);

        Exam newExam = new Exam();
        newExam.setMark(8.3);
        newExam.setStudentId(2);

        examDAO.addExam(newExam);

        verify(jsonDBMock).begin();
        verify(jsonDBMock).save(newExam);
        verify(jsonDBMock).end(true);
    }

    @Test
    public void updateExamTest() {
        ExamDAO examDAO = new ExamDAO(jsonDBMock);

        Exam newExam = new Exam();
        newExam.setMark(8.3);
        newExam.setStudentId(2);

        examDAO.updateExam(newExam);

        verify(jsonDBMock).begin();
        verify(jsonDBMock).save(newExam);
        verify(jsonDBMock).end(true);
    }

    @Test
    public void deleteStudentTest() {

        ExamDAO examDAO = new ExamDAO(jsonDBMock);

        examDAO.deleteExam(exam);

        verify(jsonDBMock).begin();
        verify(jsonDBMock).delete(exam);
        verify(jsonDBMock).end(true);
    }

}
