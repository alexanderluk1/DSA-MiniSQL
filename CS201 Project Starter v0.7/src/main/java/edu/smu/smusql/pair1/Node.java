package edu.smu.smusql.pair1;

import java.util.ArrayList;
import java.util.List;

public class Node<K> {
    private K key;
    private List<Integer> values;
    private Node<K> left = null;
    private Node<K> right = null;
    private Node<K> parent = null;
    private int height;

    public Node(K key, Integer value) {
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

    public Node<K> getLeft() {
        return left;
    }

    public void setLeft(Node<K> left) {
        this.left = left;
    }

    public Node<K> getRight() {
        return right;
    }

    public void setRight(Node<K> right) {
        this.right = right;
    }

    public Node<K> getParent() {
        return parent;
    }

    public void setParent(Node<K> p) {
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
