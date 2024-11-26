module com.example.wsmapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.wsmapp to javafx.fxml;
    exports com.example.wsmapp;
}