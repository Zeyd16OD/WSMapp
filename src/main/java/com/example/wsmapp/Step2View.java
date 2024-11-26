package com.example.wsmapp;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Step2View {
    private VBox layout;
    private TextField alternativeNameField, costField, qualityField, deadlineField;
    private Button addAlternativeButton, calculateButton;
    private Label resultLabel;
    private List<Alternative> alternatives;

    public Step2View(NavigationController navigationController) {
        layout = new VBox(15);
        layout.setPadding(new Insets(20));

        alternatives = new ArrayList<>();

        // Champs pour l'ajout des alternatives
        alternativeNameField = new TextField();
        alternativeNameField.setPromptText("Nom de l'alternative");

        costField = new TextField();
        costField.setPromptText("Score Coût (1-10)");

        qualityField = new TextField();
        qualityField.setPromptText("Score Qualité (1-10)");

        deadlineField = new TextField();
        deadlineField.setPromptText("Score Délai (1-10)");

        addAlternativeButton = new Button("Ajouter l'alternative");
        calculateButton = new Button("Calculer et classer");

        resultLabel = new Label();

        // Bouton pour aller à l'étape suivante
        Button nextStepButton = new Button("Étape Suivante");
        nextStepButton.setOnAction(e -> navigationController.loadStep3());

        // Gestion des événements
        addAlternativeButton.setOnAction(e -> addAlternative());
        calculateButton.setOnAction(e -> calculateAndRankAlternatives());

        // Ajout des champs et boutons à la vue
        layout.getChildren().addAll(
                new Label("Étape 2 : Gestion de plusieurs alternatives"),
                alternativeNameField, costField, qualityField, deadlineField,
                addAlternativeButton, calculateButton, resultLabel, nextStepButton
        );
    }

    private void addAlternative() {
        // Validation des données
        String name = alternativeNameField.getText();
        if (name.isEmpty() || costField.getText().isEmpty() || qualityField.getText().isEmpty() || deadlineField.getText().isEmpty()) {
            resultLabel.setText("Erreur : Veuillez remplir tous les champs.");
            return;
        }

        try {
            int cost = Integer.parseInt(costField.getText());
            int quality = Integer.parseInt(qualityField.getText());
            int deadline = Integer.parseInt(deadlineField.getText());

            if (cost < 1 || cost > 10 || quality < 1 || quality > 10 || deadline < 1 || deadline > 10) {
                resultLabel.setText("Erreur : Les scores doivent être entre 1 et 10.");
                return;
            }

            // Ajouter une nouvelle alternative
            alternatives.add(new Alternative(name, cost, quality, deadline));
            resultLabel.setText("Alternative ajoutée : " + name);

            // Réinitialiser les champs
            alternativeNameField.clear();
            costField.clear();
            qualityField.clear();
            deadlineField.clear();

        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Veuillez entrer des scores valides.");
        }
    }

    private void calculateAndRankAlternatives() {
        if (alternatives.isEmpty()) {
            resultLabel.setText("Erreur : Aucune alternative ajoutée.");
            return;
        }

        // Poids fixes pour les critères (modifiable si besoin)
        double weightCost = 0.4; // 40%
        double weightQuality = 0.4; // 40%
        double weightDeadline = 0.2; // 20%

        // Calculer le score pondéré pour chaque alternative
        for (Alternative alt : alternatives) {
            double weightedScore = (alt.getCost() * weightCost) +
                    (alt.getQuality() * weightQuality) +
                    (alt.getDeadline() * weightDeadline);
            alt.setWeightedScore(weightedScore);
        }

        // Classer les alternatives par score décroissant
        alternatives.sort(Comparator.comparingDouble(Alternative::getWeightedScore).reversed());

        // Afficher les résultats
        StringBuilder resultBuilder = new StringBuilder("Classement des alternatives :\n");
        for (int i = 0; i < alternatives.size(); i++) {
            Alternative alt = alternatives.get(i);
            resultBuilder.append((i + 1))
                    .append(". ")
                    .append(alt.getName())
                    .append(" - Score : ")
                    .append(String.format("%.2f", alt.getWeightedScore()))
                    .append("\n");
        }
        resultLabel.setText(resultBuilder.toString());
    }

    public VBox getLayout() {
        return layout;
    }

    /**
     * Classe interne pour représenter une alternative
     */
    private static class Alternative {
        private String name;
        private int cost;
        private int quality;
        private int deadline;
        private double weightedScore;

        public Alternative(String name, int cost, int quality, int deadline) {
            this.name = name;
            this.cost = cost;
            this.quality = quality;
            this.deadline = deadline;
        }

        public String getName() {
            return name;
        }

        public int getCost() {
            return cost;
        }

        public int getQuality() {
            return quality;
        }

        public int getDeadline() {
            return deadline;
        }

        public double getWeightedScore() {
            return weightedScore;
        }

        public void setWeightedScore(double weightedScore) {
            this.weightedScore = weightedScore;
        }
    }
}
