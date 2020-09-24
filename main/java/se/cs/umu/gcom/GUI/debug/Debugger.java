package se.cs.umu.gcom.GUI.debug;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

/**
 * The Debugger application.
 * Can be run through gradle.
 * In intellij open gradle window select Tasks -> application -> run to start.
 */
public class Debugger extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();

        URL url = new File("src/main/resources/main.fxml").toURI().toURL();
        loader.setLocation(url);
        Parent root = loader.load();

        primaryStage.setTitle("Template");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
