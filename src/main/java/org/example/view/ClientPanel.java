package org.example.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.example.controller.ResultPanel;
import org.example.dao.ClientDAO;
import org.example.model.Client;

import javax.security.auth.callback.Callback;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientPanel extends VBox {

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

        Button showAllButton = createButton("Show All");
        showAllButton.setOnAction(e -> showAllClients());

        Button updateButton = createButton("Update");
        updateButton.setOnAction(e -> updateClient());

        Button deleteButton = createButton("Delete");
        deleteButton.setOnAction(e -> deleteClient());

        Button addButton = createButton("Add");
        addButton.setOnAction(e -> addClient());

        Button getByOrderDateButton = createButton("Get by Order Date");
        getByOrderDateButton.setOnAction(e -> getClientByOrderDate());


        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton, getByOrderDateButton);

        getChildren().addAll(buttonsRow);
    }

    private Button createButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void showAllClients() {
        List<Client> clients = clientDAO.getAllEntities();
        ObservableList<Client> observableClients = FXCollections.observableArrayList(clients);
        resultPanel.showTable(createClientTable(observableClients));
    }

    private void addClient() {
        // Создание диалогового окна для ввода данных клиента
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Client");
        dialog.setHeaderText(null);

        // Создание полей ввода
        TextField fullNameField = new TextField();
        TextField phoneField = new TextField();
        DatePicker orderDatePicker = new DatePicker();
        orderDatePicker.setValue(LocalDate.now()); // Установка текущей даты по умолчанию

        // Создание сетки для размещения полей ввода
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
    }

    private void updateClient() {
        // Логика для обновления клиента
        resultPanel.setResult("Update client button is clicked");
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
                clients = clientDAO.getClientByOrderDate(Timestamp.valueOf(LocalDate.of(900, 1, 1).atStartOfDay()),selectedTimestamp);
            } else {
                clients = clientDAO.getClientByOrderDate(selectedTimestamp,Timestamp.valueOf(LocalDate.of(2300, 1, 1).atStartOfDay()));
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
        TableColumn<Client, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        idColumn.setMinWidth(50); // Минимальная ширина столбца

        TableColumn<Client, String> fullNameColumn = new TableColumn<>("Full Name");
        fullNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        fullNameColumn.setMinWidth(150); // Минимальная ширина столбца

        TableColumn<Client, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        phoneColumn.setMinWidth(100); // Минимальная ширина столбца

        TableColumn<Client, String> orderDateColumn = new TableColumn<>("Order Date");
        orderDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrderDate().toString()));
        orderDateColumn.setMinWidth(150); // Минимальная ширина столбца

        clientTable.getColumns().addAll(idColumn, fullNameColumn, phoneColumn, orderDateColumn);

        clientTable.setMinWidth(1000); // Минимальная ширина таблицы
        clientTable.setMaxWidth(Double.MAX_VALUE); // Максимальная ширина таблицы
    }

}
