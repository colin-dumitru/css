package edu.css.java;

/**
 * Created with IntelliJ IDEA.
 * User: ZIG
 * Date: 22.04.2013
 * Time: 23:08
 * To change this template use File | Settings | File Templates.
 */
public enum StudentType {
    ADMIS_BUGET("Admis buget"),
    ADMIS_CU_TAXA("Admis cu taxa"),
    RESPINS("Respins");

    StudentType(String valueAsString){
        this.valueAsString = valueAsString;
    }

    private String valueAsString;

    public String toString(){
        return valueAsString;
    }
}
