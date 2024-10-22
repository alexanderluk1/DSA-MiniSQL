package edu.smu.smusql.pair1;

public class Record {
    
    private Integer id;
    private String name;
    private Integer age;
    private Double gpa;
    private Boolean deansList;

    public Record(Integer id, String name, Integer age, Double gpa, Boolean deansList) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gpa = gpa;
        this.deansList = deansList;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public Boolean getDeansList() {
        return deansList;
    }

    public void setDeansList(Boolean deansList) {
        this.deansList = deansList;
    }

    @Override
    public String toString() {
        return id + ", " + name;
    }    

    @Override
    public int hashCode() {
        return id;
    }
}
