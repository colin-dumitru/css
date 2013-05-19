package edu.css.operations;

import edu.css.model.Exam;
import edu.css.model.Student;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 18:14
 * To change this template use File | Settings | File Templates.
 */
public class AdmissionHelperTest {

    @Test
    public void getMarkTest()
    {
        Student student = new Student("Dinu", false, 7.5);

        Exam exam = new Exam();
        exam.setMark(7.1);

        assertEquals(7.3, AdmissionHelper.getPassingMark(student, exam));
    }

    @Test
    public void passedTest()
    {
        Student student = new Student("Dinu", false, 5.0);

        Exam exam = new Exam();
        exam.setMark(5.0);

        double mark = 0;
        while(mark < 5)
        {
            student.setAverage(mark);
            exam.setMark(mark);
            assertEquals(false, AdmissionHelper.passed(student, exam));
            mark += 0.1;
        }

        while(mark <= 10)
        {
            student.setAverage(mark);
            exam.setMark(mark);
            assertEquals(true, AdmissionHelper.passed(student, exam));
            mark += 0.1;
        }
    }

}
