module com.sh.sheduler {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sh.sheduler to javafx.fxml;
    exports com.sh.sheduler;
}