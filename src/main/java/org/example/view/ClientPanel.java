package org.example.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controller.ResultPanel;
import org.example.dao.ClientDAO;
import org.example.model.Client;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClientPanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private ClientDAO clientDAO;
    private ComboBox<Client> clientComboBox;

    public ClientPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.clientDAO = new ClientDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        showAllClients();

        Button showAllButton = Panel.createButton("Show All");
        showAllButton.setOnAction(e -> showAllClients());

        Button updateButton = Panel.createButton("Update");
        updateButton.setOnAction(e -> updateClient());

        Button deleteButton = Panel.createButton("Delete");
        deleteButton.setOnAction(e -> deleteClient());

        Button addButton = Panel.createButton("Add");
        addButton.setOnAction(e -> addClient());

        Button getByOrderDateButton = Panel.createButton("Get by Order Date");
        getByOrderDateButton.setOnAction(e -> getClientByOrderDate());


        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton, getByOrderDateButton);

        getChildren().addAll(buttonsRow);
    }



    private void showAllClients() {
        List<Client> clients = clientDAO.getAllEntities();
        ObservableList<Client> observableClients = FXCollections.observableArrayList(clients);
        resultPanel.showTable(createClientTable(observableClients));
    }

    private void addClient() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Client");
        dialog.setHeaderText(null);

        TextField fullNameField = new TextField();
        TextField phoneField = new TextField();
        DatePicker orderDatePicker = new DatePicker();
        orderDatePicker.setValue(LocalDate.now());

        GridPane grid = new GridPane();
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Order Date:"), 0, 2);
        grid.add(orderDatePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String fullName = fullNameField.getText();
            String phone = phoneField.getText();
            LocalDate orderDate = orderDatePicker.getValue();
            Timestamp orderTimestamp = Timestamp.valueOf(orderDate.atStartOfDay());
            Client newClient = new Client();
            newClient.setFullName(fullName);
            newClient.setPhone(phone);
            newClient.setOrderDate(orderTimestamp);

            clientDAO.addEntity(newClient);

            resultPanel.setResult("Client added successfully");
        }
        showAllClients();
    }

    private void updateClient() {
        List<Client> clients = clientDAO.getAllEntities();

        ComboBox<String> clientComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        clients.stream()
                                .map(Client::getFullName)
                                .toList()
                )
        );
        clientComboBox.setPromptText("Select a client");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Client");
        dialog.setHeaderText("Select a client to update:");

        GridPane grid = new GridPane();
        grid.add(clientComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedClientName = clientComboBox.getValue();

            if (selectedClientName != null && !selectedClientName.isEmpty()) {
                Client selectedClient = clients.stream()
                        .filter(client -> selectedClientName.equals(client.getFullName()))
                        .findFirst()
                        .orElse(null);

                if (selectedClient != null) {
                    showUpdateClientDialog(selectedClient);
                } else {
                    resultPanel.setResult("Client not found for updating");
                }
            } else {
                resultPanel.setResult("No client selected for updating");
            }
        }
        showAllClients();
    }

    private void showUpdateClientDialog(Client client) {
        Dialog<Void> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Client");
        updateDialog.setHeaderText("Update client information:");

        TextField fullNameField = new TextField(client.getFullName());
        TextField phoneField = new TextField(client.getPhone());

        DatePicker orderDatePicker = new DatePicker();
        orderDatePicker.setValue(client.getOrderDate().toLocalDateTime().toLocalDate());

        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        updateDialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Order Date:"), 0, 2);
        grid.add(orderDatePicker, 1, 2);
        updateDialog.getDialogPane().setContent(grid);

        updateDialog.setResultConverter(buttonType -> {
            if (buttonType == updateButton) {
                String newFullName = fullNameField.getText();
                String newPhone = phoneField.getText();
                LocalDate newOrderDate = orderDatePicker.getValue();

                client.setFullName(newFullName);
                client.setPhone(newPhone);
                client.setOrderDate(Timestamp.valueOf(newOrderDate.atStartOfDay()));

                clientDAO.updateEntity(client);

                resultPanel.setResult("Client updated successfully");
            }
            return null;
        });

        // Отображение диалогового окна
        updateDialog.showAndWait();
    }

    private void deleteClient() {
        List<Client> clients = clientDAO.getAllEntities();

        ComboBox<String> clientComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        clients.stream()
                                .map(Client::getFullName)
                                .toList()
                )
        );
        clientComboBox.setPromptText("Select a client");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Client");
        dialog.setHeaderText("Select a client to delete:");

        GridPane grid = new GridPane();
        grid.add(clientComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedClientName = clientComboBox.getValue();

            if (selectedClientName != null && !selectedClientName.isEmpty()) {
                Client selectedClient = clients.stream()
                        .filter(client -> selectedClientName.equals(client.getFullName()))
                        .findFirst()
                        .orElse(null);

                if (selectedClient != null) {
                    clientDAO.deleteEntity(selectedClient.getId());

                    resultPanel.setResult("Client deleted successfully");
                } else {
                    resultPanel.setResult("Client not found for deletion");
                }
            } else {
                resultPanel.setResult("No client selected for deletion");
            }
        }
        showAllClients();
    }

    private void getClientByOrderDate() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Clients by Order Date");
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

            List<Client> clients;
            if (isBefore) {
                clients = clientDAO.getClientByOrderDate(Timestamp.valueOf(LocalDate.of(900, 1, 1).atStartOfDay()), selectedTimestamp);
            } else {
                clients = clientDAO.getClientByOrderDate(selectedTimestamp, Timestamp.valueOf(LocalDate.of(2300, 1, 1).atStartOfDay()));
            }

            resultPanel.showTable(createClientTable(FXCollections.observableArrayList(clients)));
        }
    }

    private TableView<Client> createClientTable(ObservableList<Client> clients) {
        TableView<Client> clientTable = new TableView<>();
        setupTableColumns(clientTable);

        clientTable.setItems(clients);
        return clientTable;
    }

    private void setupTableColumns(TableView<Client> clientTable) {
        TableColumn<Client, String> fullNameColumn = new TableColumn<>("Full Name");
        fullNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        fullNameColumn.setMinWidth(150); // Минимальная ширина столбца

        TableColumn<Client, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        phoneColumn.setMinWidth(100); // Минимальная ширина столбца

        TableColumn<Client, String> orderDateColumn = new TableColumn<>("Order Date");
        orderDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrderDate().toString()));
        orderDateColumn.setMinWidth(150); // Минимальная ширина столбца

        clientTable.getColumns().addAll(fullNameColumn, phoneColumn, orderDateColumn);

        clientTable.setMinWidth(1000); // Минимальная ширина таблицы
        clientTable.setMaxWidth(Double.MAX_VALUE); // Максимальная ширина таблицы
    }
}
