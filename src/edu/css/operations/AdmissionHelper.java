package edu.css.operations;

import edu.css.model.Exam;
import edu.css.model.Student;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class AdmissionHelper {
    public static Double getPassingMark(Student student, Exam exam)
    {
        return (student.getAverage() + exam.getMark()) / 2;
    }

    public static boolean passed(Student student, Exam exam)
    {
        return  getPassingMark(student, exam)> 5;
    }

}
