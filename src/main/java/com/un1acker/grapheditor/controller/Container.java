package com.un1acker.grapheditor.controller;

import com.un1acker.grapheditor.model.graph.RootGraph;
import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Container implements Serializable {

    private static final long serialVersionUID = -1890998765646621338L;

    public static RootGraph graph = new RootGraph();

    public static final DataFormat AddNode = new DataFormat("com.un1acker.grapheditor.view.dragicon.DragIcon.add");

    public static final DataFormat DragNode = new DataFormat("com.un1acker.grapheditor.view.node.DraggableNode.drag");

    public static final DataFormat AddLink = new DataFormat("com.un1acker.grapheditor.view.node.NodeLink.add");

    private final List<Pair<String, Object>> mDataPairs = new ArrayList<>();

    public void addData(String key, Object value) {
        mDataPairs.add(new Pair<>(key, value));
    }

    public <T> T getValue(String key) {

        for (Pair<String, Object> data : mDataPairs) {

            if (data.getKey().equals(key))
                return (T) data.getValue();

        }

        return null;
    }

    public static void setGraph(RootGraph rootGraph) {
        graph = rootGraph;
    }

    public List<Pair<String, Object>> getData() {
        return mDataPairs;
    }
}