package com.un1acker.grapheditor.view.path;

import com.un1acker.grapheditor.controller.GraphSerialize;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;

/**
 * @author un1acker
 */
public class PathTreeCell extends TreeCell<PathItem> {
    private TextField textField;
    private GraphSerialize graphSerialize;

    public PathTreeCell(GraphSerialize graphSerialize) {
        this.graphSerialize = graphSerialize;
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (itemProperty().get().getPath().toFile().getName().matches("^[\\w]+\\.xml$")) {
            graphSerialize.convertXMLToObject(itemProperty().get().getPath().toFile());
        }
        super.cancelEdit();
    }


    @Override
    public void updateItem(PathItem item, boolean empty) {
        super.updateItem(item, empty);

        // if the item is not empty and is a root...
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private String getString() {
        return getItem().toString();
    }
}
