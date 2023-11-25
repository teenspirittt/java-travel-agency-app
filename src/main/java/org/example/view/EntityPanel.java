package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.example.controller.ResultPanel;

public class EntityPanel extends VBox {

    private final ResultPanel resultPanel;
    private ClientPanel clientPanel;
    private FlightPanel flightPanel;
    private CarrierPanel carrierPanel;
    private AircraftPanel aircraftPanel;
    private EmployeePanel employeePanel;

    public EntityPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        setPadding(new Insets(10));

        Button clientButton = createButton("Client");
        Button hotelButton = createButton("Hotel");
        Button flightButton = createButton("Flight");
        Button aircraftButton = createButton("Aircraft");
        Button carrierButton = createButton("Carrier");
        Button routeButton = createButton("Route");
        Button employeeButton = createButton("Employee");
        Button transferButton = createButton("Transfer");
        Button clientRouteButton = createButton("Client Route");

        getChildren().addAll(clientButton, hotelButton, flightButton, aircraftButton,
                carrierButton, routeButton, employeeButton, transferButton, clientRouteButton);
    }

    private Button createButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> handleButtonClick(buttonText));
        return button;
    }

    private void handleButtonClick(String buttonText) {
        resultPanel.clearResult();

        switch (buttonText) {
            case "Client":
                showClientButtons();
                break;
            case "Hotel":
                // Логика для кнопки "Hotel"
                break;
            case "Flight":
                showFlightButtons();
                break;
            case "Carrier":
                showCarrierButtons();
                break;
            case "Aircraft":
                showAircraftButtons();
                break;
            case "Employee":
                showEmployeeButtons();
                break;
        }
    }

    private void showEmployeeButtons() {
        clearButtons();
        if (employeePanel == null) {
            employeePanel = new EmployeePanel(resultPanel);
            getChildren().add(employeePanel);
        }
    }

    private void showAircraftButtons() {
        clearButtons();
        if (aircraftPanel == null) {
            aircraftPanel = new AircraftPanel(resultPanel);
            getChildren().add(aircraftPanel);
        }
    }

    private void showClientButtons() {
        clearButtons();
        if (clientPanel == null) {
            clientPanel = new ClientPanel(resultPanel);
            getChildren().add(clientPanel);
        }
    }

    private void showFlightButtons() {
        clearButtons();
        if (flightPanel == null) {
            flightPanel = new FlightPanel(resultPanel);
            getChildren().add(flightPanel);
        }
    }

    private void showCarrierButtons() {
        clearButtons();
        if (carrierPanel == null) {
            carrierPanel = new CarrierPanel(resultPanel);
            getChildren().add(carrierPanel);
        }
    }

    private void clearButtons() {
        getChildren().removeAll(clientPanel, flightPanel, carrierPanel, aircraftPanel);
        clientPanel = null;
        flightPanel = null;
    }

}
