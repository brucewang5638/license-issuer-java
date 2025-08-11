module com.example.licenseissuer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.google.gson;
    requires java.desktop;

    opens com.example.licenseissuer to javafx.fxml;
    exports com.example.licenseissuer;
}
