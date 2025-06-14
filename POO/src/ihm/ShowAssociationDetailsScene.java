package ihm;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import basicclass.AssociationStudent;

public class ShowAssociationDetailsScene {
    public static void show(Stage primaryStage, MainApp app, AssociationStudent assoc) {
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

        VBox scoreBox = new VBox(10);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(10));

        Label scoreLabel = createInfoLabel("Score d'affinité : " +
            (assoc.getScoreAssociation() != null ? assoc.getScoreAssociation() : "Association impossible"));
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label descLabel = createInfoLabel("Description : " + assoc.describeLevelOfAffinity());
        descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        scoreBox.getChildren().addAll(scoreLabel, descLabel);

        HBox nav = new HBox(20);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(20));

        Button retourButton = new Button("Retour");
        retourButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        retourButton.setOnAction(e -> ShowMainMenuScene.show(primaryStage, app));
        nav.getChildren().addAll(retourButton);

        VBox bottomBox = new VBox(10, scoreBox, nav);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    private static Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        return label;
    }
}