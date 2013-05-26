package edu.css.operations;

import edu.css.db.JsonDB;
import edu.css.model.Student;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dinus
 * Date: 4/27/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class StudentDAO {

    private JsonDB jsonDB;

    public StudentDAO(JsonDB jsonDB) {
        assert jsonDB != null : "Invalid database : NullValue";
        this.jsonDB = jsonDB;
    }

    public List<Student> getStudents()
    {
        jsonDB.begin();
        List<Student> studentList = jsonDB.getAll(Student.class);
        jsonDB.end(false);
        Collections.sort(studentList, StudentIdComparator.getInstance());
        return studentList;
    }

    public void addStudent(Student student)
    {
        assert student != null : "Invalid student argument NullValue";
        jsonDB.begin();
        jsonDB.save(student);
        jsonDB.end(true);
    }

    public void updateStudent(Student student)
    {
        assert student != null : "Invalid student argument NullValue";
        jsonDB.begin();
        jsonDB.save(student);
        jsonDB.end(true);
    }

    public void deleteStudent(Student student)
    {
        assert student != null : "Invalid student argument NullValue";
        jsonDB.begin();
        jsonDB.delete(student);
        jsonDB.end(true);
    }

}
