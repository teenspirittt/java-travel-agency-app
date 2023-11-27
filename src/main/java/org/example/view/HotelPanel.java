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
import org.example.dao.HotelDAO;
import org.example.model.Hotel;

import java.util.List;
import java.util.Optional;

public class HotelPanel extends VBox implements Panel {

    private ResultPanel resultPanel;
    private HotelDAO hotelDAO;

    public HotelPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        this.hotelDAO = new HotelDAO();
        initUI();
    }

    private void initUI() {
        setSpacing(10);
        showAllHotels();
        setPadding(new Insets(10));

        Button showAllButton = Panel.createButton("Show All");
        showAllButton.setOnAction(e -> showAllHotels());

        Button updateButton = Panel.createButton("Update");
        updateButton.setOnAction(e -> updateHotel());

        Button deleteButton = Panel.createButton("Delete");
        deleteButton.setOnAction(e -> deleteHotel());

        Button addButton = Panel.createButton("Add");
        addButton.setOnAction(e -> addHotel());

        Button classButton = Panel.createButton("Get by Class");
        classButton.setOnAction(e -> getByClassHotel());

        HBox buttonsRow = new HBox(10, showAllButton, updateButton, deleteButton, addButton, classButton);

        getChildren().addAll(buttonsRow);
    }

    private void showAllHotels() {
        List<Hotel> hotels = hotelDAO.getAllEntities();
        ObservableList<Hotel> observableHotels = FXCollections.observableArrayList(hotels);
        resultPanel.showTable(createHotelTable(observableHotels));
    }

    private void updateHotel() {
        List<Hotel> hotels = hotelDAO.getAllEntities();

        ComboBox<String> hotelComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        hotels.stream()
                                .map(Hotel::getName)
                                .toList()
                )
        );
        hotelComboBox.setPromptText("Select a hotel");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Update Hotel");
        dialog.setHeaderText("Select a hotel to update:");

        GridPane grid = new GridPane();
        grid.add(hotelComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedHotelName = hotelComboBox.getValue();

            if (selectedHotelName != null && !selectedHotelName.isEmpty()) {
                Hotel selectedHotel = hotels.stream()
                        .filter(hotel -> selectedHotelName.equals(hotel.getName()))
                        .findFirst()
                        .orElse(null);

                if (selectedHotel != null) {
                    showUpdateHotelDialog(selectedHotel);
                } else {
                    resultPanel.setResult("Hotel not found for updating");
                }
            } else {
                resultPanel.setResult("No hotel selected for updating");
            }
        }
        showAllHotels();
    }

    private void showUpdateHotelDialog(Hotel hotel) {
        Dialog<Void> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update Hotel");
        updateDialog.setHeaderText("Update hotel information:");

        TextField nameField = new TextField(hotel.getName());
        TextField classField = new TextField(String.valueOf(hotel.getHotelClass()));
        TextField roomCategoryField = new TextField(hotel.getRoomCategory());

        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        updateDialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(classField, 1, 1);
        grid.add(new Label("Room Category:"), 0, 2);
        grid.add(roomCategoryField, 1, 2);
        updateDialog.getDialogPane().setContent(grid);

        updateDialog.setResultConverter(buttonType -> {
            if (buttonType == updateButton) {
                String newName = nameField.getText();
                int newClass = Integer.parseInt(classField.getText());
                String newRoomCategory = roomCategoryField.getText();

                hotel.setName(newName);
                hotel.setHotelClass(newClass);
                hotel.setRoomCategory(newRoomCategory);

                hotelDAO.updateEntity(hotel);

                resultPanel.setResult("Hotel updated successfully");
            }
            return null;
        });

        // Display the dialog window
        updateDialog.showAndWait();
    }


    private void deleteHotel() {
        List<Hotel> hotels = hotelDAO.getAllEntities();

        ComboBox<String> hotelComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        hotels.stream()
                                .map(Hotel::getName)
                                .toList()
                )
        );
        hotelComboBox.setPromptText("Select a hotel");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Delete Hotel");
        dialog.setHeaderText("Select a hotel to delete:");

        GridPane grid = new GridPane();
        grid.add(hotelComboBox, 0, 0);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedHotelName = hotelComboBox.getValue();

            if (selectedHotelName != null && !selectedHotelName.isEmpty()) {
                Hotel selectedHotel = hotels.stream()
                        .filter(hotel -> selectedHotelName.equals(hotel.getName()))
                        .findFirst()
                        .orElse(null);

                if (selectedHotel != null) {
                    hotelDAO.deleteEntity(selectedHotel);

                    resultPanel.setResult("Hotel deleted successfully");
                } else {
                    resultPanel.setResult("Hotel not found for deletion");
                }
            } else {
                resultPanel.setResult("No hotel selected for deletion");
            }
        }
        showAllHotels();
    }

    private void addHotel() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Add Hotel");
        dialog.setHeaderText(null);

        TextField nameField = new TextField();
        TextField classField = new TextField();
        TextField roomCategoryField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(classField, 1, 1);
        grid.add(new Label("Room Category:"), 0, 2);
        grid.add(roomCategoryField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText();
            int hotelClass = Integer.parseInt(classField.getText());
            String roomCategory = roomCategoryField.getText();

            Hotel newHotel = new Hotel();
            newHotel.setName(name);
            newHotel.setHotelClass(hotelClass);
            newHotel.setRoomCategory(roomCategory);

            hotelDAO.addEntity(newHotel);

            resultPanel.setResult("Hotel added successfully");
        }
        showAllHotels();
    }


    private void getByClassHotel() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Get Hotels by Class");
        dialog.setHeaderText(null);

        TextField classField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Class:"), 0, 0);
        grid.add(classField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String classInput = classField.getText();

            if (!classInput.isEmpty()) {
                try {
                    int hotelClass = Integer.parseInt(classInput);
                    List<Hotel> hotels = hotelDAO.getHotelByClass(hotelClass);

                    resultPanel.showTable(createHotelTable(FXCollections.observableArrayList(hotels)));
                } catch (NumberFormatException e) {
                    resultPanel.setResult("Invalid input for hotel class");
                }
            } else {
                resultPanel.setResult("Please enter a hotel class");
            }
        }
    }


    private TableView<Hotel> createHotelTable(ObservableList<Hotel> hotels) {
        TableView<Hotel> hotelTable = new TableView<>();
        setupHotelTableColumns(hotelTable);

        hotelTable.setItems(hotels);
        return hotelTable;
    }

    private void setupHotelTableColumns(TableView<Hotel> hotelTable) {
        TableColumn<Hotel, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setMinWidth(150);

        TableColumn<Hotel, Integer> hotelClassColumn = new TableColumn<>("Class");
        hotelClassColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getHotelClass()));
        hotelClassColumn.setMinWidth(100);

        TableColumn<Hotel, String> roomCategoryColumn = new TableColumn<>("Room Category");
        roomCategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomCategory()));
        roomCategoryColumn.setMinWidth(150);

        hotelTable.getColumns().addAll(nameColumn, hotelClassColumn, roomCategoryColumn);

        hotelTable.setMinWidth(500);
        hotelTable.setMaxWidth(Double.MAX_VALUE);
    }

}
