package edu.css.operations;

import edu.css.db.JsonDB;
import edu.css.model.Exam;
import edu.css.model.Student;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class ExamDAO {
    private JsonDB jsonDB;

    public ExamDAO(JsonDB jsonDB) {
        this.jsonDB = jsonDB;
    }

    public void addExam(Exam exam)
    {
        jsonDB.begin();
        jsonDB.save(exam);
        jsonDB.end(true);
    }

    public void updateExam(Exam exam)
    {
        jsonDB.begin();
        jsonDB.save(exam);
        jsonDB.end(true);
    }

    public void deleteExam(Exam exam)
    {
        jsonDB.begin();
        jsonDB.delete(exam);
        jsonDB.end(true);
    }

    public Exam getExamForStudent(Student student)            {
        jsonDB.begin();
        List<Exam> examList = jsonDB.getAll(Exam.class);
        jsonDB.end(false);
        for (Exam exam : examList) {
            if(exam.getStudentId() == student.getId())
                return exam;
        }

        return null;
    }

}
