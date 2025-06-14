package ihm;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import basicclass.Student;
import ihm.tools.CreateInfoLabel;

public class ShowGestionElevesScene {
    public static void show(Stage primaryStage, MainApp app) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Gestion des élèves");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(20));
        root.setTop(title);

        VBox studentsBox = new VBox(8);
        studentsBox.setPadding(new Insets(20, 0, 20, 0));
        studentsBox.setAlignment(Pos.TOP_CENTER);
        studentsBox.setStyle("-fx-background-color: #6A6AAA;");
        studentsBox.getChildren().add(CreateInfoLabel.create("Étudiants valides (" + app.students.size() + ") :"));
        for (Student student : app.students) {
            VBox studentCard = new VBox(3);
            studentCard.setStyle("-fx-background-color: #ffffff22; -fx-padding: 8;");
            studentCard.setAlignment(Pos.CENTER_LEFT);
            studentCard.getChildren().addAll(
                CreateInfoLabel.create("Nom : " + student.getName()),
                CreateInfoLabel.create("Prénom : " + student.getForename()),
                CreateInfoLabel.create("Pays : " + student.getCountry().getFullName()),
                CreateInfoLabel.create("Genre : " + student.getGender())
            );
            studentsBox.getChildren().add(studentCard);
        }
        ScrollPane studentsScroll = new ScrollPane(studentsBox);
        studentsScroll.setFitToWidth(true);
        studentsScroll.setStyle("-fx-background: transparent;");
        studentsScroll.setPrefWidth(320);

        VBox failedBox = new VBox(8);
        failedBox.setPadding(new Insets(20, 0, 20, 0));
        failedBox.setAlignment(Pos.TOP_CENTER);
        failedBox.setStyle("-fx-background-color: #6A6AAA;");
        failedBox.getChildren().add(CreateInfoLabel.create("Étudiants rejetés (" + app.failedStudents.size() + ") :"));
        for (String line : app.failedStudents) {
            VBox failedCard = new VBox(3);
            failedCard.setStyle("-fx-background-color: #ff000022; -fx-padding: 8;");
            failedCard.setAlignment(Pos.CENTER_LEFT);
            failedCard.getChildren().add(CreateInfoLabel.create(line));
            failedBox.getChildren().add(failedCard);
        }
        ScrollPane failedScroll = new ScrollPane(failedBox);
        failedScroll.setFitToWidth(true);
        failedScroll.setStyle("-fx-background: transparent;");
        failedScroll.setPrefWidth(320);

        HBox centerBox = new HBox(20, studentsScroll, failedScroll);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(0, 0, 0, 0));
        root.setCenter(centerBox);

        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);
        navButtons.setPadding(new Insets(20));
        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        retourButton.setOnAction(e -> ShowSelectionCSVScene.show(primaryStage, app));
        Button continuerButton = new Button("Continuer");
        continuerButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        continuerButton.setOnAction(e -> ShowMainMenuScene.show(primaryStage, app));
        navButtons.getChildren().addAll(retourButton, continuerButton);
        root.setBottom(navButtons);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Élèves");
    }
}