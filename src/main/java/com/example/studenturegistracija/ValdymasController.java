package com.example.studenturegistracija;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;

public class ValdymasController {

    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, Integer> idColumn;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, String> surnameColumn;
    @FXML
    private TableColumn<Student, String> groupColumn;


    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField editIdField;
    @FXML
    private TextField editNameField;
    @FXML
    private TextField editSurnameField;
    @FXML
    private TextField groupIdField;


    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button addRandomStudentsButton;


    @FXML
    private TextField groupNameField;
    @FXML
    private Button createGroupButton;
    @FXML
    private Button deleteGroupButton;
    @FXML
    private Button assignToGroupButton;
    @FXML
    private Button removeFromGroupButton;

    @FXML
    private Label problemGroupField;
    @FXML
    private Label problemStudentField;


    private ObservableList<Student> students = FXCollections.observableArrayList();
    private int nextId = 1;

    private Map<String, List<Student>> groups = new HashMap<>();


    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));

        studentTable.setItems(students);

        groups.put("Default", new ArrayList<>());

        addButton.setOnAction(event -> addStudent());
        editButton.setOnAction(event -> editStudent());
        removeButton.setOnAction(event -> removeStudent());
        addRandomStudentsButton.setOnAction(event -> addRandomStudents());

        createGroupButton.setOnAction(event -> createGroup());
        deleteGroupButton.setOnAction(event -> deleteGroup());
        assignToGroupButton.setOnAction(event -> assignStudentToGroup());
        removeFromGroupButton.setOnAction(event-> removeStudentFromGroup());
    }

    //DEBUG


    private void addRandomStudents() {
        String[] randomNames = {"John", "Jane", "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Hank"};
        String[] randomSurnames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Martinez", "Taylor"};

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            String name = randomNames[random.nextInt(randomNames.length)];
            String surname = randomSurnames[random.nextInt(randomSurnames.length)];
            Student student = new Student(name, surname, nextId++, "Default") {
                @Override
                public String getStudentType() {
                    return "Regular";
                }
            };
            students.add(student);
        }

        studentTable.refresh(); // Refresh the table to show the new students
        problemStudentField.setText("10 random students have been added.");
    }


    private void addStudent() {
        String name = nameField.getText();
        String surname = surnameField.getText();

        if (!name.isEmpty() && !surname.isEmpty()) {
            Student student = new Student(name, surname, nextId++, "Default") {
                @Override
                public String getStudentType() {
                    return "Regular";
                }
            };
            students.add(student);

            nameField.clear();
            surnameField.clear();
        }
    }

    private void editStudent() {
        try{
            int id = Integer.parseInt(editIdField.getText());
            String newName = editNameField.getText();
            String newSurname = editSurnameField.getText();

            for (Student student : students) {
                if (student.getStudentId() == id) {
                    if (!newName.isEmpty()) {
                        student.setName(newName);
                    }
                    if (!newSurname.isEmpty()) {
                        student.setSurname(newSurname);
                    }
                    studentTable.refresh(); // Refresh the table to show updated data
                    break;
                }
            }

            editIdField.clear();
            editNameField.clear();
            editSurnameField.clear();
        } catch (NumberFormatException e) {
            // Handle invalid ID input
        }
    }

    private void removeStudent() {
        try {
            int id = Integer.parseInt(editIdField.getText());

            students.removeIf(student -> student.getStudentId() == id);

            studentTable.refresh(); // Refresh the table to reflect changes

            // Clear the input field
            editIdField.clear();
        } catch (NumberFormatException e) {
            // Handle invalid ID input
        }
    }


    // GROUP MANAGEMENT

    private void createGroup() {
        String groupName = groupNameField.getText();
        if (!groupName.isEmpty() && !groups.containsKey(groupName)) {
            groups.put(groupName, new ArrayList<>());
            problemGroupField.setText("Group with the name " + groupName + " has been created.");
            groupNameField.clear();
        } else if (groups.containsKey(groupName)) {
            problemGroupField.setText("Group with the name " + groupName + " already exists.");
        } else {
            problemGroupField.setText("Group name field is empty.");
        }
    }

    private void deleteGroup() {
        String groupName = groupNameField.getText();

        if ("Default".equals(groupName)) {
            problemGroupField.setText("The Default group cannot be deleted.");
            return;
        }

        if (!groupName.isEmpty() && groups.containsKey(groupName)) {
            List<Student> studentsInGroup = groups.get(groupName);
            for (Student student : studentsInGroup) {
                student.setGroupName("Default");
                groups.get("Default").add(student);
            }
            groups.remove(groupName);
            problemGroupField.setText("Group " + groupName + " has been deleted.");
            groupNameField.clear();
            studentTable.refresh();
        } else {
            problemGroupField.setText("Group " + groupName + " does not exist.");
        }
    }


    private void assignStudentToGroup() {
        String groupName = groupNameField.getText();
        String studentIdText = groupIdField.getText(); // Use groupIdField instead of editIdField

        if (studentIdText.isEmpty()) {
            problemGroupField.setText("Student ID field is empty.");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdText);

            if (!groupName.isEmpty() && groups.containsKey(groupName)) {
                boolean studentAdded = false;

                for (Student student : students) {
                    if (student.getStudentId() == studentId) {
                        student.setGroupName(groupName); // Update the student's group name
                        if (!groups.get(groupName).contains(student)) {
                            groups.get(groupName).add(student); // Add the student to the group
                        }
                        studentAdded = true;
                        break;
                    }
                }

                if (studentAdded) {
                    problemGroupField.setText("Student with ID " + studentId + " has been added to group " + groupName);
                } else {
                    problemGroupField.setText("Student with ID " + studentId + " not found.");
                }
            } else {
                problemGroupField.setText("Group " + groupName + " does not exist.");
            }

            studentTable.refresh(); // Refresh the table to reflect changes
            groupNameField.clear();
            groupIdField.clear(); // Clear groupIdField
        } catch (NumberFormatException e) {
            problemGroupField.setText("Invalid student ID. Please enter a numeric value.");
        }
    }

    private void removeStudentFromGroup() {
        String studentIdText = groupIdField.getText();

        if (studentIdText.isEmpty()) {
            problemGroupField.setText("Student ID field is empty.");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdText);

            for (Student student : students) {
                if (student.getStudentId() == studentId) {
                    String currentGroup = student.getGroupName();
                    if (groups.containsKey(currentGroup)) {
                        groups.get(currentGroup).remove(student); // Remove from current group
                    }
                    student.setGroupName("Default"); // Assign to Default group
                    groups.get("Default").add(student); // Add to Default group
                    problemGroupField.setText("Student with ID " + studentId + " has been removed from their group and assigned to Default.");
                    break;
                }
            }

            studentTable.refresh(); // Refresh the table to reflect changes
            groupIdField.clear(); // Clear only the student ID field
        } catch (NumberFormatException e) {
            problemGroupField.setText("Invalid student ID. Please enter a numeric value.");
        }
    }

}