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
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        
        Label title = new Label("Sélection csv");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        VBox fileSelection = new VBox(10);
        fileSelection.setAlignment(Pos.CENTER);
        fileSelection.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");
        
        Label fileLabel = new Label("Veuillez sélectionner votre csv :");
        fileLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER);
        TextField fileField = new TextField();
        fileField.setPrefWidth(200);
        Button browseButton = new Button("...");
        
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if(file != null){
                fileField.setText(file.getAbsolutePath());
                loadStudentsFromCSV(file);
            }
        });
        
        fileBox.getChildren().addAll(fileField, browseButton);
        
        Label sizeLabel = new Label("Taille du csv : ...");
        sizeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        Label countLabel = new Label("Nombre d'élèves : ...");
        countLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        Label dotLabel = new Label("... :");
        dotLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        fileSelection.getChildren().addAll(fileLabel, fileBox, sizeLabel, countLabel, dotLabel);
        
        Button validateButton = new Button("Valider");
        validateButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font²-size: 14px;");
        validateButton.setOnAction(e -> {
            if(!students.isEmpty()){
                showGestionElevesScene();
            } else{
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
        
        Label title = new Label("Gestion des élèves");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);
        
        HBox centerBox = new HBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        
        VBox leftColumn = new VBox(10);
        leftColumn.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 15;");
        leftColumn.setPrefWidth(300);
        
        HBox leftHeader = new HBox(10);
        leftHeader.setAlignment(Pos.CENTER_LEFT);
        Label leftCount = new Label("Nombre d'élèves : " + students.size());
        leftCount.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        CheckBox leftCheckbox = new CheckBox();
        leftHeader.getChildren().addAll(leftCount, leftCheckbox);
        
        ListView<String> leftList = new ListView<>();
        leftList.setPrefHeight(200);
        for(Student student : students){
            leftList.getItems().add(student.toString());
        }
        leftColumn.getChildren().addAll(leftHeader, leftList);
        
        VBox rightColumn = new VBox(10);
        rightColumn.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 15;");
        rightColumn.setPrefWidth(300);
        
        HBox rightHeader = new HBox(10);
        rightHeader.setAlignment(Pos.CENTER_LEFT);
        Label rightCount = new Label("Nombre d'élèves sélectionnés : 0");
        rightCount.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        CheckBox rightCheckbox = new CheckBox();
        rightHeader.getChildren().addAll(rightCount, rightCheckbox);
        
        ListView<String> rightList = new ListView<>();
        rightList.setPrefHeight(200);
        
        rightColumn.getChildren().addAll(rightHeader, rightList);
        
        centerBox.getChildren().addAll(leftColumn, rightColumn);
        root.setCenter(centerBox);
        
        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        
        Label constraintLabel = new Label("Nombre de contraintes réalisatoires non respectées :");
        constraintLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        CheckBox constraintCheck = new CheckBox();
        
        VBox constraintBox = new VBox(5);
        constraintBox.getChildren().addAll(constraintLabel, constraintCheck);
        
        Label problemLabel1 = new Label("Problème de contrainte pour : Élève 1 et Élève 2");
        problemLabel1.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        
        Label problemLabel2 = new Label("Problème de contrainte pour : Élève 4 et Élève 6");
        problemLabel2.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        
        VBox problemBox = new VBox(2);
        problemBox.getChildren().addAll(problemLabel1, problemLabel2);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button bloquerButton = new Button("Bloquer\nadolescent");
        bloquerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        bloquerButton.setOnAction(e -> showBloquerAdolescentScene());
        
        Button ajusterButton = new Button("Ajuster\npondérations");
        ajusterButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        ajusterButton.setOnAction(e -> showAjusterPonderationsScene());
        
        Button fixerButton = new Button("Fixer\naffectation");
        fixerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        fixerButton.setOnAction(e -> showFixerAffectationScene());
        
        buttonBox.getChildren().addAll(bloquerButton, ajusterButton, fixerButton);
        
        VBox bottomContainer = new VBox(10);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.getChildren().addAll(constraintBox, problemBox, buttonBox);
        
        root.setBottom(bottomContainer);
        
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
    private void showFixerAffectationScene(){
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #4A4A8A;");
        
        Label title = new Label("Fixer affectation");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        VBox associations = new VBox(10);
        associations.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 20;");
        
        for(int i = 1; i <= 4; i++){
            HBox assocBox = new HBox(10);
            assocBox.setAlignment(Pos.CENTER_LEFT);
            
            Label assocLabel = new Label("Association n°" + i + " :");
            assocLabel.setStyle("-fx-text-fill: white;");
            assocLabel.setPrefWidth(120);
            
            ComboBox<String> combo1 = new ComboBox<>();
            combo1.getItems().addAll("E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8");
            combo1.setValue("E" + i);
            combo1.setPrefWidth(60);
            
            Label arrow = new Label("→");
            arrow.setStyle("-fx-text-fill: white;");
            
            ComboBox<String> combo2 = new ComboBox<>();
            combo2.getItems().addAll("E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8");
            combo2.setValue("E" +(i + 4));
            combo2.setPrefWidth(60);
            
            assocBox.getChildren().addAll(assocLabel, combo1, arrow, combo2);
            associations.getChildren().add(assocBox);
        }
        
        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> showInformationsCompletesScene());
        
        root.getChildren().addAll(title, associations, terminerButton);
        
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
    }
    
    // Scène 6: Informations complètes des étudiants
    private void showInformationsCompletesScene(){
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
        
        Label eleve1Title = new Label("Élève 1");
        eleve1Title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox eleve1Info = new VBox(5);
        eleve1Info.getChildren().addAll(
            createInfoLabel("Nom :"),
            createInfoLabel("Forename :"),
            createInfoLabel("... :")
        );
        
        eleve1Box.getChildren().addAll(eleve1Title, eleve1Info);
        
        VBox eleve2Box = new VBox(10);
        eleve2Box.setStyle("-fx-background-color: #6A6AAA; -fx-padding: 15;");
        eleve2Box.setPrefWidth(350);
        
        Label eleve2Title = new Label("Élève 2");
        eleve2Title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox eleve2Info = new VBox(5);
        eleve2Info.getChildren().addAll(
            createInfoLabel("Nom :"),
            createInfoLabel("Forename :"),
            createInfoLabel("... :")
        );
        
        eleve2Box.getChildren().addAll(eleve2Title, eleve2Info);
        
        centerBox.getChildren().addAll(eleve1Box, eleve2Box);
        root.setCenter(centerBox);
        
        Button terminerButton = new Button("Terminer");
        terminerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;");
        terminerButton.setOnAction(e -> showSelectionCSVScene());
        
        BorderPane.setAlignment(terminerButton, Pos.CENTER);
        BorderPane.setMargin(terminerButton, new Insets(20));
        root.setBottom(terminerButton);
        
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