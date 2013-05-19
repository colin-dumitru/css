package edu.css.db;

/**
 * Catalin Dumitru
 * Date: 5/18/13
 * Time: 6:18 PM
 */
@Entity
public class TestEntity {
    @Id
    @Column
    private Integer idField;

    @Column
    private Integer integerField;

    @Column
    private Double doubleField;

    @Column
    private String stringField;

    @Column
    private Boolean booleanField;

    public TestEntity() {
    }

    public TestEntity(Integer integerField, Double doubleField, String stringField, Boolean booleanField) {
        this.integerField = integerField;
        this.doubleField = doubleField;
        this.stringField = stringField;
        this.booleanField = booleanField;
    }

    public Integer getIdField() {
        return idField;
    }

    public Integer getIntegerField() {
        return integerField;
    }

    public void setIntegerField(Integer integerField) {
        this.integerField = integerField;
    }

    public Double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(Double doubleField) {
        this.doubleField = doubleField;
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public Boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(Boolean booleanField) {
        this.booleanField = booleanField;
    }
}
