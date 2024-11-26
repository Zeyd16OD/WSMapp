package com.example.wsmapp;

import javafx.scene.layout.BorderPane;

public class NavigationController {
    private BorderPane root;

    public NavigationController(BorderPane root) {
        this.root = root;
    }

    public void loadStep1() {
        root.setCenter(new Step1View(this).getLayout());
    }

    public void loadStep2() {
        root.setCenter(new Step2View(this).getLayout());
    }

    public void loadStep3() {
        root.setCenter(new Step3View(this).getLayout());
    }

    public void loadStep4() {
        root.setCenter(new Step4View(this).getLayout());
    }
}
