package org.example.view;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import org.example.controller.ResultPanel;
import org.example.dao.AircraftDAO;
import org.example.dao.ClientDAO;
import org.example.dao.FlightDAO;
import org.example.model.Aircraft;
import org.example.model.Client;
import org.example.model.Flight;
import javafx.collections.ObservableList;

import java.sql.Timestamp;
import java.util.Optional;

public class FlightPanel extends VBox {

    private ResultPanel resultPanel;
    private FlightDAO flightDAO;
    private AircraftDAO aircraftDAO;

    public FlightPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.flightDAO = new FlightDAO();
        this.aircraftDAO = new AircraftDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        showAllFlights();
        setPadding(new Insets(10));

        Button showAllButton = createButton("Show All");
        showAllButton.setOnAction(e -> showAllFlights());

        Button updateButton = createButton("Update");
        updateButton.setOnAction(e -> updateFlight());

        Button deleteButton = createButton("Delete");
        deleteButton.setOnAction(e -> deleteFlight());

        Button addButton = createButton("Add");
        addButton.setOnAction(e -> addFlight());

        Button seatsButton = createButton("Get by Available Seats");
        seatsButton.setOnAction(e -> getBySeatsFlight());

        Button dateButton = createButton("Get by Departure Date");
        dateButton.setOnAction(e -> getByDateFlight());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton, dateButton, seatsButton);

        getChildren().addAll(buttonsRow);
    }

    private Button createButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void getByDateFlight() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Flights by Departure Date");
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
        grid.add(new Label("Departure Date:"), 0, 0);
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

            List<Flight> flights;
            if (isBefore) {
                flights = flightDAO.getFlightByDepartureDate(Timestamp.valueOf(LocalDate.of(900, 1, 1).atStartOfDay()), selectedTimestamp);
            } else {
                flights = flightDAO.getFlightByDepartureDate(selectedTimestamp, Timestamp.valueOf(LocalDate.of(2300, 1, 1).atStartOfDay()));
            }

            resultPanel.showTable(createFlightTable(FXCollections.observableArrayList(flights)));
        }
    }


    private void getBySeatsFlight() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Flights by Available Seats");
        dialog.setHeaderText(null);

        TextField minSeatsField = new TextField();
        TextField maxSeatsField = new TextField();

        Label minSeatsLabel = new Label("Min Available Seats:");
        Label maxSeatsLabel = new Label("Max Available Seats:");

        GridPane grid = new GridPane();
        grid.add(minSeatsLabel, 0, 0);
        grid.add(minSeatsField, 1, 0);
        grid.add(maxSeatsLabel, 0, 1);
        grid.add(maxSeatsField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int minSeats = Integer.parseInt(minSeatsField.getText());
                int maxSeats = Integer.parseInt(maxSeatsField.getText());

                List<Flight> flights = flightDAO.getFlightByAvailableSeats(minSeats, maxSeats);

                resultPanel.showTable(createFlightTable(FXCollections.observableArrayList(flights)));
            } catch (NumberFormatException e) {
                resultPanel.setResult("Please enter valid numerical values for seats.");
            }
        }
    }



    private void showAllFlights() {
        List<Flight> flights = flightDAO.getAllEntities();
        ObservableList<Flight> observableFlights = FXCollections.observableArrayList(flights);
        resultPanel.showTable(createFlightTable(observableFlights));
    }

    private TableView<Flight> createFlightTable(ObservableList<Flight> flights) {
        TableView<Flight> flightTable = new TableView<>();
        setupTableColumns(flightTable);

        flightTable.setItems(flights);
        return flightTable;
    }

    private void setupTableColumns(TableView<Flight> flightTable) {
        TableColumn<Flight, String> flightNumberColumn = new TableColumn<>("Flight Number");
        flightNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlightNumber()));
        flightNumberColumn.setMinWidth(150);

        TableColumn<Flight, Timestamp> departureDateColumn = new TableColumn<>("Departure Date");
        departureDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDepartureDate()));
        departureDateColumn.setMinWidth(150);
        TableColumn<Flight, String> aircraftColumn = new TableColumn<>("Aircraft");
        aircraftColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAircraft().getManufacturer() + " " +cellData.getValue().getAircraft().getAircraftType()));
        aircraftColumn.setMinWidth(150);

        TableColumn<Flight, String> flightClassColumn = new TableColumn<>("Class");
        flightClassColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlightClass()));
        flightClassColumn.setMinWidth(100);

        TableColumn<Flight, Integer> availableSeatsColumn = new TableColumn<>("Available Seats");
        availableSeatsColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAvailableSeats()));
        availableSeatsColumn.setMinWidth(100);

        flightTable.getColumns().addAll(flightNumberColumn, departureDateColumn, aircraftColumn, flightClassColumn, availableSeatsColumn);

        flightTable.setMinWidth(1000);
        flightTable.setMaxWidth(Double.MAX_VALUE);
    }

    private void updateFlight() {
        List<Flight> flights = flightDAO.getAllEntities();

        ComboBox<String> flightComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        flights.stream()
                                .map(Flight::getFlightNumber)
                                .toList()
                )
        );
        flightComboBox.setPromptText("Select a flight");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Flight");
        dialog.setHeaderText("Select a flight to update:");

        GridPane grid = new GridPane();
        grid.add(flightComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedFlightNumber = flightComboBox.getValue();

            if (selectedFlightNumber != null && !selectedFlightNumber.isEmpty()) {
                Flight selectedFlight = flights.stream()
                        .filter(flight -> selectedFlightNumber.equals(flight.getFlightNumber()))
                        .findFirst()
                        .orElse(null);

                if (selectedFlight != null) {
                    showUpdateFlightDialog(selectedFlight);
                } else {
                    resultPanel.setResult("Flight not found for updating");
                }
            } else {
                resultPanel.setResult("No flight selected for updating");
            }
        }
        showAllFlights();
    }

    private void showUpdateFlightDialog(Flight selectedFlight) {
        List<Aircraft> aircraftList = aircraftDAO.getAllEntities();

        ComboBox<String> aircraftComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        aircraftList.stream()
                                .map(Aircraft::getAircraftType)
                                .toList()
                )
        );
        aircraftComboBox.setValue(selectedFlight.getAircraft().getAircraftType());

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Flight");
        dialog.setHeaderText(null);

        TextField flightNumberField = new TextField(selectedFlight.getFlightNumber());
        DatePicker departureDatePicker = new DatePicker(selectedFlight.getDepartureDate().toLocalDateTime().toLocalDate());
        TextField flightClassField = new TextField(selectedFlight.getFlightClass());
        TextField availableSeatsField = new TextField(String.valueOf(selectedFlight.getAvailableSeats()));

        GridPane grid = new GridPane();
        grid.add(new Label("Flight Number:"), 0, 0);
        grid.add(flightNumberField, 1, 0);
        grid.add(new Label("Departure Date:"), 0, 1);
        grid.add(departureDatePicker, 1, 1);
        grid.add(new Label("Aircraft Type:"), 0, 2);
        grid.add(aircraftComboBox, 1, 2);
        grid.add(new Label("Class:"), 0, 3);
        grid.add(flightClassField, 1, 3);
        grid.add(new Label("Available Seats:"), 0, 4);
        grid.add(availableSeatsField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newFlightNumber = flightNumberField.getText();
            LocalDateTime newDepartureDate = departureDatePicker.getValue().atStartOfDay();
            String newAircraftType = aircraftComboBox.getValue();
            String newFlightClass = flightClassField.getText();
            int newAvailableSeats = Integer.parseInt(availableSeatsField.getText());

            Aircraft selectedAircraft = aircraftList.stream()
                    .filter(aircraft -> newAircraftType.equals(aircraft.getAircraftType()))
                    .findFirst()
                    .orElse(null);

            if (selectedAircraft != null) {
                selectedFlight.setFlightNumber(newFlightNumber);
                selectedFlight.setDepartureDate(Timestamp.valueOf(newDepartureDate));
                selectedFlight.setAircraft(selectedAircraft);
                selectedFlight.setFlightClass(newFlightClass);
                selectedFlight.setAvailableSeats(newAvailableSeats);

                flightDAO.updateEntity(selectedFlight);

                resultPanel.setResult("Flight updated successfully");
            } else {
                resultPanel.setResult("Aircraft not found for updating flight");
            }
        }
        showAllFlights();
    }



    private void deleteFlight() {
        List<Flight> flights = flightDAO.getAllEntities();

        ComboBox<String> flightComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        flights.stream()
                                .map(Flight::getFlightNumber)
                                .toList()
                )
        );
        flightComboBox.setPromptText("Select a flight");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Flight");
        dialog.setHeaderText("Select a flight to delete:");

        GridPane grid = new GridPane();
        grid.add(flightComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedFlightNumber = flightComboBox.getValue();

            if (selectedFlightNumber != null && !selectedFlightNumber.isEmpty()) {
                Flight selectedFlight = flights.stream()
                        .filter(flight -> selectedFlightNumber.equals(flight.getFlightNumber()))
                        .findFirst()
                        .orElse(null);

                if (selectedFlight != null) {
                    flightDAO.deleteEntity(selectedFlight.getId());

                    resultPanel.setResult("Flight deleted successfully");
                } else {
                    resultPanel.setResult("Flight not found for deletion");
                }
            } else {
                resultPanel.setResult("No flight selected for deletion");
            }
        }
        showAllFlights();
    }

    private void addFlight() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Flight");
        dialog.setHeaderText(null);

        TextField flightNumberField = new TextField();
        DatePicker departureDatePicker = new DatePicker();
        departureDatePicker.setValue(LocalDate.now());

        List<Aircraft> aircraftList = aircraftDAO.getAllEntities();
        ComboBox<String> aircraftComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        aircraftList.stream()
                                .map(Aircraft::getAircraftType)
                                .toList()
                )
        );

        TextField classField = new TextField();
        TextField availableSeatsField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Flight Number:"), 0, 0);
        grid.add(flightNumberField, 1, 0);
        grid.add(new Label("Departure Date:"), 0, 1);
        grid.add(departureDatePicker, 1, 1);
        grid.add(new Label("Aircraft Type:"), 0, 2);
        grid.add(aircraftComboBox, 1, 2);
        grid.add(new Label("Class:"), 0, 3);
        grid.add(classField, 1, 3);
        grid.add(new Label("Available Seats:"), 0, 4);
        grid.add(availableSeatsField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String flightNumber = flightNumberField.getText();
            LocalDate departureDate = departureDatePicker.getValue();
            Timestamp departureTimestamp = Timestamp.valueOf(departureDate.atStartOfDay());
            String selectedAircraftType = aircraftComboBox.getValue();

            Aircraft selectedAircraft = aircraftList.stream()
                    .filter(aircraft -> aircraft.getAircraftType().equals(selectedAircraftType))
                    .findFirst()
                    .orElse(null);

            if (selectedAircraft != null) {
                Flight newFlight = new Flight();
                newFlight.setFlightNumber(flightNumber);
                newFlight.setDepartureDate(departureTimestamp);
                newFlight.setAircraft(selectedAircraft);
                newFlight.setFlightClass(classField.getText());
                newFlight.setAvailableSeats(Integer.parseInt(availableSeatsField.getText()));

                flightDAO.addEntity(newFlight);

                resultPanel.setResult("Flight added successfully");
            } else {
                resultPanel.setResult("Selected aircraft type not found");
            }
        }
        showAllFlights();
    }


}
