package edu.css.operations;

import edu.css.db.JsonDB;
import edu.css.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
public class StudentDAOTest {

    private JsonDB jsonDBMock;
    private List<Student> studentList;

    @Before
    public void setUp()
    {

        jsonDBMock = mock(JsonDB.class);
        Student student = new Student();
        student.setName("Dinu");
        student.setAverage(7.5);

        studentList = new LinkedList<>();
        studentList.add(student);

        when(jsonDBMock.getAll(Student.class)).thenReturn(studentList);
    }

    @Test
    public void simpleMockingTest() {
        StudentDAO studentDAO = mock(StudentDAO.class);
        List<Student> studentList = new LinkedList<>();

        Student student = new Student();

        student.setName("Dinu");
        student.setAverage(7.5);

        studentList.add(student);

        when(studentDAO.getStudents()).thenReturn(studentList);

        List<Student> students = studentDAO.getStudents();

        assertEquals(students.size(), studentList.size());
        assertEquals(students.get(0), student);
    }

    @Test
    public void getStudentsTest() {
        StudentDAO studentDAO = new StudentDAO(jsonDBMock);

        List<Student> foundStudents = studentDAO.getStudents();

        assertEquals("Different student lists", studentList, foundStudents);
        verify(jsonDBMock).begin();
        verify(jsonDBMock).end(false); // no changes should be
    }

    @Test
    public void newStudentTest() {
        StudentDAO studentDAO = new StudentDAO(jsonDBMock);

        Student newStudent = new Student("Andrew", false, 7.9);
        studentDAO.addStudent(newStudent);

        verify(jsonDBMock).begin();
        verify(jsonDBMock).save(newStudent);
        verify(jsonDBMock).end(true); // should save
    }

    @Test
    public void updateStudentTest() {

        StudentDAO studentDAO = new StudentDAO(jsonDBMock);

        Student newStudent = new Student("Andrew", false, 7.9);
        studentDAO.updateStudent(newStudent);

        verify(jsonDBMock).begin();
        verify(jsonDBMock).save(newStudent);
        verify(jsonDBMock).end(true); // should save
    }

    @Test
    public void deleteStudentTest() {

        StudentDAO studentDAO = new StudentDAO(jsonDBMock);
        Student existingStudent = studentList.get(0);
        studentDAO.deleteStudent(existingStudent);

        verify(jsonDBMock).begin();
        verify(jsonDBMock).delete(existingStudent);
        verify(jsonDBMock).end(true);
    }

}
