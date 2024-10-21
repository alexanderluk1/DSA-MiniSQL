package edu.smu.smusql.pair1;

import java.util.ArrayList;
import java.util.List;

public class AVLNode<K> {
    private K key;
    private List<Integer> values;
    private AVLNode<K> left = null;
    private AVLNode<K> right = null;
    private AVLNode<K> parent = null;
    private int height;

    public AVLNode(K key, Integer value) {
        this.key = key;
        values = new ArrayList<>();
        values.add(value);
        height = 1;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public List<Integer> getValues() {
        return values;
    }

    public AVLNode<K> getLeft() {
        return left;
    }

    public void setLeft(AVLNode<K> left) {
        this.left = left;
    }

    public AVLNode<K> getRight() {
        return right;
    }

    public void setRight(AVLNode<K> right) {
        this.right = right;
    }

    public AVLNode<K> getParent() {
        return parent;
    }

    public void setParent(AVLNode<K> p) {
        parent = p;
    }

    public int getHeight() {
        return height;
    }

    public boolean isExternal() {
        return left == null && right == null;
    }

    public void updateHeight() {
        if (isExternal()) {
            height = 1;
        }
        int leftHeight = left == null ? 0 : left.getHeight();
        int rightHeight = right == null ? 0 : right.getHeight();
        height = Math.max(leftHeight, rightHeight) + 1;
    }

    public int numChildren() {
        int count = 0;
        if (left != null) {
            count++;
        }
        if (right != null) {
            count++;
        }
        return count;
    }

    @Override
    public String toString() {
        return key + " " + values + " " + height;
    }
}
