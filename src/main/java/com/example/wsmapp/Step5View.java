package com.example.wsmapp;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

public class Step5View {
    private VBox layout;
    private TextField criterionNameField, minValueField, maxValueField, realValueField;
    private Button addConstraintButton, validateButton;
    private List<CriterionConstraint> constraints;
    private Label resultLabel;

    public Step5View(NavigationController navigationController) {
        layout = new VBox(15);
        layout.setPadding(new Insets(20));

        constraints = new ArrayList<>();

        // Champs pour ajouter un critère et ses contraintes
        criterionNameField = new TextField();
        criterionNameField.setPromptText("Nom du critère");

        minValueField = new TextField();
        minValueField.setPromptText("Valeur minimale");

        maxValueField = new TextField();
        maxValueField.setPromptText("Valeur maximale");

        addConstraintButton = new Button("Ajouter contrainte");

        // Champs pour la validation des valeurs réelles
        realValueField = new TextField();
        realValueField.setPromptText("Valeur réelle à valider");

        validateButton = new Button("Valider la valeur");
        validateButton.setDisable(true);

        resultLabel = new Label();

        // Bouton pour terminer ou naviguer
        Button nextStepButton = new Button("Terminer");
        nextStepButton.setOnAction(e -> navigationController.loadStep4()); // Exemple de navigation

        // Gestion des événements
        addConstraintButton.setOnAction(e -> addConstraint());
        validateButton.setOnAction(e -> validateRealValue());

        // Ajouter les éléments à la vue
        layout.getChildren().addAll(
                new Label("Étape 5 : Gestion des contraintes"),
                criterionNameField, minValueField, maxValueField, addConstraintButton,
                new Label("Valeur réelle à valider :"), realValueField, validateButton, resultLabel, nextStepButton
        );
    }

    private void addConstraint() {
        String name = criterionNameField.getText();
        String minText = minValueField.getText();
        String maxText = maxValueField.getText();

        if (name.isEmpty() || minText.isEmpty() || maxText.isEmpty()) {
            resultLabel.setText("Erreur : Remplissez tous les champs pour ajouter une contrainte.");
            return;
        }

        try {
            double min = Double.parseDouble(minText);
            double max = Double.parseDouble(maxText);

            if (min >= max) {
                resultLabel.setText("Erreur : La valeur minimale doit être inférieure à la valeur maximale.");
                return;
            }

            CriterionConstraint constraint = new CriterionConstraint(name, min, max);
            constraints.add(constraint);

            resultLabel.setText("Contrainte ajoutée pour : " + name);

            // Réinitialiser les champs
            criterionNameField.clear();
            minValueField.clear();
            maxValueField.clear();

            // Activer le bouton de validation
            validateButton.setDisable(false);

        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Entrez des valeurs numériques valides pour les limites.");
        }
    }

    private void validateRealValue() {
        if (constraints.isEmpty()) {
            resultLabel.setText("Erreur : Aucune contrainte définie.");
            return;
        }

        String realValueText = realValueField.getText();
        if (realValueText.isEmpty()) {
            resultLabel.setText("Erreur : Entrez une valeur réelle à valider.");
            return;
        }

        try {
            double realValue = Double.parseDouble(realValueText);
            StringBuilder results = new StringBuilder("Résultats de validation :\n");

            for (CriterionConstraint constraint : constraints) {
                if (realValue < constraint.getMinValue() || realValue > constraint.getMaxValue()) {
                    results.append("Erreur : La valeur pour ")
                            .append(constraint.getName())
                            .append(" est hors des limites [")
                            .append(constraint.getMinValue())
                            .append(", ")
                            .append(constraint.getMaxValue())
                            .append("]\n");
                } else {
                    results.append("La valeur pour ")
                            .append(constraint.getName())
                            .append(" est valide.\n");
                }
            }

            resultLabel.setText(results.toString());

        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Entrez une valeur numérique valide.");
        }
    }

    public VBox getLayout() {
        return layout;
    }

    /**
     * Classe interne pour représenter une contrainte sur un critère
     */
    private static class CriterionConstraint {
        private String name;
        private double minValue;
        private double maxValue;

        public CriterionConstraint(String name, double minValue, double maxValue) {
            this.name = name;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public double getMinValue() {
            return minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }
    }
}
