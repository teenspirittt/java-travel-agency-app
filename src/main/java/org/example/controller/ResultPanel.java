package org.example.controller;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class ResultPanel extends VBox {

    private Label resultLabel;

    private TableView<?> currentTableView;

    public ResultPanel() {
        initUI();
    }

    private void initUI() {
        setSpacing(10);

        resultLabel = new Label("Results will be displayed here");
        getChildren().add(resultLabel);
    }

    public void setResult(String result) {
        resultLabel.setText(result);
    }

    public void clearResult() {
        resultLabel.setText("");
        getChildren().clear();
        currentTableView = null;
    }
    public void showTable(TableView<?> tableView) {
        clearResult();

        if (currentTableView != null) {
            getChildren().remove(currentTableView);
        }

        currentTableView = tableView;
        getChildren().add(currentTableView);
    }
}
