package ihm;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import basicclass.*;
import manager.StudentManager;
import manager.HistoryManager;
import algorithm.MatchingSolver;
import utils.CSVExport;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainApp extends Application {
    
    private Stage primaryStage;
    private List<Student> students = new ArrayList<>();
    private List<String> failedStudents = new ArrayList<>();
    private List<AssociationStudent> associations = new ArrayList<>();
    public static HistoryManager historyManager = new HistoryManager();


    // Champs pour la configuration
    private Country selectedHost = null;
    private Country selectedGuest = null;
    private MatchingEnum selectedAlgo = null;
    private boolean configDone = false;

    private Set<Student> ignoredHosts = new HashSet<>();
    private Set<Student> ignoredGuests = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Charge l'historique dès le début
        historyManager.loadFromFile("POO/data/historique.dat");
        primaryStage.setTitle("Gestion des étudiants");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.setWidth(1100);
        primaryStage.setHeight(750);
        showSelectionCSVScene();
        primaryStage.show();
    }

    // 1. Sélection du CSV
    private void showSelectionCSVScene() {
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
                    students = studentManager.getStudents();
                    failedStudents = studentManager.getFailedStudents();
                } catch (Exception ex) {
                    students.clear();
                    failedStudents.clear();
                }
                sizeLabel.setText("Taille du csv : " + file.length() + " octets");
                countLabel.setText("Nombre d'élèves : " + students.size());
            }
        });

        fileBox.getChildren().addAll(fileField, browseButton);
        fileSelection.getChildren().addAll(fileLabel, fileBox, sizeLabel, countLabel);

        Button validateButton = new Button("Valider");
        validateButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        validateButton.setOnAction(e -> {
            if (!students.isEmpty()) {
                showGestionElevesScene();
            } else {
                showAlert("Erreur", "Veuillez d'abord sélectionner un fichier CSV valide.");
            }
        });

        root.getChildren().addAll(title, fileSelection, validateButton);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    // 2. Visualisation des étudiants chargés et rejetés
    private void showGestionElevesScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        // Titre en haut
        Label title = new Label("Gestion des élèves");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);

        // Étudiants valides (gauche)
        VBox studentsBox = new VBox(8); // réduit l'espacement
        studentsBox.setPadding(new Insets(20, 0, 20, 0)); // padding horizontal supprimé
        studentsBox.setAlignment(Pos.TOP_CENTER);
        studentsBox.setStyle("-fx-background-color: #6A6AAA;"); // border-radius supprimé
        studentsBox.getChildren().add(createInfoLabel("Étudiants valides (" + students.size() + ") :"));
        for (Student student : students) {
            VBox studentCard = new VBox(3);
            studentCard.setStyle("-fx-background-color: #ffffff22; -fx-padding: 8;"); // border-radius supprimé
            studentCard.setAlignment(Pos.CENTER_LEFT);
            studentCard.getChildren().addAll(
                createInfoLabel("Nom : " + student.getName()),
                createInfoLabel("Prénom : " + student.getForename()),
                createInfoLabel("Pays : " + student.getCountry().getFullName()),
                createInfoLabel("Genre : " + student.getGender())
            );
            studentsBox.getChildren().add(studentCard);
        }
        ScrollPane studentsScroll = new ScrollPane(studentsBox);
        studentsScroll.setFitToWidth(true);
        studentsScroll.setStyle("-fx-background: transparent;");
        studentsScroll.setPrefWidth(320); // réduit la largeur

        // Étudiants rejetés (droite)
        VBox failedBox = new VBox(8); // réduit l'espacement
        failedBox.setPadding(new Insets(20, 0, 20, 0)); // padding horizontal supprimé
        failedBox.setAlignment(Pos.TOP_CENTER);
        failedBox.setStyle("-fx-background-color: #6A6AAA;"); // border-radius supprimé
        failedBox.getChildren().add(createInfoLabel("Étudiants rejetés (" + failedStudents.size() + ") :"));
        for (String line : failedStudents) {
            VBox failedCard = new VBox(3);
            failedCard.setStyle("-fx-background-color: #ff000022; -fx-padding: 8;"); // border-radius supprimé
            failedCard.setAlignment(Pos.CENTER_LEFT);
            failedCard.getChildren().add(createInfoLabel(line));
            failedBox.getChildren().add(failedCard);
        }
        ScrollPane failedScroll = new ScrollPane(failedBox);
        failedScroll.setFitToWidth(true);
        failedScroll.setStyle("-fx-background: transparent;");
        failedScroll.setPrefWidth(320); // réduit la largeur

        // Les deux listes rapprochées dans un HBox
        HBox centerBox = new HBox(20, studentsScroll, failedScroll); // réduit l'espacement à 20
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(0, 0, 0, 0)); // pas de padding autour

        root.setCenter(centerBox);

        // Boutons en bas
        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));
        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showSelectionCSVScene());
        Button continuerButton = new Button("Continuer");
        continuerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        continuerButton.setOnAction(e -> showMainMenuScene());
        navButtons.getChildren().addAll(retourButton, continuerButton);
        root.setBottom(navButtons);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    // 3. Menu principal (matching, boutons, configuration)
    private void showMainMenuScene() {
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
        voirElevesBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        voirElevesBtn.setOnAction(e -> showListeEtudiantsScene());

        Button changerCsvBtn = new Button("Changer CSV");
        changerCsvBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        changerCsvBtn.setOnAction(e -> showSelectionCSVScene());

        Button configBtn = new Button("Configuration");
        configBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        Button executerBtn = new Button("Exécuter");
        executerBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        executerBtn.setDisable(!configDone);
        configBtn.setOnAction(e -> showConfigurationDialog(executerBtn));

        Button historiqueBtn = new Button("Voir historique");
        historiqueBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        historiqueBtn.setOnAction(e -> showHistoriqueScene());

        executerBtn.setOnAction(e -> {
            if (selectedHost == null || selectedGuest == null || selectedAlgo == null) {
                showAlert("Erreur", "Veuillez d'abord configurer les pays et l'algorithme.");
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
                MatchingSolver solver = new MatchingSolver(hosts, guests, historyManager);
                associations = solver.algorithmMatching(selectedAlgo);
                // Met à jour l'historique et sauvegarde
                historyManager.addOrReplaceMatching(selectedHost, selectedGuest, associations);
                historyManager.saveToFile("POO/data/historique.dat");
                showMainMenuScene();
            } catch (Exception ex) {
                showAlert("Erreur", "Erreur lors de l'exécution de l'algorithme : " + ex.getMessage());
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

        // Ajoute ceci juste après la création du GridPane
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(45);
        col1.setHalignment(javafx.geometry.HPos.CENTER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(10);
        col2.setHalignment(javafx.geometry.HPos.CENTER);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(45);
        col3.setHalignment(javafx.geometry.HPos.CENTER);

        grid.getColumnConstraints().addAll(col1, col2, col3);

        // Ligne 0 : Label principal centré sur 3 colonnes
        Label foundLabel = new Label("Appariement(s) Trouvé(s)");
        foundLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        GridPane.setHalignment(foundLabel, javafx.geometry.HPos.CENTER);
        grid.add(foundLabel, 0, 0, 3, 1);

        // Ligne 1 : Headers Hôte(s) et Invité(s)
        Label hostHeader = new Label("Hôte(s)");
        hostHeader.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label arrowHeader = new Label(""); // colonne centrale vide
        Label guestHeader = new Label("Invité(s)");
        guestHeader.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        GridPane.setHalignment(hostHeader, javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(guestHeader, javafx.geometry.HPos.CENTER);
        grid.add(hostHeader, 0, 1);
        grid.add(arrowHeader, 1, 1);
        grid.add(guestHeader, 2, 1);

        // Ligne 2+ : Associations
        int row = 2;
        if (associations.isEmpty()) {
            Label emptyLabel = new Label("Aucune association à afficher.\nCliquez sur 'Configuration' puis 'Exécuter' pour générer les associations.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            emptyLabel.setAlignment(Pos.CENTER);
            emptyLabel.setPadding(new Insets(30, 0, 0, 0));
            GridPane.setHalignment(emptyLabel, javafx.geometry.HPos.CENTER);
            grid.add(emptyLabel, 0, row, 3, 1);
        } else {
            for (AssociationStudent assoc : associations) {
                // Ne pas afficher si host ou guest est null
                if (assoc.getHost() == null || assoc.getGuest() == null) continue;
                // NE PAS AFFICHER si le score est null (association impossible)
                if (assoc.getScoreAssociation() == null) continue;

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
                hostBox.setAlignment(Pos.CENTER); // <-- Déjà centré

                HBox arrowBox = new HBox(arrow);
                arrowBox.setAlignment(Pos.CENTER);
                arrowBox.setPrefWidth(0.10 * 636);

                HBox guestBox = new HBox(guest);
                guestBox.setPrefWidth(0.45 * 636);
                guestBox.setAlignment(Pos.CENTER); // <-- Déjà centré

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
                card.setOnMouseClicked(e -> showAssociationDetailsScene(assoc));

                grid.add(card, 0, row, 3, 1);
                row++;
            }
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // Ajoute la couleur ici aussi pour remplir tout le center
        scroll.setStyle("-fx-background: #6A6AAA;"); // <-- Ajoute cette ligne
        scroll.setPrefHeight(500);

        root.setCenter(scroll);

        // Création du conteneur principal pour permettre les boutons flottants
        StackPane stack = new StackPane();
        stack.getChildren().add(root);

        AnchorPane floatingPane = new AnchorPane();
        floatingPane.setPickOnBounds(false);

        // Bouton Export CSV (affiché seulement si associations)
        if (!associations.isEmpty()) {
            Button exportBtn = new Button("Exporter CSV");
            exportBtn.setStyle(
                "-fx-background-color: #FFD700; -fx-text-fill: #222; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-background-radius: 30; -fx-padding: 12 28 12 28; -fx-effect: dropshadow(gaussian, #22222244, 8, 0.2, 2, 2);"
            );
            exportBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("associations.csv");
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    try {
                        CSVExport.exportMatchingToCsv(associations, file.getAbsolutePath().replace(".csv", ""));
                        showAlert("Export réussi", "Le fichier a été exporté avec succès !");
                    } catch (IOException ex) {
                        showAlert("Erreur", "Erreur lors de l'export : " + ex.getMessage());
                    }
                }
            });

            AnchorPane.setBottomAnchor(exportBtn, 90.0);
            AnchorPane.setRightAnchor(exportBtn, 50.0);
            floatingPane.getChildren().add(exportBtn);
        }

        // Bouton Étudiants sans matching (toujours affiché)
        Button unmatchedBtn = new Button("Étudiants sans matching");
        unmatchedBtn.setStyle(
            "-fx-background-color:rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 15px; -fx-font-weight: bold;" +
            "-fx-background-radius: 30; -fx-padding: 12 28 12 28; -fx-effect: dropshadow(gaussian,rgba(17, 17, 17, 0.27), 8, 0.2, 2, 2);"
        );
        unmatchedBtn.setOnAction(e -> showRemainingStudentsDialog());

        AnchorPane.setBottomAnchor(unmatchedBtn, 90.0);
        AnchorPane.setLeftAnchor(unmatchedBtn, 50.0);
        floatingPane.getChildren().add(unmatchedBtn);

        stack.getChildren().add(floatingPane);

        Scene scene = new Scene(stack, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    // Fenêtre modale de configuration
    private void showConfigurationDialog(Button executerBtn) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Configuration");

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #4A4A8A;");
        

        // --- Onglet Automatique ---
        VBox autoRoot = new VBox(18);
        autoRoot.setAlignment(Pos.TOP_CENTER);
        autoRoot.setPadding(new Insets(20));
        autoRoot.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");

        // Choix des pays
        Set<Country> countries = students.stream().map(Student::getCountry).collect(Collectors.toSet());
        List<Country> countryList = new ArrayList<>(countries);

        ComboBox<Country> hostCombo = new ComboBox<>();
        ComboBox<Country> guestCombo = new ComboBox<>();
        hostCombo.getItems().addAll(countryList);
        guestCombo.getItems().addAll(countryList);
        hostCombo.setPromptText("Pays hôte");
        guestCombo.setPromptText("Pays invité");
        if (selectedHost != null) hostCombo.setValue(selectedHost);
        if (selectedGuest != null) guestCombo.setValue(selectedGuest);

        HBox countryBox = new HBox(30, new VBox(new Label("Pays hôte"), hostCombo), new VBox(new Label("Pays invité"), guestCombo));
        countryBox.setAlignment(Pos.CENTER);

        // Choix de l'algo
        ComboBox<MatchingEnum> algoCombo = new ComboBox<>();
        algoCombo.getItems().addAll(MatchingEnum.values());
        algoCombo.setPromptText("Algorithme");
        if (selectedAlgo != null) algoCombo.setValue(selectedAlgo);

        VBox algoBox = new VBox(4, new Label("Algorithme"), algoCombo);
        algoBox.setAlignment(Pos.CENTER);

        // Liste des étudiants filtrés selon le pays sélectionné
        ListView<Student> studentListView = new ListView<>();
        studentListView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox ignoreBox = new CheckBox();
            private final Label nameLabel = new Label();
            private final Region spacer = new Region();
            private final HBox cellBox = new HBox();

            {
                cellBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                cellBox.getChildren().addAll(nameLabel, spacer, ignoreBox);
                cellBox.setSpacing(0);
                ignoreBox.setStyle("-fx-cursor: hand;");
                cellBox.setStyle("-fx-padding: 4 0 4 0;");
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                ignoreBox.setOnAction(e -> {
                    Student s = getItem();
                    if (s == null) return;
                    if (hostCombo.getValue() != null && s.getCountry().equals(hostCombo.getValue())) {
                        if (ignoreBox.isSelected()) ignoredHosts.add(s); else ignoredHosts.remove(s);
                    }
                    if (guestCombo.getValue() != null && s.getCountry().equals(guestCombo.getValue())) {
                        if (ignoreBox.isSelected()) ignoredGuests.add(s); else ignoredGuests.remove(s);
                    }
                });
            }

            @Override
            protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(s.getName() + " " + s.getForename() + " (" + s.getCountry().getFullName() + ")");
                    nameLabel.setStyle("-fx-text-fill: #222; -fx-font-size: 13px;"); // texte foncé
                    if (hostCombo.getValue() != null && s.getCountry().equals(hostCombo.getValue()))
                        ignoreBox.setSelected(ignoredHosts.contains(s));
                    else if (guestCombo.getValue() != null && s.getCountry().equals(guestCombo.getValue()))
                        ignoreBox.setSelected(ignoredGuests.contains(s));
                    else
                        ignoreBox.setSelected(false);
                    setGraphic(cellBox);
                }
            }
        });

        // Rafraîchit la liste selon les choix de pays
        Runnable refreshList = () -> {
            List<Student> filtered = students.stream()
                .filter(s -> (hostCombo.getValue() != null && s.getCountry().equals(hostCombo.getValue()))
                          || (guestCombo.getValue() != null && s.getCountry().equals(guestCombo.getValue())))
                .collect(Collectors.toList());
            studentListView.getItems().setAll(filtered);
        };
        hostCombo.setOnAction(e -> refreshList.run());
        guestCombo.setOnAction(e -> refreshList.run());
        refreshList.run();

        // Bouton valider
        Button validerBtn = new Button("Valider");
        validerBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        validerBtn.setOnAction(e -> {
            if (hostCombo.getValue() != null && guestCombo.getValue() != null && algoCombo.getValue() != null && !hostCombo.getValue().equals(guestCombo.getValue())) {
                selectedHost = hostCombo.getValue();
                selectedGuest = guestCombo.getValue();
                selectedAlgo = algoCombo.getValue();
                configDone = true;
                executerBtn.setDisable(false);
                dialog.close();
            } else {
                showAlert("Erreur", "Veuillez sélectionner deux pays différents et un algorithme.");
            }
        });

        // Ajout de l'en-tête pour l'ignorer
        Label ignoreHeader = new Label("Cochez la case pour bloquer l'étudiant");
        ignoreHeader.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        autoRoot.getChildren().setAll(
            countryBox,
            algoBox,
            ignoreHeader,
            studentListView,
            validerBtn
        );
        Tab tabAuto = new Tab("Automatique", autoRoot);
        tabAuto.setClosable(false);

        // --- Onglets vides pour la suite ---
        Tab tabManual = new Tab("Manuelle", new Label("À implémenter"));
        tabManual.setClosable(false);
        Tab tabPond = new Tab("Pondération", new Label("À implémenter"));
        tabPond.setClosable(false);

        tabPane.getTabs().addAll(tabAuto, tabManual, tabPond);

        Scene scene = new Scene(tabPane, 500, 600);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Affichage détaillé d'une association
    private void showAssociationDetailsScene(AssociationStudent assoc) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Détails de l'association");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);

        HBox centerBox = new HBox(40);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(30));

        VBox hostBox = new VBox(8,
            createInfoLabel("Hôte : " + assoc.getHost().getName() + " " + assoc.getHost().getForename()),
            createInfoLabel("Pays : " + assoc.getHost().getCountry().getFullName()),
            createInfoLabel("Genre : " + assoc.getHost().getGender()),
            createInfoLabel("Âge : " + assoc.getHost().getAge()),
            createInfoLabel("Contraintes : " + assoc.getHost().getConstraintsMap())
        );
        hostBox.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10; -fx-padding: 15;");
        hostBox.setPrefWidth(350);

        VBox guestBox = new VBox(8,
            createInfoLabel("Invité : " + assoc.getGuest().getName() + " " + assoc.getGuest().getForename()),
            createInfoLabel("Pays : " + assoc.getGuest().getCountry().getFullName()),
            createInfoLabel("Genre : " + assoc.getGuest().getGender()),
            createInfoLabel("Âge : " + assoc.getGuest().getAge()),
            createInfoLabel("Contraintes : " + assoc.getGuest().getConstraintsMap())
        );
        guestBox.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10; -fx-padding: 15;");
        guestBox.setPrefWidth(350);

        centerBox.getChildren().addAll(hostBox, guestBox);
        root.setCenter(centerBox);

        // Affichage du score et de la description d'affinité
        VBox scoreBox = new VBox(10);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(10));

        Label scoreLabel = createInfoLabel("Score d'affinité : " +
            (assoc.getScoreAssociation() != null ? assoc.getScoreAssociation() : "Association impossible"));
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label descLabel = createInfoLabel("Description : " + assoc.describeLevelOfAffinity());
        descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        scoreBox.getChildren().addAll(scoreLabel, descLabel);

        // Navigation
        HBox nav = new HBox(20);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(20));

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showMainMenuScene());
        nav.getChildren().addAll(retourButton);

        // Ajoute la navigation sous le score
        VBox bottomBox = new VBox(10, scoreBox, nav);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        return label;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Historique (à compléter)
    private void showHistoriqueScene() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Historique des associations");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Historique des associations");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox listBox = new VBox(8);
        listBox.setAlignment(Pos.CENTER);

        Map<String, List<AssociationStudent>> historique = historyManager.getHistorique();
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
                        if (assoc.getScoreAssociation() == null) continue; // <-- Ajoute ce filtre ici aussi

                        Label label = new Label(
                            assoc.getHost().getName() + " " + assoc.getHost().getForename() +
                            " ⇄ " +
                            assoc.getGuest().getName() + " " + assoc.getGuest().getForename() +
                            " | Affinité : " + assoc.describeLevelOfAffinity()
                        );
                        label.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");
                        label.setMaxWidth(320); // Limite la largeur
                        label.setPrefWidth(320);
                        label.setWrapText(true); // Retour à la ligne automatique
                        listBox.getChildren().add(label);
                    }
                }
            }
        }

        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #6A6AAA;");
        scroll.setPrefHeight(300);

        // Boutons centrés
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button clearBtn = new Button("Vider l'historique");
        clearBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px;");
        clearBtn.setOnAction(e -> {
            historyManager.clearHistorique();
            historyManager.saveToFile("POO/data/historique.dat");
            dialog.close();
            showAlert("Historique vidé", "L'historique a bien été supprimé.");
        });

        Button closeBtn = new Button("Fermer");
        closeBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        closeBtn.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(clearBtn, closeBtn);

        root.getChildren().setAll(title, scroll, buttonBox);

        Scene scene = new Scene(root, 500, 470);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Nouvelle fenêtre modale pour les étudiants restants
    private void showRemainingStudentsDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Étudiants sans matching");

        // Filtrer uniquement les étudiants des pays sélectionnés
        Set<Student> matched = new HashSet<>();
        for (AssociationStudent assoc : associations) {
            if (assoc.getScoreAssociation() != null) {
                matched.add(assoc.getHost());
                matched.add(assoc.getGuest());
            }
        }
        List<Student> remaining = students.stream()
            .filter(s -> (selectedHost != null && s.getCountry().equals(selectedHost)) ||
                         (selectedGuest != null && s.getCountry().equals(selectedGuest)))
            .filter(s -> !matched.contains(s))
            .filter(s -> !ignoredHosts.contains(s) && !ignoredGuests.contains(s)) // <-- Ajoute cette ligne
            .toList();

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #4A4A8A; -fx-background-radius: 18;");

        Label title = new Label("Étudiants sans matching (" + remaining.size() + ")");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox listBox = new VBox(8);
        listBox.setAlignment(Pos.CENTER);

        for (Student s : remaining) {
            Label label = new Label(s.getName() + " " + s.getForename() + " (" + s.getCountry().getFullName() + ")");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");
            listBox.getChildren().add(label);
        }

        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent;");
        scroll.setPrefHeight(300);

        Button closeBtn = new Button("Fermer");
        closeBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        closeBtn.setOnAction(e -> dialog.close());

        root.getChildren().addAll(title, scroll, closeBtn);

        Scene scene = new Scene(root, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showListeEtudiantsScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        // Label principal en haut
        Label title = new Label("Liste de tous les étudiants du CSV");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(30, 0, 20, 0));
        root.setTop(title);

        // Liste centrée dans un VBox avec fond bleu clair
        VBox listBox = new VBox(12);
        listBox.setAlignment(Pos.CENTER);
        listBox.setPadding(new Insets(30));
        for (Student student : students) {
            Label label = new Label(student.getName() + " " + student.getForename() + " (" + student.getCountry().getFullName() + ")");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            listBox.getChildren().add(label);
        }

        StackPane centerPane = new StackPane();
        centerPane.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 14;");
        centerPane.setPadding(new Insets(40, 60, 40, 60));
        centerPane.getChildren().add(listBox);

        ScrollPane scroll = new ScrollPane(centerPane);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent;");
        scroll.setPrefHeight(400);
        root.setCenter(scroll);

        // Bouton retour en bas, centré
        Button retourBtn = new Button("Retour");
        retourBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        retourBtn.setOnAction(e -> showMainMenuScene());
        HBox nav = new HBox(retourBtn);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(30));
        root.setBottom(nav);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }
    
}