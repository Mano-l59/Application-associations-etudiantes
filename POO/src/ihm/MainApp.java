package ihm;

import javafx.application.Application;
import javafx.stage.Stage;
import manager.HistoryManager;
import basicclass.*;
import java.util.*;

public class MainApp extends Application {
    private Stage primaryStage;
    public static HistoryManager historyManager = new HistoryManager();

    // État global
    public List<Student> students = new ArrayList<>();
    public List<String> failedStudents = new ArrayList<>();
    public List<AssociationStudent> associations = new ArrayList<>();
    public Country selectedHost = null;
    public Country selectedGuest = null;
    public MatchingEnum selectedAlgo = null;
    public boolean configDone = false;
    public Set<Student> ignoredHosts = new HashSet<>();
    public Set<Student> ignoredGuests = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        historyManager.loadFromFile("POO/data/historique.dat");
        primaryStage.setTitle("Gestion des étudiants");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.setWidth(1100);
        primaryStage.setHeight(750);
        ShowSelectionCSVScene.show(primaryStage, this);
        primaryStage.show();
    }

    public Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        launch(args);
    }
}