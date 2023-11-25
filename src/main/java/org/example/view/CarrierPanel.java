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
import org.example.dao.CarrierDAO;
import org.example.model.Carrier;

import java.util.List;
import java.util.Optional;

public class CarrierPanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private CarrierDAO carrierDAO;
    private ComboBox<Carrier> carrierComboBox;

    public CarrierPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.carrierDAO = new CarrierDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        showAllCarriers();

        Button showAllButton = Panel.createButton("Show All");
        showAllButton.setOnAction(e -> showAllCarriers());

        Button updateButton = Panel.createButton("Update");
        updateButton.setOnAction(e -> updateCarrier());

        Button deleteButton = Panel.createButton("Delete");
        deleteButton.setOnAction(e -> deleteCarrier());

        Button addButton = Panel.createButton("Add");
        addButton.setOnAction(e -> addCarrier());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton);

        getChildren().addAll(buttonsRow);
    }


    private void showAllCarriers() {
        List<Carrier> carriers = carrierDAO.getAllEntities();
        ObservableList<Carrier> observableCarriers = FXCollections.observableArrayList(carriers);
        resultPanel.showTable(createCarrierTable(observableCarriers));
    }

    private void updateCarrier() {
        List<Carrier> carriers = carrierDAO.getAllEntities();

        ComboBox<String> carrierComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        carriers.stream()
                                .map(Carrier::getName)
                                .toList()
                )
        );
        carrierComboBox.setPromptText("Select a carrier");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Carrier");
        dialog.setHeaderText("Select a carrier to update:");

        GridPane grid = new GridPane();
        grid.add(carrierComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedCarrierName = carrierComboBox.getValue();

            if (selectedCarrierName != null && !selectedCarrierName.isEmpty()) {
                Carrier selectedCarrier = carriers.stream()
                        .filter(carrier -> selectedCarrierName.equals(carrier.getName()))
                        .findFirst()
                        .orElse(null);

                if (selectedCarrier != null) {
                    showUpdateCarrierDialog(selectedCarrier);
                } else {
                    resultPanel.setResult("Carrier not found for updating");
                }
            } else {
                resultPanel.setResult("No carrier selected for updating");
            }
        }
        showAllCarriers();
    }

    private void deleteCarrier() {
        List<Carrier> carriers = carrierDAO.getAllEntities();

        ComboBox<String> carrierComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        FXCollections.observableArrayList(
                                carriers.stream()
                                        .map(Carrier::getName)
                                        .toList()))
        );
        carrierComboBox.setPromptText("Select a carrier");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Carrier");
        dialog.setHeaderText("Select a carrier to delete:");

        GridPane grid = new GridPane();
        grid.add(carrierComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedCarrierName = carrierComboBox.getValue();

            if (selectedCarrierName != null  && !selectedCarrierName.isEmpty()) {
                Carrier selectedCarrier = carriers.stream()
                                .filter(carrier -> selectedCarrierName.equals(carrier.getName()))
                                        .findFirst().orElse(null);
                assert selectedCarrier != null;
                carrierDAO.deleteEntity(selectedCarrier.getId());

                resultPanel.setResult("Carrier deleted successfully");
            } else {
                resultPanel.setResult("Carrier not found for deletion");
            }
        }
        showAllCarriers();
    }

    private void addCarrier() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Carrier");
        dialog.setHeaderText(null);

        TextField nameField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText();

            Carrier newCarrier = new Carrier();
            newCarrier.setName(name);

            carrierDAO.addEntity(newCarrier);

            resultPanel.setResult("Carrier added successfully");
        }
        showAllCarriers();
    }

    private void showUpdateCarrierDialog(Carrier selectedCarrier) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Carrier");

        TextField nameField = new TextField(selectedCarrier.getName());


        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newName = nameField.getText();


            selectedCarrier.setName(newName);

            carrierDAO.updateEntity(selectedCarrier);

            resultPanel.setResult("Carrier updated successfully");
        }
    }

    private TableView<Carrier> createCarrierTable(ObservableList<Carrier> carriers) {
        TableView<Carrier> carrierTable = new TableView<>();
        setupTableColumns(carrierTable);

        carrierTable.setItems(carriers);
        return carrierTable;
    }

    private void setupTableColumns(TableView<Carrier> carrierTable) {

        TableColumn<Carrier, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setMinWidth(150);


        carrierTable.getColumns().addAll(nameColumn);

        carrierTable.setMinWidth(1000);
        carrierTable.setMaxWidth(Double.MAX_VALUE);
    }
}
