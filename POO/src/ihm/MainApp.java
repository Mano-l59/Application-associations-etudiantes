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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import manager.StudentManager;

public class MainApp extends Application{
    
    private Stage primaryStage;
    private List<Student> students = new ArrayList<>();
    private List<Student> selectedStudents = new ArrayList<>();
    private List<AssociationStudent> associations = new ArrayList<>();
    private List<Student> allStudents = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Gestion des étudiants");
        showSelectionCSVScene();
        primaryStage.show();
    }
    
    // Scène 1: Sélection CSV
    private void showSelectionCSVScene(){
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Sélection du fichier CSV");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox fileSelection = new VBox(10);
        fileSelection.setAlignment(Pos.CENTER);
        fileSelection.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");

        Label fileLabel = new Label("Veuillez sélectionner votre fichier CSV :");
        fileLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER);
        TextField fileField = new TextField();
        fileField.setPrefWidth(300);
        Button browseButton = new Button("Parcourir");

        final File[] selectedFile = new File[1];

        Label sizeLabel = new Label("Taille du csv : ");
        sizeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        Label countLabel = new Label("Nombre d'élèves : ");
        countLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        StudentManager studentManager = new StudentManager();

        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if(file != null){
                fileField.setText(file.getAbsolutePath());
                selectedFile[0] = file;
                try {
                    studentManager.loadStudentsFromCsv(file.getAbsolutePath());
                    students = studentManager.getStudents();
                    allStudents = new ArrayList<>(students); // Sauvegarde tous les étudiants
                } catch (Exception ex) {
                    students.clear();
                    allStudents.clear();
                }
                sizeLabel.setText("Taille du csv : " + file.length() + " octets");
                countLabel.setText("Nombre d'élèves : " + students.size());
            }
        });

        fileBox.getChildren().addAll(fileField, browseButton);

        Button validateButton = new Button("Valider");
        validateButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        validateButton.setOnAction(e -> {
            if(!students.isEmpty()){
                showGestionElevesScene();
            }
        });

        fileSelection.getChildren().addAll(fileLabel, fileBox, sizeLabel, countLabel);

        root.getChildren().addAll(title, fileSelection, validateButton);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    // Nouvelle scène pour choisir les pays hôte et invité
    private void showChoixPaysScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Sélection des pays hôte et invité");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Récupère tous les pays présents dans la liste des étudiants
        List<Country> countries = students.stream().map(Student::getCountry).distinct().toList();

        if (countries.size() < 2) {
            // Si un seul pays, on ne peut pas faire de matching biparti
            Label info = new Label("Il faut au moins deux pays différents pour créer des associations biparties.");
            info.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            root.getChildren().addAll(title, info);

            Button retourButton = new Button("Retour");
            retourButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
            retourButton.setOnAction(e -> showGestionElevesScene());
            root.getChildren().add(retourButton);

            Scene scene = new Scene(root, 600, 300);
            primaryStage.setScene(scene);
            return;
        }

        ComboBox<Country> hostCombo = new ComboBox<>();
        hostCombo.getItems().addAll(countries);
        hostCombo.setValue(countries.get(0));
        hostCombo.setPrefWidth(200);

        ComboBox<Country> guestCombo = new ComboBox<>();
        guestCombo.getItems().addAll(countries);
        guestCombo.setValue(countries.size() > 1 ? countries.get(1) : countries.get(0));
        guestCombo.setPrefWidth(200);

        HBox choixBox = new HBox(20);
        choixBox.setAlignment(Pos.CENTER);
        choixBox.getChildren().addAll(
            new Label("Pays hôte :"), hostCombo,
            new Label("Pays invité :"), guestCombo
        );

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        Button validerButton = new Button("Valider");
        validerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        validerButton.setOnAction(e -> {
            Country hostCountry = hostCombo.getValue();
            Country guestCountry = guestCombo.getValue();
            if (hostCountry == null || guestCountry == null) {
                errorLabel.setText("Veuillez sélectionner deux pays.");
                return;
            }
            if (hostCountry.equals(guestCountry)) {
                errorLabel.setText("Le pays hôte et le pays invité doivent être différents.");
                return;
            }
            // Passe à la scène de gestion avec les pays sélectionnés
            showGestionElevesScene();
        });

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showGestionElevesScene());

        HBox navButtons = new HBox(20, retourButton, validerButton);
        navButtons.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, choixBox, errorLabel, navButtons);

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setScene(scene);
    }

    // Scène 2: Gestion des élèves
    private void showGestionElevesScene(){
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Gestion des élèves");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);

        VBox pairsVBox = new VBox(15);
        pairsVBox.setAlignment(Pos.CENTER);
        pairsVBox.setPadding(new Insets(20));

        List<AssociationStudent> currentAssociations = new ArrayList<>();

        if (!associations.isEmpty()) {
            currentAssociations.addAll(associations);
        } else if (students != null && students.size() >= 2) {
            try {
                // Récupère tous les pays présents
                List<Country> countries = students.stream().map(Student::getCountry).distinct().toList();

                // Si plusieurs pays, propose d'aller choisir explicitement les pays
                if (countries.size() > 1) {
                    Label info = new Label("Plusieurs pays détectés. Cliquez sur le bouton ci-dessous pour choisir les pays hôte et invité.");
                    info.setStyle("-fx-text-fill: yellow; -fx-font-size: 16px;");
                    pairsVBox.getChildren().add(info);

                    Button choixPaysButton = new Button("Choisir pays hôte/invité");
                    choixPaysButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
                    choixPaysButton.setOnAction(e -> showChoixPaysScene());
                    pairsVBox.getChildren().add(choixPaysButton);
                } else {
                    // Mono-pays : matching sur tous les élèves
                    algorithm.MatchingSolver solver = new algorithm.MatchingSolver(
                        new java.util.HashSet<>(students),
                        new java.util.HashSet<>(students),
                        null
                    );
                    currentAssociations = solver.algorithmMatching(basicclass.MatchingEnum.HONGROIS_MATCHING);

                    for (AssociationStudent assoc : currentAssociations){
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

                        pairBox.setOnMouseClicked(e -> showInformationsCompletesScene(assoc.getHost(), assoc.getGuest()));

                        pairsVBox.getChildren().add(pairBox);
                    }
                }
            } catch (Exception ex) {
                pairsVBox.getChildren().add(createInfoLabel("Erreur lors du calcul des associations : " + ex.getMessage()));
            }
        } else {
            pairsVBox.getChildren().add(createInfoLabel("Aucun élève chargé."));
        }

        ScrollPane scrollPane = new ScrollPane(pairsVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        root.setCenter(scrollPane);

        // Boutons de navigation en bas (fond noir, police blanche)
        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));

        Button retourAccueilButton = new Button("Retour accueil");
        retourAccueilButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        retourAccueilButton.setOnAction(e -> showSelectionCSVScene());

        Button bloquerButton = new Button("Bloquer adolescent");
        bloquerButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        bloquerButton.setOnAction(e -> showBloquerAdolescentScene());

        Button ponderationsButton = new Button("Ajuster pondérations");
        ponderationsButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        ponderationsButton.setOnAction(e -> showAjusterPonderationsScene());

        Button affectationButton = new Button("Fixer affectation");
        affectationButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        affectationButton.setOnAction(e -> showFixerAffectationScene());

        navButtons.getChildren().addAll(retourAccueilButton, bloquerButton, ponderationsButton, affectationButton);

        root.setBottom(navButtons);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
    }
    
    // Scène 3: Bloquer adolescent
    private void showBloquerAdolescentScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Bloquer adolescent");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox studentList = new VBox(10);
        studentList.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");
        studentList.setAlignment(Pos.CENTER_LEFT);

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (Student s : allStudents) {
            HBox studentBox = new HBox(10);
            CheckBox checkbox = new CheckBox();
            checkbox.setSelected(students.contains(s)); // Coche si actif
            Label studentLabel = new Label("Élève ID " + s.getId() + " : " + s.getName() + " " + s.getForename());
            studentLabel.setStyle("-fx-text-fill: white;");
            studentBox.getChildren().addAll(studentLabel, checkbox);
            studentList.getChildren().add(studentBox);
            checkBoxes.add(checkbox);
        }

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showGestionElevesScene());

        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> {
            List<Student> actifs = new ArrayList<>();
            for (int i = 0; i < allStudents.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    actifs.add(allStudents.get(i));
                }
            }
            students = actifs;
            showGestionElevesScene();
        });

        HBox navButtons = new HBox(20, retourButton, terminerButton);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));

        root.getChildren().addAll(title, studentList, navButtons);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }
    
    // Variables pour pondérations (exemple)
    private int pondHobbie = 1;
    private int pondGenre = 1;
    private int pondAge = 1;

    // Scène 4: Ajuster pondérations
    private void showAjusterPonderationsScene() {
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
        TextField hobbieField = new TextField(String.valueOf(pondHobbie));
        hobbieField.setPrefWidth(100);
        hobbieBox.getChildren().addAll(hobbieLabel, hobbieField);

        HBox genreBox = new HBox(10);
        genreBox.setAlignment(Pos.CENTER);
        Label genreLabel = new Label("Valeur genre :");
        genreLabel.setStyle("-fx-text-fill: white;");
        TextField genreField = new TextField(String.valueOf(pondGenre));
        genreField.setPrefWidth(100);
        genreBox.getChildren().addAll(genreLabel, genreField);

        HBox ageBox = new HBox(10);
        ageBox.setAlignment(Pos.CENTER);
        Label ageLabel = new Label("Valeur âge :");
        ageLabel.setStyle("-fx-text-fill: white;");
        TextField ageField = new TextField(String.valueOf(pondAge));
        ageField.setPrefWidth(100);
        ageBox.getChildren().addAll(ageLabel, ageField);

        adjustments.getChildren().addAll(hobbieBox, genreBox, ageBox);

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showGestionElevesScene());

        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> {
            try {
                pondHobbie = Integer.parseInt(hobbieField.getText());
                pondGenre = Integer.parseInt(genreField.getText());
                pondAge = Integer.parseInt(ageField.getText());
            } catch (NumberFormatException ex) {
                pondHobbie = 1;
                pondGenre = 1;
                pondAge = 1;
            }
            showGestionElevesScene();
        });

        HBox navButtons = new HBox(20, retourButton, terminerButton);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));

        root.getChildren().addAll(title, adjustments, navButtons);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }
    
    // Scène 5: Fixer affectation
    private void showFixerAffectationScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Fixer affectation");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox associationsBox = new VBox(10);
        associationsBox.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");
        associationsBox.setAlignment(Pos.CENTER);

        int nbPairs = students.size() / 2;
        List<ComboBox<Student>> combo1List = new ArrayList<>();
        List<ComboBox<Student>> combo2List = new ArrayList<>();

        // Utilise les associations existantes si elles existent, sinon crée par défaut
        List<AssociationStudent> currentAssociations;
        if (!associations.isEmpty()) {
            currentAssociations = new ArrayList<>(associations);
        } else {
            currentAssociations = new ArrayList<>();
            for (int i = 0; i + 1 < students.size(); i += 2) {
                currentAssociations.add(new AssociationStudent(students.get(i), students.get(i + 1)));
            }
        }

        for (int i = 0; i < nbPairs; i++) {
            HBox assocBox = new HBox(10);
            assocBox.setAlignment(Pos.CENTER);

            Label assocLabel = new Label("Association n°" + (i + 1) + " :");
            assocLabel.setStyle("-fx-text-fill: white;");
            assocLabel.setPrefWidth(120);

            ComboBox<Student> combo1 = new ComboBox<>();
            ComboBox<Student> combo2 = new ComboBox<>();
            combo1.getItems().addAll(students);
            combo2.getItems().addAll(students);

            // Préselectionne selon les associations actuelles
            AssociationStudent assoc = currentAssociations.get(i);
            combo1.setValue(assoc.getHost());
            combo2.setValue(assoc.getGuest());

            combo1.setPrefWidth(180);
            combo2.setPrefWidth(180);

            Label arrow = new Label("→");
            arrow.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

            assocBox.getChildren().addAll(assocLabel, combo1, arrow, combo2);
            associationsBox.getChildren().add(assocBox);

            combo1List.add(combo1);
            combo2List.add(combo2);
        }

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showGestionElevesScene());

        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> {
            // Met à jour la liste globale des associations utilisée par la scène 2
            associations.clear();
            for (int i = 0; i < nbPairs; i++) {
                Student s1 = combo1List.get(i).getValue();
                Student s2 = combo2List.get(i).getValue();
                if (s1 != null && s2 != null && !s1.equals(s2)) {
                    associations.add(new AssociationStudent(s1, s2));
                }
            }
            showGestionElevesScene();
        });

        HBox navButtons = new HBox(20, retourButton, terminerButton);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));

        root.getChildren().addAll(title, associationsBox, navButtons);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
    }
    
    // Scène 6: Informations complètes des étudiants
    private void showInformationsCompletesScene(Student eleve1, Student eleve2){
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Informations complètes des étudiants");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);

        HBox centerBox = new HBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        VBox eleve1Box = new VBox(10);
        eleve1Box.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 15;");
        eleve1Box.setPrefWidth(350);

        Label eleve1Title = new Label("Élève ID " + (eleve1 != null ? eleve1.getId() : ""));
        eleve1Title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox eleve1Info = new VBox(5);
        if (eleve1 != null) {
            eleve1Info.getChildren().addAll(
                createInfoLabel("Nom : " + eleve1.getName()),
                createInfoLabel("Prénom : " + eleve1.getForename()),
                createInfoLabel("Genre : " + eleve1.getGender()),
                createInfoLabel("Date de naissance : " + eleve1.getBirthday()),
                createInfoLabel("Pays : " + eleve1.getCountry()),
                createInfoLabel("Contraintes : " + eleve1.getConstraintsMap())
            );
        }
        eleve1Box.getChildren().addAll(eleve1Title, eleve1Info);

        VBox eleve2Box = new VBox(10);
        eleve2Box.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 15;");
        eleve2Box.setPrefWidth(350);

        Label eleve2Title = new Label("Élève ID " + (eleve2 != null ? eleve2.getId() : ""));
        eleve2Title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox eleve2Info = new VBox(5);
        if (eleve2 != null) {
            eleve2Info.getChildren().addAll(
                createInfoLabel("Nom : " + eleve2.getName()),
                createInfoLabel("Prénom : " + eleve2.getForename()),
                createInfoLabel("Genre : " + eleve2.getGender()),
                createInfoLabel("Date de naissance : " + eleve2.getBirthday()),
                createInfoLabel("Pays : " + eleve2.getCountry()),
                createInfoLabel("Contraintes : " + eleve2.getConstraintsMap())
            );
        }
        eleve2Box.getChildren().addAll(eleve2Title, eleve2Info);

        centerBox.getChildren().addAll(eleve1Box, eleve2Box);
        root.setCenter(centerBox);

        // Bouton retour en bas
        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14px;");
        retourButton.setOnAction(e -> showGestionElevesScene());

        navButtons.getChildren().add(retourButton);
        root.setBottom(navButtons);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }
    
    private Label createInfoLabel(String text){
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        return label;
    }
    
    private void loadStudentsFromCSV(File file){
        students.clear();
        for(int i = 1; i <= 8; i++){
            HashMap<Constraints, String> constraints = Student.constraintsMapInit();
            Student student = new Student(
                "Nom" + i,
                "Prénom" + i,
                i % 2 == 0 ? "M" : "F",
                LocalDate.of(2000 + i, 1, 1),
                Country.FR,
                constraints
            );
            students.add(student);
        }
    }
    
    public static void main(String[] args){
        launch(args);
    }
}