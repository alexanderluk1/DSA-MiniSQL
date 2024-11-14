package edu.smu.smusql.model;

import java.util.*;
// Node class to represent a node in the B+ Tree

import java.util.*;

// Node class to represent a node in the B+ Tree
class BPlusTreeNode {
    public TreeMap<Integer, Object[]> values;
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

            // Insert a key and array of values (record) into the B+ Tree
            public void insert(int key, Object[] record) {
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

            // Search for a key in the B+ Tree and return the record as an array
            public Object[] search(int key) {
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
            public boolean update(int key, Object[] record) {
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
                    currentNode.values.put(key, record); // Update the record
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
                        // Merge with left sibling
                        mergeNodes(leftSibling, node, parent, nodeIndex - 1);
                    } else if (rightSibling != null) {
                        // Merge with right sibling
                        mergeNodes(node, rightSibling, parent, nodeIndex);
                    }
                }

                // If the parent node is now underflowing, propagate upwards
                if (parent.keys.size() < minKeys && parent != root) {
                    BPlusTreeNode grandparent = findParent(root, parent);
                    handleUnderflow(parent, grandparent);
                }
            }

            private void mergeNodes(BPlusTreeNode left, BPlusTreeNode right, BPlusTreeNode parent, int parentIndex) {
                left.keys.add(parent.keys.remove(parentIndex)); // Move the separating key to the left sibling
                left.keys.addAll(right.keys);
                if (!left.isLeaf) {
                    left.children.addAll(right.children); // Transfer child pointers
                }

                parent.children.remove(right);

                if (parent == root && parent.keys.isEmpty()) {
                    root = left; // If the parent was the root and became empty, the left child becomes the new root
                }
            }



    // Print the structure of the B+ Tree
    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(BPlusTreeNode node, int level) {

        if (node != null) {
            System.out.println("Level " + level + " " + node.keys);
            for(int i=0;i<node.keys.size();i++){
                String result = Arrays.toString(node.values.get(node.keys.get(i)));
                if(node.values.get(node.keys.get(i))!=null) {
                    System.out.println(result);
                }

            }

            if (!node.isLeaf) {
                for (BPlusTreeNode child : node.children) {
                    printTree(child, level + 1);

                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Table Name: Student");
        BPlusTree tree = new BPlusTree(4); // Assuming order of 3 for the B+ Tree
        String[] cols = {"id", "name", "age", "gpa"};

// Record data as arrays (instead of HashMap)
        Object[] record1 = {1, "Bob", 14, 1.4};
        Object[] record2 = {2, "Sam", 15, 2.4};
        Object[] record3 = {3, "Tim", 16, 3.4};
        Object[] record4 = {4, "Ham", 17, 4.0};

// Insert records into the tree
        tree.insert(1, record1);
        tree.insert(2, record2);
        tree.insert(3, record3);
        tree.insert(4, record4); // Note: This is the same as record1
//        tree.insert(6, record1); // Also same as record1

//// Update record with id 6
//        tree.update(6, record3); // Update record with id 6 to record3
//
//// Print the result of the update and search for id 6
//        System.out.println(tree.update(6, record1)); // Attempt to update id 6 with record2
//        System.out.println(Arrays.toString(tree.search(6))); // Search for record with id 6

// Print the tree structure
        tree.printTree();


        // tree.printTree();
    }

}











