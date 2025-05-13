package com.example.studenturegistracija;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML
    private Label problemEditField;




    private ObservableList<Student> students = FXCollections.observableArrayList(SharedData.getInstance().getStudents());
    private Map<String, List<Student>> groups = SharedData.getInstance().getGroups();

    private int nextId = 1;




    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));

        studentTable.setItems(students);



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
            Student student = new Student(name, surname, nextId++, "Bendra") {
                @Override
                public String getStudentType() {
                    return "Regular";
                }
            };
            students.add(student);
            SharedData.getInstance().addStudent(student);

            if (SharedData.getInstance().getGroups().containsKey("Bendra")) {
                List<Student> bendraGroup = SharedData.getInstance().getGroups().get("Bendra");
                if (!bendraGroup.contains(student)) {
                    bendraGroup.add(student);
                }
            } else {
                // Create Bendra group if it doesn't exist
                SharedData.getInstance().addGroup("Bendra");
                SharedData.getInstance().getGroups().get("Bendra").add(student);
            }

        }

        studentTable.refresh(); // Refresh the table to show the new students
        problemStudentField.setText("Pridėta 10 atsitiktinių studentų.");
    }


    private void addStudent() {
        String name = nameField.getText();
        String surname = surnameField.getText();

        if (name.isEmpty() || surname.isEmpty()) {
            problemStudentField.setText("Vardo arba pavardės laukas yra tuščias.");
            return;
        }

        Student student = new Student(name, surname, nextId++, "Bendra") {
            @Override
            public String getStudentType() {
                return "Regular";
            }
        };
        students.add(student);

        SharedData.getInstance().addStudent(student);
        SharedData.getInstance().addStudentToGroup(student, "Bendra");

        students.setAll(SharedData.getInstance().getStudents());

        if (SharedData.getInstance().getGroups().containsKey("Bendra")) {
            List<Student> bendraGroup = SharedData.getInstance().getGroups().get("Bendra");
            if (!bendraGroup.contains(student)) {
                bendraGroup.add(student);
            }
        } else {
            // Create Bendra group if it doesn't exist
            SharedData.getInstance().addGroup("Bendra");
            SharedData.getInstance().getGroups().get("Bendra").add(student);
        }

        problemStudentField.setText("Studentas " + name + " " + surname + " sėkmingai pridėtas.");
        nameField.clear();
        surnameField.clear();
    }



    private void editStudent() {
        String idText = editIdField.getText();

        if (idText.isEmpty()) {
            problemEditField.setText("ID neįvestas.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            boolean studentFound = false;

            for (Student student : students) {
                if (student.getStudentId() == id) {
                    studentFound = true;

                    String newName = editNameField.getText();
                    String newSurname = editSurnameField.getText();

                    if (!newName.isEmpty()) {
                        student.setName(newName);
                    }
                    if (!newSurname.isEmpty()) {
                        student.setSurname(newSurname);
                    }

                    studentTable.refresh(); // Refresh the table to show updated data
                    problemEditField.setText("Studento su ID " + id + " duomenys atnaujinti į " + student.getName() + " " + student.getSurname() + ".");
                    break;
                }
            }

            if (!studentFound) {
                problemEditField.setText("Studentas su ID " + id + " nerastas.");
            }

        } catch (NumberFormatException e) {
            problemEditField.setText("Neteisingas ID. Įveskite skaitinę reikšmę.");
        }

        editIdField.clear();
        editNameField.clear();
        editSurnameField.clear();
    }

    private void removeStudent() {
        String idText = editIdField.getText();

        if (idText.isEmpty()) {
            problemEditField.setText("ID neįvestas.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            boolean studentRemoved = students.removeIf(student -> student.getStudentId() == id);

            if (studentRemoved) {
                problemEditField.setText("Studentas su ID " + id + " sėkmingai ištrintas.");
            } else {
                problemEditField.setText("Studentas su ID " + id + " nerastas.");
            }

            studentTable.refresh(); // Refresh the table to reflect changes
            editIdField.clear(); // Clear the input field
        } catch (NumberFormatException e) {
            problemEditField.setText("Neteisingas ID. Įveskite skaitinę reikšmę.");;
        }
    }


    // GROUP MANAGEMENT

    private void createGroup() {
        String groupName = groupNameField.getText();
        if (groupName == null || groupName.trim().isEmpty()) {
            problemGroupField.setText("Grupės pavadinimo laukas yra tuščias.");
            return;
        }

        if (SharedData.getInstance().getGroups().containsKey(groupName)) {
            problemGroupField.setText("Grupė su pavadinimu " + groupName + " jau egzistuoja.");
        } else {
            SharedData.getInstance().addGroup(groupName);
            problemGroupField.setText("Grupė su pavadinimu " + groupName + " sukurta.");
            groupNameField.clear();
        }
    }

    private void deleteGroup() {
        String groupName = groupNameField.getText();

        if ("Bendra".equals(groupName)) {
            problemGroupField.setText("Grupės \"Bendra\" ištrinti negalima.");
            return;
        }

        if (!groupName.isEmpty() && groups.containsKey(groupName)) {
            List<Student> studentsInGroup = groups.get(groupName);
            for (Student student : studentsInGroup) {
                student.setGroupName("Bendra");
                groups.get("Bendra").add(student);
            }
            groups.remove(groupName);
            problemGroupField.setText("Grupė " + groupName + " ištrinta.");
            groupNameField.clear();
            studentTable.refresh();
        } else {
            problemGroupField.setText("Grupė " + groupName + " neegzistuoja.");
        }
    }


    private void assignStudentToGroup() {
        String groupName = groupNameField.getText();
        String studentIdText = groupIdField.getText(); // Use groupIdField instead of editIdField

        if (studentIdText.isEmpty()) {
            problemGroupField.setText("Studento ID laukas yra tuščias.");
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
                    problemGroupField.setText("Studentas su ID " + studentId + " pridėtas į grupę " + groupName + ".");
                } else {
                    problemGroupField.setText("Studentas su ID " + studentId + " nerastas.");
                }
            } else {
                problemGroupField.setText("Grupė " + groupName + " neegzistuoja.");
            }

            studentTable.refresh(); // Refresh the table to reflect changes
            //groupNameField.clear();
            groupIdField.clear(); // Clear groupIdField
        } catch (NumberFormatException e) {
            problemGroupField.setText("Neteisingas studento ID. Įveskite skaitinę reikšmę.");
        }
    }

    private void removeStudentFromGroup() {
        String studentIdText = groupIdField.getText();

        if (studentIdText.isEmpty()) {
            problemGroupField.setText("Studento ID laukas yra tuščias.");
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
                    student.setGroupName("Bendra"); // Assign to Bendra group
                    groups.get("Bendra").add(student); // Add to Bendra group
                    problemGroupField.setText("Studentas su ID " + studentId + " pašalintas iš grupės ir priskirtas grupei \"Bendra\".");
                    break;
                }
            }

            studentTable.refresh(); // Refresh the table to reflect changes
            groupIdField.clear(); // Clear only the student ID field
        } catch (NumberFormatException e) {
            problemGroupField.setText("Neteisingas studento ID. Įveskite skaitinę reikšmę.");
        }
    }


    // LANKOMUMAS





}