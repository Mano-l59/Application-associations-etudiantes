package ihm;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import java.util.*;
import basicclass.*;

public class ShowHistoriqueScene {
    public static void show(Stage primaryStage, MainApp app) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Historique des associations");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Historique des associations");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox listBox = new VBox(8);
        listBox.setAlignment(Pos.CENTER);

        Map<String, List<AssociationStudent>> historique = MainApp.historyManager.getHistorique();
        if (historique == null || historique.isEmpty()) {
            Label empty = new Label("Aucun historique enregistré.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");
            listBox.getChildren().add(empty);
        } else {
            for (Map.Entry<String, List<AssociationStudent>> entry : historique.entrySet()) {
                String key = entry.getKey();
                List<AssociationStudent> assocList = entry.getValue();
                Label keyLabel = new Label("Appariement : " + key);
                keyLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16px; -fx-font-weight: bold;");
                listBox.getChildren().add(keyLabel);
                if (assocList == null || assocList.isEmpty()) {
                    Label empty = new Label("Aucune association pour ce couple.");
                    empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    listBox.getChildren().add(empty);
                } else {
                    for (AssociationStudent assoc : assocList) {
                        if (assoc.getHost() == null || assoc.getGuest() == null) continue;
                        if (assoc.getScoreAssociation() == null) continue;
                        Label label = new Label(
                            assoc.getHost().getName() + " " + assoc.getHost().getForename() +
                            " ⇄ " +
                            assoc.getGuest().getName() + " " + assoc.getGuest().getForename() +
                            " | Affinité : " + assoc.describeLevelOfAffinity()
                        );
                        label.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");
                        label.setMaxWidth(320);
                        label.setPrefWidth(320);
                        label.setWrapText(true);
                        listBox.getChildren().add(label);
                    }
                }
            }
        }

        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #6A6AAA;");
        scroll.setPrefHeight(300);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button clearBtn = new Button("Vider l'historique");
        clearBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            MainApp.historyManager.clearHistorique();
            MainApp.historyManager.saveToFile("POO/data/historique.dat");
            dialog.close();
            ihm.tools.ShowAlert.show("Historique vidé", "L'historique a bien été supprimé.");
        });

        Button closeBtn = new Button("Fermer");
        closeBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(clearBtn, closeBtn);

        root.getChildren().setAll(title, scroll, buttonBox);

        Scene scene = new Scene(root, 500, 470);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}