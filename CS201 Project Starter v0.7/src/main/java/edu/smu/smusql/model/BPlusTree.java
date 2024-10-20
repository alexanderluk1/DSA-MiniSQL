package edu.smu.smusql.model;
import java.util.*;


// Node class to represent a node in the B+ Tree
class BPlusNode<K extends Comparable<K>, V> {
    protected boolean isLeaf;
    protected List<K> keys;
    protected List<BPlusNode<K, V>> children;
    protected BPlusNode<K, V> next; // For leaf node linking
    protected List<V> values; // Only used in leaf nodes

    // Constructor for internal node
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

// BPlusTree class that implements the B+ Tree structure
public class BPlusTree<K extends Comparable<K>, V> {
    private int order; // The maximum number of keys a node can hold
    private BPlusNode<K, V> root; // Root node of the tree

    // Constructor
    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusNode<>(true); // Initialize root as a leaf node
    }

    // Search for a key in the B+ Tree and return the corresponding value
    public V search(K key) {
        BPlusNode<K, V> currentNode = root;

        // Traverse internal nodes until we reach a leaf node
        while (!currentNode.isLeaf) {
            int idx = Collections.binarySearch(currentNode.keys, key);
            int childIndex = idx >= 0 ? idx + 1 : -(idx + 1);
            currentNode = currentNode.children.get(childIndex);
        }

        // Perform a binary search on the leaf node's keys
        int idx = Collections.binarySearch(currentNode.keys, key);
        if (idx >= 0) {
            return currentNode.values.get(idx); // Key found
        } else {
            return null; // Key not found
        }
    }

    public void insert(K key, V value) {
        BPlusNode<K, V> currentNode = root;

        // If the root is full, split it
        if (currentNode.keys.size() == order - 1) {
            BPlusNode<K, V> newRoot = new BPlusNode<>(false);
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }

        // Insert the key-value pair into the non-full node
        insertNonFull(currentNode, key, value);
    }

    public V update(K key, V Value){
        BPlusNode<K, V> currentNode = root;
        // Traverse internal nodes until we reach a leaf node
        while (!currentNode.isLeaf) {
            int idx = Collections.binarySearch(currentNode.keys, key);
            int childIndex = idx >= 0 ? idx + 1 : -(idx + 1);
            currentNode = currentNode.children.get(childIndex);
        }

        // Perform a binary search on the leaf node's keys
        int idx = Collections.binarySearch(currentNode.keys, key);
        if (idx >= 0) {

            currentNode.values.set(idx, Value); // Key found
            return currentNode.values.get(idx);
        } else {
            return null; // Key not found
        }
    }

    // Helper function to insert into a non-full node
    private void insertNonFull(BPlusNode<K, V> node, K key, V value) {
        if (node.isLeaf) {
            // Insert the key-value pair into the leaf node
            int idx = Collections.binarySearch(node.keys, key);
            if (idx >= 0) {
                // Key already exists, update the value
                node.values.set(idx, value);
            } else {
                // Key doesn't exist, insert new key-value pair
                int insertPosition = -(idx + 1);
                node.keys.add(insertPosition, key);
                node.values.add(insertPosition, value);
            }
        } else {
            // Traverse down to the correct child
            int idx = Collections.binarySearch(node.keys, key);
            int childIndex = idx >= 0 ? idx + 1 : -(idx + 1);
            BPlusNode<K, V> childNode = node.children.get(childIndex);

            // Split the child if it's full
            if (childNode.keys.size() == order - 1) {
                splitChild(node, childIndex);
                if (key.compareTo(node.keys.get(childIndex)) > 0) {
                    childNode = node.children.get(childIndex + 1);
                }
            }

            insertNonFull(childNode, key, value);
        }
    }

    // Split a full child node
    private void splitChild(BPlusNode<K, V> parentNode, int childIndex) {
        BPlusNode<K, V> fullChild = parentNode.children.get(childIndex);
        BPlusNode<K, V> newChild = new BPlusNode<>(fullChild.isLeaf);

        // Split the keys and children/values of the full node
        int medianIndex = (order - 1) / 2;
        K medianKey = fullChild.keys.get(medianIndex);

        // Transfer the second half of keys/values to the new child
        newChild.keys.addAll(fullChild.keys.subList(medianIndex + 1, fullChild.keys.size()));
        fullChild.keys.subList(medianIndex, fullChild.keys.size()).clear();

        if (fullChild.isLeaf) {
            // Transfer values for leaf node
            newChild.values.addAll(fullChild.values.subList(medianIndex + 1, fullChild.values.size()));
            fullChild.values.subList(medianIndex, fullChild.values.size()).clear();
            newChild.next = fullChild.next;
            fullChild.next = newChild;
        } else {
            // Transfer children for internal node
            newChild.children.addAll(fullChild.children.subList(medianIndex + 1, fullChild.children.size()));
            fullChild.children.subList(medianIndex + 1, fullChild.children.size()).clear();
        }

        // Insert the median key into the parent node
        parentNode.keys.add(childIndex, medianKey);
        parentNode.children.add(childIndex + 1, newChild);
    }

    // Delete a key from the B+ Tree
    public void delete(K key) {
        delete(root, key);
        if (!root.isLeaf && root.keys.isEmpty()) {
            root = root.children.get(0); // Root has become empty, adjust the tree
        }
    }

    private void delete(BPlusNode<K, V> node, K key) {
        // If node is a leaf, remove the key
        if (node.isLeaf) {
            int idx = Collections.binarySearch(node.keys, key);
            if (idx >= 0) {
                node.keys.remove(idx);
                node.values.remove(idx);
            }
        } else {
            // Internal node: find the child containing the key
            int idx = Collections.binarySearch(node.keys, key);
            int childIndex = idx >= 0 ? idx + 1 : -(idx + 1);
            BPlusNode<K, V> childNode = node.children.get(childIndex);

            delete(childNode, key);

            // Handle underflow (child has fewer than minimum keys)
            if (childNode.keys.size() < (order - 1) / 2) {
                handleUnderflow(node, childIndex);
            }
        }
    }

    // Handle node underflow by borrowing or merging
    private void handleUnderflow(BPlusNode<K, V> parentNode, int childIndex) {
        BPlusNode<K, V> underflowNode = parentNode.children.get(childIndex);

        // Try borrowing from the left sibling
        if (childIndex > 0) {
            BPlusNode<K, V> leftSibling = parentNode.children.get(childIndex - 1);
            if (leftSibling.keys.size() > (order - 1) / 2) {
                underflowNode.keys.add(0, parentNode.keys.get(childIndex - 1));
                parentNode.keys.set(childIndex - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));

                if (underflowNode.isLeaf) {
                    underflowNode.values.add(0, leftSibling.values.remove(leftSibling.values.size() - 1));
                } else {
                    underflowNode.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
                }
                return;
            }
        }

        // Try borrowing from the right sibling
        if (childIndex < parentNode.children.size() - 1) {
            BPlusNode<K, V> rightSibling = parentNode.children.get(childIndex + 1);
            if (rightSibling.keys.size() > (order - 1) / 2) {
                underflowNode.keys.add(parentNode.keys.get(childIndex));
                parentNode.keys.set(childIndex, rightSibling.keys.remove(0));

                if (underflowNode.isLeaf) {
                    underflowNode.values.add(rightSibling.values.remove(0));
                } else {
                    underflowNode.children.add(rightSibling.children.remove(0));
                }
                return;
            }
        }

        // If borrowing is not possible, merge with a sibling
        if (childIndex > 0) {
            // Merge with left sibling
            BPlusNode<K, V> leftSibling = parentNode.children.get(childIndex - 1);
            leftSibling.keys.add(parentNode.keys.remove(childIndex - 1));
            leftSibling.keys.addAll(underflowNode.keys);

            if (underflowNode.isLeaf) {
                leftSibling.values.addAll(underflowNode.values);
                leftSibling.next = underflowNode.next;
            } else {
                leftSibling.children.addAll(underflowNode.children);
            }

            parentNode.children.remove(childIndex);
        } else {
            // Merge with right sibling
            BPlusNode<K, V> rightSibling = parentNode.children.get(childIndex + 1);
            underflowNode.keys.add(parentNode.keys.remove(childIndex));
            underflowNode.keys.addAll(rightSibling.keys);

            if (underflowNode.isLeaf) {
                underflowNode.values.addAll(rightSibling.values);
                underflowNode.next = rightSibling.next;
            } else {
                underflowNode.children.addAll(rightSibling.children);
            }

            parentNode.children.remove(childIndex + 1);
        }
    }

    // Optional: In-order traversal of B+ Tree (for debugging purposes)
    public void traverse() {
        BPlusNode<K, V> currentNode = root;
        while (!currentNode.isLeaf) {
            currentNode = currentNode.children.get(0); // Traverse to the first leaf node
        }
        while (currentNode != null) {
            for (int i = 0; i < currentNode.keys.size(); i++) {
                Map<String, Object> record = (Map<String, Object>) currentNode.values.get(i);

                System.out.println("Index: " + currentNode.keys.get(i) +
                        ", Name: " + record.get("name") +
                        ", Age: " + record.get("age"));
            }
            currentNode = currentNode.next; // Move to the next leaf node
        }
    }
}
