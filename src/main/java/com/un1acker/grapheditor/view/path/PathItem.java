package com.un1acker.grapheditor.view.path;


import java.nio.file.Path;

/**
 * @author un1acker
 */
public class PathItem {
    private Path path;

    public PathItem(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        if (path.getFileName() == null) {
            return path.toString();
        } else {
            return path.getFileName().toString();
        }
    }
}
