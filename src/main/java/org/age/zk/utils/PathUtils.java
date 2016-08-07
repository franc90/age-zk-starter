package org.age.zk.utils;

import java.util.Arrays;

public class PathUtils {

    private static final String ROOT = "/";

    private static final String SEPARATOR = "/";

    public static String createPath(String... nodes) {
        String path = Arrays
                .stream(nodes)
                .reduce((x, y) -> x + SEPARATOR + y)
                .orElse("");
        return ROOT + path;
    }

    public static String appendNode(String nodePath, String child) {
        return nodePath + SEPARATOR + child;
    }
}
