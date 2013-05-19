package edu.css.model;

import edu.css.operations.AdmissionHelper;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class StudentExportMetadataTest {

    @Test
    public void testExportMetadata()
    {
        Student student = mock(Student.class);
        when(student.getId()).thenReturn(1);
        when(student.getAverage()).thenReturn(7.5);
        when(student.getName()).thenReturn("Dinu");

        Exam exam = new Exam(7.3, student.getId());

        String[] vector = StudentExportMetadata.getDataVector(student, exam);

        //"Id","Name","BacAverage","ExamMark","Average","Passed"
        assertEquals(6, StudentExportMetadata.columnNames.length);
        assertEquals(StudentExportMetadata.columnNames.length, vector.length);

        assertEquals(student.getId().toString(), vector[0]);
        assertEquals(student.getName(), vector[1]);
        assertEquals(student.getAverage().toString(), vector[2]);
        assertEquals(exam.getMark().toString(), vector[3]);
        assertEquals(AdmissionHelper.getPassingMark(student, exam).toString(), vector[4]);
        assertEquals(String.valueOf(true), vector[5]);

    }
}
