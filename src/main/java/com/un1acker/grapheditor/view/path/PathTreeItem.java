package com.un1acker.grapheditor.view.path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * @author un1acker
 */
public class PathTreeItem extends TreeItem<PathItem> {
    private boolean isLeaf = false;
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;

    private PathTreeItem(PathItem pathItem) {
        super(pathItem);
    }

    public static TreeItem<PathItem> createNode(PathItem pathItem) {
        return new PathTreeItem(pathItem);
    }

    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            Path path = getValue().getPath();
            isLeaf = !Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
        }
        return isLeaf;
    }

    @Override
    public ObservableList<TreeItem<PathItem>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    private ObservableList<TreeItem<PathItem>> buildChildren(TreeItem<PathItem> treeItem) {
        Path path = treeItem.getValue().getPath();
        if (path != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            ObservableList<TreeItem<PathItem>> children = FXCollections.observableArrayList();
            try (DirectoryStream<Path> dirs = Files.newDirectoryStream(path)) {
                for (Path dir : dirs) {
                    if (dir.getFileName().toFile().getName().matches("^[\\w]+\\.xml$")) {

                        PathItem pathItem = new PathItem(dir);
                        children.add(createNode(pathItem));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return children;
        }
        return FXCollections.emptyObservableList();
    }
}