package edu.css.db;

import edu.css.model.Student;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Catalin Dumitru
 * Universitatea Alexandru Ioan Cuza
 */
public class ParserTest {
    @Test
    public void testGetAllMeta() throws Exception {
        JsonDB db = JsonDBImpl.fromFile("db\\test\\student");

        db.begin();
        List<Student> students = db.getAll(Student.class);
        assertEquals(students.size(), 5);
        db.end(true);
    }

    @Test
    public void testFind() throws Exception {
        JsonDB db = JsonDBImpl.fromFile("db\\test\\student");

        db.begin();
        Student student = db.find(2, Student.class);
        assertNotNull(student);
        assertEquals(student.getName(), "Mike");
        db.end(true);
    }

    @Test
    public void testSave() throws Exception {
        JsonDB db = JsonDBImpl.fromFile("db\\test\\student");

        db.begin();
        Student student = new Student("Virgil", false, 10d);
        db.save(student);
        db.end(true);

        db.begin();
        Student savedStudent = db.find(student.getId(), Student.class);
        assertEquals(savedStudent.getName(), "Virgil");
        db.end(false);

        db.begin();
        db.delete(student);
        db.end(true);
    }

    @Test
    public void testUpdate() throws Exception {
        JsonDB db = JsonDBImpl.fromFile("db\\test\\student");

        db.begin();
        Student student = db.find(0, Student.class);
        student.setName("Max");
        db.save(student);
        db.end(true);

        db.begin();
        Student savedStudent = db.find(student.getId(), Student.class);
        assertEquals(savedStudent.getName(), "Max");
        db.end(false);
    }

    @Test
    public void testDelete() throws Exception {
        JsonDB db = JsonDBImpl.fromFile("db\\test\\student");

        db.begin();
        Student student = new Student("Virgil", false, 10d);
        db.save(student);
        db.end(true);

        db.begin();
        assertEquals(db.getAll(Student.class).size(), 6);
        db.end(true);

        db.begin();
        db.delete(student);
        db.end(true);

        db.begin();
        assertEquals(db.getAll(Student.class).size(), 5);
        db.end(true);
    }
}
