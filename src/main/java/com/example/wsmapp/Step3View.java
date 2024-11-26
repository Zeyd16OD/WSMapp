package com.example.wsmapp;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.util.ArrayList;
import java.util.List;

public class Step3View {
    private VBox layout;
    private TextField newCriterionNameField, newCriterionWeightField;
    private Button addCriterionButton, evaluateButton;
    private List<Criterion> criteria;
    private List<TextField> alternativeFields;
    private Label resultLabel;

    public Step3View(NavigationController navigationController) {
        layout = new VBox(15);
        layout.setPadding(new Insets(20));

        criteria = new ArrayList<>();
        alternativeFields = new ArrayList<>();

        // Champs pour ajouter un critère
        newCriterionNameField = new TextField();
        newCriterionNameField.setPromptText("Nom du critère");

        newCriterionWeightField = new TextField();
        newCriterionWeightField.setPromptText("Poids du critère (entre 0 et 1)");

        addCriterionButton = new Button("Ajouter le critère");
        resultLabel = new Label();

        // Bouton pour évaluer les alternatives
        evaluateButton = new Button("Évaluer les alternatives");
        evaluateButton.setDisable(true); // Activé uniquement après l'ajout de critères

        // Bouton pour passer à l'étape suivante
        Button nextStepButton = new Button("Étape Suivante");
        nextStepButton.setOnAction(e -> navigationController.loadStep4());

        // Gestion des événements
        addCriterionButton.setOnAction(e -> addDynamicCriterion());
        evaluateButton.setOnAction(e -> evaluateAlternatives());

        // Ajouter les éléments à la vue
        layout.getChildren().addAll(
                new Label("Étape 3 : Gestion des critères dynamiques"),
                newCriterionNameField, newCriterionWeightField, addCriterionButton, resultLabel, evaluateButton, nextStepButton
        );
    }

    private void addDynamicCriterion() {
        String name = newCriterionNameField.getText();
        String weightText = newCriterionWeightField.getText();

        if (name.isEmpty() || weightText.isEmpty()) {
            resultLabel.setText("Erreur : Veuillez entrer le nom et le poids du critère.");
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);
            if (weight <= 0 || weight > 1) {
                resultLabel.setText("Erreur : Le poids doit être entre 0 et 1.");
                return;
            }

            // Ajouter un nouveau critère
            criteria.add(new Criterion(name, weight));
            resultLabel.setText("Critère ajouté : " + name);

            // Ajouter un champ d'évaluation pour les alternatives
            TextField alternativeField = new TextField();
            alternativeField.setPromptText("Évaluation pour " + name);
            alternativeFields.add(alternativeField);
            layout.getChildren().add(layout.getChildren().size() - 3, alternativeField);

            // Activer le bouton d'évaluation si au moins 2 critères existent
            if (criteria.size() >= 2) {
                evaluateButton.setDisable(false);
            }

            // Réinitialiser les champs
            newCriterionNameField.clear();
            newCriterionWeightField.clear();

        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Veuillez entrer un poids valide.");
        }
    }

    private void evaluateAlternatives() {
        if (criteria.isEmpty() || alternativeFields.isEmpty()) {
            resultLabel.setText("Erreur : Aucun critère ou évaluation ajouté.");
            return;
        }

        try {
            double totalWeight = criteria.stream().mapToDouble(Criterion::getWeight).sum();
            if (totalWeight != 1.0) {
                resultLabel.setText("Erreur : La somme des poids doit être égale à 1.");
                return;
            }

            // Calculer le score pondéré total pour les alternatives
            double totalScore = 0.0;
            for (int i = 0; i < criteria.size(); i++) {
                double evaluation = Double.parseDouble(alternativeFields.get(i).getText());
                if (evaluation < 1 || evaluation > 10) {
                    resultLabel.setText("Erreur : Les évaluations doivent être entre 1 et 10.");
                    return;
                }
                totalScore += criteria.get(i).getWeight() * evaluation;
            }

            resultLabel.setText("Score total de l'alternative : " + String.format("%.2f", totalScore));

        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Veuillez entrer des évaluations valides.");
        }
    }

    public VBox getLayout() {
        return layout;
    }

    /**
     * Classe interne pour représenter un critère
     */
    private static class Criterion {
        private String name;
        private double weight;

        public Criterion(String name, double weight) {
            this.name = name;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public double getWeight() {
            return weight;
        }
    }
}
