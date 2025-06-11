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
                } catch (Exception ex) {
                    students.clear();
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
    
    // Scène 2: Gestion des élèves
    private void showGestionElevesScene(){
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        // Titre en haut
        Label title = new Label("Gestion des élèves");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);

        VBox pairsVBox = new VBox(15);
        pairsVBox.setAlignment(Pos.CENTER);
        pairsVBox.setPadding(new Insets(20));

        List<AssociationStudent> pairs = new ArrayList<>();
        for (int i = 0; i + 1 < students.size(); i += 2){
            pairs.add(new AssociationStudent(students.get(i), students.get(i + 1)));
        }

        for (AssociationStudent assoc : pairs){
            HBox pairBox = new HBox(30);
            pairBox.setAlignment(Pos.CENTER);
            pairBox.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");

            VBox hostBox = new VBox(3);
            hostBox.setAlignment(Pos.CENTER);
            hostBox.getChildren().addAll(
                createInfoLabel("Hôte : " + assoc.getHost().getName() + " " + assoc.getHost().getForename()),
                createInfoLabel("Genre : " + assoc.getHost().getGender()),
                createInfoLabel("Âge : " + assoc.getHost().getAge()),
                createInfoLabel("Pays : " + assoc.getHost().getCountry().getFullName())
            );

            VBox guestBox = new VBox(3);
            guestBox.setAlignment(Pos.CENTER);
            guestBox.getChildren().addAll(
                createInfoLabel("Invité : " + assoc.getGuest().getName() + " " + assoc.getGuest().getForename()),
                createInfoLabel("Genre : " + assoc.getGuest().getGender()),
                createInfoLabel("Âge : " + assoc.getGuest().getAge()),
                createInfoLabel("Pays : " + assoc.getGuest().getCountry().getFullName())
            );

            VBox arrowBox = new VBox();
            arrowBox.setAlignment(Pos.CENTER);
            Label arrow = new Label("⇄");
            arrow.setStyle("-fx-text-fill: white; -fx-font-size: 36px;");
            arrowBox.getChildren().add(arrow);

            pairBox.getChildren().addAll(hostBox, arrowBox, guestBox);

            // Action : afficher la scène détaillée pour cette association
            pairBox.setOnMouseClicked(e -> showInformationsCompletesScene(assoc.getHost(), assoc.getGuest()));

            pairsVBox.getChildren().add(pairBox);
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

        Button bloquerButton = new Button("Bloquer adolescent");
        bloquerButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        bloquerButton.setOnAction(e -> showBloquerAdolescentScene());

        Button ponderationsButton = new Button("Ajuster pondérations");
        ponderationsButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        ponderationsButton.setOnAction(e -> showAjusterPonderationsScene());

        Button affectationButton = new Button("Fixer affectation");
        affectationButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        affectationButton.setOnAction(e -> showFixerAffectationScene());

        navButtons.getChildren().addAll(bloquerButton, ponderationsButton, affectationButton);

        root.setBottom(navButtons);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
    }
    
    // Scène 3: Bloquer adolescent
    private void showBloquerAdolescentScene(){
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");
        
        Label title = new Label("Bloquer adolescent");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        VBox studentList = new VBox(10);
        studentList.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");
        studentList.setAlignment(Pos.CENTER_LEFT);
        
        for(int i = 1; i <= 5; i++){
            HBox studentBox = new HBox(10);
            CheckBox checkbox = new CheckBox();
            Label studentLabel = new Label("Élève " + i + " :");
            studentLabel.setStyle("-fx-text-fill: white;");
            
            if(i == 5){
                checkbox.setSelected(false);
            } else{
                checkbox.setSelected(true);
            }
            
            studentBox.getChildren().addAll(studentLabel, checkbox);
            studentList.getChildren().add(studentBox);
        }
        
        Label dots = new Label("... :");
        dots.setStyle("-fx-text-fill: white;");
        studentList.getChildren().add(dots);
        
        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> showGestionElevesScene());
        
        root.getChildren().addAll(title, studentList, terminerButton);
        
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }
    
    // Scène 4: Ajuster pondérations
    private void showAjusterPonderationsScene(){
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");
        
        Label title = new Label("Ajuster pondérations");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        VBox adjustments = new VBox(15);
        adjustments.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");
        adjustments.setAlignment(Pos.CENTER);
        
        HBox hobbieBox = new HBox(10);
        hobbieBox.setAlignment(Pos.CENTER);
        Label hobbieLabel = new Label("Valeur hobbie :");
        hobbieLabel.setStyle("-fx-text-fill: white;");
        TextField hobbieField = new TextField();
        hobbieField.setPrefWidth(100);
        hobbieBox.getChildren().addAll(hobbieLabel, hobbieField);
        
        HBox genreBox = new HBox(10);
        genreBox.setAlignment(Pos.CENTER);
        Label genreLabel = new Label("Valeur genre :");
        genreLabel.setStyle("-fx-text-fill: white;");
        TextField genreField = new TextField();
        genreField.setPrefWidth(100);
        genreBox.getChildren().addAll(genreLabel, genreField);
        
        HBox ageBox = new HBox(10);
        ageBox.setAlignment(Pos.CENTER);
        Label ageLabel = new Label("Valeur âge :");
        ageLabel.setStyle("-fx-text-fill: white;");
        TextField ageField = new TextField();
        ageField.setPrefWidth(100);
        ageBox.getChildren().addAll(ageLabel, ageField);
        
        Label dots = new Label("... :");
        dots.setStyle("-fx-text-fill: white;");
        TextField dotsField = new TextField();
        dotsField.setPrefWidth(100);
        HBox dotsBox = new HBox(10);
        dotsBox.setAlignment(Pos.CENTER);
        dotsBox.getChildren().addAll(dots, dotsField);
        
        adjustments.getChildren().addAll(hobbieBox, genreBox, ageBox, dotsBox);
        
        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> showGestionElevesScene());
        
        root.getChildren().addAll(title, adjustments, terminerButton);
        
        Scene scene = new Scene(root, 600, 500);
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

        Label title = new Label("Fixer affectation");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox associations = new VBox(10);
        associations.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");
        associations.setAlignment(Pos.CENTER);

        // Affichage dynamique des associations selon la liste d'élèves
        int nbPairs = students.size() / 2;
        for (int i = 0; i < nbPairs; i++) {
            HBox assocBox = new HBox(10);
            assocBox.setAlignment(Pos.CENTER);

            Label assocLabel = new Label("Association n°" + (i + 1) + " :");
            assocLabel.setStyle("-fx-text-fill: white;");
            assocLabel.setPrefWidth(120);

            ComboBox<String> combo1 = new ComboBox<>();
            ComboBox<String> combo2 = new ComboBox<>();
            for (int j = 0; j < students.size(); j++) {
                String label = "E" + (j + 1) + " - " + students.get(j).getName() + " " + students.get(j).getForename();
                combo1.getItems().add(label);
                combo2.getItems().add(label);
            }
            combo1.setValue(combo1.getItems().get(i));
            combo1.setPrefWidth(180);
            combo2.setValue(combo2.getItems().get(i + nbPairs));
            combo2.setPrefWidth(180);

            Label arrow = new Label("→");
            arrow.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

            assocBox.getChildren().addAll(assocLabel, combo1, arrow, combo2);
            associations.getChildren().add(assocBox);
        }

        // Boutons de navigation en bas (fond noir, police blanche)
        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showGestionElevesScene());

        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> {
            // On affiche la première association détaillée si dispo
            if (students.size() >= 2) {
                showInformationsCompletesScene(students.get(0), students.get(1));
            }
        });

        navButtons.getChildren().addAll(retourButton, terminerButton);

        root.getChildren().addAll(title, associations, navButtons);

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