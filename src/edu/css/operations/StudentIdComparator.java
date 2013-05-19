package edu.css.operations;

import edu.css.model.Student;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
class StudentIdComparator implements Comparator<Student>
{
    private StudentIdComparator()
    {}

    private static StudentIdComparator instance = new StudentIdComparator();
    public static StudentIdComparator getInstance()
    {
        return instance;
    }

    @Override
    public int compare(Student o1, Student o2) {
        return o2.getId() - o1.getId();
    }
}
