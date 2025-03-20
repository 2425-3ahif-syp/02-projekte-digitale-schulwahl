module at.htl.digitaleschulwahl {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports at.htl.digitaleschulwahl.app to javafx.graphics;
    opens at.htl.digitaleschulwahl.app to javafx.graphics;
    opens at.htl.digitaleschulwahl.controller to javafx.base;
    opens at.htl.digitaleschulwahl to javafx.fxml;
    opens at.htl.digitaleschulwahl.model to java.base;
}