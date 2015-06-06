package com.un1acker.grapheditor.view;

import insidefx.undecorator.UndecoratorScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *  @author un1acker
 **/
public class App extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        Font.loadFont(App.class.getResource("/font/fontAwesome.ttf").toExternalForm(), 10);

        Region root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/App.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
        undecoratorScene.addStylesheet("/css/modena.css");
        undecoratorScene.setFadeInTransition();

        stage.setTitle("Graph Editor");
        stage.setScene(undecoratorScene);
        stage.sizeToScene();
        stage.toFront();
        stage.getIcons().add(new Image("images/app_icon.png"));
        stage.setMinWidth(undecoratorScene.getUndecorator().getMinWidth());
        stage.setMinHeight(undecoratorScene.getUndecorator().getMinHeight());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}