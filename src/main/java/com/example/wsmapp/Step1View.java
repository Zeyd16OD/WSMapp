package com.example.wsmapp;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

public class Step1View {
    private VBox layout;
    private TextField weightCost, weightQuality, weightDeadline;
    private TextField scoreCost, scoreQuality, scoreDeadline;
    private Button calculateButton;
    private Label resultLabel;

    public Step1View(NavigationController navigationController) {
        layout = new VBox(15);
        layout.setPadding(new Insets(20));

        // Champs pour les poids
        weightCost = new TextField();
        weightQuality = new TextField();
        weightDeadline = new TextField();

        // Champs pour les scores
        scoreCost = new TextField();
        scoreQuality = new TextField();
        scoreDeadline = new TextField();

        // Bouton de calcul
        calculateButton = new Button("Calculer");
        resultLabel = new Label();

        // Navigation
        Button nextStepButton = new Button("Étape Suivante");
        nextStepButton.setOnAction(e -> navigationController.loadStep2());

        calculateButton.setOnAction(e -> calculateWSM());

        layout.getChildren().addAll(
                new Label("Poids du Coût"), weightCost,
                new Label("Poids de la Qualité"), weightQuality,
                new Label("Poids du Délai"), weightDeadline,
                new Label("Score du Coût"), scoreCost,
                new Label("Score de la Qualité"), scoreQuality,
                new Label("Score du Délai"), scoreDeadline,
                calculateButton, resultLabel, nextStepButton
        );
    }

    private void calculateWSM() {
        try {
            double weightCost = Double.parseDouble(this.weightCost.getText());
            double weightQuality = Double.parseDouble(this.weightQuality.getText());
            double weightDeadline = Double.parseDouble(this.weightDeadline.getText());

            double totalWeight = weightCost + weightQuality + weightDeadline;
            if (totalWeight != 1.0) {
                resultLabel.setText("Erreur : La somme des poids doit être égale à 1.");
                return;
            }

            int scoreCost = Integer.parseInt(this.scoreCost.getText());
            int scoreQuality = Integer.parseInt(this.scoreQuality.getText());
            int scoreDeadline = Integer.parseInt(this.scoreDeadline.getText());

            double totalScore = (weightCost * scoreCost) + (weightQuality * scoreQuality) + (weightDeadline * scoreDeadline);
            resultLabel.setText("Score total : " + totalScore);
        } catch (NumberFormatException e) {
            resultLabel.setText("Erreur : Veuillez entrer des valeurs valides.");
        }
    }

    public VBox getLayout() {
        return layout;
    }
}
