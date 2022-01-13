package com.InfantaPlayer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/*
Everything is clear here
*/
public class Main extends Application {

    private ObservableList<Track> trackData = FXCollections.observableArrayList();
    private static Stage primaryStage;

    public Main() {}

    @Override
    public void start(Stage primaryStage) throws Exception {

        setStage(primaryStage);

        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("InfantaPlayer.fxml"));
        Parent root =(Parent) fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.setMain(this);

        Scene scene = new Scene(root, 820, 740);
        scene.getStylesheets().add(ClassLoader.getSystemResource("DarkTheme.css").toExternalForm());

        scene.setFill(Color.TRANSPARENT);

        primaryStage.setTitle("InfantaPlayer v0.1");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return Main.primaryStage;
    }

    private void setStage(Stage stage) {
        Main.primaryStage = stage;
    }
}

