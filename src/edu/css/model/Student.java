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
        this.name = name;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return getName();
    }
}


