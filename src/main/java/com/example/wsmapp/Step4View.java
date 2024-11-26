package com.example.wsmapp;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

public class Step4View {
    private VBox layout;
    private TextField criterionNameField, minValueField, maxValueField;
    private Button addCriterionButton, normalizeButton;
    private List<CriterionNormalization> criteria;
    private Label resultLabel;

    public Step4View(NavigationController navigationController) {
        layout = new VBox(15);
        layout.setPadding(new Insets(20));

        criteria = new ArrayList<>();

        // Champs pour ajouter un critère et ses limites
        criterionNameField = new TextField();
        criterionNameField.setPromptText("Nom du critère");

        minValueField = new TextField();
        minValueField.setPromptText("Valeur minimale");

        maxValueField = new TextField();
        maxValueField.setPromptText("Valeur maximale");

        addCriterionButton = new Button("Ajouter le critère");

        // Bouton pour normaliser toutes les valeurs réelles
        normalizeButton = new Button("Normaliser les valeurs");
        normalizeButton.setDisable(true);

        resultLabel = new Label();

        // Bouton pour terminer ou passer à une autre étape
        Button nextStepButton = new Button("Terminer");
        nextStepButton.setOnAction(e -> navigationController.loadStep3()); // Exemple de navigation

        // Gestion des événements
        addCriterionButton.setOnAction(e -> addNormalizationCriterion());
        normalizeButton.setOnAction(e -> normalizeValues());

        // Ajouter les éléments à la vue
        layout.getChildren().addAll(
                new Label("Étape 4 : Normalisation des critères"),
                criterionNameField, minValueField, maxValueField, addCriterionButton,
                normalizeButton, resultLabel, nextStepButton
        );
    }

    private void addNormalizationCriterion() {
        String name = criterionNameField.getText();
        String minText = minValueField.getText();
        String maxText = maxValueField.getText();

        if (name.isEmpty() || minText.isEmpty() || maxText.isEmpty()) {
            resultLabel.setText("Erreur : Remplissez tous les champs pour ajouter un critère.");
            return;
        }

        try {
            double min = Double.parseDouble(minText);
            double max = Double.parseDouble(maxText);

            if (min >= max) {
                resultLabel.setText("Erreur : La valeur minimale doit être inférieure à la valeur maximale.");
                return;
            }

            // Ajouter un nouveau critère
            CriterionNormalization criterion = new CriterionNormalization(name, min, max);
            criteria.add(criterion);

            // Ajouter des champs pour entrer une valeur réelle pour ce critère
            HBox criterionRow = new HBox(10);
            Label nameLabel = new Label(name + " : ");
            TextField realValueField = new TextField();
            realValueField.setPromptText("Valeur réelle");
            criterion.setRealValueField(realValueField);
            criterionRow.getChildren().addAll(nameLabel, realValueField);
            layout.getChildren().add(layout.getChildren().size() - 3, criterionRow);

            resultLabel.setText("Critère ajouté : " + name);

            // Activer le bouton de normalisation
            normalizeButton.setDisable(false);

            // Réinitialiser les champs
            criterionNameField.clear();
            minValueField.clear();
            maxValueField.clear();

        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Entrez des valeurs minimales et maximales valides.");
        }
    }

    private void normalizeValues() {
        if (criteria.isEmpty()) {
            resultLabel.setText("Erreur : Aucun critère à normaliser.");
            return;
        }

        StringBuilder results = new StringBuilder("Valeurs normalisées :\n");

        for (CriterionNormalization criterion : criteria) {
            try {
                String realValueText = criterion.getRealValueField().getText();
                if (realValueText.isEmpty()) {
                    results.append("Erreur : Valeur réelle manquante pour ").append(criterion.getName()).append("\n");
                    continue;
                }

                double realValue = Double.parseDouble(realValueText);

                if (realValue < criterion.getMinValue() || realValue > criterion.getMaxValue()) {
                    results.append("Erreur : La valeur réelle pour ")
                            .append(criterion.getName())
                            .append(" est hors des limites [")
                            .append(criterion.getMinValue())
                            .append(", ")
                            .append(criterion.getMaxValue())
                            .append("]\n");
                    continue;
                }

                // Calculer la valeur normalisée
                double normalized = 1 + (realValue - criterion.getMinValue()) * 9 / (criterion.getMaxValue() - criterion.getMinValue());
                results.append(criterion.getName())
                        .append(" : ")
                        .append(String.format("%.2f", normalized))
                        .append("\n");

            } catch (NumberFormatException e) {
                results.append("Erreur : Entrée invalide pour ").append(criterion.getName()).append("\n");
            }
        }

        resultLabel.setText(results.toString());
    }

    public VBox getLayout() {
        return layout;
    }

    /**
     * Classe interne pour représenter un critère avec normalisation
     */
    private static class CriterionNormalization {
        private String name;
        private double minValue;
        private double maxValue;
        private TextField realValueField;

        public CriterionNormalization(String name, double minValue, double maxValue) {
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

        public TextField getRealValueField() {
            return realValueField;
        }

        public void setRealValueField(TextField realValueField) {
            this.realValueField = realValueField;
        }
    }
}
