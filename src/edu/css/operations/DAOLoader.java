package edu.css.operations;

import edu.css.db.JsonDB;
import edu.css.db.JsonDBImpl;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class DAOLoader {

    private static JsonDB jsonDB;
    static {
//        String projectPath = System.getProperty("user.dir");
//        String dbPath = new File(projectPath, "db/student").getAbsolutePath();

        jsonDB = JsonDBImpl.fromFile("db/student");
    }

    public static StudentDAO getStudentDAO()
    {
        return new StudentDAO(jsonDB);
    }

    public static ExamDAO getExamDAO()
    {
        return new ExamDAO(jsonDB);
    }
}
