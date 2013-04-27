package edu.css.model;

/**
 * Created with IntelliJ IDEA.
 * User: Dinus
 * Date: 4/27/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentExportMetadata
{
    public static String[] columnNames = {"Id","Name","Passed","Average"};

    public static String[] getDataVector(Student student){
        String[] values = new String[7];
        values[0] = student.getId().toString();
        values[1] = student.getName();
        values[2] = student.getPassed().toString();
        values[3] = student.getAverage().toString();

        return values;
    }
}