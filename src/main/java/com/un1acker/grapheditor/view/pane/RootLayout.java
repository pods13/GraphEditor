package com.un1acker.grapheditor.view.pane;

import com.un1acker.grapheditor.controller.Container;
import com.un1acker.grapheditor.model.DragIconType;
import com.un1acker.grapheditor.model.graph.GraphNode;
import com.un1acker.grapheditor.model.graph.RootGraph;
import com.un1acker.grapheditor.view.dragicon.DragIcon;
import com.un1acker.grapheditor.view.node.DraggableNode;
import com.un1acker.grapheditor.view.node.NodeLink;
import com.un1acker.grapheditor.view.tabs.TabWithZoom;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * @author un1acker
 **/
public class RootLayout extends AnchorPane {

    @FXML
    public SplitPane base_pane;
    @FXML
    private VBox left_pane;
    @FXML
    public TabPane graphTab;

    private int numberNode = 1;
    private int numberGlobal = 1;
    private int numberJoint = 1;
    public TabWithZoom right_pane;
    private DragIcon mDragOverIcon = null;
    private Map<Tab, TabWithZoom> tabsContent;
    private Map<Tab, RootGraph> tabsGraphs;
    private EventHandler mIconDragOverRoot = null;
    private EventHandler mIconDragDropped = null;
    private EventHandler mIconDragOverRightPane = null;
    private TabWithZoom tabWithZoom = new TabWithZoom();


    public RootLayout() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        tabsContent = new HashMap<>();
        tabsGraphs = new HashMap<>();
        right_pane = new TabWithZoom();
        right_pane.setPickOnBounds(true);
        right_pane.getStyleClass().add("tabs");
    }

    @FXML
    private void initialize() {

        //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root AnchorPane so it can be
        //visible on both sides of the split com.un1acker.grapheditor.pane.
        mDragOverIcon = new DragIcon();

        mDragOverIcon.setVisible(false);
        mDragOverIcon.setOpacity(0.65);
        getChildren().add(mDragOverIcon);


        //populate left com.un1acker.grapheditor.pane with multiple colored icons for testing
        for (int i = 0; i < 3; i++) {

            DragIcon icn = new DragIcon();

            addDragDetection(icn);

            icn.setType(DragIconType.values()[i]);

            left_pane.getChildren().add(icn);
        }

        buildDragHandlers();

        graphTab.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, oldTab, newTab) -> {
                            int numOfTabs = graphTab.getTabs().size();
                            if (numOfTabs > 1) {
                                oldTab.setContent(null);
                                Container.setGraph(tabsGraphs.get(newTab));
                                right_pane = tabsContent.get(newTab);
                                newTab.setContent(right_pane);
                            } else if (numOfTabs == 1) {
                                Container.setGraph(tabsGraphs.get(newTab));
                                right_pane = tabsContent.get(newTab);
                                newTab.setContent(right_pane);
                            }
                        });
    }

    public void addNewTab(TabWithZoom tabContent, String tabName) {

        Tab tabToRemove = graphTab.getTabs().stream()
                .filter(tab1 -> tab1.getId().equals(tabName))
                .findFirst().orElse(null);
        graphTab.getTabs().remove(ofNullable(tabToRemove).orElse(new Tab()));
        Tab newTab = new Tab(tabName, tabContent);
        newTab.setId(tabName);
        newTab.setOnClosed(event -> {
            tabsContent.remove(newTab);
            tabsGraphs.remove(newTab);
            graphTab.getTabs().remove(newTab);
        });

        right_pane = tabContent;

        tabsContent.put(newTab, tabContent);
        tabsGraphs.put(newTab, Container.graph);

        graphTab.getTabs().add(newTab);
        graphTab.getSelectionModel().select(newTab);
    }

    private void addDragDetection(DragIcon dragIcon) {

        dragIcon.setOnDragDetected(event -> {

            // set the other drag event handles on their respective objects
            base_pane.setOnDragOver(mIconDragOverRoot);
            right_pane.setOnDragOver(mIconDragOverRightPane);
            right_pane.setOnDragDropped(mIconDragDropped);

            // get a reference to the clicked com.un1acker.grapheditor.view.dragicon.DragIcon object
            DragIcon icn = (DragIcon) event.getSource();

            //begin drag ops
            mDragOverIcon.setType(icn.getType());
            mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();
            Container container = new Container();

            container.addData("type", mDragOverIcon.getType().toString());
            content.put(Container.AddNode, container);

            mDragOverIcon.startDragAndDrop(TransferMode.ANY).setContent(content);
            mDragOverIcon.setVisible(true);
            mDragOverIcon.setMouseTransparent(true);

            event.consume();
        });
    }

    private void buildDragHandlers() {

        //drag over transition to move widget form left com.un1acker.grapheditor.pane to right com.un1acker.grapheditor.pane
        mIconDragOverRoot = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                Point2D p = right_pane.getContent().sceneToLocal(event.getSceneX(), event.getSceneY());

                //turn on transfer mode and track in the right-com.un1acker.grapheditor.pane's context
                //if (and only if) the mouse cursor falls within the right com.un1acker.grapheditor.pane's bounds.
                if (!right_pane.getContent().boundsInLocalProperty().get().contains(p)) {

                    event.acceptTransferModes(TransferMode.ANY);
                    mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }

                event.consume();
            }
        };

        mIconDragOverRightPane = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);

                //convert the mouse coordinates to scene coordinates,
                //then convert back to coordinates that are relative to
                //the parent of mDragIcon.  Since mDragIcon is a child of the root
                //com.un1acker.grapheditor.pane, coodinates must be in the root com.un1acker.grapheditor.pane's coordinate system to work
                //properly.
                mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                event.consume();
            }
        };

        mIconDragDropped = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                Container container = (Container) event.getDragboard().getContent(Container.AddNode);

                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                content.put(Container.AddNode, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };

        this.setOnDragDone(event -> {

            right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
            right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
            base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

            mDragOverIcon.setVisible(false);

            Container container = (Container) event.getDragboard().getContent(Container.AddNode);

            if (container != null) {
                if (container.getValue("scene_coords") != null) {

                    DragIconType type = DragIconType.valueOf(container.getValue("type"));
                    DraggableNode node = new DraggableNode(getNumberNode(type));

                    node.setType(type);
                    right_pane.getContent().getChildren().add(node);

                    Point2D cursorPoint = container.getValue("scene_coords");
                    node.setPointNode(cursorPoint);
                    node.relocateToPoint(
                            new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                    );

                    //SERIALIZE NODE FROM DRAGBOARD
                    Container.graph.addNode(new GraphNode(node));
                }
            }

            container = (Container) event.getDragboard().getContent(Container.AddLink);

            if (container != null) {

                //bind the ends of our link to the nodes whose id's are stored in the drag container
                String sourceId = container.getValue("source");
                String targetId = container.getValue("target");

                if (sourceId != null && targetId != null) {

                    NodeLink link = new NodeLink();

                    //add our link at the top of the rendering order so it's rendered first
                    right_pane.getContent().getChildren().add(0, link);

                    DraggableNode source = null;
                    DraggableNode target = null;

                    for (Node node : right_pane.getContent().getChildren()) {

                        if (node.getId() == null)
                            continue;

                        if (node.getId().equals(sourceId))
                            source = (DraggableNode) node;

                        if (node.getId().equals(targetId))
                            target = (DraggableNode) node;

                    }

                    //Serialize links between nodes
                    for (GraphNode graphNode : Container.graph.getNodes()) {
                        if (graphNode.getId().equals(targetId)) {
                            GraphNode graphSource = new GraphNode(source);
                            if (!graphNode.getLinkedNodes().contains(graphSource)) {
                                graphNode.addLinkedNodes(graphSource);
                            }
                        }

                        if (graphNode.getId().equals(sourceId)) {
                            GraphNode graphTarget = new GraphNode(target);
                            if (!graphNode.getLinkedNodes().contains(graphTarget)) {
                                graphNode.addLinkedNodes(graphTarget);
                            }
                        }
                    }

                    if (source != null && target != null)
                        link.bindEnds(source, target);
                }

            }

            event.consume();
        });
    }

    private int getNumberNode(DragIconType type) {

        int name;

        switch (type) {
            case node:
                name = numberNode++;
                break;
            case global:
                name = numberGlobal++;
                break;
            case joint:
                name = numberJoint++;
                break;
            default:
                name = -1;
        }
        return name;
    }

    public void setNumberNode(DragIconType type, int num) {
        ++num;
        switch (type) {
            case node:
                numberNode = num;
                break;
            case global:
                numberGlobal = num;
                break;
            case joint:
                numberJoint = num;
                break;
        }
    }
}