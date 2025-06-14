package ihm;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import basicclass.AssociationStudent;
import basicclass.Student;

public class ShowFailedAssociationsDialog {
    public static void show(Stage primaryStage, MainApp app, List<AssociationStudent> failedAssociations, List<Student> unmatchedStudents) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Associations échouées");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #4A4A8A; -fx-background-radius: 18;");

        Label title = new Label("Associations échouées");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-background-color: #6A6AAA; -fx-text-fill: white;");

        Set<Student> studentsInAssoc = new HashSet<>();
        for (AssociationStudent assoc : failedAssociations) {
            if (assoc.getHost() == null || assoc.getGuest() == null) continue;
            if (assoc.getHost().getId() < 0 || assoc.getGuest().getId() < 0) continue;
            if (assoc.getScoreAssociation() != null) continue;
            String msg = assoc.getHost().getName() + " " + assoc.getHost().getForename()
                       + " ⇄ "
                       + assoc.getGuest().getName() + " " + assoc.getGuest().getForename()
                       + " : " + assoc.getInvalidReason();
            listView.getItems().add(msg);
            studentsInAssoc.add(assoc.getHost());
            studentsInAssoc.add(assoc.getGuest());
        }

        for (Student s : unmatchedStudents) {
            if (s.getId() < 0) continue;
            if (studentsInAssoc.contains(s)) continue;
            String msg = s.getName() + " " + s.getForename() + " (" + s.getCountry().getFullName() + ") — Aucun binôme possible";
            listView.getItems().add(msg);
        }

        Button closeBtn = new Button("Fermer");
        closeBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        closeBtn.setOnAction(e -> dialog.close());

        root.getChildren().addAll(title, listView, closeBtn);

        Scene scene = new Scene(root, 600, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}