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
                } catch (Exception ex) {
                    students.clear();
                }
                // Mise à jour dynamique des labels après sélection
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

        ScrollPane scrollPane = new ScrollPane(pairsVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        root.setCenter(scrollPane);

        // Boutons de navigation en bas (fond noir, police blanche)
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

        Scene scene = new Scene(root, 1000, 600);
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
    
    // Scène 5: Fixer affectation
    private void showFixerAffectationScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
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