package com.un1acker.grapheditor.controller;

import com.un1acker.grapheditor.model.DragIconType;
import com.un1acker.grapheditor.model.graph.GraphNode;
import com.un1acker.grapheditor.view.App;
import com.un1acker.grapheditor.view.dialogs.ErrorDialog;
import com.un1acker.grapheditor.view.dialogs.KodresDialog;
import com.un1acker.grapheditor.view.pane.RootLayout;
import com.un1acker.grapheditor.view.path.PathItem;
import com.un1acker.grapheditor.view.path.PathTreeCell;
import com.un1acker.grapheditor.view.path.PathTreeItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author un1acker
 * */

public class AppController implements Initializable {

    private static final Path ROOT_PATH = Paths.get(System.getProperty("user.home"));

    @FXML
    private Button newGraph;

    @FXML
    private Button openGraph;

    @FXML
    private Button saveGraph;

    @FXML
    private Button kodres;

    @FXML
    private RootLayout rootLayout;

    @FXML
    private TreeView<PathItem> fileTreeView;

    private GraphSerialize graphSerialize;

    /**
     * Provides actions to the main button and fileTreeView
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        graphSerialize = new GraphSerialize(rootLayout);
        graphSerialize.convertXMLToObject(new File(App.class
                .getResource("/files/untitled").getPath()));

        newGraph.setOnAction(event1 -> graphSerialize.convertXMLToObject(new File(App.class
                .getResource("/files/untitled").getPath())));
        openGraph.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            directoryChooser.setTitle("Open Graph");

            File file = directoryChooser.showDialog(rootLayout.getScene().getWindow());
            Optional.ofNullable(file).ifPresent(cons -> createTreeView(Paths.get(cons.getPath())));
        });

        saveGraph.setOnAction(event -> {
            if (!rootLayout.graphTab.getTabs().isEmpty())
                graphSerialize.saveGraphToXML(rootLayout.graphTab.getSelectionModel().getSelectedItem().getId());
        });

        kodres.setOnAction(event -> {
            if (!rootLayout.graphTab.getTabs().isEmpty()) {
                setKodresParametrics();
            }
        });

        createTreeView(ROOT_PATH);
    }

    /**
     * Create {@link #fileTreeView}, where adds
     * all files with .xml extension in directory
     *
     * @param rootPath contain path to directory
     *                 where search files .xml
     *                 By default it is {@link #ROOT_PATH}
     * */
    private void createTreeView(Path rootPath) {
        PathItem pathItem = new PathItem(rootPath);
        TreeItem<PathItem> rootTreeItem = PathTreeItem.createNode(pathItem);
        rootTreeItem.setExpanded(true);
        fileTreeView.setRoot(rootTreeItem);
        fileTreeView.setCellFactory(param -> new PathTreeCell(graphSerialize));
    }


    private void setKodresParametrics() {
        if (!isGraphError()) {
            new KodresDialog();
        }
    }


    private boolean isGraphError() {
        ErrorDialog errorDialog = new ErrorDialog();
        String graphError = "Graph Error";
        if (Container.graph.isEmpty()) {
            errorDialog.showErrorDialog(graphError, "Your graph is empty, please, adds nodes!");
            return true;
        } else if (isGlobalNodesError()) {
            errorDialog.showErrorDialog(graphError, "Some off your global nodes does not have link!");
            return true;
        } else if (isJoinNodesError()) {
            errorDialog.showErrorDialog(graphError, "Some off your join nodes have less then two connections!");
            return true;
        } else if (isNodesError()) {
            errorDialog.showErrorDialog(graphError, "Some off your nodes does not connect!");
            return true;
        }
        return false;
    }

    private boolean isGlobalNodesError() {
        for (GraphNode node : Container.graph.getNodesByType(DragIconType.global)) {
            if (Container.graph.getNodesLink(node).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isJoinNodesError() {
        for (GraphNode node : Container.graph.getNodesByType(DragIconType.joint)) {
            if (Container.graph.getNodesLink(node).size() < 2) {
                return true;
            }
        }
        return false;
    }

    private boolean isNodesError() {
        for (GraphNode node : Container.graph.getNodesByType(DragIconType.node)) {
            if (Container.graph.getNodesLink(node).isEmpty()) {
                return true;
            }
        }
        return false;
    }


}
