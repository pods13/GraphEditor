package com.un1acker.grapheditor.model.graph;

import com.un1acker.grapheditor.model.DragIconType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class-model hypergraph
 *
 * @author un1acker
 * */

@XmlRootElement(name = "graph")
public class RootGraph {

    @XmlElements({
            @XmlElement(name = "node", type = GraphNode.class)
    })
    private List<GraphNode> nodes;

    public RootGraph() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(GraphNode node) {
        nodes.add(node);
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public int getOrder() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.size() == 0;
    }

    public int getSizeGraphNodes() {
        return getNodesByType(DragIconType.node).size();
    }

    public List<GraphNode> getNodesByType(DragIconType type) {

        List<GraphNode> nodesByType = new ArrayList<>();
        for (GraphNode node : nodes) {
            if (node.getType().equals(type)) {
                nodesByType.add(node);
            }
        }

        return nodesByType;
    }

    public List<GraphNode> getNodesLink(GraphNode node) {
        int index = nodes.indexOf(node);
        return nodes.get(index).getLinkedNodes();
    }

    public String toString() {
        return "RootGraph{" +
                "nodes=" + nodes +
                '}';
    }
}