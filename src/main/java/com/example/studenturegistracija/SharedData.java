
package com.example.studenturegistracija;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedData {
    private static SharedData instance;
    private final List<Student> students = new ArrayList<>();
    private final Map<String, List<Student>> groups = new HashMap<>();
    private final List<Runnable> groupChangeListeners = new ArrayList<>();

    private SharedData() {
        // Initialize the "Bendra" group
        groups.put("Bendra", new ArrayList<>());
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);

            // Also add to Bendra group
            List<Student> bendraGroup = groups.get("Bendra");
            if (bendraGroup != null && !bendraGroup.contains(student)) {
                bendraGroup.add(student);
            }

            notifyGroupChangeListeners();
        }
    }

    public void removeStudent(Student student) {
        students.remove(student);

        // Remove from all groups including Bendra
        for (List<Student> groupStudents : groups.values()) {
            groupStudents.remove(student);
        }

        notifyGroupChangeListeners();
    }

    public void addGroup(String groupName) {
        if (!groups.containsKey(groupName)) {
            groups.put(groupName, new ArrayList<>());
            notifyGroupChangeListeners();
        }
    }

    public void addStudentToGroup(Student student, String groupName) {
        if (!groups.containsKey(groupName)) {
            addGroup(groupName);
        }

        List<Student> groupStudents = groups.get(groupName);
        if (!groupStudents.contains(student)) {
            groupStudents.add(student);
        }

        // Ensure the student is in the main students list
        if (!students.contains(student)) {
            students.add(student);


            if (!groupName.equals("Bendra")) {
                List<Student> bendraStudents = groups.get("Bendra");
                if (bendraStudents != null && !bendraStudents.contains(student)) {
                    bendraStudents.add(student);
                }
            }
        }

        notifyGroupChangeListeners();
    }

    public Map<String, List<Student>> getGroups() {
        return groups;
    }

    public List<Student> getStudents() {
        return students;
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