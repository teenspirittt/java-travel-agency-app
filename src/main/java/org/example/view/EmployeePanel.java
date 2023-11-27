package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controller.ResultPanel;
import org.example.dao.EmployeeDAO;
import org.example.model.Carrier;
import org.example.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeePanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private EmployeeDAO employeeDAO;


    public EmployeePanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.employeeDAO = new EmployeeDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        showAllEmployees();

        Button showAllButton = createButton("Show All");
        showAllButton.setOnAction(e -> showAllEmployees());

        Button updateButton = createButton("Update");
        updateButton.setOnAction(e -> updateEmployee());

        Button deleteButton = createButton("Delete");
        deleteButton.setOnAction(e -> deleteEmployee());

        Button addButton = createButton("Add");
        addButton.setOnAction(e -> addEmployee());

        Button getBySalary = createButton("Get by Salary");
        getBySalary.setOnAction(e -> getEmployeeBySalary());

        Button getByPosition= createButton("Get by Position");
        getByPosition.setOnAction(e -> getEmployeeByPosition());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton,getByPosition,getBySalary);

        getChildren().addAll(buttonsRow);
    }

    private void getEmployeeBySalary() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Employees by Salary");
        dialog.setHeaderText(null);

        TextField minSalaryField = new TextField();
        TextField maxSalaryField = new TextField();

        minSalaryField.setPromptText("Min Salary");
        maxSalaryField.setPromptText("Max Salary");

        GridPane grid = new GridPane();
        grid.add(new Label("Min Salary:"), 0, 0);
        grid.add(minSalaryField, 1, 0);
        grid.add(new Label("Max Salary:"), 0, 1);
        grid.add(maxSalaryField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double minSalary = Double.parseDouble(minSalaryField.getText());
                double maxSalary = Double.parseDouble(maxSalaryField.getText());

                List<Employee> employees = employeeDAO.getBySalary(minSalary, maxSalary);
                resultPanel.showTable(createEmployeeTable(FXCollections.observableArrayList(employees)));
            } catch (NumberFormatException e) {
                resultPanel.setResult("Please enter valid salary values");
            }
        }
    }

    private void getEmployeeByPosition() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Employees by Position");
        dialog.setHeaderText(null);

        TextField positionField = new TextField();
        positionField.setPromptText("Enter position");

        GridPane grid = new GridPane();
        grid.add(new Label("Position:"), 0, 0);
        grid.add(positionField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String position = positionField.getText();

            if (!position.isEmpty()) {
                List<Employee> employees = employeeDAO.getByPosition(position);
                resultPanel.showTable(createEmployeeTable(FXCollections.observableArrayList(employees)));
            } else {
                resultPanel.setResult("Please enter a position for search");
            }
        }
    }

    private Button createButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void setupTableColumns(TableView<Employee> employeeTable) {
        TableColumn<Employee, String> fullNameColumn = new TableColumn<>("Full Name");
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<Employee, java.sql.Date> dateOfBirthColumn = new TableColumn<>("Date of Birth");
        dateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        TableColumn<Employee, String> positionColumn = new TableColumn<>("Position");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        employeeTable.getColumns().addAll(fullNameColumn, dateOfBirthColumn, positionColumn, salaryColumn);
        employeeTable.setMinWidth(1000);
    }

    private TableView<Employee> createEmployeeTable(ObservableList<Employee> employees) {
        TableView<Employee> employeeTable = new TableView<>();
        setupTableColumns(employeeTable);

        employeeTable.setItems(employees);
        return employeeTable;
    }

    private void showAllEmployees() {
        List<Employee> employees = employeeDAO.getAllEntities();
        ObservableList<Employee> observableEmployees = FXCollections.observableArrayList(employees);
        resultPanel.showTable(createEmployeeTable(observableEmployees));
    }

    private void updateEmployee() {
        List<Employee> employees = employeeDAO.getAllEntities();

        ComboBox<String> employeeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        FXCollections.observableArrayList(
                                employees.stream()
                                        .map(Employee::getFullName)
                                        .toList()))
        );
        employeeComboBox.setPromptText("Select an employee");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Employee");
        dialog.setHeaderText("Select an employee to update:");

        GridPane grid = new GridPane();
        grid.add(employeeComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedEmployeeName = employeeComboBox.getValue();

            if (selectedEmployeeName != null && !selectedEmployeeName.isEmpty()) {
                Employee selectedEmployee = employees.stream()
                        .filter(employee -> selectedEmployeeName.equals(employee.getFullName()))
                        .findFirst()
                        .orElse(null);

                if (selectedEmployee != null) {
                    showUpdateEmployeeDialog(selectedEmployee);
                } else {
                    resultPanel.setResult("Employee not found for updating");
                }
            } else {
                resultPanel.setResult("No employee selected for updating");
            }
        }
        showAllEmployees();
    }

    private void showUpdateEmployeeDialog(Employee selectedEmployee) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Employee");
        dialog.setHeaderText("Update details for employee ID: " + selectedEmployee.getId());

        TextField fullNameField = new TextField(selectedEmployee.getFullName());
        TextField positionField = new TextField(selectedEmployee.getPosition());
        TextField salaryField = new TextField(String.valueOf(selectedEmployee.getSalary()));

        // Add a DatePicker for selecting the date of birth
        DatePicker dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setValue(selectedEmployee.getDateOfBirth().toLocalDate());

        GridPane grid = new GridPane();
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Date of Birth:"), 0, 1);
        grid.add(dateOfBirthPicker, 1, 1);
        grid.add(new Label("Position:"), 0, 2);
        grid.add(positionField, 1, 2);
        grid.add(new Label("Salary:"), 0, 3);
        grid.add(salaryField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newFullName = fullNameField.getText();
            String newPosition = positionField.getText();
            double newSalary = Double.parseDouble(salaryField.getText());
            LocalDate newDateOfBirth = dateOfBirthPicker.getValue();
            java.sql.Date sqlDateOfBirth = java.sql.Date.valueOf(newDateOfBirth);

            selectedEmployee.setFullName(newFullName);
            selectedEmployee.setPosition(newPosition);
            selectedEmployee.setSalary(newSalary);
            selectedEmployee.setDateOfBirth(sqlDateOfBirth);

            employeeDAO.updateEntity(selectedEmployee);

            resultPanel.setResult("Employee updated successfully");
        }
    }


    private void deleteEmployee() {
        List<Employee> employees = employeeDAO.getAllEntities();

        ComboBox<String> employeeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        FXCollections.observableArrayList(
                                employees.stream()
                                        .map(Employee::getFullName)
                                        .toList()))
        );
        employeeComboBox.setPromptText("Select an employee");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Employee");
        dialog.setHeaderText("Select an employee to delete:");

        GridPane grid = new GridPane();
        grid.add(employeeComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedEmployeeName = employeeComboBox.getValue();

            if (selectedEmployeeName != null && !selectedEmployeeName.isEmpty()) {
                Employee selectedEmployee = employees.stream()
                        .filter(employee -> selectedEmployeeName.equals(employee.getFullName()))
                        .findFirst().orElse(null);

                if (selectedEmployee != null) {
                    employeeDAO.deleteEntity(selectedEmployee);
                    resultPanel.setResult("Employee deleted successfully");
                } else {
                    resultPanel.setResult("Employee not found for deletion");
                }
            } else {
                resultPanel.setResult("No employee selected for deletion");
            }
        }
        showAllEmployees();
    }

    private void addEmployee() {
        Alert dialog = new Alert(AlertType.CONFIRMATION);
        dialog.setTitle("Add Employee");
        dialog.setHeaderText(null);

        TextField fullNameField = new TextField();
        DatePicker dateOfBirthPicker = new DatePicker();
        TextField positionField = new TextField();
        TextField salaryField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Date of Birth:"), 0, 1);
        grid.add(dateOfBirthPicker, 1, 1);
        grid.add(new Label("Position:"), 0, 2);
        grid.add(positionField, 1, 2);
        grid.add(new Label("Salary:"), 0, 3);
        grid.add(salaryField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String fullName = fullNameField.getText();
            java.sql.Date dateOfBirth = java.sql.Date.valueOf(dateOfBirthPicker.getValue());
            String position = positionField.getText();
            Double salary = Double.parseDouble(salaryField.getText());

            Employee newEmployee = new Employee();
            newEmployee.setFullName(fullName);
            newEmployee.setDateOfBirth(dateOfBirth);
            newEmployee.setPosition(position);
            newEmployee.setSalary(salary);

            employeeDAO.addEntity(newEmployee);

            resultPanel.setResult("Employee added successfully");
        }
        showAllEmployees();
    }
}
