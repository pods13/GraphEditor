package com.un1acker.grapheditor.controller;

import com.un1acker.grapheditor.model.graph.GraphNode;
import com.un1acker.grapheditor.model.graph.RootGraph;
import com.un1acker.grapheditor.view.node.DraggableNode;
import com.un1acker.grapheditor.view.node.NodeLink;
import com.un1acker.grapheditor.view.pane.RootLayout;
import com.un1acker.grapheditor.view.tabs.TabWithZoom;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Implement method needs to serialize/deserialize graph
 *
 * @author un1acker
 */
public class GraphSerialize {

    private final RootLayout rootLayout;

    public GraphSerialize(RootLayout rootLayout) {
        this.rootLayout = rootLayout;
    }

    public void loadGraphFromXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        setExtFilters(fileChooser);
        fileChooser.setTitle("Open Graph");

        File file = fileChooser.showOpenDialog(rootLayout.getScene().getWindow());
        if (file != null) {
            convertXMLToObject(file);
        }
    }

    public void saveGraphToXML(String fileNameToSave) {
        FileChooser fileChooser = new FileChooser();
        setExtFilters(fileChooser);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName(fileNameToSave);
        fileChooser.setTitle("Save Graph");

        File file = fileChooser.showSaveDialog(rootLayout.getScene().getWindow());
        if (file != null) {
            convertObjectToXml(file);
        }
    }

    public void convertXMLToObject(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(RootGraph.class);
            Unmarshaller um = context.createUnmarshaller();
            Container.setGraph((RootGraph) um.unmarshal(file));
            AnchorPane newPane = new AnchorPane();
            deSerialNodes(newPane);
            deSerialLinkBetweenNodes(newPane);
            createNewTab(newPane, file.getName());

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void createNewTab(AnchorPane content, String name) {
        TabWithZoom tabWithZoom = new TabWithZoom();
        tabWithZoom.setContent(content);
        rootLayout.addNewTab(tabWithZoom, name);
    }


    public void convertObjectToXml(File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(RootGraph.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(Container.graph, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void deSerialNodes(AnchorPane pane) {
        for (GraphNode graphNode : Container.graph.getNodes()) {

            DraggableNode node = new DraggableNode(graphNode.getNumNode(), graphNode.getId());
            node.setType(graphNode.getType());
            rootLayout.setNumberNode(graphNode.getType(), graphNode.getNumNode());
            pane.getChildren().add(node);

            Point2D cursorPoint = graphNode.getCursorPoint();
            node.setPointNode(cursorPoint);
            node.relocate(cursorPoint.getX(), cursorPoint.getY());

            graphNode.setNode(node);
        }
    }

    private void deSerialLinkBetweenNodes(AnchorPane pane) {
        for (GraphNode graphNode : Container.graph.getNodes()) {
            for (GraphNode link : graphNode.getLinkedNodes()) {
                NodeLink nodeLink = new NodeLink();

                pane.getChildren().add(0, nodeLink);

                DraggableNode dragSource = null;
                DraggableNode dragTarget = null;

                for (Node node : pane.getChildren()) {
                    if (node.getId() == null)
                        continue;

                    if (node.getId().equals(graphNode.getId())) {
                        dragSource = (DraggableNode) node;
                    }

                    if (node.getId().equals(link.getId())) {
                        dragTarget = (DraggableNode) node;
                    }
                }

                link.setNode(dragTarget);
                if (dragSource != null && dragTarget != null) {
                    nodeLink.bindEnds(dragSource, dragTarget);
                }
            }
        }
    }

    private void setExtFilters(FileChooser chooser) {
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML", "*.xml"));
    }
}
