package ihm.tools;

import javafx.scene.control.Alert;

public class ShowAlert {
    public static void show(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}