package edu.smu.smusql.model;

import java.util.*;
// Node class to represent a node in the B+ Tree
class BPlusTreeNode {
    public TreeMap<Integer, HashMap<String, Object>> values;
    protected boolean isLeaf;
    protected ArrayList<Integer> keys;
    protected ArrayList<BPlusTreeNode> children;
    protected BPlusTreeNode next; // Only for leaf nodes

    // Constructor
    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = isLeaf ? null : new ArrayList<>();
        this.next = null;
        this.values = new TreeMap<>();
    }
}

public class BPlusTree {
    private final int order; // Maximum number of keys a node can hold
    private BPlusTreeNode root;

    // Constructor
    public BPlusTree(int order) {
        if (order < 3) throw new IllegalArgumentException("Order must be at least 3.");
        this.order = order;
        this.root = new BPlusTreeNode(true); // Initialize root as a leaf node
    }

    // Insert a key and value into the B+ Tree
    public void insert(int key, HashMap<String, Object> record) {
        BPlusTreeNode currentNode = root;
        BPlusTreeNode parent = null;

        // Traverse down to the leaf node
        while (!currentNode.isLeaf) {
            parent = currentNode;
            int i;
            for (i = 0; i < currentNode.keys.size(); i++) {
                if (key < currentNode.keys.get(i)) {
                    break;
                }
            }
            currentNode = currentNode.children.get(i);
        }

        // Insert the key and record into the leaf node
        currentNode.keys.add(key);
        currentNode.values.put(key, record);
        Collections.sort(currentNode.keys);

        // Split the leaf node if necessary
        if (currentNode.keys.size() >= order) {
            splitLeafNode(currentNode, parent);
        }
    }

    // Search for a key in the B+ Tree
    public Map<String, Object> search(int key) {
        BPlusTreeNode currentNode = root;
        while (!currentNode.isLeaf) {
            int i;
            for (i = 0; i < currentNode.keys.size(); i++) {
                if (key < currentNode.keys.get(i)) {
                    break;
                }
            }
            currentNode = currentNode.children.get(i);
        }
        return currentNode.values.getOrDefault(key, null); // Return the value if found
    }

    // Update a key in the B+ Tree
    public boolean update(int key, Map<String, Object> record) {
        BPlusTreeNode currentNode = root;
        while (!currentNode.isLeaf) {
            int i;
            for (i = 0; i < currentNode.keys.size(); i++) {
                if (key < currentNode.keys.get(i)) {
                    break;
                }
            }
            currentNode = currentNode.children.get(i);
        }
        if (currentNode.values.containsKey(key)) {
            currentNode.values.put(key, (HashMap<String, Object>) record); // Update the record
            return true;
        }
        return false; // Key not found
    }

    // Delete a key from the B+ Tree
    public boolean delete(int key) {
        BPlusTreeNode currentNode = root;
        BPlusTreeNode parent = null;
        while (!currentNode.isLeaf) {
            parent = currentNode;
            int i;
            for (i = 0; i < currentNode.keys.size(); i++) {
                if (key < currentNode.keys.get(i)) {
                    break;
                }
            }
            currentNode = currentNode.children.get(i);
        }

        if (currentNode.values.containsKey(key)) {
            currentNode.keys.remove(Integer.valueOf(key));
            currentNode.values.remove(key);

            // Handle underflow if necessary
            if (currentNode.keys.size() < (order - 1) / 2 && parent != null) {
                handleUnderflow(currentNode, parent);
            }
            return true;
        }
        return false; // Key not found
    }

    // Handle the splitting of a leaf node
    private void splitLeafNode(BPlusTreeNode leaf, BPlusTreeNode parent) {
        int midIndex = leaf.keys.size() / 2;
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);
        newLeaf.keys.addAll(leaf.keys.subList(midIndex, leaf.keys.size()));
        for (int key : newLeaf.keys) {
            newLeaf.values.put(key, leaf.values.remove(key));
        }
        leaf.keys.subList(midIndex, leaf.keys.size()).clear();

        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        if (parent == null) {
            root = new BPlusTreeNode(false);
            root.keys.add(newLeaf.keys.get(0));
            root.children.add(leaf);
            root.children.add(newLeaf);
        } else {
            insertIntoParent(parent, newLeaf.keys.get(0), newLeaf);
        }
    }

    // Insert a new key into the parent node
    private void insertIntoParent(BPlusTreeNode parent, int key, BPlusTreeNode newChild) {
        int insertIndex = Collections.binarySearch(parent.keys, key);
        if (insertIndex < 0) insertIndex = -(insertIndex + 1);
        parent.keys.add(insertIndex, key);
        parent.children.add(insertIndex + 1, newChild);

        if (parent.keys.size() >= order) {
            splitInternalNode(parent);
        }
    }

    // Split an internal node
    private void splitInternalNode(BPlusTreeNode node) {
        int midIndex = node.keys.size() / 2;
        int midKey = node.keys.get(midIndex);

        BPlusTreeNode newInternal = new BPlusTreeNode(false);
        newInternal.keys.addAll(node.keys.subList(midIndex + 1, node.keys.size()));
        newInternal.children.addAll(node.children.subList(midIndex + 1, node.children.size()));

        node.keys.subList(midIndex, node.keys.size()).clear();
        node.children.subList(midIndex + 1, node.children.size()).clear();

        if (node == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(midKey);
            newRoot.children.add(node);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            BPlusTreeNode parent = findParent(root, node);
            insertIntoParent(parent, midKey, newInternal);
        }
    }

    // Find the parent of a node
    private BPlusTreeNode findParent(BPlusTreeNode currentNode, BPlusTreeNode child) {
        if (currentNode.isLeaf || currentNode.children == null) return null;

        for (BPlusTreeNode c : currentNode.children) {
            if (c == child) return currentNode;
            BPlusTreeNode parent = findParent(c, child);
            if (parent != null) return parent;
        }
        return null;
    }

    private void handleUnderflow(BPlusTreeNode node, BPlusTreeNode parent) {
        int nodeIndex = parent.children.indexOf(node);

        // Identify left and right siblings if available
        BPlusTreeNode leftSibling = (nodeIndex > 0) ? parent.children.get(nodeIndex - 1) : null;
        BPlusTreeNode rightSibling = (nodeIndex < parent.children.size() - 1) ? parent.children.get(nodeIndex + 1) : null;

        // Minimum number of keys a node can have
        int minKeys = (order - 1) / 2;

        // Case 1: Try to borrow from the left sibling
        if (leftSibling != null && leftSibling.keys.size() > minKeys) {
            // Borrow the rightmost key from the left sibling
            node.keys.add(0, parent.keys.get(nodeIndex - 1)); // Move the separating key to the underflowing node
            parent.keys.set(nodeIndex - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1)); // Update parent key

            if (!node.isLeaf) {
                // Transfer the child pointer
                node.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            }
        }
        // Case 2: Try to borrow from the right sibling
        else if (rightSibling != null && rightSibling.keys.size() > minKeys) {
            // Borrow the leftmost key from the right sibling
            node.keys.add(parent.keys.get(nodeIndex)); // Move the separating key to the underflowing node
            parent.keys.set(nodeIndex, rightSibling.keys.remove(0)); // Update parent key

            if (!node.isLeaf) {
                // Transfer the child pointer
                node.children.add(rightSibling.children.remove(0));
            }
        }
        // Case 3: Merge with left or right sibling
        else {
            if (leftSibling != null) {
                // Merge node with left sibling
                leftSibling.keys.add(parent.keys.remove(nodeIndex - 1)); // Move separating key from parent to left sibling
                leftSibling.keys.addAll(node.keys); // Merge keys
                if (!node.isLeaf) {
                    leftSibling.children.addAll(node.children); // Merge children
                }
                parent.children.remove(node); // Remove the underflowing node from the parent
            } else if (rightSibling != null) {
                // Merge node with right sibling
                node.keys.add(parent.keys.remove(nodeIndex)); // Move separating key from parent to node
                node.keys.addAll(rightSibling.keys); // Merge keys
                if (!node.isLeaf) {
                    node.children.addAll(rightSibling.children); // Merge children
                }
                parent.children.remove(rightSibling); // Remove the right sibling from the parent
            }

            // Check if parent needs underflow handling
            if (parent != root && parent.keys.size() < minKeys) {
                BPlusTreeNode grandParent = findParent(root, parent);
                assert grandParent != null;
                handleUnderflow(parent, grandParent);
            }
        }

        // Special case: If the root becomes empty, adjust the root
        if (parent == root && parent.keys.isEmpty()) {
            root = (!parent.children.isEmpty()) ? parent.children.get(0) : null;
        }
    }


    // Print the structure of the B+ Tree
    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(BPlusTreeNode node, int level) {
        if (node != null) {
            System.out.println("Level " + level + " " + node.keys + node.values);
            if (!node.isLeaf) {
                for (BPlusTreeNode child : node.children) {
                    printTree(child, level + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        BPlusTree tree = new BPlusTree(3);
        String[] cols = {"id","name","age","gpa"};
        HashMap<String, Object> record1 = new HashMap<>();
        HashMap<String, Object> record2 = new HashMap<>();
        HashMap<String, Object> record3 = new HashMap<>();
        HashMap<String, Object> record4 = new HashMap<>();

        List<Object> r1 = Arrays.asList(1,"Bob",34,1.4);
        List<Object> r2 = Arrays.asList(1,"Sam",34,1.4);
        List<Object> r3 = Arrays.asList(1,"Tim",34,1.4);
        List<Object> r4 = Arrays.asList(1,"Ham",34,1.4);

        for (int i = 0; i < cols.length; i++) {
            record1.put(cols[i], r1.get(i));
        }
        for (int i = 0; i < cols.length; i++) {
            record2.put(cols[i], r2.get(i));
        }
        for (int i = 0; i < cols.length; i++) {
            record3.put(cols[i], r3.get(i));
        }
        for (int i = 0; i < cols.length; i++) {
            record4.put(cols[i], r4.get(i));
        }
        tree.insert(1,record1);
        tree.insert(2,record2);
        tree.insert(4,record4);
        tree.insert(5, record1);
        tree.insert(6, record1);
        tree.update(6, record3);
        System.out.println(tree.update(6,record2));
        System.out.println(tree.search(6));
        tree.printTree();

        // tree.printTree();
    }

}











