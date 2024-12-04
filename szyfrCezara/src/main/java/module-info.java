module com.example.szyfrcezara {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.szyfrcezara to javafx.fxml;
    exports com.example.szyfrcezara;
}