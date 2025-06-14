package ihm.tools;

import javafx.scene.control.Label;

public class CreateInfoLabel {
    public static Label create(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        return label;
    }
}
