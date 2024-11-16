package edu.smu.smusql.model;

import java.util.*;

class BPlusNode<K extends Comparable<K>, V> {
    boolean isLeaf;
    List<K> keys;
    List<V> values;
    List<BPlusNode<K, V>> children;
    BPlusNode<K, V> next;

    public BPlusNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        if (isLeaf) {
            this.values = new ArrayList<>();
            this.next = null;
        } else {
            this.children = new ArrayList<>();
        }
    }
}

public class BPlusTree<K extends Comparable<K>, V> {
    private BPlusNode<K, V> root;
    private final int order;
    private int size;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusNode<>(true);
        this.size = 0;
    }

    // Modified search function to return a list of values for a key (handles
    // duplicates)
    public List<V> search(K key) {
        BPlusNode<K, V> leaf = findLeaf(key);
        List<V> results = new ArrayList<>();

        // Iterate over the keys in the leaf node from the beginning
        for (int idx = 0; idx < leaf.keys.size(); idx++) {
            if (leaf.keys.get(idx).compareTo(key) == 0) {
                results.add(leaf.values.get(idx));
            } else if (leaf.keys.get(idx).compareTo(key) > 0) {
                // Since the keys are sorted, we can stop once we've gone past the target key
                break;
            }
        }

        return results; // Return empty list if the key is not found
    }

    public List<V> searchRangeInclusive(K start, K end) {
        List<V> result = new ArrayList<>();
        BPlusNode<K, V> leaf = findLeaf(start);

        while (leaf != null) {
            for (int i = 0; i < leaf.keys.size(); i++) {
                K key = leaf.keys.get(i);
                if (key.compareTo(start) >= 0 && key.compareTo(end) <= 0) {
                    result.add(leaf.values.get(i));
                }
                if (key.compareTo(end) > 0) {
                    return result;
                }
            }
            leaf = leaf.next;
        }

        return result;
    }

    public List<V> searchRangeExclusive(K start, K end) {
        List<V> result = new ArrayList<>();
        BPlusNode<K, V> leaf = findLeaf(start);

        while (leaf != null) {
            for (int i = 0; i < leaf.keys.size(); i++) {
                K key = leaf.keys.get(i);
                if (key.compareTo(start) > 0 && key.compareTo(end) < 0) {
                    result.add(leaf.values.get(i));
                }
                if (key.compareTo(end) > 0) {
                    return result;
                }
            }
            leaf = leaf.next;
        }

        return result;
    }

    public void insert(K key, V value) {
        if (root == null) {
            root = new BPlusNode<>(true);
        }

        BPlusNode<K, V> leaf = findLeaf(key);
        // Determine the correct index to insert the key using a linear search
        int idx = 0;
        while (idx < leaf.keys.size() && leaf.keys.get(idx).compareTo(key) <= 0) {
            idx++;
        }

        // Insert the key and value at the found index
        leaf.keys.add(idx, key);
        leaf.values.add(idx, value);
        size++;

        if (leaf.keys.size() >= order) {
            splitLeaf(leaf);
        }
    }

    public boolean update(K key, V newValue) {
        if (root == null)
            return false;

        BPlusNode<K, V> leaf = findLeaf(key);
        int idx = Collections.binarySearch(leaf.keys, key);

        // If the key doesn't exist, return false
        if (idx < 0) {
            return false;
        }

        // Update the value
        leaf.values.set(idx, newValue);

        // If the leaf node is not the root and the size is smaller than the min
        // threshold, we rebalance
        if (leaf != root && leaf.keys.size() < (order / 2)) {
            rebalanceLeaf(leaf);
        }

        return true;
    }

    public boolean updateDuplicate(K oldKey, K newKey, V value) {
        if (root == null)
            return false;

        BPlusNode<K, V> leaf = findLeaf(oldKey);

        // We need to search linearly for the correct instance of oldKey with the
        // matching value
        int idx = -1;
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i).compareTo(oldKey) == 0 && leaf.values.get(i).equals(value)) {
                idx = i;
                break;
            }
        }

        // If the key-value pair doesn't exist, return false
        if (idx == -1) {
            return false;
        }

        // Delete the current column and index
        leaf.keys.remove(idx);
        leaf.values.remove(idx);
        size--;

        if (leaf != root && leaf.keys.size() < (order / 2)) {
            rebalanceLeaf(leaf);
        }

        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
        }

        // Insert the new column and index
        insert(newKey, value);

        return true;
    }

    public boolean delete(K key) {
        if (root == null)
            return false;

        BPlusNode<K, V> leaf = findLeaf(key);
        int idx = Collections.binarySearch(leaf.keys, key);

        if (idx < 0)
            return false;

        leaf.keys.remove(idx);
        leaf.values.remove(idx);
        size--;

        if (leaf != root && leaf.keys.size() < (order / 2)) {
            rebalanceLeaf(leaf);
        }

        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
        }

        return true;
    }

    public boolean deleteDuplicate(K key, V value) {
        if (root == null)
            return false;

        BPlusNode<K, V> leaf = findLeaf(key);

        // Linear search to find the correct key-value pair
        int idx = -1;
        for (int i = 0; i < leaf.keys.size(); i++) {
            if (leaf.keys.get(i).compareTo(key) == 0 && leaf.values.get(i).equals(value)) {
                idx = i;
                break;
            }
        }

        // If the key-value pair doesn't exist, return false
        if (idx == -1) {
            return false;
        }

        leaf.keys.remove(idx);
        leaf.values.remove(idx);
        size--;

        if (leaf != root && leaf.keys.size() < (order / 2)) {
            rebalanceLeaf(leaf);
        }

        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
        }

        return true;
    }

    private BPlusNode<K, V> findLeaf(K key) {
        BPlusNode<K, V> node = root;
        while (!node.isLeaf) {
            int idx = Collections.binarySearch(node.keys, key);
            if (idx < 0)
                idx = -(idx + 1);
            else
                idx++;
            node = node.children.get(idx);
        }
        return node;
    }

    private void splitLeaf(BPlusNode<K, V> leaf) {
        int mid = leaf.keys.size() / 2;
        BPlusNode<K, V> newLeaf = new BPlusNode<>(true);

        newLeaf.keys = new ArrayList<>(leaf.keys.subList(mid, leaf.keys.size()));
        newLeaf.values = new ArrayList<>(leaf.values.subList(mid, leaf.values.size()));

        leaf.keys.subList(mid, leaf.keys.size()).clear();
        leaf.values.subList(mid, leaf.values.size()).clear();

        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        insertInParent(leaf, newLeaf.keys.get(0), newLeaf);
    }

    private void insertInParent(BPlusNode<K, V> left, K key, BPlusNode<K, V> right) {
        if (left == root) {
            BPlusNode<K, V> newRoot = new BPlusNode<>(false);
            newRoot.keys.add(key);
            newRoot.children.add(left);
            newRoot.children.add(right);
            root = newRoot;
            return;
        }

        BPlusNode<K, V> parent = findParent(root, left);
        int idx = Collections.binarySearch(parent.keys, key);
        idx = -(idx + 1);

        parent.keys.add(idx, key);
        parent.children.add(idx + 1, right);

        if (parent.keys.size() >= order) {
            splitInternal(parent);
        }
    }

    private void splitInternal(BPlusNode<K, V> node) {
        int mid = node.keys.size() / 2;
        K promoteKey = node.keys.get(mid);

        BPlusNode<K, V> newNode = new BPlusNode<>(false);
        newNode.keys = new ArrayList<>(node.keys.subList(mid + 1, node.keys.size()));
        newNode.children = new ArrayList<>(node.children.subList(mid + 1, node.children.size()));

        node.keys.subList(mid, node.keys.size()).clear();
        node.children.subList(mid + 1, node.children.size()).clear();

        insertInParent(node, promoteKey, newNode);
    }

    private void rebalanceLeaf(BPlusNode<K, V> leaf) {
        BPlusNode<K, V> parent = findParent(root, leaf);
        int idx = findChildIndex(parent, leaf);

        // Try borrowing from left sibling
        if (idx > 0) {
            BPlusNode<K, V> leftSibling = parent.children.get(idx - 1);
            if (leftSibling.keys.size() > order / 2) {
                borrowFromLeft(leaf, leftSibling, parent, idx - 1);
                return;
            }
        }

        // Try borrowing from right sibling
        if (idx < parent.children.size() - 1) {
            BPlusNode<K, V> rightSibling = parent.children.get(idx + 1);
            if (rightSibling.keys.size() > order / 2) {
                borrowFromRight(leaf, rightSibling, parent, idx);
                return;
            }
        }

        // Merge with a sibling
        if (idx > 0) {
            mergeLeaves(parent.children.get(idx - 1), leaf, parent, idx - 1);
        } else {
            mergeLeaves(leaf, parent.children.get(idx + 1), parent, idx);
        }
    }

    private BPlusNode<K, V> findParent(BPlusNode<K, V> root, BPlusNode<K, V> node) {
        if (root == null || root.isLeaf)
            return null;

        for (int i = 0; i < root.children.size(); i++) {
            if (root.children.get(i) == node)
                return root;
            BPlusNode<K, V> parent = findParent(root.children.get(i), node);
            if (parent != null)
                return parent;
        }

        return null;
    }

    private int findChildIndex(BPlusNode<K, V> parent, BPlusNode<K, V> child) {
        for (int i = 0; i < parent.children.size(); i++) {
            if (parent.children.get(i) == child)
                return i;
        }
        return -1;
    }

    private void borrowFromLeft(BPlusNode<K, V> node, BPlusNode<K, V> leftSibling,
            BPlusNode<K, V> parent, int parentIndex) {
        node.keys.add(0, leftSibling.keys.remove(leftSibling.keys.size() - 1));
        node.values.add(0, leftSibling.values.remove(leftSibling.values.size() - 1));
        parent.keys.set(parentIndex, node.keys.get(0));
    }

    private void borrowFromRight(BPlusNode<K, V> node, BPlusNode<K, V> rightSibling,
            BPlusNode<K, V> parent, int parentIndex) {
        node.keys.add(rightSibling.keys.remove(0));
        node.values.add(rightSibling.values.remove(0));
        parent.keys.set(parentIndex, rightSibling.keys.get(0));
    }

    private void mergeLeaves(BPlusNode<K, V> left, BPlusNode<K, V> right,
            BPlusNode<K, V> parent, int parentIndex) {
        left.keys.addAll(right.keys);
        left.values.addAll(right.values);
        left.next = right.next;

        parent.keys.remove(parentIndex);
        parent.children.remove(parentIndex + 1);

        if (parent == root && parent.keys.isEmpty()) {
            root = left;
        }
    }

    public int size() {
        return size;
    }
}