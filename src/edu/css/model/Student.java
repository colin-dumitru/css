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
    private Double average;

    public Student() {
    }

    public Student(String name, Double average) {
        assert name != null && name.length() != 0 && average != null : "Invalid Student constructor aguments";
        this.name = name;
        this.average = average;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getAverage() {
        return average;
    }

    public void setName(String name) {
        assert name != null && name.length() > 0 : "Invalid student name";
        this.name = name;
    }

    public void setAverage(Double average) {
        assert average != null : "Average should not be null for student";
        this.average = average;
    }

    @Override
    public String toString() {
        return getName();
    }
}


