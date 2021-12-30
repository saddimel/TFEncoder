package me.ahmetmelihomerabdullah.textfileencoder.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.ahmetmelihomerabdullah.textfileencoder.Runner;

import java.io.IOException;

public class TFEncoder extends Application {

    private Image TFEncoderIcon = new Image(getClass().getResourceAsStream("/icons/icon.png"));

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Runner.class.getResource("tfencoder.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1120, 480);
        stage.getIcons().add(TFEncoderIcon);
        stage.setTitle("TFEncoder");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}