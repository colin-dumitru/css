package edu.css.model;

import edu.css.db.Column;
import edu.css.db.Entity;
import edu.css.db.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Dinus
 * Date: 4/29/13
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Exam {
    @Id
    @Column
    private Integer id;

    @Column
    private Double mark;

    @Column
    private Integer studentId;


    public Exam() {
    }

    public Exam(Double mark, Integer studentId) {
        assert mark != null && studentId != null : "Invalid constructor arguments for Exam";
        this.mark = mark;
        this.studentId = studentId;
    }

    public Integer getId() {
        return id;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        assert mark != null : "Mark shouldn't be null for Exam";
        this.mark = mark;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        assert studentId != null : "StudentId shouldn't be null for Exam";
        this.studentId = studentId;
    }
}
