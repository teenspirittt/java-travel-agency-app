package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.example.controller.ResultPanel;

public class EntityPanel extends VBox {

    private final ResultPanel resultPanel;
    private ClientPanel clientPanel;
    private FlightPanel flightPanel;

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

    private void clearButtons() {
        getChildren().removeAll(clientPanel, flightPanel);
        clientPanel = null;
        flightPanel = null;
    }

}
