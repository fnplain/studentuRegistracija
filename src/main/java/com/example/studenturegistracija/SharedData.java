package com.example.studenturegistracija;

import java.util.*;

public class SharedData {
    private static final SharedData instance = new SharedData();

    private List<Student> students = new ArrayList<>();
    private Map<String, List<Student>> groups = new HashMap<>();
    private List<Runnable> groupChangeListeners = new ArrayList<>();

    private SharedData() {
        groups.put("Bendra", new ArrayList<>()); // Default group
    }

    public static SharedData getInstance() {
        return instance;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Map<String, List<Student>> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, List<Student>> groups) {
        this.groups = groups;
        notifyGroupChangeListeners();
    }

    public void addGroup(String groupName) {
        if (!groups.containsKey(groupName)) {
            groups.put(groupName, new ArrayList<>());
            notifyGroupChangeListeners();
        }
    }

    public void addGroupChangeListener(Runnable listener) {
        groupChangeListeners.add(listener);
    }

    private void notifyGroupChangeListeners() {
        for (Runnable listener : groupChangeListeners) {
            listener.run();
        }
    }
}