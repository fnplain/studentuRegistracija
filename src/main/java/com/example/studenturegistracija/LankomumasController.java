package com.example.studenturegistracija;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LankomumasController {

    @FXML
    private ChoiceBox<String> chooseGroup;

    @FXML
    private DatePicker startingGroupDate;

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> surnameColumn;

    @FXML
    private TableColumn<Student, Boolean> mondayColumn;

    @FXML
    private TableColumn<Student, Boolean> tuesdayColumn;

    @FXML
    private TableColumn<Student, Boolean> wednesdayColumn;

    @FXML
    private TableColumn<Student, Boolean> thursdayColumn;

    @FXML
    private TableColumn<Student, Boolean> fridayColumn;

    @FXML
    private TableColumn<Student, Boolean> saturdayColumn;

    @FXML
    private TableColumn<Student, Boolean> sundayColumn;

    @FXML
    private TableColumn<Student, Boolean> totalColumn;

    @FXML
    private Button rodytiButton;

    @FXML
    private Label problemField;


    private final ObservableList<Student> studentsInGroup = FXCollections.observableArrayList();

    public void initialize() {

        studentTable.setEditable(true);

        SharedData.getInstance().addGroupChangeListener(this::refreshGroupChoices);
        rodytiButton.setOnAction(event -> updateTable());

        // Restrict startingGroupDate to Mondays
        startingGroupDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.getDayOfWeek() != java.time.DayOfWeek.MONDAY);
            }
        });

        // Bind name and surname columns
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        surnameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSurname()));

        // Configure attendance columns
        configureAttendanceColumn(mondayColumn, 0);
        configureAttendanceColumn(tuesdayColumn, 1);
        configureAttendanceColumn(wednesdayColumn, 2);
        configureAttendanceColumn(thursdayColumn, 3);
        configureAttendanceColumn(fridayColumn, 4);
        configureAttendanceColumn(saturdayColumn, 5);
        configureAttendanceColumn(sundayColumn, 6);

        configureTotalColumn();
        totalColumn.setText("Total");
    }


    private void configureAttendanceColumn(TableColumn<Student, Boolean> column, int dayOffset) {
        // Set cell value factory to create a property that reflects the attendance state
        column.setCellValueFactory(data -> {
            Student student = data.getValue();
            LocalDate startDate = startingGroupDate.getValue();
            if (startDate == null) return new SimpleBooleanProperty(false);

            String date = startDate.plusDays(dayOffset).toString();

            // Create a property bound to the student's attendance
            BooleanProperty prop = new SimpleBooleanProperty(student.getAttendanceForDate(date));

            // Update the attendance map when property changes
            prop.addListener((obs, oldValue, newValue) -> {
                student.markAttendance(date, newValue);
            });

            return prop;
        });

        // Use CheckBoxTableCell for editing
        column.setCellFactory(CheckBoxTableCell.forTableColumn(column));

        // Make sure the column is editable
        column.setEditable(true);
    }


    private void configureTotalColumn() {
        totalColumn.setCellValueFactory(data -> {
            // This will be updated by updateTotalColumn()
            return new SimpleBooleanProperty(false);
        });

        totalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    Student student = getTableView().getItems().get(getIndex());
                    setText(String.valueOf(calculateStudentAttendance(student)));
                }
            }
        });
    }

    private int calculateStudentAttendance(Student student) {
        LocalDate startDate = startingGroupDate.getValue();
        if (startDate == null) return 0;

        int total = 0;
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            String date = startDate.plusDays(dayOffset).toString();
            if (student.getAttendanceForDate(date)) {
                total++;
            }
        }
        return total;
    }

    private void updateTotalColumn() {
        studentTable.refresh(); // This will trigger the totalColumn's cell factory to recalculate
    }

    private void updateTable() {
        String selectedGroup = chooseGroup.getValue();
        LocalDate startDate = startingGroupDate.getValue();

        problemField.setText("");

        if (selectedGroup == null) {
            problemField.setText("Nepasirinkta grupė");
            return;
        }

        if (startDate == null) {
            problemField.setText("Nepasirinkta data");
            return;
        }



        // Get students for the selected group
        Map<String, List<Student>> groups = SharedData.getInstance().getGroups();
        List<Student> groupStudents = groups.get(selectedGroup);

        if (groupStudents == null || groupStudents.isEmpty()) {
            problemField.setText("Grupėje \"" + selectedGroup + "\" nėra studentų");
            studentsInGroup.clear();
            return;
        }

        // Populate the table
        studentsInGroup.setAll(groupStudents);
        studentTable.setItems(studentsInGroup);

        // Format column headers to display month and day
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
        mondayColumn.setText(startDate.format(formatter));
        tuesdayColumn.setText(startDate.plusDays(1).format(formatter));
        wednesdayColumn.setText(startDate.plusDays(2).format(formatter));
        thursdayColumn.setText(startDate.plusDays(3).format(formatter));
        fridayColumn.setText(startDate.plusDays(4).format(formatter));
        saturdayColumn.setText(startDate.plusDays(5).format(formatter));
        sundayColumn.setText(startDate.plusDays(6).format(formatter));


        LocalDate endDate = startDate.plusDays(6);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", new java.util.Locale("lt"));
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d");

        String month = startDate.format(monthFormatter);
        String startDay = startDate.format(dayFormatter);
        String endDay = endDate.format(dayFormatter);

        problemField.setText("Rodomas " + month + " mėn. sav. (" + startDay + " - " + endDay + " d.) lankomumas");


        updateTotalColumn();

    }

    private void refreshGroupChoices() {
        // Make sure Bendra group is updated


        Map<String, List<Student>> groups = SharedData.getInstance().getGroups();
        System.out.println("Available groups: " + groups.keySet());

        chooseGroup.getItems().clear();
        chooseGroup.getItems().addAll(groups.keySet());

        // If there are items, select Bendra by default
        if (!chooseGroup.getItems().isEmpty() && groups.containsKey("Bendra")) {
            chooseGroup.setValue("Bendra");
        }

        System.out.println("Number of students in Bendra: " +
                (groups.get("Bendra") == null ? 0 : groups.get("Bendra").size()));
    }
}