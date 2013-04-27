package edu.css.json;

import org.junit.Test;

import java.io.File;
import java.util.Scanner;

/**
 * Catalin Dumitru
 * Date: 4/27/13
 * Time: 3:14 PM
 */
public class JsonTest {
    @Test
    public void testLoadStudent() throws Exception {
        JsonParser parser = new JsonParser();
        Scanner scanner = new Scanner(new File("db/student/student.json"));
        scanner.useDelimiter("\\Z");
        String input = scanner.next();

        parser.parse(input);
    }

    @Test
    public void testLoadStudentMeta() throws Exception {
        JsonParser parser = new JsonParser();
        Scanner scanner = new Scanner(new File("db/student/student.meta.json"));
        scanner.useDelimiter("\\Z");
        String input = scanner.next();

        parser.parse(input);

    }
}
