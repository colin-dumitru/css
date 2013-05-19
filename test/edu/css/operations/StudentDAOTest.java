package edu.css.operations;

import edu.css.model.Student;
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

    @Test
    public void zeroTest() {
        StudentDAO studentDAO = mock(StudentDAO.class);
        List<Student> studentList = new LinkedList<Student>();

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
    public void firstTest() {
//
//        JsonDBImpl mockedJsonDB = mock(JsonDBImpl.class);
//
//        when(mockedJsonDB.fromFile)

    }
}
