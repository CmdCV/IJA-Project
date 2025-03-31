module ija.project.ijaproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens ija.project.ijaproject to javafx.fxml;
    exports ija.project.ijaproject;
}