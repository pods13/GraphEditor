package com.un1acker.grapheditor.view.tabs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * @author un1acker
 */
public class TabWithZoom extends AnchorPane {
    @FXML
    public ZoomFX zoomFX;
    @FXML
    private Button actualSize;

    @FXML
    private Button zoomOut;

    @FXML
    private Button zoomIn;

    @FXML
    private Label zoomFactor;

    @FXML
    private AnchorPane content;

    public TabWithZoom() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ZoomFX.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setContent(AnchorPane content) {
        this.content.getChildren().addAll(content.getChildren());
    }

    public AnchorPane getContent() {
        return content;
    }

    public void initialize() {
        zoomFX.zoomFactorProperty().addListener((prop, oldVal, newVal) -> zoomFactor.setText(String.format("%d%%", Math.round(newVal.doubleValue() * 100))));
        actualSize.setOnAction((event) -> zoomFX.zoomFactorProperty().set(1.0));
        zoomOut.setOnAction((event) -> zoomFX.zoomFactorProperty().set(zoomFX.zoomFactorProperty().get() * 0.75));
        zoomIn.setOnAction((event) -> zoomFX.zoomFactorProperty().set(zoomFX.zoomFactorProperty().get() * 1.25));
    }
}
