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
import org.example.dao.ClientRouteDAO;
import org.example.dao.RouteDAO;
import org.example.dao.ClientDAO;
import org.example.model.Client;
import org.example.model.ClientRoute;
import org.example.model.ClientRouteId;
import org.example.model.Route;

import java.util.List;
import java.util.Optional;

public class ClientRoutePanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private ClientRouteDAO clientRouteDAO;

    private RouteDAO routeDAO;

    private ClientDAO clientDAO;

    public ClientRoutePanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.clientRouteDAO = new ClientRouteDAO();
        this.clientDAO = new ClientDAO();
        this.routeDAO = new RouteDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        showAllClientRoutes();

        Button showAllButton = Panel.createButton("Show All");
        showAllButton.setOnAction(e -> showAllClientRoutes());

        Button updateButton = Panel.createButton("Update");
        updateButton.setOnAction(e -> updateClientRoute());

        Button deleteButton = Panel.createButton("Delete");
        deleteButton.setOnAction(e -> deleteClientRoute());

        Button addButton = Panel.createButton("Add");
        addButton.setOnAction(e -> addClientRoute());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton);

        getChildren().addAll(buttonsRow);
    }

    private void showAllClientRoutes() {
        List<ClientRoute> clientRoutes = clientRouteDAO.getAllEntities();
        ObservableList<ClientRoute> observableClientRoutes = FXCollections.observableArrayList(clientRoutes);
        resultPanel.showTable(createClientRouteTable(observableClientRoutes));
    }

    private TableView<ClientRoute> createClientRouteTable(ObservableList<ClientRoute> clientRoutes) {
        TableView<ClientRoute> clientRouteTable = new TableView<>();
        setupClientRouteTableColumns(clientRouteTable);

        clientRouteTable.setItems(clientRoutes);
        return clientRouteTable;
    }

    private void setupClientRouteTableColumns(TableView<ClientRoute> clientRouteTable) {
        TableColumn<ClientRoute, String> clientNameColumn = new TableColumn<>("Client Name");
        clientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getFullName()));
        clientNameColumn.setMinWidth(150);

        TableColumn<ClientRoute, String> routeNameColumn = new TableColumn<>("Route");
        routeNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoute().getName()));
        routeNameColumn.setMinWidth(150);

        TableColumn<ClientRoute, String> seatColumn = new TableColumn<>("Seat");
        seatColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeat()));
        seatColumn.setMinWidth(100);

        clientRouteTable.getColumns().addAll(clientNameColumn, routeNameColumn, seatColumn);

        clientRouteTable.setMinWidth(1000);
        clientRouteTable.setMaxWidth(Double.MAX_VALUE);
    }

    private void updateClientRoute() {
        List<ClientRoute> clientRoutes = clientRouteDAO.getAllEntities();

        ComboBox<String> clientRouteComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        clientRoutes.stream()
                                .map(this::getClientRouteString)
                                .toList()
                )
        );
        clientRouteComboBox.setPromptText("Select a client route");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Client Route");
        dialog.setHeaderText("Select a client route to update:");

        GridPane grid = new GridPane();
        grid.add(clientRouteComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedClientRouteString = clientRouteComboBox.getValue();


            if (selectedClientRouteString != null && !selectedClientRouteString.isEmpty()) {
                ClientRoute selectedClientRoute = clientRoutes.stream()
                        .filter(clientRoute -> selectedClientRouteString.equals(getClientRouteString(clientRoute)))
                        .findFirst()
                        .orElse(null);

                if (selectedClientRoute != null) {
                    showUpdateClientRouteDialog(selectedClientRoute);
                } else {
                    resultPanel.setResult("Client Route not found for updating");
                    System.out.println("Client Route not found for updating");
                }
            } else {
                resultPanel.setResult("No client route selected for updating");
                System.out.println("No client route selected for updating");
            }
        }
        showAllClientRoutes();
    }

    private void showUpdateClientRouteDialog(ClientRoute selectedClientRoute) {
        Dialog<Void> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Client Route");
        updateDialog.setHeaderText("Update client route information:");

        TextField seatTextField = new TextField(selectedClientRoute.getSeat());

        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        updateDialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(new Label("Seat:"), 0, 0);
        grid.add(seatTextField, 1, 0);
        updateDialog.getDialogPane().setContent(grid);

        updateDialog.setResultConverter(buttonType -> {
            if (buttonType == updateButton) {
                String newSeat = seatTextField.getText();

                selectedClientRoute.setSeat(newSeat);

                clientRouteDAO.updateEntity(selectedClientRoute);

                resultPanel.setResult("Client Route updated successfully");
            }
            return null;
        });

        updateDialog.showAndWait();
    }



    private void deleteClientRoute() {
        List<ClientRoute> clientRoutes = clientRouteDAO.getAllEntities();

        ComboBox<String> clientRouteComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        clientRoutes.stream()
                                .map(this::getClientRouteString)
                                .toList()
                )
        );
        clientRouteComboBox.setPromptText("Select a client route");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Client Route");
        dialog.setHeaderText("Select a client route to delete:");

        GridPane grid = new GridPane();
        grid.add(clientRouteComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedClientRouteString = clientRouteComboBox.getValue();

            if (selectedClientRouteString != null && !selectedClientRouteString.isEmpty()) {
                ClientRoute selectedClientRoute = clientRoutes.stream()
                        .filter(clientRoute -> selectedClientRouteString.equals(getClientRouteString(clientRoute)))
                        .findFirst()
                        .orElse(null);

                if (selectedClientRoute != null) {
                    clientRouteDAO.deleteEntity(selectedClientRoute);

                    resultPanel.setResult("Client Route deleted successfully");
                } else {
                    resultPanel.setResult("Client Route not found for deletion");
                }
            } else {
                resultPanel.setResult("No client route selected for deletion");
            }
        }
        showAllClientRoutes();
    }

    private String getClientRouteString(ClientRoute clientRoute) {
        return clientRoute.getClient().getFullName() + " - " + clientRoute.getRoute().getName();
    }


    private void addClientRoute() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Client Route");
        dialog.setHeaderText(null);

        List<Client> clients = clientDAO.getAllEntities();
        List<Route> routes = routeDAO.getAllEntities();

        ComboBox<String> clientComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        clients.stream()
                                .map(Client::getFullName)
                                .toList()
                )
        );
        clientComboBox.setPromptText("Select a client");

        ComboBox<String> routeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        routes.stream()
                                .map(Route::getName)
                                .toList()
                )
        );
        routeComboBox.setPromptText("Select a route");

        TextField seatField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Client:"), 0, 0);
        grid.add(clientComboBox, 1, 0);
        grid.add(new Label("Route:"), 0, 1);
        grid.add(routeComboBox, 1, 1);
        grid.add(new Label("Seat:"), 0, 2);
        grid.add(seatField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedClientName = clientComboBox.getValue();
            String selectedRouteName = routeComboBox.getValue();

            Client selectedClient = clients.stream()
                    .filter(client -> selectedClientName.equals(client.getFullName()))
                    .findFirst()
                    .orElse(null);

            Route selectedRoute = routes.stream()
                    .filter(route -> selectedRouteName.equals(route.getName()))
                    .findFirst()
                    .orElse(null);

            if (selectedClient != null && selectedRoute != null) {
                ClientRoute newClientRoute = new ClientRoute();
                ClientRouteId clientRouteId = new ClientRouteId(selectedClient.getId(), selectedRoute.getId());
                newClientRoute.setId(clientRouteId);
                newClientRoute.setClient(selectedClient);
                newClientRoute.setRoute(selectedRoute);
                newClientRoute.setSeat(seatField.getText());

                clientRouteDAO.addEntity(newClientRoute);

                resultPanel.setResult("Client Route added successfully");
            } else {
                resultPanel.setResult("Selected client or route not found");
            }
        }
        showAllClientRoutes();
    }

}
