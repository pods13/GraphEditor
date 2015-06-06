package com.un1acker.grapheditor.view.dragicon;

import com.un1acker.grapheditor.model.DragIconType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Implementation view of GraphNode
 *
 * @author un1acker
 */

public class DragIcon extends AnchorPane {

    private DragIconType mType;

    public DragIcon() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/DragIcon.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void relocateToPoint(Point2D p) {

        Point2D localCoords = getParent().sceneToLocal(p);

        relocate(
                (int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
                (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
        );
    }

    public DragIconType getType() {
        return mType;
    }

    public void setType(DragIconType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("drag_icon");

        //added because the cubic curve will persist into other icons
        if (this.getChildren().size() > 0)
            getChildren().clear();

        switch (mType) {

            case node:
                getStyleClass().add("icon-node-panel");
                break;

            case joint:
                getStyleClass().add("icon-joint-panel");
                break;

            case global:
                getStyleClass().add("icon-global-panel");
                break;

            default:
                break;
        }
    }
}