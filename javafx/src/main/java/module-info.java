module at.htl.digitaleschulwahl {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.apache.pdfbox;
    requires java.desktop;

    exports at.htl.digitaleschulwahl.app to javafx.graphics;
    opens at.htl.digitaleschulwahl.app to javafx.graphics;
    opens at.htl.digitaleschulwahl.presenter to javafx.base;
    opens at.htl.digitaleschulwahl to javafx.fxml;
    opens at.htl.digitaleschulwahl.model to java.base;
}