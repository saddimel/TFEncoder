module me.t3sl4.textfileencoder {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires commons.collections;
    requires org.apache.commons.codec;
    requires java.desktop;

    opens me.ahmetmelihomerabdullah.textfileencoder to javafx.fxml;
    exports me.ahmetmelihomerabdullah.textfileencoder;
    exports me.ahmetmelihomerabdullah.textfileencoder.Utils;
    opens me.ahmetmelihomerabdullah.textfileencoder.Utils to javafx.fxml;
    exports me.ahmetmelihomerabdullah.textfileencoder.Controllers;
    opens me.ahmetmelihomerabdullah.textfileencoder.Controllers to javafx.fxml;
    exports me.ahmetmelihomerabdullah.textfileencoder.Main;
    opens me.ahmetmelihomerabdullah.textfileencoder.Main to javafx.fxml;
}