package org.example.view;

import javafx.scene.control.Button;

public interface Panel {
     static Button createButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }
}
