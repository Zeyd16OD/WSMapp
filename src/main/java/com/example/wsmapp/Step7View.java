package com.example.wsmapp;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.io.*;
import java.util.*;

public class Step7View {
    private VBox layout;
    private ListView<String> problemsListView;
    private Button loadButton, saveButton, deleteButton, createNewButton;
    private Map<String, DecisionProblem> problems;
    private Label resultLabel;
    private TextField newProblemNameField;

    public Step7View(NavigationController navigationController) {
        layout = new VBox(20);
        layout.setPadding(new Insets(20));

        problems = new HashMap<>();
        problemsListView = new ListView<>();

        // Boutons pour gérer les problèmes
        loadButton = new Button("Charger");
        saveButton = new Button("Sauvegarder");
        deleteButton = new Button("Supprimer");
        createNewButton = new Button("Créer un nouveau problème");

        // Champs pour entrer un nouveau problème
        newProblemNameField = new TextField();
        newProblemNameField.setPromptText("Nom du nouveau problème");

        resultLabel = new Label();

        // Bouton pour revenir à une étape précédente
        Button backButton = new Button("Retour");
        backButton.setOnAction(e -> navigationController.loadStep6());

        // Gestion des événements
        loadButton.setOnAction(e -> loadProblem());
        saveButton.setOnAction(e -> saveProblem());
        deleteButton.setOnAction(e -> deleteProblem());
        createNewButton.setOnAction(e -> createNewProblem());

        layout.getChildren().addAll(
                new Label("Étape 7 : Gestion de multiples problèmes de décision"),
                problemsListView, newProblemNameField, createNewButton,
                new HBox(10, loadButton, saveButton, deleteButton), resultLabel, backButton
        );

        // Charger les problèmes existants au démarrage
        loadAllProblemsFromFile();
    }

    private void createNewProblem() {
        String problemName = newProblemNameField.getText();
        if (problemName.isEmpty()) {
            resultLabel.setText("Erreur : Entrez un nom pour le problème.");
            return;
        }

        if (problems.containsKey(problemName)) {
            resultLabel.setText("Erreur : Un problème avec ce nom existe déjà.");
            return;
        }

        DecisionProblem newProblem = new DecisionProblem(problemName);
        problems.put(problemName, newProblem);
        problemsListView.getItems().add(problemName);
        resultLabel.setText("Problème créé : " + problemName);
        newProblemNameField.clear();
    }

    private void loadProblem() {
        String selectedProblem = problemsListView.getSelectionModel().getSelectedItem();
        if (selectedProblem == null) {
            resultLabel.setText("Erreur : Aucun problème sélectionné.");
            return;
        }

        DecisionProblem problem = problems.get(selectedProblem);
        if (problem != null) {
            resultLabel.setText("Problème chargé : " + problem.getName());
            // Ici, vous pourriez charger les données dans une autre vue
        }
    }

    private void saveProblem() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("decision_problems.dat"))) {
            oos.writeObject(problems);
            resultLabel.setText("Problèmes sauvegardés avec succès.");
        } catch (IOException e) {
            resultLabel.setText("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void deleteProblem() {
        String selectedProblem = problemsListView.getSelectionModel().getSelectedItem();
        if (selectedProblem == null) {
            resultLabel.setText("Erreur : Aucun problème sélectionné.");
            return;
        }

        problems.remove(selectedProblem);
        problemsListView.getItems().remove(selectedProblem);
        resultLabel.setText("Problème supprimé : " + selectedProblem);
        saveProblem(); // Sauvegarder les modifications après suppression
    }

    private void loadAllProblemsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("decision_problems.dat"))) {
            problems = (Map<String, DecisionProblem>) ois.readObject();
            problemsListView.getItems().addAll(problems.keySet());
            resultLabel.setText("Problèmes chargés avec succès.");
        } catch (IOException | ClassNotFoundException e) {
            resultLabel.setText("Aucun fichier de sauvegarde trouvé.");
        }
    }

    public VBox getLayout() {
        return layout;
    }

    /**
     * Classe interne pour représenter un problème de décision
     */
    private static class DecisionProblem implements Serializable {
        private String name;
        private List<Criterion> criteria;
        private List<Alternative> alternatives;

        public DecisionProblem(String name) {
            this.name = name;
            this.criteria = new ArrayList<>();
            this.alternatives = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        public List<Alternative> getAlternatives() {
            return alternatives;
        }

        public void addCriterion(String name, double weight, double minValue, double maxValue) {
            criteria.add(new Criterion(name, weight, minValue, maxValue));
        }

        public void addAlternative(String name, List<Double> evaluations) {
            alternatives.add(new Alternative(name, evaluations));
        }
    }

    /**
     * Classe interne pour représenter un critère
     */
    private static class Criterion implements Serializable {
        private String name;
        private double weight, minValue, maxValue;

        public Criterion(String name, double weight, double minValue, double maxValue) {
            this.name = name;
            this.weight = weight;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }

    /**
     * Classe interne pour représenter une alternative
     */
    private static class Alternative implements Serializable {
        private String name;
        private List<Double> evaluations;

        public Alternative(String name, List<Double> evaluations) {
            this.name = name;
            this.evaluations = evaluations;
        }
    }
}
