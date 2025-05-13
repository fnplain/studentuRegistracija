package com.example.studenturegistracija;

import java.util.HashMap;
import java.util.Map;

public abstract class Student {
    private String name;
    private String surname;
    private int studentId;
    private String groupName;
    private Map<String, Boolean> attendance = new HashMap<>();

    public Student(String name, String surname, int studentId, String groupName) {
        this.name = name;
        this.surname = surname;
        this.studentId = studentId;
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Map<String, Boolean> getAttendance() {
        return attendance;
    }

    public void markAttendance(String date, boolean attended) {
        if (attended) {
            attendance.put(date, true);
        } else {
            attendance.remove(date);
        }
    }

    public Boolean getAttendanceForDate(String date) {
        return attendance.getOrDefault(date, false);
    }

    public abstract String getStudentType();



}