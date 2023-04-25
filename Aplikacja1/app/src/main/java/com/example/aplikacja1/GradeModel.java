package com.example.aplikacja1;

public class GradeModel {

    private String name;
    private int grade;

    public GradeModel(String name, int grade){
        this.name = name;
        if(grade > 5 || grade < 2)
            this.grade = 2;
        else
            this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        if(grade > 5 || grade < 2)
            this.grade = 2;
        else
            this.grade = grade;
    }

}
