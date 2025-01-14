package com.example.carproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.time.Year;
import java.util.Optional;

public class MainController {
    @FXML private TextField licensePlateField;
    @FXML private TextField nameField;
    @FXML private TextField yearField;
    @FXML private TableView<Car> tableView;
    @FXML private TableColumn<Car, String> licensePlateCol;
    @FXML private TableColumn<Car, String> nameCol;
    @FXML private TableColumn<Car, Integer> yearCol;
    @FXML private Button editButton;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;
    @FXML private Button loadButton;

    private ObservableList<Car> carList = FXCollections.observableArrayList();
    private Car selectedCar = null;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        licensePlateCol.setCellValueFactory(new PropertyValueFactory<>("licensePlateNumber"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        tableView.setItems(carList);

        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCar = newSelection;
            if (!editMode) {
                clearInputFields();
            }
        });

        tableView.setRowFactory(tv -> {
            TableRow<Car> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) {
                    tableView.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
    }

    @FXML
    private void handleEditCar() {
        if (selectedCar == null) {
            showAlert("Error", "Please select a car to edit");
            return;
        }

        editMode = true;
        populateFields(selectedCar);
        editButton.setVisible(false);
        updateButton.setVisible(true);
        cancelButton.setVisible(true);
        tableView.setDisable(true);
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
        loadButton.setDisable(true);
    }

    @FXML
    private void handleUpdateCar() {
        try {
            String licensePlate = licensePlateField.getText();
            String name = nameField.getText();
            int year = Integer.parseInt(yearField.getText());

            Car updatedCar = new Car(licensePlate, name, year);

            int index = carList.indexOf(selectedCar);
            carList.set(index, updatedCar);

            saveCarsToCsv();

            exitEditMode();
            showAlert("Success", "Car updated and saved successfully!");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid year");
        } catch (IllegalArgumentException e) {
            showAlert("Invalid Input", e.getMessage());
        }
    }


    private void exitEditMode() {
        editMode = false;
        selectedCar = null;
        clearInputFields();
        editButton.setVisible(true);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);
        tableView.setDisable(false);
        tableView.getSelectionModel().clearSelection();
        addButton.setDisable(false);
        deleteButton.setDisable(false);
        saveButton.setDisable(false);
        loadButton.setDisable(false);
    }

    private void saveCarsToCsv() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("cars.csv"))) {
            for (Car car : carList) {
                writer.println(car.getLicensePlateNumber() + "," + car.getName() + "," + car.getYear());
            }
        } catch (IOException e) {
            showAlert("Error", "Error saving cars: " + e.getMessage());
        }
    }


    @FXML
    private void handleCancelEdit() {
        exitEditMode();
    }

    private void populateFields(Car car) {
        licensePlateField.setText(car.getLicensePlateNumber());
        nameField.setText(car.getName());
        yearField.setText(String.valueOf(car.getYear()));
    }

    @FXML
    private void handleAddCar() {
        try {
            String licensePlate = licensePlateField.getText();
            String name = nameField.getText();
            int year = Integer.parseInt(yearField.getText());

            Car newCar = new Car(licensePlate, name, year);
            carList.add(newCar);
            clearInputFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid year");
        } catch (IllegalArgumentException e) {
            showAlert("Invalid Input", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCar() {
        if (selectedCar == null) {
            showAlert("Error", "Please select a car to delete");
            return;
        }

        if (showConfirmationDialog("Delete Car", "Are you sure you want to delete this car?")) {
            carList.remove(selectedCar);
            saveCarsToCsv();
            selectedCar = null;
            clearInputFields();
            tableView.getSelectionModel().clearSelection();
            showAlert("Success", "Car deleted and CSV updated successfully!");
        }
    }

    @FXML
    private void handleSave() {
        if (showConfirmationDialog("Save", "Are you sure you want to save? This action cannot be undone.")) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("cars.csv"))) {
                for (Car car : carList) {
                    writer.println(car.getLicensePlateNumber() + "," + car.getName() + "," + car.getYear());
                }
                showAlert("Success", "Cars saved successfully!");
            } catch (IOException e) {
                showAlert("Error", "Error saving cars: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLoad() {
        if (showConfirmationDialog("Load", "Are you sure you want to load? Current data will be lost and this action cannot be undone.")) {
            try (BufferedReader reader = new BufferedReader(new FileReader("cars.csv"))) {
                carList.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        carList.add(new Car(parts[0], parts[1], Integer.parseInt(parts[2])));
                    }
                }
                showAlert("Success", "Cars loaded successfully!");
            } catch (IOException e) {
                showAlert("Error", "Error loading cars: " + e.getMessage());
            }
        }
    }

    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearInputFields() {
        licensePlateField.clear();
        nameField.clear();
        yearField.clear();
    }
}
