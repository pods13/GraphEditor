package com.un1acker.grapheditor.controller.algorithms;

import com.un1acker.grapheditor.model.DragIconType;
import com.un1acker.grapheditor.model.graph.GraphNode;
import com.un1acker.grapheditor.model.graph.RootGraph;

import java.util.*;

/**
 * Implementation of Kodres Algorithm
 *
 * @author un1acker
 */

public class KodresAlgorithm {

    private RootGraph graph;

    /**
     * S(v)
     */
    private int maxAreaBlock;

    /**
     * T(v)
     */
    private int maxNumberTerminal;

    private int numberBlock = 1;

    private Map<Integer, List<GraphNode>> resultBlockMap = new HashMap<>();

    public KodresAlgorithm(RootGraph graph, int maxAreaBlock, int maxNumberTerminal) {
        this.graph = graph;
        this.maxAreaBlock = maxAreaBlock;
        this.maxNumberTerminal = maxNumberTerminal;
    }

    public RootGraph getGraph() {
        return graph;
    }

    public void setGraph(RootGraph graph) {
        this.graph = graph;
    }

    private Map<GraphNode, Integer> getConNodes() {

        Map<GraphNode, Integer> conMap = new HashMap<>();
        for (GraphNode node : graph.getNodesByType(DragIconType.node)) {
            int numberLinks = 0;
            for (GraphNode linkNode : node.getLinkedNodes()) {
                if (linkNode.getType().equals(DragIconType.global)) {
                    ++numberLinks;
                }
            }
            conMap.put(node, numberLinks);
        }

        return conMap;
    }

    private Map<GraphNode, Integer> getMinConNodes(Map<GraphNode, Integer> maxCon) {

        Map<GraphNode, Integer> conMap = new HashMap<>();

        int numberLink = Integer.MAX_VALUE;
        for (Map.Entry<GraphNode, Integer> entry : maxCon.entrySet()) {
            int count = 0;
            for (GraphNode linkNode : entry.getKey().getLinkedNodes()) {
                if (graph.getNodesLink(linkNode).size() > 1) {
                    ++count;
                }
            }
            if (count < numberLink) {
                numberLink = count;
                conMap.clear();

                conMap.put(entry.getKey(), count);
            } else if (count == numberLink) {
                conMap.put(entry.getKey(), count);
            }
        }

        return conMap;
    }

    private Map<GraphNode, Integer> getMaxCons(Map<GraphNode, Integer> conMap) {

        int maxValue = Collections.max(conMap.values());

        Map<GraphNode, Integer> maxCon = new HashMap<>();
        for (Map.Entry<GraphNode, Integer> entry : conMap.entrySet()) {
            if (entry.getValue().equals(maxValue)) {
                maxCon.put(entry.getKey(), entry.getValue());
            }
        }
        return maxCon;
    }

    private GraphNode chooseFirstNode() {

        Map<GraphNode, Integer> conMap = getConNodes();
        Map<GraphNode, Integer> maxCon = getMaxCons(conMap);

        if (maxCon.size() == 1) {
            return maxCon.keySet().iterator().next();
        } else {
            conMap = getMinConNodes(maxCon);
            if (conMap.size() == 1) {
                return conMap.keySet().iterator().next();
            } else {
                return Collections.min(conMap.keySet(),
                        (key1, key2) -> key1.getNumNode() > key2.getNumNode() ? 1 : -1);
            }
        }
    }

    private Map<GraphNode, Integer> getConNodeWithBlock(List<GraphNode> blocksNodes) {

        Map<GraphNode, Integer> conMap = new HashMap<>();
        List<GraphNode> linkNodes = new ArrayList<>();

        for (GraphNode blocksNode : blocksNodes) {
            for (GraphNode link : blocksNode.getLinkedNodes()) {
                if (graph.getNodesLink(link).size() > 1 && !linkNodes.contains(link)) {
                    linkNodes.add(link);
                }
            }
        }

        for (GraphNode node : graph.getNodesByType(DragIconType.node)) {
            if (!blocksNodes.contains(node)) {
                int count = 0;
                for (GraphNode link : linkNodes) {
                    if (node.getLinkedNodes().contains(link)) {
                        conMap.put(node, ++count);
                    }
                }
            }
        }

        if (conMap.size() == 0) {
            for (GraphNode node : graph.getNodesByType(DragIconType.node)) {
                if (!blocksNodes.contains(node)) {
                    conMap.put(node, 0);
                }
            }
        }

        return conMap;
    }

    private Map<GraphNode, Integer> getDisNodeWithBlock(List<GraphNode> blocksNodes, Map<GraphNode, Integer> maxCon) {

        Map<GraphNode, Integer> conMap = new HashMap<>();
        List<GraphNode> linkNodes = new ArrayList<>();

        for (GraphNode blocksNode : blocksNodes) {
            for (GraphNode link : blocksNode.getLinkedNodes()) {
                if (!linkNodes.contains(link)) {
                    linkNodes.add(link);
                }
            }
        }

        int numberLink = Integer.MAX_VALUE;
        for (Map.Entry<GraphNode, Integer> entry : maxCon.entrySet()) {

            int count = 0;
            for (GraphNode node : graph.getNodesLink(entry.getKey())) {
                if (linkNodes.contains(node)) {
                    count++;
                }
            }
            count = graph.getNodesLink(entry.getKey()).size() + linkNodes.size() - count;

            if (count < numberLink) {
                numberLink = count;
                conMap.clear();

                conMap.put(entry.getKey(), count);
            } else if (count == numberLink) {
                conMap.put(entry.getKey(), count);
            }
        }

        return conMap;
    }

    private GraphNode shiftToBlock(List<GraphNode> blocksNode) {

        Map<GraphNode, Integer> conMap = getConNodeWithBlock(blocksNode);
        Map<GraphNode, Integer> maxCon = getMaxCons(conMap);

        if (maxCon.size() == 1) {
            return maxCon.keySet().iterator().next();
        } else {
            conMap = getDisNodeWithBlock(blocksNode, maxCon);
            if (conMap.size() == 1) {
                return conMap.keySet().iterator().next();
            } else {
                return Collections.min(conMap.keySet(),
                        (key1, key2) -> key1.getNumNode() > key2.getNumNode() ? 1 : -1);
            }
        }
    }

    private boolean limitOfBlock(List<GraphNode> nodeToBlock, GraphNode node) {

        List<GraphNode> linkNodes = new ArrayList<>();
        List<GraphNode> nodes = new ArrayList<>(nodeToBlock);
        nodes.add(node);

        int numberLink = 0;
        for (GraphNode blocksNode : nodes) {
            for (GraphNode link : blocksNode.getLinkedNodes()) {
                if (!linkNodes.contains(link)) {
                    linkNodes.add(link);
                }
                ++numberLink;
            }
        }

        return linkNodes.size() <= maxNumberTerminal && numberLink <= maxAreaBlock;
    }

    private boolean lexicographicalVerification(List<GraphNode> nodesToBlock) {

        List<GraphNode> nodes = graph.getNodesByType(DragIconType.node);
        while (true) {
            if (nodes.size() != 0) {

                GraphNode minNode = Collections.min(nodes,
                        (key1, key2) -> key1.getNumNode() > key2.getNumNode() ? 1 : -1);
                if (limitOfBlock(nodesToBlock, minNode)) {
                    nodesToBlock.add(minNode);
                    resultBlockMap.put(numberBlock, nodesToBlock);
                    removeBlock(nodesToBlock);

                    return true;
                }
                nodes.remove(minNode);
            } else {
                return false;
            }
        }
    }

    private void changeJointToGlobal(List<GraphNode> jointList) {

        for (GraphNode jointNode : jointList) {
            for (GraphNode node : graph.getNodes()) {
                if (node.equals(jointNode)) {
                    node.setType(DragIconType.global);
                } else {
                    for (GraphNode linkNode : graph.getNodesLink(node)) {
                        if (linkNode.equals(jointNode)) {
                            linkNode.setType(DragIconType.global);
                        }
                    }
                }
            }

            for (GraphNode node : resultBlockMap.get(numberBlock)) {
                if (node.equals(jointNode)) {
                    node.setType(DragIconType.global);
                } else {
                    for (GraphNode linkNode : node.getLinkedNodes()) {
                        if (linkNode.equals(jointNode)) {
                            linkNode.setType(DragIconType.global);
                        }
                    }
                }
            }
        }
    }

    private void removeBlock(List<GraphNode> nodesToBlock) {

        List<GraphNode> jointList = new ArrayList<>();
        for (GraphNode node : graph.getNodesByType(DragIconType.node)) {
            if (nodesToBlock.contains(node)) {

                for (GraphNode link : node.getLinkedNodes()) {
                    if (graph.getNodesLink(link).size() == 1) {
                        graph.getNodes().remove(link);
                    } else {
                        graph.getNodesLink(link).remove(node);
                        jointList.add(link);
                    }
                }
                graph.getNodes().remove(node);
            }
        }

        for (GraphNode node : graph.getNodes()) {
            node.getLinkedNodes().removeAll(nodesToBlock);
        }

        changeJointToGlobal(jointList);
    }

    public Map<Integer, List<GraphNode>> formBlocks() {
        while (graph.getOrder() != 0) {

            List<GraphNode> nodesToBlock = new ArrayList<>();

            nodesToBlock.add(chooseFirstNode());
            resultBlockMap.put(numberBlock, nodesToBlock);

            while (graph.getSizeGraphNodes() != nodesToBlock.size()) {
                GraphNode node = shiftToBlock(resultBlockMap.get(numberBlock));
                if (limitOfBlock(nodesToBlock, node)) {
                    nodesToBlock.add(node);
                } else {
                    removeBlock(nodesToBlock);
                    lexicographicalVerification(nodesToBlock);
                    break;
                }
            }

            if (graph.getSizeGraphNodes() == nodesToBlock.size()) {
                removeBlock(nodesToBlock);
            }

            resultBlockMap.put(numberBlock, nodesToBlock);
            System.out.println(resultBlockMap);

            ++numberBlock;
        }

        return resultBlockMap;
    }

    public void init() {
        formBlocks();
    }
}