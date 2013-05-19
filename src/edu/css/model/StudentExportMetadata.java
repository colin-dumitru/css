package edu.css.model;

import edu.css.operations.AdmissionHelper;
import edu.css.operations.DAOLoader;
import edu.css.operations.ExamDAO;
import edu.css.operations.StudentDAO;

/**
 * Created with IntelliJ IDEA.
 * User: Dinus
 * Date: 4/27/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentExportMetadata
{
    public static String[] columnNames = {"Id","Name","BacAverage","ExamMark","Average","Passed"};

    public static String[] getDataVector(Student student, Exam exam){
        String[] values = new String[6];
        values[0] = student.getId().toString();
        values[1] = student.getName();
        values[2] = student.getAverage().toString();
        values[3] = exam.getMark().toString();
        values[4] = AdmissionHelper.getPassingMark(student, exam).toString();
        values[5] = String.valueOf(AdmissionHelper.passed(student, exam));


        return values;
    }
}