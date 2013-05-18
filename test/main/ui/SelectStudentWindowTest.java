package main.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Sergiu Soltan
 */
public class SelectStudentWindowTest {
    private SelectStudentWindow selectStudentWindow;

    @Before
    public void setUp() throws Exception {
        selectStudentWindow = new SelectStudentWindow();
    }

    @Test
    public void testSelectStudentWindow() throws Exception {
        Assert.assertNotNull(selectStudentWindow);
        Assert.assertSame(selectStudentWindow.isModal(),true);
    }

}
