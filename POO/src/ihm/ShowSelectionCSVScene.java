package ihm;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import java.io.File;
import manager.StudentManager;
import ihm.tools.ShowAlert;

public class ShowSelectionCSVScene {
    public static void show(Stage primaryStage, MainApp app) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Sélection du fichier CSV");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox fileSelection = new VBox(15);
        fileSelection.setAlignment(Pos.CENTER);
        fileSelection.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20; -fx-background-radius: 10;");

        Label fileLabel = new Label("Veuillez sélectionner votre fichier CSV :");
        fileLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER);
        TextField fileField = new TextField();
        fileField.setPrefWidth(300);
        Button browseButton = new Button("Parcourir");

        Label sizeLabel = new Label("Taille du csv : ...");
        sizeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        Label countLabel = new Label("Nombre d'élèves : ...");
        countLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        StudentManager studentManager = new StudentManager();

        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                fileField.setText(file.getAbsolutePath());
                try {
                    studentManager.loadStudentsFromCsv(file.getAbsolutePath());
                    app.students = studentManager.getStudents();
                    app.failedStudents = studentManager.getFailedStudents();
                } catch (Exception ex) {
                    app.students.clear();
                    app.failedStudents.clear();
                }
                sizeLabel.setText("Taille du csv : " + file.length() + " octets");
                countLabel.setText("Nombre d'élèves : " + app.students.size());
            }
        });

        fileBox.getChildren().addAll(fileField, browseButton);
        fileSelection.getChildren().addAll(fileLabel, fileBox, sizeLabel, countLabel);

        Button validateButton = new Button("Valider");
        validateButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        validateButton.setOnAction(e -> {
            if (!app.students.isEmpty()) {
                ShowGestionElevesScene.show(primaryStage, app);
            } else {
                ShowAlert.show("Erreur", "Veuillez d'abord sélectionner un fichier CSV valide.");
            }
        });

        root.getChildren().addAll(title, fileSelection, validateButton);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sélection du fichier CSV");
    }
}