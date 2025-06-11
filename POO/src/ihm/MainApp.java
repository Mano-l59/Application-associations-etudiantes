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
    private HistoryManager historyManager = new HistoryManager();
    private File loadedFile = null;
    private static final String HISTORIQUE_FILE = "POO/data/historique.dat";

    // Champs pour la configuration
    private Country selectedHost = null;
    private Country selectedGuest = null;
    private MatchingEnum selectedAlgo = null;
    private boolean configDone = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Gestion des étudiants");
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

        Scene scene = new Scene(root, 800, 600);
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
        VBox studentsBox = new VBox(15);
        studentsBox.setPadding(new Insets(20));
        studentsBox.setAlignment(Pos.TOP_CENTER);
        studentsBox.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");
        studentsBox.getChildren().add(createInfoLabel("Étudiants valides (" + students.size() + ") :"));
        for (Student student : students) {
            VBox studentCard = new VBox(3);
            studentCard.setStyle("-fx-background-color: #ffffff22; -fx-background-radius: 8; -fx-padding: 8;");
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
        studentsScroll.setPrefWidth(350);
        root.setLeft(studentsScroll);

        // Étudiants rejetés (droite)
        VBox failedBox = new VBox(15);
        failedBox.setPadding(new Insets(20));
        failedBox.setAlignment(Pos.TOP_CENTER);
        failedBox.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");
        failedBox.getChildren().add(createInfoLabel("Étudiants rejetés (" + failedStudents.size() + ") :"));
        for (String line : failedStudents) {
            VBox failedCard = new VBox(3);
            failedCard.setStyle("-fx-background-color: #ff000022; -fx-background-radius: 8; -fx-padding: 8;");
            failedCard.setAlignment(Pos.CENTER_LEFT);
            failedCard.getChildren().add(createInfoLabel(line));
            failedBox.getChildren().add(failedCard);
        }
        ScrollPane failedScroll = new ScrollPane(failedBox);
        failedScroll.setFitToWidth(true);
        failedScroll.setStyle("-fx-background: transparent;");
        failedScroll.setPrefWidth(350);
        root.setRight(failedScroll);

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

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
    }

    // 3. Menu principal (matching, boutons, configuration)
    private void showMainMenuScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        // Titre
        Label title = new Label("Menu principal");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);

        // Centre : résultats d'appariement ou placeholder
        BorderPane centerPane = new BorderPane();
        centerPane.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");
        centerPane.setPadding(new Insets(30));

        VBox resultsBox = new VBox(20);
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setPadding(new Insets(10));

        // Affichage dynamique des résultats
        Runnable updateResults = () -> {
            resultsBox.getChildren().clear();
            if (associations.isEmpty()) {
                Label noMatch = new Label("Aucun matching exécuté pour l'instant.");
                noMatch.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
                resultsBox.getChildren().add(noMatch);
            } else {
                for (AssociationStudent assoc : associations) {
                    HBox pairBox = new HBox(30);
                    pairBox.setAlignment(Pos.CENTER);
                    pairBox.setPadding(new Insets(12));
                    pairBox.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.13);"
                        + "-fx-background-radius: 16;"
                        + "-fx-border-radius: 16;"
                        + "-fx-border-color: #ffffff44;"
                        + "-fx-border-width: 1;"
                        + "-fx-cursor: hand;"
                    );

                    VBox hostBox = new VBox(3,
                        createInfoLabel("Hôte : " + assoc.getHost().getName() + " " + assoc.getHost().getForename()),
                        createInfoLabel(assoc.getHost().getCountry().getFullName())
                    );
                    hostBox.setAlignment(Pos.CENTER_LEFT);

                    VBox guestBox = new VBox(3,
                        createInfoLabel("Invité : " + assoc.getGuest().getName() + " " + assoc.getGuest().getForename()),
                        createInfoLabel(assoc.getGuest().getCountry().getFullName())
                    );
                    guestBox.setAlignment(Pos.CENTER_LEFT);

                    Label arrow = new Label("\u21C4"); // ⇄
                    arrow.setStyle("-fx-text-fill: white; -fx-font-size: 36px;");

                    pairBox.getChildren().addAll(hostBox, arrow, guestBox);
                    pairBox.setOnMouseClicked(e -> showAssociationDetailsScene(assoc));
                    resultsBox.getChildren().add(pairBox);
                }
            }
        };
        updateResults.run();

        ScrollPane scrollPane = new ScrollPane(resultsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");
        centerPane.setCenter(scrollPane);

        // Bouton Export en bas à droite de la box résultats
        Button exportButton = new Button("Exporter en CSV");
        exportButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        exportButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("appariement.csv");
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
        HBox exportBox = new HBox(exportButton);
        exportBox.setAlignment(Pos.BOTTOM_RIGHT);
        exportBox.setPadding(new Insets(10, 10, 0, 0));
        centerPane.setBottom(exportBox);

        root.setCenter(centerPane);

        // Boutons en bas
        Button voirElevesBtn = new Button("Voir étudiants");
        voirElevesBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        voirElevesBtn.setOnAction(e -> showGestionElevesScene());

        Button changerCsvBtn = new Button("Changer CSV");
        changerCsvBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        changerCsvBtn.setOnAction(e -> showSelectionCSVScene());

        Button executerBtn = new Button("Exécuter");
        executerBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        executerBtn.setDisable(!configDone);

        executerBtn.setOnAction(e -> {
            if (selectedHost != null && selectedGuest != null && selectedAlgo != null) {
                // Exécute le matching et met à jour resultsBox
                List<Student> hostsList = students.stream().filter(s -> s.getCountry().equals(selectedHost)).toList();
                List<Student> guestsList = students.stream().filter(s -> s.getCountry().equals(selectedGuest)).toList();
                Set<Student> hosts = new HashSet<>(hostsList);
                Set<Student> guests = new HashSet<>(guestsList);

                historyManager.loadFromFile(HISTORIQUE_FILE);
                MatchingSolver solver = new MatchingSolver(hosts, guests, historyManager);
                associations = solver.algorithmMatching(selectedAlgo);
                historyManager.addOrReplaceMatching(selectedHost, selectedGuest, associations);
                historyManager.saveToFile(HISTORIQUE_FILE);

                updateResults.run();
            } else {
                showAlert("Configuration incomplète", "Veuillez configurer les paramètres avant d'exécuter.");
            }
        });

        Button historiqueBtn = new Button("Voir historique");
        historiqueBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        historiqueBtn.setOnAction(e -> showHistoriqueScene());

        Button configBtn = new Button("Configuration");
        configBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        configBtn.setOnAction(e -> showConfigurationDialog(executerBtn));

        HBox nav = new HBox(20, voirElevesBtn, changerCsvBtn, executerBtn, historiqueBtn, configBtn);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(20));
        root.setBottom(nav);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
    }

    // Fenêtre modale de configuration
    private void showConfigurationDialog(Button executerBtn) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Configuration");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #4A4A8A;");

        // Pays
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

        // Algo
        ComboBox<MatchingEnum> algoCombo = new ComboBox<>();
        algoCombo.getItems().addAll(MatchingEnum.values());
        algoCombo.setPromptText("Algorithme");
        if (selectedAlgo != null) algoCombo.setValue(selectedAlgo);

        // Pondérations (à compléter)
        Label pondLabel = createInfoLabel("Pondérations (non implémenté ici)");

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

        root.getChildren().addAll(
            createInfoLabel("Pays qui reçoit :"), hostCombo,
            createInfoLabel("Pays qui visite :"), guestCombo,
            createInfoLabel("Algorithme :"), algoCombo,
            pondLabel,
            validerBtn
        );

        Scene scene = new Scene(root, 400, 450);
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

        Button exportButton = new Button("Exporter en CSV");
        exportButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        exportButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("details_association.csv");
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    CSVExport.exportMatchingToCsv(Collections.singletonList(assoc), file.getAbsolutePath().replace(".csv", ""));
                    showAlert("Export réussi", "Le fichier a été exporté avec succès !");
                } catch (IOException ex) {
                    showAlert("Erreur", "Erreur lors de l'export : " + ex.getMessage());
                }
            }
        });

        nav.getChildren().addAll(retourButton, exportButton);

        // Ajoute la navigation sous le score
        VBox bottomBox = new VBox(10, scoreBox, nav);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 800, 550);
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
        // À implémenter si besoin
    }

    public static void main(String[] args) {
        launch(args);
    }
}