package com.example.wsmapp;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

public class Step6View {
    private VBox layout;
    private TextField problemNameField;
    private TextField criterionNameField, criterionWeightField, criterionMinField, criterionMaxField;
    private Button addCriterionButton, addAlternativeButton, calculateButton;
    private List<Criterion> criteria;
    private List<Alternative> alternatives;
    private VBox criteriaSection, alternativesSection;
    private Label resultLabel;

    public Step6View(NavigationController navigationController) {
        layout = new VBox(20);
        layout.setPadding(new Insets(20));

        criteria = new ArrayList<>();
        alternatives = new ArrayList<>();

        // Champs pour le nom du problème
        problemNameField = new TextField();
        problemNameField.setPromptText("Nom du problème");

        // Section pour les critères
        criteriaSection = new VBox(10);
        Label criteriaLabel = new Label("Gestion des paramètres (Critères)");
        criterionNameField = new TextField();
        criterionNameField.setPromptText("Nom du critère");
        criterionWeightField = new TextField();
        criterionWeightField.setPromptText("Poids du critère (entre 0 et 1)");
        criterionMinField = new TextField();
        criterionMinField.setPromptText("Valeur minimale");
        criterionMaxField = new TextField();
        criterionMaxField.setPromptText("Valeur maximale");
        addCriterionButton = new Button("Ajouter critère");
        addCriterionButton.setOnAction(e -> addCriterion());

        criteriaSection.getChildren().addAll(
                criteriaLabel, criterionNameField, criterionWeightField,
                criterionMinField, criterionMaxField, addCriterionButton
        );

        // Section pour les alternatives
        alternativesSection = new VBox(10);
        Label alternativesLabel = new Label("Saisie des alternatives");
        TextField alternativeNameField = new TextField();
        alternativeNameField.setPromptText("Nom de l'alternative");
        Button addAlternativeValuesButton = new Button("Ajouter évaluations");
        addAlternativeValuesButton.setOnAction(e -> addAlternative(alternativeNameField));
        alternativesSection.getChildren().addAll(alternativesLabel, alternativeNameField, addAlternativeValuesButton);

        // Bouton de calcul
        calculateButton = new Button("Calculer");
        calculateButton.setOnAction(e -> calculateScores());
        resultLabel = new Label();

        // Bouton pour naviguer
        Button nextStepButton = new Button("Terminer");
        nextStepButton.setOnAction(e -> navigationController.loadStep5());

        layout.getChildren().addAll(
                new Label("Étape 6 : Séparation des paramètres et des données"),
                new Label("Nom du problème"), problemNameField,
                criteriaSection, alternativesSection,
                calculateButton, resultLabel, nextStepButton
        );
    }

    private void addCriterion() {
        String name = criterionNameField.getText();
        String weightText = criterionWeightField.getText();
        String minText = criterionMinField.getText();
        String maxText = criterionMaxField.getText();

        if (name.isEmpty() || weightText.isEmpty() || minText.isEmpty() || maxText.isEmpty()) {
            resultLabel.setText("Erreur : Remplissez tous les champs pour ajouter un critère.");
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);
            double min = Double.parseDouble(minText);
            double max = Double.parseDouble(maxText);

            if (weight <= 0 || weight > 1) {
                resultLabel.setText("Erreur : Le poids doit être entre 0 et 1.");
                return;
            }

            if (min >= max) {
                resultLabel.setText("Erreur : La valeur minimale doit être inférieure à la valeur maximale.");
                return;
            }

            criteria.add(new Criterion(name, weight, min, max));
            resultLabel.setText("Critère ajouté : " + name);

            // Réinitialiser les champs
            criterionNameField.clear();
            criterionWeightField.clear();
            criterionMinField.clear();
            criterionMaxField.clear();

        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Entrez des valeurs numériques valides pour les poids et les limites.");
        }
    }

    private void addAlternative(TextField alternativeNameField) {
        String alternativeName = alternativeNameField.getText();
        if (alternativeName.isEmpty()) {
            resultLabel.setText("Erreur : Veuillez saisir le nom de l'alternative.");
            return;
        }

        List<Double> evaluations = new ArrayList<>();
        for (Criterion criterion : criteria) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Évaluation pour " + criterion.getName());
            dialog.setHeaderText("Alternative : " + alternativeName);
            dialog.setContentText("Entrez une valeur réelle pour le critère (" +
                    criterion.getName() + ") [" + criterion.getMinValue() + " - " + criterion.getMaxValue() + "]:");
            String result = dialog.showAndWait().orElse("");
            try {
                double value = Double.parseDouble(result);
                if (value < criterion.getMinValue() || value > criterion.getMaxValue()) {
                    resultLabel.setText("Erreur : La valeur pour " + criterion.getName() + " est hors des limites.");
                    return;
                }
                evaluations.add(value);
            } catch (NumberFormatException e) {
                resultLabel.setText("Erreur : Entrez une valeur numérique pour le critère " + criterion.getName());
                return;
            }
        }

        alternatives.add(new Alternative(alternativeName, evaluations));
        resultLabel.setText("Alternative ajoutée : " + alternativeName);
        alternativeNameField.clear();
    }

    private void calculateScores() {
        if (criteria.isEmpty()) {
            resultLabel.setText("Erreur : Aucun critère défini.");
            return;
        }
        if (alternatives.isEmpty()) {
            resultLabel.setText("Erreur : Aucune alternative ajoutée.");
            return;
        }

        double totalWeight = criteria.stream().mapToDouble(Criterion::getWeight).sum();
        if (totalWeight != 1.0) {
            resultLabel.setText("Erreur : La somme des poids doit être égale à 1.");
            return;
        }

        StringBuilder results = new StringBuilder("Résultats :\n");
        for (Alternative alternative : alternatives) {
            double score = 0.0;
            for (int i = 0; i < criteria.size(); i++) {
                Criterion criterion = criteria.get(i);
                double evaluation = alternative.getEvaluations().get(i);
                score += criterion.getWeight() * (evaluation - criterion.getMinValue()) /
                        (criterion.getMaxValue() - criterion.getMinValue()) * 9 + 1;
            }
            results.append(alternative.getName())
                    .append(" : Score = ")
                    .append(String.format("%.2f", score))
                    .append("\n");
        }
        resultLabel.setText(results.toString());
    }

    public VBox getLayout() {
        return layout;
    }

    /**
     * Classe interne pour représenter un critère
     */
    private static class Criterion {
        private String name;
        private double weight, minValue, maxValue;

        public Criterion(String name, double weight, double minValue, double maxValue) {
            this.name = name;
            this.weight = weight;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public double getWeight() {
            return weight;
        }

        public double getMinValue() {
            return minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }
    }

    /**
     * Classe interne pour représenter une alternative
     */
    private static class Alternative {
        private String name;
        private List<Double> evaluations;

        public Alternative(String name, List<Double> evaluations) {
            this.name = name;
            this.evaluations = evaluations;
        }

        public String getName() {
            return name;
        }

        public List<Double> getEvaluations() {
            return evaluations;
        }
    }
}
