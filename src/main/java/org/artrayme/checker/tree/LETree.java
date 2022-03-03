package org.artrayme.checker.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LETree {
    private final LENode root;

    public LETree(LENode root) {
        this.root = root;
    }

    public LENode getRoot() {
        return root;
    }

    public boolean isSknf(){
        Map<String, List<LENode>> part = new HashMap<>();
        return false;
    }


}
