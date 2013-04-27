package edu.css.model;


import edu.css.db.Column;
import edu.css.db.Entity;
import edu.css.db.Id;

/**
 * Catalin Dumitru
 * Universitatea Alexandru Ioan Cuza
 */
@Entity
public class Student {
    @Id
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private Boolean passed;

    @Column
    private Double average;

    public Student() {
        passed = true; //TODO this should automatically determined
    }

    public Student(String name, Boolean passed, Double average) {
        this.name = name;
        this.passed = passed;
        this.average = average;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getPassed() {
        return passed;
    }

    public Double getAverage() {
        return average;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return getName();
    }
}


