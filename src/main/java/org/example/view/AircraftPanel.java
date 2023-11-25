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
import org.example.dao.AircraftDAO;
import org.example.dao.CarrierDAO;
import org.example.model.Aircraft;
import org.example.model.Carrier;

import java.util.List;
import java.util.Optional;

public class AircraftPanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private AircraftDAO aircraftDAO;
    private ComboBox<Aircraft> aircraftComboBox;

    public AircraftPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.aircraftDAO = new AircraftDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        showAllAircraft();

        Button showAllButton = Panel.createButton("Show All");
        showAllButton.setOnAction(e -> showAllAircraft());

        Button updateButton = Panel.createButton("Update");
        updateButton.setOnAction(e -> updateAircraft());

        Button deleteButton = Panel.createButton("Delete");
        deleteButton.setOnAction(e -> deleteAircraft());

        Button addButton = Panel.createButton("Add");
        addButton.setOnAction(e -> addAircraft());

        Button getByCapacity = Panel.createButton("Get by capacity");
        getByCapacity.setOnAction(e -> getAircraftByCapacity());

        Button getByManufacturer = Panel.createButton("Get by manufacturer");
        getByManufacturer.setOnAction(e -> getAircraftByManufacturer());

        Button getByType = Panel.createButton("Get by model");
        getByType.setOnAction(e -> getAircraftByType());

        Button getByCarrier = Panel.createButton("Get by carrier");
        getByCarrier.setOnAction(e -> getAircraftByCarrier());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton,
                getByCarrier, getByManufacturer, getByType, getByCapacity);

        getChildren().addAll(buttonsRow);
    }

    private void getAircraftByCarrier() {
        List<Carrier> carriers = new CarrierDAO().getAllEntities();

        ComboBox<String> carrierComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        carriers.stream()
                                .map(Carrier::getName)
                                .toList()
                )
        );
        carrierComboBox.setPromptText("Select a carrier");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Aircraft by Carrier");
        dialog.setHeaderText("Select a carrier:");

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
                    List<Aircraft> aircraftList = aircraftDAO.getAircraftByCarrier(selectedCarrier.getId());
                    resultPanel.showTable(createAircraftTable(FXCollections.observableArrayList(aircraftList)));
                } else {
                    resultPanel.setResult("Carrier not found");
                }
            } else {
                resultPanel.setResult("No carrier selected");
            }
        }
    }


    private void getAircraftByType() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Aircraft by Type");
        dialog.setHeaderText(null);

        TextField typeField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Aircraft Type:"), 0, 0);
        grid.add(typeField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String aircraftType = typeField.getText();

            List<Aircraft> aircraftList = aircraftDAO.getAircraftByType(aircraftType);

            resultPanel.showTable(createAircraftTable(FXCollections.observableArrayList(aircraftList)));
        }
    }


    private void getAircraftByManufacturer() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Aircraft by Manufacturer");
        dialog.setHeaderText(null);

        TextField manufacturerField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Manufacturer:"), 0, 0);
        grid.add(manufacturerField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String manufacturer = manufacturerField.getText();

            List<Aircraft> aircraftList = aircraftDAO.getAircraftByManufacturer(manufacturer);

            resultPanel.showTable(createAircraftTable(FXCollections.observableArrayList(aircraftList)));
        }
    }

    private void getAircraftByCapacity() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Aircraft by Capacity");
        dialog.setHeaderText(null);

        TextField minCapacityField = new TextField();
        TextField maxCapacityField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Min Capacity:"), 0, 0);
        grid.add(minCapacityField, 1, 0);
        grid.add(new Label("Max Capacity:"), 0, 1);
        grid.add(maxCapacityField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int minCapacity = Integer.parseInt(minCapacityField.getText());
                int maxCapacity = Integer.parseInt(maxCapacityField.getText());

                List<Aircraft> aircraftList = aircraftDAO.getAircraftByCapacity(minCapacity, maxCapacity);

                resultPanel.showTable(createAircraftTable(FXCollections.observableArrayList(aircraftList)));
            } catch (NumberFormatException e) {
                resultPanel.setResult("Invalid capacity values. Please enter valid numbers.");
            }
        }
    }

    private void showAllAircraft() {
        List<Aircraft> aircraftList = aircraftDAO.getAllEntities();
        ObservableList<Aircraft> observableAircraft = FXCollections.observableArrayList(aircraftList);
        resultPanel.showTable(createAircraftTable(observableAircraft));
    }

    private void updateAircraft() {
        List<Aircraft> aircraftList = aircraftDAO.getAllEntities();

        ComboBox<String> aircraftComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        aircraftList.stream()
                                .map(Aircraft::getAircraftType)
                                .toList()
                )
        );
        aircraftComboBox.setPromptText("Select an aircraft");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Aircraft");
        dialog.setHeaderText("Select an aircraft to update:");

        GridPane grid = new GridPane();
        grid.add(aircraftComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedAircraftType = aircraftComboBox.getValue();

            if (selectedAircraftType != null && !selectedAircraftType.isEmpty()) {
                Aircraft selectedAircraft = aircraftList.stream()
                        .filter(aircraft -> selectedAircraftType.equals(aircraft.getAircraftType()))
                        .findFirst()
                        .orElse(null);

                if (selectedAircraft != null) {
                    showUpdateAircraftDialog(selectedAircraft);
                } else {
                    resultPanel.setResult("Aircraft not found for updating");
                }
            } else {
                resultPanel.setResult("No aircraft selected for updating");
            }
        }
        showAllAircraft();
    }

    private void showUpdateAircraftDialog(Aircraft selectedAircraft) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Aircraft");
        dialog.setHeaderText("Update details for aircraft ID: " + selectedAircraft.getId());

        TextField typeField = new TextField(selectedAircraft.getAircraftType());
        TextField manufacturerField = new TextField(selectedAircraft.getManufacturer());
        TextField capacityField = new TextField(String.valueOf(selectedAircraft.getCapacity()));

        // Add a ComboBox for selecting the carrier
        List<Carrier> carriers = new CarrierDAO().getAllEntities();
        ComboBox<String> carrierComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        carriers.stream()
                                .map(Carrier::getName)
                                .toList()
                )
        );
        carrierComboBox.setValue(selectedAircraft.getCarrier().getName());

        GridPane grid = new GridPane();
        grid.add(new Label("Aircraft Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Manufacturer:"), 0, 1);
        grid.add(manufacturerField, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Carrier:"), 0, 3);
        grid.add(carrierComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newType = typeField.getText();
            String newManufacturer = manufacturerField.getText();
            int newCapacity = Integer.parseInt(capacityField.getText());
            String newCarrierName = carrierComboBox.getValue();

            // Find the Carrier object corresponding to the selected carrier name
            Carrier newCarrier = carriers.stream()
                    .filter(carrier -> newCarrierName.equals(carrier.getName()))
                    .findFirst()
                    .orElse(null);

            selectedAircraft.setAircraftType(newType);
            selectedAircraft.setManufacturer(newManufacturer);
            selectedAircraft.setCapacity(newCapacity);
            selectedAircraft.setCarrier(newCarrier);

            aircraftDAO.updateEntity(selectedAircraft);

            resultPanel.setResult("Aircraft updated successfully");
        }
    }

    private void deleteAircraft() {
        List<Aircraft> aircraftList = aircraftDAO.getAllEntities();

        ComboBox<String> aircraftComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        aircraftList.stream()
                                .map(Aircraft::getAircraftType)
                                .toList()
                )
        );
        aircraftComboBox.setPromptText("Select an aircraft");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Aircraft");
        dialog.setHeaderText("Select an aircraft to delete:");

        GridPane grid = new GridPane();
        grid.add(aircraftComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedAircraftType = aircraftComboBox.getValue();

            if (selectedAircraftType != null && !selectedAircraftType.isEmpty()) {
                Aircraft selectedAircraft = aircraftList.stream()
                        .filter(aircraft -> selectedAircraftType.equals(aircraft.getAircraftType()))
                        .findFirst()
                        .orElse(null);

                if (selectedAircraft != null) {
                    aircraftDAO.deleteEntity(selectedAircraft.getId());

                    resultPanel.setResult("Aircraft deleted successfully");
                } else {
                    resultPanel.setResult("Aircraft not found for deletion");
                }
            } else {
                resultPanel.setResult("No aircraft selected for deletion");
            }
        }
        showAllAircraft();
    }

    private void addAircraft() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Aircraft");
        dialog.setHeaderText(null);

        TextField typeField = new TextField();
        TextField manufacturerField = new TextField();
        TextField capacityField = new TextField();

        // Add a ComboBox for selecting the carrier
        List<Carrier> carriers = new CarrierDAO().getAllEntities();
        ComboBox<String> carrierComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        carriers.stream()
                                .map(Carrier::getName)
                                .toList()
                )
        );
        carrierComboBox.setPromptText("Select a carrier");

        GridPane grid = new GridPane();
        grid.add(new Label("Aircraft Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Manufacturer:"), 0, 1);
        grid.add(manufacturerField, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Carrier:"), 0, 3);
        grid.add(carrierComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newType = typeField.getText();
            String newManufacturer = manufacturerField.getText();
            int newCapacity = Integer.parseInt(capacityField.getText());
            String newCarrierName = carrierComboBox.getValue();

            // Find the Carrier object corresponding to the selected carrier name
            Carrier newCarrier = carriers.stream()
                    .filter(carrier -> newCarrierName.equals(carrier.getName()))
                    .findFirst()
                    .orElse(null);

            Aircraft newAircraft = new Aircraft();
            newAircraft.setAircraftType(newType);
            newAircraft.setManufacturer(newManufacturer);
            newAircraft.setCapacity(newCapacity);
            newAircraft.setCarrier(newCarrier);

            aircraftDAO.addEntity(newAircraft);

            resultPanel.setResult("Aircraft added successfully");
        }
        showAllAircraft();
    }

    private TableView<Aircraft> createAircraftTable(ObservableList<Aircraft> aircraft) {
        TableView<Aircraft> aircraftTable = new TableView<>();
        setupAircraftTableColumns(aircraftTable);

        aircraftTable.setItems(aircraft);
        return aircraftTable;
    }

    private void setupAircraftTableColumns(TableView<Aircraft> aircraftTable) {
        TableColumn<Aircraft, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        idColumn.setMinWidth(50);

        TableColumn<Aircraft, String> typeColumn = new TableColumn<>("Aircraft Type");
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAircraftType()));
        typeColumn.setMinWidth(150);

        TableColumn<Aircraft, String> manufacturerColumn = new TableColumn<>("Manufacturer");
        manufacturerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getManufacturer()));
        manufacturerColumn.setMinWidth(150);

        TableColumn<Aircraft, Integer> capacityColumn = new TableColumn<>("Capacity");
        capacityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCapacity()));
        capacityColumn.setMinWidth(100);

        TableColumn<Aircraft, String> carrierColumn = new TableColumn<>("Carrier");
        carrierColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCarrier().getName()));
        carrierColumn.setMinWidth(150);

        aircraftTable.getColumns().addAll(idColumn, typeColumn, manufacturerColumn, capacityColumn, carrierColumn);

        aircraftTable.setMinWidth(1000);
        aircraftTable.setMaxWidth(Double.MAX_VALUE);
    }
}
