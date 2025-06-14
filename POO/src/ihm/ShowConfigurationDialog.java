package ihm;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import basicclass.*;
import exceptions.InvalidAssociationException;
import java.util.*;
import java.util.stream.Collectors;

public class ShowConfigurationDialog {
    public static void show(Stage primaryStage, MainApp app) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Configuration");

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #4A4A8A;");

        // --- Onglet Automatique ---
        VBox autoRoot = new VBox(18);
        autoRoot.setAlignment(Pos.TOP_CENTER);
        autoRoot.setPadding(new Insets(20));
        autoRoot.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");

        Set<Country> countries = app.students.stream().map(Student::getCountry).collect(Collectors.toSet());
        List<Country> countryList = new ArrayList<>(countries);

        ComboBox<Country> hostCombo = new ComboBox<>();
        ComboBox<Country> guestCombo = new ComboBox<>();
        hostCombo.getItems().addAll(countryList);
        guestCombo.getItems().addAll(countryList);
        hostCombo.setPromptText("Pays hôte");
        guestCombo.setPromptText("Pays invité");
        if (app.selectedHost != null) hostCombo.setValue(app.selectedHost);
        if (app.selectedGuest != null) guestCombo.setValue(app.selectedGuest);

        HBox countryBox = new HBox(30, new VBox(new Label("Pays hôte"), hostCombo), new VBox(new Label("Pays invité"), guestCombo));
        countryBox.setAlignment(Pos.CENTER);

        ComboBox<MatchingEnum> algoCombo = new ComboBox<>();
        algoCombo.getItems().addAll(MatchingEnum.values());
        algoCombo.setPromptText("Algorithme");
        if (app.selectedAlgo != null) algoCombo.setValue(app.selectedAlgo);

        VBox algoBox = new VBox(4, new Label("Algorithme"), algoCombo);
        algoBox.setAlignment(Pos.CENTER);

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
                        if (ignoreBox.isSelected()) app.ignoredHosts.add(s); else app.ignoredHosts.remove(s);
                    }
                    if (guestCombo.getValue() != null && s.getCountry().equals(guestCombo.getValue())) {
                        if (ignoreBox.isSelected()) app.ignoredGuests.add(s); else app.ignoredGuests.remove(s);
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
                    nameLabel.setStyle("-fx-text-fill: #222; -fx-font-size: 13px;");
                    if (hostCombo.getValue() != null && s.getCountry().equals(hostCombo.getValue()))
                        ignoreBox.setSelected(app.ignoredHosts.contains(s));
                    else if (guestCombo.getValue() != null && s.getCountry().equals(guestCombo.getValue()))
                        ignoreBox.setSelected(app.ignoredGuests.contains(s));
                    else
                        ignoreBox.setSelected(false);
                    setGraphic(cellBox);
                }
            }
        });

        Runnable refreshList = () -> {
            List<Student> filtered = app.students.stream()
                .filter(s -> (hostCombo.getValue() != null && s.getCountry().equals(hostCombo.getValue()))
                          || (guestCombo.getValue() != null && s.getCountry().equals(guestCombo.getValue())))
                .collect(Collectors.toList());
            studentListView.getItems().setAll(filtered);
        };
        hostCombo.setOnAction(e -> refreshList.run());
        guestCombo.setOnAction(e -> refreshList.run());
        refreshList.run();

        Button validerBtn = new Button("Valider");
        validerBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px;-fx-cursor: hand;");
        validerBtn.setOnAction(e -> {
            if (hostCombo.getValue() != null && guestCombo.getValue() != null && algoCombo.getValue() != null && !hostCombo.getValue().equals(guestCombo.getValue())) {
                app.selectedHost = hostCombo.getValue();
                app.selectedGuest = guestCombo.getValue();
                app.selectedAlgo = algoCombo.getValue();
                app.configDone = true;
                dialog.close();
                ShowMainMenuScene.show(primaryStage, app);
            } else {
                ihm.tools.ShowAlert.show("Erreur", "Veuillez sélectionner deux pays différents et un algorithme.");
            }
        });

        Label ignoreHeader = new Label("Cochez la case pour bloquer l'étudiant");
        ignoreHeader.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        autoRoot.getChildren().setAll(
            countryBox,
            algoBox,
            ignoreHeader,
            studentListView,
            validerBtn
        );

        // --- Onglet Pondération ---
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setHgap(18);
        grid.setVgap(8);
        grid.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");

        AffinityWeights w = AffinityWeights.getInstance();
        Map<String, Spinner<Integer>> spinners = new LinkedHashMap<>();

        java.util.function.Function<Integer, Spinner<Integer>> makeSpinner = (value) -> {
            Spinner<Integer> s = new Spinner<>(-20, 100, value);
            s.setEditable(true);
            s.setPrefWidth(60);
            s.getEditor().setStyle("-fx-font-size: 12px;");
            return s;
        };

        String[] labels = {
            "Bonus historique", "Même âge", "Âge entre 2 et 5", "Âge > 5",
            "Genre différent", "Coût hobby différent", "Coût hobby en moins"
        };
        int[] values = {
            w.getBonusHistory(), w.getSameAge(), w.getAgeBetween2And5(), w.getAgeSuperior5(),
            w.getDifferentGender(), w.getCostOfHavingDiffHobbie(), w.getCostOfHavingLessHobbie()
        };

        for (int i = 0; i < labels.length; i++) {
            Label l = new Label(labels[i]);
            l.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            Spinner<Integer> s = makeSpinner.apply(values[i]);
            spinners.put(labels[i], s);
            grid.add(l, 0, i);
            grid.add(s, 1, i);
        }

        String[] blocages = {"Blocage historique", "Blocage animal", "Blocage régime", "Blocage France"};
        String[] blocageKeys = {"HistoryOtherDetected", "AnimalAllergy", "RegimeRestriction", "FranceRule"};
        Integer[] blocageDefaults = {
            w.getHistoryOtherDetected() != null ? w.getHistoryOtherDetected() : 99,
            w.getAnimalAllergy() != null ? w.getAnimalAllergy() : 99,
            w.getRegimeRestriction() != null ? w.getRegimeRestriction() : 99,
            w.getFranceRule() != null ? w.getFranceRule() : 99
        };

        Map<String, RadioButton> blocageRadios = new HashMap<>();
        Map<String, HBox> blocageMalusBoxes = new HashMap<>();
        Map<String, Spinner<Integer>> blocageMalusSpinners = new HashMap<>();

        for (int i = 0; i < blocages.length; i++) {
            int row = labels.length + i;
            Label l = new Label(blocages[i]);
            l.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            RadioButton rb = new RadioButton("Bloquer");
            rb.setSelected(true);
            rb.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            blocageRadios.put(blocageKeys[i], rb);

            Label malusLabel = new Label("Malus :");
            malusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            Spinner<Integer> malusSpinner = makeSpinner.apply(blocageDefaults[i]);
            HBox malusBox = new HBox(5, malusLabel, malusSpinner);
            malusBox.setAlignment(Pos.CENTER_LEFT);
            malusBox.setVisible(false);
            blocageMalusBoxes.put(blocageKeys[i], malusBox);
            blocageMalusSpinners.put(blocageKeys[i], malusSpinner);

            rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                malusBox.setVisible(!isSelected);
            });

            grid.add(l, 0, row);
            grid.add(rb, 1, row);
            grid.add(malusBox, 2, row);
        }

        HBox btnBoxPond = new HBox(20);
        btnBoxPond.setAlignment(Pos.CENTER);

        Button enregistrerBtn = new Button("Enregistrer");
        enregistrerBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 13px;-fx-cursor: hand;");
        enregistrerBtn.setOnAction(e -> {
            w.setBonusHistory(spinners.get("Bonus historique").getValue());
            w.setSameAge(spinners.get("Même âge").getValue());
            w.setAgeBetween2And5(spinners.get("Âge entre 2 et 5").getValue());
            w.setAgeSuperior5(spinners.get("Âge > 5").getValue());
            w.setDifferentGender(spinners.get("Genre différent").getValue());
            w.setCostOfHavingDiffHobbie(spinners.get("Coût hobby différent").getValue());
            w.setCostOfHavingLessHobbie(spinners.get("Coût hobby en moins").getValue());
            w.setHistoryOtherDetected(blocageRadios.get("HistoryOtherDetected").isSelected() ? null : blocageMalusSpinners.get("HistoryOtherDetected").getValue());
            w.setAnimalAllergy(blocageRadios.get("AnimalAllergy").isSelected() ? null : blocageMalusSpinners.get("AnimalAllergy").getValue());
            w.setRegimeRestriction(blocageRadios.get("RegimeRestriction").isSelected() ? null : blocageMalusSpinners.get("RegimeRestriction").getValue());
            w.setFranceRule(blocageRadios.get("FranceRule").isSelected() ? null : blocageMalusSpinners.get("FranceRule").getValue());
            ihm.tools.ShowAlert.show("Pondérations enregistrées", "Les pondérations ont été mises à jour.");
        });

        Button resetBtn = new Button("Reset");
        resetBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 13px;-fx-cursor: hand;");
        resetBtn.setOnAction(e -> {
            w.reset();
            spinners.get("Bonus historique").getValueFactory().setValue(w.getBonusHistory());
            spinners.get("Même âge").getValueFactory().setValue(w.getSameAge());
            spinners.get("Âge entre 2 et 5").getValueFactory().setValue(w.getAgeBetween2And5());
            spinners.get("Âge > 5").getValueFactory().setValue(w.getAgeSuperior5());
            spinners.get("Genre différent").getValueFactory().setValue(w.getDifferentGender());
            spinners.get("Coût hobby différent").getValueFactory().setValue(w.getCostOfHavingDiffHobbie());
            spinners.get("Coût hobby en moins").getValueFactory().setValue(w.getCostOfHavingLessHobbie());
            for (int i = 0; i < blocageKeys.length; i++) {
                blocageRadios.get(blocageKeys[i]).setSelected(true);
                blocageMalusBoxes.get(blocageKeys[i]).setVisible(false);
                blocageMalusSpinners.get(blocageKeys[i]).getValueFactory().setValue(99);
            }
        });

        btnBoxPond.getChildren().addAll(enregistrerBtn, resetBtn);

        VBox pondRoot = new VBox(10,
            new Label("Modifiez les pondérations d'affinité ci-dessous :"),
            grid,
            btnBoxPond
        );
        pondRoot.setAlignment(Pos.CENTER);
        pondRoot.setPadding(new Insets(10));
        pondRoot.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");

        // --- Onglet Manuel ---
        VBox manuelRoot = new VBox(18);
        manuelRoot.setAlignment(Pos.TOP_CENTER);
        manuelRoot.setPadding(new Insets(20));
        manuelRoot.setStyle("-fx-background-color: #6A6AAA; -fx-background-radius: 10;");

        HBox paysBox = new HBox(20);
        paysBox.setAlignment(Pos.CENTER);
        ComboBox<Country> hostComboManual = new ComboBox<>();
        ComboBox<Country> guestComboManual = new ComboBox<>();
        hostComboManual.getItems().addAll(app.students.stream().map(Student::getCountry).distinct().toList());
        guestComboManual.getItems().addAll(app.students.stream().map(Student::getCountry).distinct().toList());
        hostComboManual.setPromptText("Pays hôte");
        guestComboManual.setPromptText("Pays invité");
        paysBox.getChildren().addAll(new Label("Pays hôte :"), hostComboManual, new Label("Pays invité :"), guestComboManual);

        ListView<Student> hostListView = new ListView<>();
        ListView<Student> guestListView = new ListView<>();
        ListView<AssociationStudent> assocListView = new ListView<>();

        hostListView.setPrefWidth(180); guestListView.setPrefWidth(180); assocListView.setPrefWidth(320);
        hostListView.setPrefHeight(350); guestListView.setPrefHeight(350); assocListView.setPrefHeight(350);

        Label previewLabel = new Label();
        previewLabel.setStyle("-fx-text-fill:rgb(189, 0, 0); -fx-font-size: 11px; -fx-font-weight: normal; " +
                              "-fx-background-color: rgba(255,255,255,0.85); -fx-padding: 4 12 4 12; -fx-background-radius: 8;");
        previewLabel.setVisible(false);
        previewLabel.setMaxWidth(hostListView.getPrefWidth() + guestListView.getPrefWidth());
        previewLabel.setWrapText(true);

        Runnable refreshLists = () -> {
            Country h = hostComboManual.getValue();
            Country g = guestComboManual.getValue();
            if (h != null && g != null && h.equals(g)) {
                previewLabel.setText("Erreur : Les deux pays doivent être différents.");
                previewLabel.setVisible(true);
                hostListView.getItems().clear();
                guestListView.getItems().clear();
                return;
            }
            previewLabel.setVisible(false);
            if (h != null) {
                hostListView.getItems().setAll(app.students.stream()
                    .filter(s -> s.getCountry().equals(h))
                    .filter(s -> !app.ignoredHosts.contains(s))
                    .toList());
            } else {
                hostListView.getItems().clear();
            }
            if (g != null) {
                guestListView.getItems().setAll(app.students.stream()
                    .filter(s -> s.getCountry().equals(g))
                    .filter(s -> !app.ignoredGuests.contains(s))
                    .toList());
            } else {
                guestListView.getItems().clear();
            }
        };
        hostComboManual.setOnAction(e -> refreshLists.run());
        guestComboManual.setOnAction(e -> refreshLists.run());

        hostListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        guestListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        hostListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateManualPreview(hostComboManual, guestComboManual, hostListView, guestListView, previewLabel);
        });
        guestListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateManualPreview(hostComboManual, guestComboManual, hostListView, guestListView, previewLabel);
        });

        Button undoBtn = new Button("Undo");
        undoBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 13px;-fx-cursor: hand;");
        undoBtn.setOnAction(e -> {
            if (!assocListView.getItems().isEmpty()) {
                AssociationStudent last = assocListView.getItems().remove(assocListView.getItems().size() - 1);
                hostListView.getItems().add(last.getHost());
                guestListView.getItems().add(last.getGuest());
            }
        });
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 13px;-fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            for (AssociationStudent assoc : assocListView.getItems()) {
                hostListView.getItems().add(assoc.getHost());
                guestListView.getItems().add(assoc.getGuest());
            }
            assocListView.getItems().clear();
        });
        HBox undoClearBox = new HBox(10, undoBtn, clearBtn);
        undoClearBox.setAlignment(Pos.CENTER);

        VBox hostsBox = new VBox(new Label("Hôtes"), hostListView);
        hostsBox.setAlignment(Pos.CENTER);
        hostsBox.setPrefWidth(180);
        VBox guestsBox = new VBox(new Label("Invités"), guestListView);
        guestsBox.setAlignment(Pos.CENTER);
        guestsBox.setPrefWidth(180);
        VBox assocsBox = new VBox(new Label("Associations"), assocListView, undoClearBox);
        assocsBox.setAlignment(Pos.CENTER);
        assocsBox.setPrefWidth(320);

        HBox listsBox = new HBox(0, hostsBox, guestsBox);
        listsBox.setAlignment(Pos.CENTER_LEFT);

        StackPane listsStack = new StackPane(listsBox, previewLabel);
        listsStack.setPrefHeight(370);
        StackPane.setAlignment(previewLabel, Pos.BOTTOM_CENTER);

        VBox associerBox = new VBox();
        Button associerBtn = new Button("Associer →");
        associerBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #222; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 30;-fx-cursor: hand;");
        associerBtn.setOnAction(e -> {
            Country h = hostComboManual.getValue();
            Country g = guestComboManual.getValue();
            if (h != null && g != null && h.equals(g)) {
                ihm.tools.ShowAlert.show("Erreur", "Les deux pays doivent être différents.");
                return;
            }
            Student host = hostListView.getSelectionModel().getSelectedItem();
            Student guest = guestListView.getSelectionModel().getSelectedItem();
            if (host == null || guest == null) {
                ihm.tools.ShowAlert.show("Erreur", "Sélectionnez un hôte et un invité.");
                return;
            }
            try {
                AssociationStudent assoc = new AssociationStudent(host, guest);
                if (assoc.getScoreAssociation() == null) {
                    throw new InvalidAssociationException("Association impossible : " + assoc.getInvalidReason());
                }
                assocListView.getItems().add(assoc);
                hostListView.getItems().remove(host);
                guestListView.getItems().remove(guest);
            } catch (InvalidAssociationException ex) {
                ihm.tools.ShowAlert.show("Association impossible", ex.getMessage());
            }
        });
        associerBox.getChildren().add(associerBtn);
        associerBox.setAlignment(Pos.CENTER);

        HBox centerBox = new HBox(30, listsStack, associerBox, assocsBox);
        centerBox.setAlignment(Pos.CENTER);

        Button validerBtnManual = new Button("Valider");
        validerBtnManual.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 15px;-fx-cursor: hand;");
        validerBtnManual.setOnAction(e -> {
            List<AssociationStudent> associations = new ArrayList<>(assocListView.getItems());
            Country h = hostComboManual.getValue();
            Country g = guestComboManual.getValue();
            if (h != null && g != null && !h.equals(g)) {
                MainApp.historyManager.addOrReplaceMatching(h, g, associations);
                MainApp.historyManager.saveToFile("POO/data/historique.dat");
                app.associations = associations;
                app.selectedHost = h;
                app.selectedGuest = g;
                app.configDone = true;
            }
            dialog.close();
            ShowMainMenuScene.show(primaryStage, app);
        });
        HBox btnBox = new HBox(validerBtnManual);
        btnBox.setAlignment(Pos.CENTER);

        manuelRoot.getChildren().setAll(
            paysBox,
            centerBox,
            btnBox
        );

        Tab tabAuto = new Tab("Configuration automatique", autoRoot);
        tabAuto.setClosable(false);
        Tab tabManual = new Tab("Configuration manuelle", manuelRoot);
        tabManual.setClosable(false);
        Tab tabPond = new Tab("Pondération", pondRoot);
        tabPond.setClosable(false);

        tabPane.getTabs().addAll(tabAuto, tabManual, tabPond);

        VBox root = new VBox(tabPane);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 1100, 700);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static void updateManualPreview(
        ComboBox<Country> hostComboManual,
        ComboBox<Country> guestComboManual,
        ListView<Student> hostListView,
        ListView<Student> guestListView,
        Label previewLabel
    ) {
        Country h = hostComboManual.getValue();
        Country g = guestComboManual.getValue();
        if (h != null && g != null && h.equals(g)) {
            previewLabel.setText("Erreur : Les deux pays doivent être différents.");
            previewLabel.setVisible(true);
            return;
        }
        Student host = hostListView.getSelectionModel().getSelectedItem();
        Student guest = guestListView.getSelectionModel().getSelectedItem();
        if (host != null && guest != null) {
            AssociationStudent assoc = new AssociationStudent(host, guest);
            if (assoc.getScoreAssociation() == null) {
                previewLabel.setText("Association impossible : " + assoc.getInvalidReason());
                previewLabel.setVisible(true);
            } else {
                previewLabel.setVisible(false);
            }
        } else {
            previewLabel.setVisible(false);
        }
    }
}