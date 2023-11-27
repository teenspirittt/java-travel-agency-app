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
import org.example.dao.EmployeeDAO;
import org.example.dao.FlightDAO;
import org.example.dao.HotelDAO;
import org.example.dao.RouteDAO;
import org.example.model.Employee;
import org.example.model.Flight;
import org.example.model.Hotel;
import org.example.model.Route;

import java.util.List;
import java.util.Optional;

public class RoutePanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private RouteDAO routeDAO;
    private HotelDAO hotelDAO;
    private FlightDAO flightDAO;
    private EmployeeDAO employeeDAO;

    public RoutePanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.routeDAO = new RouteDAO();
        this.hotelDAO = new HotelDAO();
        this.flightDAO = new FlightDAO();
        this.employeeDAO = new EmployeeDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        showAllRoutes();
        setPadding(new Insets(10));

        Button showAllButton = Panel.createButton("Show All");
        showAllButton.setOnAction(e -> showAllRoutes());

        Button updateButton = Panel.createButton("Update");
        updateButton.setOnAction(e -> updateRoute());

        Button deleteButton = Panel.createButton("Delete");
        deleteButton.setOnAction(e -> deleteRoute());

        Button addButton = Panel.createButton("Add");
        addButton.setOnAction(e -> addRoute());

        Button countryButton = Panel.createButton("Get By Country");
        countryButton.setOnAction(e -> getByCountry());

        Button cityButton = Panel.createButton("Get By City");
        cityButton.setOnAction(e -> getByCity());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton, cityButton, countryButton);
        getChildren().addAll(buttonsRow);
    }

    private void getByCity() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Routes by City");
        dialog.setHeaderText(null);

        TextField cityField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("City:"), 0, 0);
        grid.add(cityField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedCity = cityField.getText();
            List<Route> routes = routeDAO.getRouteByCity(selectedCity);

            resultPanel.showTable(createRouteTable(FXCollections.observableArrayList(routes)));
        }
    }


    private void getByCountry() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Routes by Country");
        dialog.setHeaderText(null);

        TextField countryField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Country:"), 0, 0);
        grid.add(countryField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedCountry = countryField.getText();
            List<Route> routes = routeDAO.getRouteByCountry(selectedCountry);

            resultPanel.showTable(createRouteTable(FXCollections.observableArrayList(routes)));
        }
    }


    private void showAllRoutes() {
        List<Route> routes = routeDAO.getAllEntities();
        ObservableList<Route> observableRoutes = FXCollections.observableArrayList(routes);
        resultPanel.showTable(createRouteTable(observableRoutes));
    }

    private void updateRoute() {
        List<Route> routes = routeDAO.getAllEntities();

        ComboBox<String> routeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        routes.stream()
                                .map(Route::getName)
                                .toList()
                )
        );
        routeComboBox.setPromptText("Select a route");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Route");
        dialog.setHeaderText("Select a route to update:");

        GridPane grid = new GridPane();
        grid.add(routeComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedRouteName = routeComboBox.getValue();

            if (selectedRouteName != null && !selectedRouteName.isEmpty()) {
                Route selectedRoute = routes.stream()
                        .filter(route -> selectedRouteName.equals(route.getName()))
                        .findFirst()
                        .orElse(null);

                if (selectedRoute != null) {
                    showUpdateRouteDialog(selectedRoute);
                } else {
                    resultPanel.setResult("Route not found for updating");
                }
            } else {
                resultPanel.setResult("No route selected for updating");
            }
        }
        showAllRoutes();
    }

    private void showUpdateRouteDialog(Route selectedRoute) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Route");
        dialog.setHeaderText(null);

        TextField nameField = new TextField(selectedRoute.getName());
        TextField countryField = new TextField(selectedRoute.getCountry());
        TextField cityField = new TextField(selectedRoute.getCity());
        TextField durationField = new TextField(String.valueOf(selectedRoute.getDuration()));

        // Assuming you have ComboBoxes for associated entities like Hotel, Flight, Employee
        List<Hotel> hotels = hotelDAO.getAllEntities();
        ComboBox<String> hotelComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        hotels.stream()
                                .map(Hotel::getName)
                                .toList()
                )
        );
        hotelComboBox.setValue(selectedRoute.getHotel().getName());

        List<Flight> flights = flightDAO.getAllEntities();
        ComboBox<String> flightComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        flights.stream()
                                .map(Flight::getFlightNumber)
                                .toList()
                )
        );
        flightComboBox.setValue(selectedRoute.getFlight().getFlightNumber());

        List<Employee> employees = employeeDAO.getAllEntities();
        ComboBox<String> employeeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        employees.stream()
                                .map(Employee::getFullName)
                                .toList()
                )
        );
        employeeComboBox.setValue(selectedRoute.getEmployee().getFullName());

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Country:"), 0, 1);
        grid.add(countryField, 1, 1);
        grid.add(new Label("City:"), 0, 2);
        grid.add(cityField, 1, 2);
        grid.add(new Label("Duration:"), 0, 3);
        grid.add(durationField, 1, 3);
        grid.add(new Label("Hotel:"), 0, 4);
        grid.add(hotelComboBox, 1, 4);
        grid.add(new Label("Flight:"), 0, 5);
        grid.add(flightComboBox, 1, 5);
        grid.add(new Label("Employee:"), 0, 6);
        grid.add(employeeComboBox, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newName = nameField.getText();
            String newCountry = countryField.getText();
            String newCity = cityField.getText();
            int newDuration = Integer.parseInt(durationField.getText());

            // Update the selectedRoute with the new values
            selectedRoute.setName(newName);
            selectedRoute.setCountry(newCountry);
            selectedRoute.setCity(newCity);
            selectedRoute.setDuration(newDuration);

            // Update associated entities
            Hotel selectedHotel = hotels.stream()
                    .filter(hotel -> hotel.getName().equals(hotelComboBox.getValue()))
                    .findFirst()
                    .orElse(null);
            selectedRoute.setHotel(selectedHotel);

            Flight selectedFlight = flights.stream()
                    .filter(flight -> flight.getFlightNumber().equals(flightComboBox.getValue()))
                    .findFirst()
                    .orElse(null);
            selectedRoute.setFlight(selectedFlight);

            Employee selectedEmployee = employees.stream()
                    .filter(employee -> employee.getFullName().equals(employeeComboBox.getValue()))
                    .findFirst()
                    .orElse(null);
            selectedRoute.setEmployee(selectedEmployee);

            // Update the route in the database
            routeDAO.updateEntity(selectedRoute);

            resultPanel.setResult("Route updated successfully");
        }
        showAllRoutes();
    }


    private void deleteRoute() {
        List<Route> routes = routeDAO.getAllEntities();

        ComboBox<String> routeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        routes.stream()
                                .map(Route::getName)
                                .toList()
                )
        );
        routeComboBox.setPromptText("Select a route");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Route");
        dialog.setHeaderText("Select a route to delete:");

        GridPane grid = new GridPane();
        grid.add(routeComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedRouteName = routeComboBox.getValue();

            if (selectedRouteName != null && !selectedRouteName.isEmpty()) {
                Route selectedRoute = routes.stream()
                        .filter(route -> selectedRouteName.equals(route.getName()))
                        .findFirst()
                        .orElse(null);

                if (selectedRoute != null) {
                    routeDAO.deleteEntity(selectedRoute);

                    resultPanel.setResult("Route deleted successfully");
                } else {
                    resultPanel.setResult("Route not found for deletion");
                }
            } else {
                resultPanel.setResult("No route selected for deletion");
            }
        }
        showAllRoutes();
    }


    private void addRoute() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Route");
        dialog.setHeaderText(null);

        TextField nameField = new TextField();
        TextField countryField = new TextField();
        TextField cityField = new TextField();
        TextField durationField = new TextField();

        List<Hotel> hotels = hotelDAO.getAllEntities();
        ComboBox<String> hotelComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        hotels.stream()
                                .map(Hotel::getName)
                                .toList()
                )
        );

        List<Flight> flights = flightDAO.getAllEntities();
        ComboBox<String> flightComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        flights.stream()
                                .map(Flight::getFlightNumber)
                                .toList()
                )
        );

        List<Employee> employees = employeeDAO.getAllEntities();
        ComboBox<String> employeeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        employees.stream()
                                .map(Employee::getFullName)
                                .toList()
                )
        );

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Country:"), 0, 1);
        grid.add(countryField, 1, 1);
        grid.add(new Label("City:"), 0, 2);
        grid.add(cityField, 1, 2);
        grid.add(new Label("Duration:"), 0, 3);
        grid.add(durationField, 1, 3);
        grid.add(new Label("Hotel:"), 0, 4);
        grid.add(hotelComboBox, 1, 4);
        grid.add(new Label("Flight:"), 0, 5);
        grid.add(flightComboBox, 1, 5);
        grid.add(new Label("Employee:"), 0, 6);
        grid.add(employeeComboBox, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText();
            String country = countryField.getText();
            String city = cityField.getText();
            Integer duration = Integer.parseInt(durationField.getText());
            String selectedHotelName = hotelComboBox.getValue();
            String selectedFlightNumber = flightComboBox.getValue();
            String selectedEmployeeName = employeeComboBox.getValue();

            Hotel selectedHotel = hotels.stream()
                    .filter(hotel -> hotel.getName().equals(selectedHotelName))
                    .findFirst()
                    .orElse(null);

            Flight selectedFlight = flights.stream()
                    .filter(flight -> flight.getFlightNumber().equals(selectedFlightNumber))
                    .findFirst()
                    .orElse(null);

            Employee selectedEmployee = employees.stream()
                    .filter(employee -> employee.getFullName().equals(selectedEmployeeName))
                    .findFirst()
                    .orElse(null);

            if (selectedHotel != null && selectedFlight != null && selectedEmployee != null) {
                Route newRoute = new Route();
                newRoute.setName(name);
                newRoute.setCountry(country);
                newRoute.setCity(city);
                newRoute.setDuration(duration);
                newRoute.setHotel(selectedHotel);
                newRoute.setFlight(selectedFlight);
                newRoute.setEmployee(selectedEmployee);

                routeDAO.addEntity(newRoute);

                resultPanel.setResult("Route added successfully");
            } else {
                resultPanel.setResult("Selected hotel, flight, or employee not found");
            }
        }
        showAllRoutes();
    }


    private TableView<Route> createRouteTable(ObservableList<Route> routes) {
        TableView<Route> routeTable = new TableView<>();
        setupRouteTableColumns(routeTable);

        routeTable.setItems(routes);
        return routeTable;
    }

    private void setupRouteTableColumns(TableView<Route> routeTable) {
        TableColumn<Route, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setMinWidth(150);

        TableColumn<Route, String> countryColumn = new TableColumn<>("Country");
        countryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCountry()));
        countryColumn.setMinWidth(150);

        TableColumn<Route, String> cityColumn = new TableColumn<>("City");
        cityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCity()));
        cityColumn.setMinWidth(150);

        TableColumn<Route, Integer> durationColumn = new TableColumn<>("Duration");
        durationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDuration()));
        durationColumn.setMinWidth(100);

        TableColumn<Route, String> hotelColumn = new TableColumn<>("Hotel");
        hotelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHotel().getName()));
        // Assuming Hotel has a meaningful toString() method, otherwise, you can customize the cell value factory
        hotelColumn.setMinWidth(150);

        TableColumn<Route, String> flightColumn = new TableColumn<>("Flight");
        flightColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlight().getFlightNumber()));
        // Assuming Flight has a meaningful toString() method, otherwise, you can customize the cell value factory
        flightColumn.setMinWidth(150);

        TableColumn<Route, String> employeeColumn = new TableColumn<>("Employee");
        employeeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployee().getFullName()));
        // Assuming Employee has a meaningful toString() method, otherwise, you can customize the cell value factory
        employeeColumn.setMinWidth(150);

        routeTable.getColumns().addAll(nameColumn, countryColumn, cityColumn, durationColumn, hotelColumn, flightColumn, employeeColumn);

        routeTable.setMinWidth(1000);
        routeTable.setMaxWidth(Double.MAX_VALUE);
    }

}
