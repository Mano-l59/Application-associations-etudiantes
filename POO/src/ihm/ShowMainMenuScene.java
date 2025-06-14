package ihm;

import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import basicclass.*;
import algorithm.MatchingSolver;
import utils.CSVExport;
import ihm.tools.ShowAlert;

public class ShowMainMenuScene {
    public static void show(Stage primaryStage, MainApp app) {
        // Utilise l'état global de app (fidèle à Sauvegarde final.java)
        List<Student> students = app.students;
        List<AssociationStudent> associations = app.associations;
        Country selectedHost = app.selectedHost;
        Country selectedGuest = app.selectedGuest;
        MatchingEnum selectedAlgo = app.selectedAlgo;
        Set<Student> ignoredHosts = app.ignoredHosts;
        Set<Student> ignoredGuests = app.ignoredGuests;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        // Haut : Label principal
        Label title = new Label("Menu principal");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        StackPane topBar = new StackPane(title);
        topBar.setPadding(new Insets(20));
        root.setTop(topBar);

        // Bas : Boutons nécessaires
        Button voirElevesBtn = new Button("Voir étudiants");
        voirElevesBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        voirElevesBtn.setOnAction(e -> ShowListeEtudiantsScene.show(primaryStage, app));

        Button changerCsvBtn = new Button("Changer CSV");
        changerCsvBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        changerCsvBtn.setOnAction(e -> ShowSelectionCSVScene.show(primaryStage, app));

        Button configBtn = new Button("Configuration");
        configBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        Button executerBtn = new Button("Exécuter");
        executerBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        executerBtn.setDisable(!app.configDone);
        configBtn.setOnAction(e -> ShowConfigurationDialog.show(primaryStage, app));

        Button historiqueBtn = new Button("Voir historique");
        historiqueBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        historiqueBtn.setOnAction(e -> ShowHistoriqueScene.show(primaryStage, app));

        executerBtn.setOnAction(e -> {
            if (selectedHost == null || selectedGuest == null || selectedAlgo == null) {
                ShowAlert.show("Erreur", "Veuillez d'abord configurer les pays et l'algorithme.");
                return;
            }
            try {
                Set<Student> hosts = students.stream()
                    .filter(s -> s.getCountry().equals(selectedHost))
                    .filter(s -> !ignoredHosts.contains(s))
                    .collect(Collectors.toSet());

                Set<Student> guests = students.stream()
                    .filter(s -> s.getCountry().equals(selectedGuest))
                    .filter(s -> !ignoredGuests.contains(s))
                    .collect(Collectors.toSet());
                MatchingSolver solver = new MatchingSolver(hosts, guests, MainApp.historyManager);
                app.associations = solver.algorithmMatching(selectedAlgo);
                // Met à jour l'historique et sauvegarde
                MainApp.historyManager.addOrReplaceMatching(selectedHost, selectedGuest, app.associations);
                MainApp.historyManager.saveToFile("POO/data/historique.dat");
                show(primaryStage, app);
            } catch (Exception ex) {
                ShowAlert.show("Erreur", "Erreur lors de l'exécution de l'algorithme : " + ex.getMessage());
            }
        });

        HBox nav = new HBox(20, voirElevesBtn, changerCsvBtn, configBtn, historiqueBtn, executerBtn);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(20));
        root.setBottom(nav);

        // Centre : Liste des associations cliquables
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(30, 60, 30, 60));
        grid.setVgap(14);
        grid.setHgap(24);
        grid.setStyle("-fx-background-color: #6A6AAA;");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(45);
        col1.setHalignment(HPos.CENTER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(10);
        col2.setHalignment(HPos.CENTER);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(45);
        col3.setHalignment(HPos.CENTER);

        grid.getColumnConstraints().addAll(col1, col2, col3);

        Label foundLabel = new Label("Appariement(s) Trouvé(s)");
        foundLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        GridPane.setHalignment(foundLabel, HPos.CENTER);
        grid.add(foundLabel, 0, 0, 3, 1);

        Label hostHeader = new Label("Hôte(s)");
        hostHeader.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label arrowHeader = new Label("");
        Label guestHeader = new Label("Invité(s)");
        guestHeader.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        GridPane.setHalignment(hostHeader, HPos.CENTER);
        GridPane.setHalignment(guestHeader, HPos.CENTER);
        grid.add(hostHeader, 0, 1);
        grid.add(arrowHeader, 1, 1);
        grid.add(guestHeader, 2, 1);

        int row = 2;
        if (associations == null || associations.isEmpty()) {
            Label emptyLabel = new Label("Aucune association à afficher.\nCliquez sur 'Configuration' puis 'Exécuter' pour générer les associations.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            emptyLabel.setAlignment(Pos.CENTER);
            emptyLabel.setPadding(new Insets(30, 0, 0, 0));
            GridPane.setHalignment(emptyLabel, HPos.CENTER);
            grid.add(emptyLabel, 0, row, 3, 1);
        } else {
            for (AssociationStudent assoc : associations) {
                if (assoc.getHost() == null || assoc.getGuest() == null) continue;
                if (assoc.getScoreAssociation() == null) continue;
                if (assoc.getHost().getId() < 0 || assoc.getGuest().getId() < 0) continue;

                Label host = new Label(assoc.getHost().getName() + " " + assoc.getHost().getForename());
                host.setStyle("-fx-text-fill: #222; -fx-font-size: 16px; -fx-font-weight: bold;");
                Label arrow = new Label("⇄");
                arrow.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 22px; -fx-font-weight: bold;");
                Label guest = new Label(assoc.getGuest().getName() + " " + assoc.getGuest().getForename());
                guest.setStyle("-fx-text-fill: #222; -fx-font-size: 16px; -fx-font-weight: bold;");

                HBox card = new HBox();
                card.setAlignment(Pos.CENTER);
                card.setSpacing(0);
                card.setPrefWidth(636);
                card.setMaxWidth(Double.MAX_VALUE);

                HBox hostBox = new HBox(host);
                hostBox.setPrefWidth(0.45 * 636);
                hostBox.setAlignment(Pos.CENTER);

                HBox arrowBox = new HBox(arrow);
                arrowBox.setAlignment(Pos.CENTER);
                arrowBox.setPrefWidth(0.10 * 636);

                HBox guestBox = new HBox(guest);
                guestBox.setPrefWidth(0.45 * 636);
                guestBox.setAlignment(Pos.CENTER);

                card.getChildren().addAll(hostBox, arrowBox, guestBox);

                card.setStyle(
                    "-fx-background-color: #F4F4F4;" +
                    "-fx-background-radius: 14;" +
                    "-fx-padding: 14 0 14 0;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, #4A4A8A22, 6, 0.15, 2, 2);"
                );
                card.setOnMouseEntered(e -> card.setStyle(
                    "-fx-background-color: #B3D1FF;" +
                    "-fx-background-radius: 14;" +
                    "-fx-padding: 14 0 14 0;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, #4A4A8A, 12, 0.25, 2, 2);"
                ));
                card.setOnMouseExited(e -> card.setStyle(
                    "-fx-background-color: #F4F4F4;" +
                    "-fx-background-radius: 14;" +
                    "-fx-padding: 14 0 14 0;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, #4A4A8A22, 6, 0.15, 2, 2);"
                ));
                card.setOnMouseClicked(e -> ShowAssociationDetailsScene.show(primaryStage, app, assoc));

                grid.add(card, 0, row, 3, 1);
                row++;
            }
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: #6A6AAA;");
        scroll.setPrefHeight(500);

        root.setCenter(scroll);

        StackPane stack = new StackPane();
        stack.getChildren().add(root);

        AnchorPane floatingPane = new AnchorPane();
        floatingPane.setPickOnBounds(false);

        if (associations != null && !associations.isEmpty()) {
            Button exportBtn = new Button("Exporter CSV");
            exportBtn.setStyle(
                "-fx-background-color: #FFD700; -fx-text-fill: #222; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand; " +
                "-fx-background-radius: 30; -fx-padding: 12 28 12 28; -fx-effect: dropshadow(gaussian, #22222244, 8, 0.2, 2, 2);"
            );
            exportBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("associations.csv");
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    try {
                        CSVExport.exportMatchingToCsv(associations, file.getAbsolutePath().replace(".csv", ""));
                        ShowAlert.show("Export réussi", "Le fichier a été exporté avec succès !");
                    } catch (IOException ex) {
                        ShowAlert.show("Erreur", "Erreur lors de l'export : " + ex.getMessage());
                    }
                }
            });

            AnchorPane.setBottomAnchor(exportBtn, 90.0);
            AnchorPane.setRightAnchor(exportBtn, 50.0);
            floatingPane.getChildren().add(exportBtn);
        }

        Button failedBtn = new Button("Associations échouées");
        failedBtn.setStyle(
            "-fx-background-color:rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand;" +
            "-fx-background-radius: 30; -fx-padding: 12 28 12 28; -fx-effect: dropshadow(gaussian,rgba(17, 17, 17, 0.27), 8, 0.2, 2, 2);"
        );
        failedBtn.setOnAction(e -> {
            List<AssociationStudent> failedAssociations = new ArrayList<>();
            List<Student> matched = new ArrayList<>();
            if (associations != null) {
                for (AssociationStudent assoc : associations) {
                    if (assoc.getScoreAssociation() != null) {
                        matched.add(assoc.getHost());
                        matched.add(assoc.getGuest());
                    }
                }
            }
            if (selectedHost != null && selectedGuest != null) {
                Set<Student> hosts = students.stream()
                    .filter(s -> s.getCountry().equals(selectedHost))
                    .filter(s -> !ignoredHosts.contains(s))
                    .collect(Collectors.toSet());
                Set<Student> guests = students.stream()
                    .filter(s -> s.getCountry().equals(selectedGuest))
                    .filter(s -> !ignoredGuests.contains(s))
                    .collect(Collectors.toSet());
                MatchingSolver solver = new MatchingSolver(hosts, guests, MainApp.historyManager);
                solver.algorithmMatching(selectedAlgo);
                failedAssociations = solver.getAssociationsInvalid();
            }
            List<Student> unmatched = students.stream()
                .filter(s -> (selectedHost != null && s.getCountry().equals(selectedHost)) ||
                             (selectedGuest != null && s.getCountry().equals(selectedGuest)))
                .filter(s -> !matched.contains(s))
                .filter(s -> !ignoredHosts.contains(s) && !ignoredGuests.contains(s))
                .toList();

            ShowFailedAssociationsDialog.show(primaryStage, app, failedAssociations, unmatched);
        });

        AnchorPane.setBottomAnchor(failedBtn, 90.0);
        AnchorPane.setLeftAnchor(failedBtn, 50.0);
        floatingPane.getChildren().add(failedBtn);

        stack.getChildren().add(floatingPane);

        Scene scene = new Scene(stack, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Menu principal");
        primaryStage.show();
    }
}