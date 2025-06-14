package ihm;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import basicclass.Student;

public class ShowListeEtudiantsScene {
    public static void show(Stage primaryStage, MainApp app) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #4A4A8A;");

        Label title = new Label("Liste de tous les étudiants du CSV");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(30, 0, 20, 0));
        root.setTop(title);

        VBox listBox = new VBox(12);
        listBox.setAlignment(Pos.CENTER);
        listBox.setPadding(new Insets(30));
        for (Student student : app.students) {
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

        Button retourBtn = new Button("Retour");
        retourBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        retourBtn.setOnAction(e -> ShowMainMenuScene.show(primaryStage, app));
        HBox nav = new HBox(retourBtn);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(30));
        root.setBottom(nav);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }
}