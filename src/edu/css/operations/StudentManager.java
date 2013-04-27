package edu.css.operations;

import edu.css.db.JsonDB;
import edu.css.db.JsonDBImpl;
import edu.css.model.Student;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dinus
 * Date: 4/27/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentManager {

    private static JsonDB studentsDB;

    static {
        String projectPath = System.getProperty("user.dir");
        String dbPath = new File(projectPath, "db/student").getAbsolutePath();

        studentsDB = JsonDBImpl.fromFile(dbPath);
    }

    public static List<Student> getStudents()
    {
        studentsDB.begin();
        List<Student> studentList = studentsDB.getAll(Student.class);
        studentsDB.end(false);
        Collections.sort(studentList, StudentIdComparator.getInstance());
        return studentList;
    }

    public static void addStudent(Student student)
    {
        studentsDB.begin();
        studentsDB.save(student);
        studentsDB.end(true);
    }

    public static void updateStudent(Student student)
    {
        studentsDB.begin();
        studentsDB.save(student);
        studentsDB.end(true);
    }

    public static void deleteStudent(Student student)
    {
        studentsDB.begin();
        studentsDB.delete(student);
        studentsDB.end(true);
    }

//    public static void main(String[] args) {
//        String current = System.getProperty("user.dir");
//        System.out.println(current);
//    }
}

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