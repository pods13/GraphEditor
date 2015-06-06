package com.un1acker.grapheditor.view.dialogs;

import com.un1acker.grapheditor.controller.Container;
import com.un1acker.grapheditor.controller.algorithms.KodresAlgorithm;
import com.un1acker.grapheditor.model.graph.GraphNode;
import com.un1acker.grapheditor.view.App;
import insidefx.undecorator.UndecoratorScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author un1acker
 */
public class KodresDialog {
    @FXML
    private TextField area;
    @FXML
    private TextField terminal;

    private Stage utilityStage;
    private Region root;

    public KodresDialog() {
        utilityStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/KodresDialog.fxml"));
            fxmlLoader.setController(this);
            root = fxmlLoader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        showErrorDialog();
    }

    public void showErrorDialog() {
        utilityStage.setTitle("Set Kodres parametric");
        UndecoratorScene scene = new UndecoratorScene(utilityStage, StageStyle.UTILITY, root, null);
        scene.addStylesheet("/css/error_dialog.css");
        utilityStage.setScene(scene);
        utilityStage.initModality(Modality.WINDOW_MODAL);
        utilityStage.initOwner(App.primaryStage);

        utilityStage.setResizable(false);
        utilityStage.show();
    }

    public void handleRunKodres(ActionEvent event) {
        int areas = Integer.parseInt(area.getText());
        int terminals = Integer.parseInt(terminal.getText());
        System.out.println("Area=" + area + ", Terminal=" + terminals);

        long start = System.nanoTime();

        Map<Integer, List<GraphNode>> resultMapKodres =
                new KodresAlgorithm(Container.graph, areas, terminals).formBlocks();

        long end = System.nanoTime();
        System.out.println("Time: " + (end - start));

        repaintNodes(resultMapKodres);
        handleCloseDialog(event);
    }

    public void handleCloseDialog(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    private void repaintNodes(Map<Integer, List<GraphNode>> resultMapKodres) {
        Random random = new Random();
        for (Map.Entry<Integer, List<GraphNode>> entry : resultMapKodres.entrySet()) {
            Color randomColor = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 0.7);
            String color = randomColor.toString().substring(2, 8);

            for (GraphNode node : entry.getValue()) {
                node.getDraggableNode().setStyle("-fx-background-color: #" + color + ";");
                for (GraphNode link : node.getLinkedNodes()) {
                    link.getDraggableNode().setStyle("-fx-background-color: rgba(255, 255, 255, 1.0); -fx-background-image: none;");
                }
            }
        }

    }

}
