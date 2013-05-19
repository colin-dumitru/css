package edu.css.java;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Dinu
 * Date: 19/05/13
 * Time: 23:27
 * To change this template use File | Settings | File Templates.
 */
public class StudentTypeTest {

    @Test
    public void studentTypeTest() {
        assertEquals(3, StudentType.values().length);
        assertEquals("Admis buget", StudentType.ADMIS_BUGET.toString());
        assertEquals("Admis cu taxa", StudentType.ADMIS_CU_TAXA.toString());
        assertEquals("Respins", StudentType.RESPINS.toString());
    }
}
