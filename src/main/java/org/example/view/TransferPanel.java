package org.example.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controller.ResultPanel;
import org.example.dao.CarrierDAO;
import org.example.dao.EmployeeDAO;
import org.example.dao.EmployeeTransferDAO;
import org.example.model.Carrier;
import org.example.model.Employee;
import org.example.model.EmployeeTransfer;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransferPanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private EmployeeTransferDAO employeeTransferDAO;

    public TransferPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.employeeTransferDAO = new EmployeeTransferDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        showAllEmployeeTransfers();

        Button showAllButton = Panel.createButton("Show All");
        showAllButton.setOnAction(e -> showAllEmployeeTransfers());

        Button updateButton = Panel.createButton("Update");
        updateButton.setOnAction(e -> updateEmployeeTransfer());

        Button deleteButton = Panel.createButton("Delete");
        deleteButton.setOnAction(e -> deleteEmployeeTransfer());

        Button addButton = Panel.createButton("Add");
        addButton.setOnAction(e -> addEmployeeTransfer());

        Button dateButton = Panel.createButton("Get by order date");
        dateButton.setOnAction(e -> getByDateTransfer());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton, dateButton);

        getChildren().addAll(buttonsRow);
    }

    private void getByDateTransfer() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Employee Transfers by Order Date");
        dialog.setHeaderText(null);

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        RadioButton beforeButton = new RadioButton("Before");
        RadioButton afterButton = new RadioButton("After");
        ToggleGroup toggleGroup = new ToggleGroup();
        beforeButton.setToggleGroup(toggleGroup);
        afterButton.setToggleGroup(toggleGroup);

        beforeButton.setSelected(true);
        datePicker.setPromptText("Choose a date");

        GridPane grid = new GridPane();
        grid.add(new Label("Order Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Select:"), 0, 1);
        grid.add(beforeButton, 1, 1);
        grid.add(afterButton, 2, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            LocalDate selectedDate = datePicker.getValue();
            boolean isBefore = beforeButton.isSelected();

            Timestamp selectedTimestamp = Timestamp.valueOf(selectedDate.atStartOfDay());

            List<EmployeeTransfer> employeeTransfers;
            if (isBefore) {
                employeeTransfers = employeeTransferDAO.getByOrderDate(
                        Timestamp.valueOf(LocalDate.of(900, 1, 1).atStartOfDay()), selectedTimestamp);
            } else {
                employeeTransfers = employeeTransferDAO.getByOrderDate(selectedTimestamp,
                        Timestamp.valueOf(LocalDate.of(2300, 1, 1).atStartOfDay()));
            }

            resultPanel.showTable(createEmployeeTransferTable(FXCollections.observableArrayList(employeeTransfers)));
        }
    }


    private void showAllEmployeeTransfers() {
        List<EmployeeTransfer> employeeTransfers = employeeTransferDAO.getAllEntities();
        ObservableList<EmployeeTransfer> observableEmployeeTransfers = FXCollections.observableArrayList(employeeTransfers);
        resultPanel.showTable(createEmployeeTransferTable(observableEmployeeTransfers));
    }

    private void updateEmployeeTransfer() {
        List<EmployeeTransfer> employeeTransfers = employeeTransferDAO.getAllEntities();

        ComboBox<String> employeeTransferComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        employeeTransfers.stream()
                                .map(EmployeeTransfer::getOrderNumber)
                                .toList()
                )
        );
        employeeTransferComboBox.setPromptText("Select an Employee Transfer");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Employee Transfer");
        dialog.setHeaderText("Select an Employee Transfer to update:");

        GridPane grid = new GridPane();
        grid.add(employeeTransferComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedEmployeeTransferOrderNumber = employeeTransferComboBox.getValue();

            if (selectedEmployeeTransferOrderNumber != null && !selectedEmployeeTransferOrderNumber.isEmpty()) {
                EmployeeTransfer selectedEmployeeTransfer = employeeTransfers.stream()
                        .filter(et -> selectedEmployeeTransferOrderNumber.equals(et.getOrderNumber()))
                        .findFirst()
                        .orElse(null);

                if (selectedEmployeeTransfer != null) {
                    showUpdateEmployeeTransferDialog(selectedEmployeeTransfer);
                } else {
                    resultPanel.setResult("Employee Transfer not found for updating");
                }
            } else {
                resultPanel.setResult("No Employee Transfer selected for updating");
            }
        }
        showAllEmployeeTransfers();
    }

    private void deleteEmployeeTransfer() {
        List<EmployeeTransfer> employeeTransfers = employeeTransferDAO.getAllEntities();

        ComboBox<String> employeeTransferComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        employeeTransfers.stream()
                                .map(EmployeeTransfer::getOrderNumber)
                                .toList()
                )
        );
        employeeTransferComboBox.setPromptText("Select an Employee Transfer");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Employee Transfer");
        dialog.setHeaderText("Select an Employee Transfer to delete:");

        GridPane grid = new GridPane();
        grid.add(employeeTransferComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedEmployeeTransferOrderNumber = employeeTransferComboBox.getValue();

            if (selectedEmployeeTransferOrderNumber != null && !selectedEmployeeTransferOrderNumber.isEmpty()) {
                EmployeeTransfer selectedEmployeeTransfer = employeeTransfers.stream()
                        .filter(et -> selectedEmployeeTransferOrderNumber.equals(et.getOrderNumber()))
                        .findFirst()
                        .orElse(null);

                if (selectedEmployeeTransfer != null) {
                    employeeTransferDAO.deleteEntity(selectedEmployeeTransfer);
                    resultPanel.setResult("Employee Transfer deleted successfully");
                } else {
                    resultPanel.setResult("Employee Transfer not found for deletion");
                }
            } else {
                resultPanel.setResult("No Employee Transfer selected for deletion");
            }
        }
        showAllEmployeeTransfers();
    }

    private void addEmployeeTransfer() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Employee Transfer");
        dialog.setHeaderText(null);

        TextField oldPositionField = new TextField();
        TextField transferReasonField = new TextField();
        TextField orderNumberField = new TextField();
        DatePicker orderDatePicker = new DatePicker();
        orderDatePicker.setValue(LocalDate.now());

        // Add a ComboBox for selecting the employee
        List<Employee> employees = new EmployeeDAO().getAllEntities();
        ComboBox<String> employeeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        employees.stream()
                                .map(Employee::getFullName)
                                .toList()
                )
        );
        employeeComboBox.setPromptText("Select an employee");

        GridPane grid = new GridPane();
        grid.add(new Label("Old Position:"), 0, 0);
        grid.add(oldPositionField, 1, 0);
        grid.add(new Label("Transfer Reason:"), 0, 1);
        grid.add(transferReasonField, 1, 1);
        grid.add(new Label("Order Number:"), 0, 2);
        grid.add(orderNumberField, 1, 2);
        grid.add(new Label("Order Date:"), 0, 3);
        grid.add(orderDatePicker, 1, 3);
        grid.add(new Label("Employee:"), 0, 4);
        grid.add(employeeComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String oldPosition = oldPositionField.getText();
            String transferReason = transferReasonField.getText();
            String orderNumber = orderNumberField.getText();
            LocalDate orderDate = orderDatePicker.getValue();
            Timestamp orderTimestamp = Timestamp.valueOf(orderDate.atStartOfDay());
            String employeeFullName = employeeComboBox.getValue();

            Employee employee = employees.stream()
                    .filter(emp -> employeeFullName.equals(emp.getFullName()))
                    .findFirst()
                    .orElse(null);

            EmployeeTransfer newEmployeeTransfer = new EmployeeTransfer();
            newEmployeeTransfer.setOldPosition(oldPosition);
            newEmployeeTransfer.setTransferReason(transferReason);
            newEmployeeTransfer.setOrderNumber(orderNumber);
            newEmployeeTransfer.setOrderDate(orderTimestamp);
            newEmployeeTransfer.setEmployee(employee);

            employeeTransferDAO.addEntity(newEmployeeTransfer);

            resultPanel.setResult("Employee Transfer added successfully");
        }
        showAllEmployeeTransfers();
    }


    private TableView<EmployeeTransfer> createEmployeeTransferTable(ObservableList<EmployeeTransfer> employeeTransfers) {
        TableView<EmployeeTransfer> employeeTransferTable = new TableView<>();
        setupTableColumns(employeeTransferTable);

        employeeTransferTable.setItems(employeeTransfers);
        return employeeTransferTable;
    }

    private void setupTableColumns(TableView<EmployeeTransfer> employeeTransferTable) {
        TableColumn<EmployeeTransfer, String> employeeColumn = new TableColumn<>("Employee");
        employeeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployee().getFullName()));
        employeeColumn.setMinWidth(150);

        TableColumn<EmployeeTransfer, String> oldPositionColumn = new TableColumn<>("Old Position");
        oldPositionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOldPosition()));
        oldPositionColumn.setMinWidth(150);

        TableColumn<EmployeeTransfer, String> transferReasonColumn = new TableColumn<>("Transfer Reason");
        transferReasonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransferReason()));
        transferReasonColumn.setMinWidth(150);

        TableColumn<EmployeeTransfer, String> orderNumberColumn = new TableColumn<>("Order Number");
        orderNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrderNumber()));
        orderNumberColumn.setMinWidth(150);

        TableColumn<EmployeeTransfer, Timestamp> orderDateColumn = new TableColumn<>("Order Date");
        orderDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOrderDate()));
        orderDateColumn.setMinWidth(150);

        employeeTransferTable.setMinWidth(1000);
        employeeTransferTable.getColumns().addAll(employeeColumn, oldPositionColumn, transferReasonColumn, orderNumberColumn, orderDateColumn);
    }

    private void showUpdateEmployeeTransferDialog(EmployeeTransfer selectedEmployeeTransfer) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Employee Transfer");
        dialog.setHeaderText("Update details for Employee Transfer Order Number: " + selectedEmployeeTransfer.getOrderNumber());

        TextField oldPositionField = new TextField(selectedEmployeeTransfer.getOldPosition());
        TextField transferReasonField = new TextField(selectedEmployeeTransfer.getTransferReason());
        TextField orderNumberField = new TextField(selectedEmployeeTransfer.getOrderNumber());

        DatePicker orderDatePicker = new DatePicker();
        orderDatePicker.setValue(selectedEmployeeTransfer.getOrderDate().toLocalDateTime().toLocalDate());

        GridPane grid = new GridPane();
        grid.add(new Label("Old Position:"), 0, 0);
        grid.add(oldPositionField, 1, 0);
        grid.add(new Label("Transfer Reason:"), 0, 1);
        grid.add(transferReasonField, 1, 1);
        grid.add(new Label("Order Number:"), 0, 2);
        grid.add(orderNumberField, 1, 2);
        grid.add(new Label("Order Date:"), 0, 3);
        grid.add(orderDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newOldPosition = oldPositionField.getText();
            String newTransferReason = transferReasonField.getText();
            String newOrderNumber = orderNumberField.getText();
            LocalDateTime newOrderDate = orderDatePicker.getValue().atStartOfDay();

            selectedEmployeeTransfer.setOldPosition(newOldPosition);
            selectedEmployeeTransfer.setTransferReason(newTransferReason);
            selectedEmployeeTransfer.setOrderNumber(newOrderNumber);
            selectedEmployeeTransfer.setOrderDate(Timestamp.valueOf(newOrderDate));

            employeeTransferDAO.updateEntity(selectedEmployeeTransfer);

            resultPanel.setResult("Employee Transfer updated successfully");
        }
    }

}
