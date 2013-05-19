package main.ui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * User: Sergiu Soltan
 */
public class ShowResultsWindowTest {

    private ShowResultsWindow showResultsWindow;

    @Before
    public void setUp() throws Exception {
        showResultsWindow = new ShowResultsWindow();
    }

    @Test
    public void testShowResultsWindow() throws Exception {
        assertNotNull(showResultsWindow);
        assertSame(showResultsWindow.isModal(), true);
    }
}
