package com.un1acker.grapheditor.model.graph;

import com.un1acker.grapheditor.model.DragIconType;
import com.un1acker.grapheditor.view.node.DraggableNode;
import javafx.geometry.Point2D;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class-model hypergraph nodes
 *
 * @author un1acker
 * */

@XmlType(propOrder = {"id", "numNode", "type", "x", "y", "linkedNodes"})
public class GraphNode {

    private String id;

    private int numNode;

    private double X;

    private double Y;

    private DragIconType type;

    @XmlTransient
    private DraggableNode node;

    @XmlElementWrapper(name = "linkedNodes")
    @XmlElement(name = "node", type = GraphNode.class)
    private List<GraphNode> linkedNodes;

    public GraphNode() {
        this.linkedNodes = new ArrayList<>();
    }

    public GraphNode(DraggableNode node) {
        this();
        this.id = node.getId();
        this.numNode = node.getNumberDraggableNode();
        this.type = node.getType();
        this.node = node;
    }

    public int getNumNode() {
        return numNode;
    }

    public void setNumNode(int numNode) {
        this.numNode = numNode;
    }

    public DragIconType getType() {
        return type;
    }

    public void setType(DragIconType type) {
        this.type = type;
    }

    public double getX() {
        return node.getPointNode().getX();
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return node.getPointNode().getY();
    }

    public void setY(double y) {
        Y = y;
    }

    public Point2D getCursorPoint() {
        return new Point2D(X, Y);
    }

    public List<GraphNode> getLinkedNodes() {
        return linkedNodes;
    }

    public void addLinkedNodes(GraphNode node) {
        this.linkedNodes.add(node);
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GraphNode)) {
            return false;
        }
        GraphNode node = (GraphNode) obj;
        return this.numNode == node.getNumNode() &&
                this.getType().equals(node.getType()) &&
                this.getX() == node.getX() &&
                this.getY() == node.getY();

    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNode(DraggableNode node) {
        this.node = node;
    }

    public String toString() {
        return "GraphNode{" +
                type + "__" + numNode +
                "}";
    }

    public DraggableNode getDraggableNode() {
        return node;
    }
}
