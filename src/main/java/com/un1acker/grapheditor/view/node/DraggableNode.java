package com.un1acker.grapheditor.view.node;

import com.un1acker.grapheditor.controller.Container;
import com.un1acker.grapheditor.model.DragIconType;
import com.un1acker.grapheditor.model.graph.GraphNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class DraggableNode extends AnchorPane {

    private int numberDraggableNode;

    private Point2D pointNode;

    @FXML
    private AnchorPane left_link_handle;

    @FXML
    private AnchorPane right_link_handle;

    @FXML
    private Label title_bar;

    @FXML
    private Label close_button;

    private EventHandler<MouseEvent> mLinkHandleDragDetected;

    private EventHandler<DragEvent> mLinkHandleDragDropped;

    private EventHandler<DragEvent> mContextLinkDragOver;

    private EventHandler<DragEvent> mContextLinkDragDropped;

    private EventHandler<DragEvent> mContextDragOver;

    private EventHandler<DragEvent> mContextDragDropped;

    private DragIconType mType = null;

    private Point2D mDragOffset = new Point2D(0.0, 0.0);

    private final DraggableNode self;

    private NodeLink mDragLink = null;

    private AnchorPane right_pane = null;

    private final List<String> mLinkIds = new ArrayList<>();

    public DraggableNode() {
        this.pointNode = new Point2D(0.0, 0.0);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/DraggableNode.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        self = this;

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //provide a universally unique identifier for this object
        setId(UUID.randomUUID().toString());
    }

    public DraggableNode(int numberDraggableNode) {
        this();
        this.numberDraggableNode = numberDraggableNode;
    }

    public DraggableNode(int numberDraggableNode, String id) {
        this(numberDraggableNode);
        setId(id);
    }

    @Override
    public String toString() {
        return "D{type=" + getType() +
                ", name=" + numberDraggableNode + '}';
    }

    @FXML
    private void initialize() {

        buildNodeDragHandlers();
        buildLinkDragHandlers();

        left_link_handle.setOnDragDetected(mLinkHandleDragDetected);
        right_link_handle.setOnDragDetected(mLinkHandleDragDetected);

        left_link_handle.setOnDragDropped(mLinkHandleDragDropped);
        right_link_handle.setOnDragDropped(mLinkHandleDragDropped);

        mDragLink = new NodeLink();
        mDragLink.setVisible(false);

        parentProperty().addListener((observable, oldValue, newValue) -> {
            right_pane = (AnchorPane) getParent();
        });

    }

    public void setPointNode(Point2D pointNode) {
        this.pointNode = pointNode;
    }

    public Point2D getPointNode() {
        return this.pointNode;
    }

    public void registerLink(String linkId) {
        mLinkIds.add(linkId);
    }

    public List<String> getmLinkIds() {
        return mLinkIds;
    }

    public void setNumberDraggableNode(int numberDraggableNode) {
        this.numberDraggableNode = numberDraggableNode;
    }

    public int getNumberDraggableNode() {
        return numberDraggableNode;
    }

    public void relocateToPoint(Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates

        Point2D localCoords = getParent().sceneToLocal(p);
        double x = (localCoords.getX() - mDragOffset.getX());
        double y = (localCoords.getY() - mDragOffset.getY());
        relocate(x, y);

        //Need for serialize links
        setPointNode(new Point2D(x, y));
    }

    public DragIconType getType() {
        return mType;
    }

    public void setType(DragIconType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("drag_icon");

        switch (mType) {

            case node:
                title_bar.setText("N_" + getNumberDraggableNode());
                getStyleClass().add("icon-node");
                break;

            case joint:
                title_bar.setText("J_" + getNumberDraggableNode());
                getStyleClass().add("icon-joint");
                break;

            case global:
                title_bar.setText("G_" + getNumberDraggableNode());
                getStyleClass().add("icon-global");
                break;

            default:
                break;
        }
    }

    public void buildNodeDragHandlers() {

        mContextDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
            event.consume();
        };

        //dragdrop for node dragging
        mContextDragDropped = event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);
            event.setDropCompleted(true);
            event.consume();
        };

        //close button click
        close_button.setOnMouseClicked(event -> {
            AnchorPane parent = (AnchorPane) self.getParent();
            removeNode(new GraphNode(self));
            parent.getChildren().remove(self);

            //iterate each link id connected to this node
            //find it's corresponding component in the right-hand
            //AnchorPane and delete it.

            //Note:  other nodes connected to these links are not being
            //notified that the link has been removed.
            for (ListIterator<String> iterId = mLinkIds.listIterator(); iterId.hasNext(); ) {

                String id = iterId.next();

                for (ListIterator<Node> iterNode = parent.getChildren().listIterator(); iterNode.hasNext(); ) {

                    Node node = iterNode.next();

                    if (node.getId() == null)
                        continue;

                    if (node.getId().equals(id))
                        iterNode.remove();
                }

                iterId.remove();
            }

        });

        //drag detection for node dragging
        title_bar.setOnDragDetected(event -> {

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            getParent().setOnDragOver(mContextDragOver);
            getParent().setOnDragDropped(mContextDragDropped);

            //begin drag ops
            mDragOffset = new Point2D(event.getX(), event.getY());

            relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();
            Container container = new Container();

            container.addData("type", mType.toString());
            content.put(Container.AddNode, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);

            event.consume();
        });
    }

    public boolean removeNode(GraphNode nodeToRemove) {
        for (GraphNode node : Container.graph.getNodes()) {
            node.getLinkedNodes().remove(nodeToRemove);
        }
        return Container.graph.getNodes().remove(nodeToRemove);
    }

    private void buildLinkDragHandlers() {

        mLinkHandleDragDetected = event -> {

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            getParent().setOnDragOver(mContextLinkDragOver);
            getParent().setOnDragDropped(mContextLinkDragDropped);

            //Set up user-draggable link
            right_pane.getChildren().add(0, mDragLink);

            mDragLink.setVisible(false);

            Point2D p = new Point2D(
                    getLayoutX() + (getWidth() / 2.0),
                    getLayoutY() + (getHeight() / 2.0)
            );

            mDragLink.setStart(p);

            //Drag content code
            ClipboardContent content = new ClipboardContent();
            Container container = new Container();

            //pass the UUID of the source node for later lookup
            container.addData("source", getId());

            content.put(Container.AddLink, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);

            event.consume();
        };

        mLinkHandleDragDropped = event -> {

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            //get the drag data.  If it's null, abort.
            //This isn't the drag event we're looking for.
            Container container =
                    (Container) event.getDragboard().getContent(Container.AddLink);

            if (container == null)
                return;

            //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
            mDragLink.setVisible(false);
            right_pane.getChildren().remove(0);

            AnchorPane link_handle = (AnchorPane) event.getSource();

            ClipboardContent content = new ClipboardContent();

            //pass the UUID of the target node for later lookup
            container.addData("target", getId());

            content.put(Container.AddLink, container);

            event.getDragboard().setContent(content);
            event.setDropCompleted(true);
            event.consume();
        };

        mContextLinkDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);

            //Relocate end of user-draggable link
            if (!mDragLink.isVisible())
                mDragLink.setVisible(true);

            mDragLink.setEnd(new Point2D(event.getX(), event.getY()));

            event.consume();

        };

        //drop event for link creation
        mContextLinkDragDropped = event -> {
            System.out.println("context link drag dropped");

            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
            mDragLink.setVisible(false);
            right_pane.getChildren().remove(0);

            event.setDropCompleted(true);
            event.consume();
        };
    }
}
