package edu.smu.smusql.pair1;

public class AVLTree<K extends Comparable<K>> {

    private AVLNode<K> root;
    private int size;

    private int height(AVLNode<K> node) {
        return node == null ? 0 : node.getHeight();
    }

    private int getBalance(AVLNode<K> node) { // > 1: left-heavy, < -1: right-heavy
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }

    private AVLNode<K> largestOnLeft(AVLNode<K> node) {
        AVLNode<K> current = node;
        while (current.getRight() != null) {
            current = current.getRight();
        }
        return current;
    }

    // Set node's parent to replace node - assume children intact
    private void updateParentChildLink(AVLNode<K> node, AVLNode<K> replacement) {
        AVLNode<K> parent = node.getParent();
        if (parent != null) {
            if (parent.getLeft() == node) {
                parent.setLeft(replacement);
            } else {
                parent.setRight(replacement);
            }
            if (replacement != null) {
                replacement.setParent(parent);
            }
        }
    }

    private AVLNode<K> deleteLeafOrSingleChildNode(AVLNode<K> node) {
        // Replacement node is child of deleted node
        AVLNode<K> replacement = (node.getLeft() == null) ? node.getRight() : node.getLeft();

        if (node == root) {
            root = replacement;
            if (replacement != null) {
                replacement.setParent(null);
            }
        } else {
            updateParentChildLink(node, replacement);
        }

        return node.getParent();
    }

    private AVLNode<K> BSTDeletion(AVLNode<K> node) {
        if (node.numChildren() <= 1) {
            return deleteLeafOrSingleChildNode(node);
        } else {
            return deleteTwoChildrenNode(node);
        }
    }

    private AVLNode<K> deleteTwoChildrenNode(AVLNode<K> node) {
        // Find replacement node
        AVLNode<K> replacement = largestOnLeft(node.getLeft());

        // Delete the replacement node
        AVLNode<K> parentOfReplacement = BSTDeletion(replacement); // replacement is not root for now

        // Reattach the replacement node
        if (node == root) {
            root = replacement;
        }

        // Move children
        replaceNode(replacement, node);

        return parentOfReplacement;
    }

    // Move children
    private void replaceNode(AVLNode<K> replacement, AVLNode<K> node) {
        replacement.setLeft(node.getLeft());
        if (node.getLeft() != null) {
            node.getLeft().setParent(replacement);
        }
        replacement.setRight(node.getRight());
        if (node.getRight() != null) {
            node.getRight().setParent(replacement);
        }
        replacement.setParent(node.getParent());
    }

    private AVLNode<K> rightRotate(AVLNode<K> z) {
        AVLNode<K> y = z.getLeft();
        AVLNode<K> reattach = y.getRight();
        System.out.println("Right: z " + z + ", y " + y + ", loose " + reattach);

        // Rotate
        updateParentChildLink(z, y);
        z.setParent(y);
        y.setRight(z);
        z.setLeft(reattach);

        // Update height
        z.updateHeight();
        y.updateHeight();

        return y; // new parent
    }

    private AVLNode<K> leftRotate(AVLNode<K> z) {
        AVLNode<K> y = z.getRight();
        AVLNode<K> reattach = y.getLeft();
        System.out.println("Left: z " + z + ", y " + y + ", loose " + reattach);

        // Rotate
        updateParentChildLink(z, y);
        z.setParent(y);
        y.setLeft(z);
        z.setRight(reattach);

        // Update Height
        z.updateHeight();
        y.updateHeight();

        return y;
    }

    // bal, > 0: go left, < 0: go right
    private AVLNode<K> rebalance(int bal, AVLNode<K> node) {
        // left left - single rotation - same height
        if (bal > 1 && getBalance(node.getLeft()) >= 0) {
            return rightRotate(node);
        }

        // right right - single rotation - same height
        if (bal < -1 && getBalance(node.getRight()) <= 0) {
            return leftRotate(node);
        }

        // left right - double rotation
        if (bal > 1 && getBalance(node.getLeft()) < 0) {
            leftRotate(node.getLeft());
            return rightRotate(node);
        }

        // right left - double rotation
        if (bal < -1 && getBalance(node.getRight()) > 0) {
            rightRotate(node.getRight());
            return leftRotate(node);
        }

        return null; // should never reach here
    }

    public void remove(K key, Integer id) {
        AVLNode<K> toDelete = get(key); // get entry where key = key

        toDelete.getValues().remove(id); // remove id

        if (toDelete.getValues().size() == 0) { // Node should no longer exist
            size--;
            AVLNode<K> parent = BSTDeletion(toDelete); // Return parent of Node deleted

            // Check balance of all ancestors
            boolean done = false;
            while (parent != null && !done) { // parent is above root
                int prevHeight = parent.getHeight();
                int bal = getBalance(parent);

                if (bal >= -1 && bal <= 1) { // it is balanced
                    parent.updateHeight();
                    done = parent.getHeight() == prevHeight; // Height of remaining ancestors do not change
                    parent = parent.getParent(); // go up 1 level

                } else {
                    if (parent == root) { // rebalancing root node
                        root = rebalance(bal, parent); // Now balanced
                        done = true;
                    } else {
                        parent = rebalance(bal, parent); // Now balanced
                        parent = parent.getParent(); // Continue checking ancestors
                    }
                }
            }
        }
    }

    public AVLNode<K> get(K key) {
        if (root == null) {
            return root; // Tree is empty
        }

        AVLNode<K> walk = root;

        while (walk != null) {
            int comp = key.compareTo(walk.getKey());
            if (comp == 0) {
                return walk;
            } else if (comp > 0) {
                walk = root.getRight();
            } else {
                walk = root.getLeft();
            }
        }

        return null; // KeyNotFound
    }

    // debugging purposes

    public void printTree(AVLNode<K> node, String prefix, boolean isLeft, AVLNode<K> parent) {
        if (node != null) {
            // Print current node with its parent
            System.out.print(prefix + (isLeft ? "├ L: " : "└ R: ") + node + " (Parent: " + (parent != null ? parent : "null") + ")\n");
            
            // Prepare the prefix for the next level
            String newPrefix = prefix + (isLeft ? "│   " : "    ");
            
            // Recursively print the left and right children
            printTree(node.getLeft(), newPrefix, true, node);
            printTree(node.getRight(), newPrefix, false, node);
        }
    }
    
    public void print() {
        if (root != null) {
            System.out.println("Root: " + root);
            printTree(root.getLeft(), "",true, root);
            printTree(root.getRight(), "", false, root);
        } else {
            System.out.println("Tree is empty.");
        }
    }
    

    public static void main(String[] args) {
        // test();
        AVLTree<Integer> ageTree = new AVLTree<>();
        AVLNode<Integer> rootNode = new AVLNode<Integer>(18, 1);
        rootNode.getValues().add(6);
        AVLNode<Integer> leftNode = new AVLNode<Integer>(14, 2);
        AVLNode<Integer> rightNode = new AVLNode<Integer>(22, 3);
        AVLNode<Integer> rightRightChild = new AVLNode<Integer>(26, 4);
        AVLNode<Integer> rightLeftChild = new AVLNode<Integer>(20, 5);
        ageTree.root = rootNode;
        leftNode.setParent(rootNode);
        rootNode.setLeft(leftNode);
        rootNode.setRight(rightNode);
        rightNode.setParent(rootNode);

        rightNode.setRight(rightRightChild);
        rightRightChild.setParent(rightNode);

        rightNode.setLeft(rightLeftChild);
        rightLeftChild.setParent(rightNode);
        leftNode.updateHeight();
        rightNode.updateHeight();
        rootNode.updateHeight();
        System.out.println("Before:");
        ageTree.print();

        // Test BSTDeleteion
        // testBSTDeletion(ageTree, rootNode, rightLeftChild, rightNode);

        ageTree.remove(18, 1);
        // System.out.println();
        // System.out.println("Remove: Key 18, Id 1");
        // ageTree.print();
        ageTree.remove(14, 2);
        System.out.println();
        System.out.println("Remove: Key 14, Id 2");
        ageTree.print();
    }

    public static void testBSTDeletion(AVLTree<Integer> tree, AVLNode<Integer> root, AVLNode<Integer> noChild,
            AVLNode<Integer> oneChild) {
        tree.BSTDeletion(root);
        System.out.println("Delete Root");
        tree.print();
        tree.BSTDeletion(noChild);
        System.out.println("Delete no child");
        tree.print();
        System.out.println("Delete 1 child");
        tree.BSTDeletion(oneChild);
        tree.print();
    }

    public static void test() {
        AVLTree<Integer> ageTree = new AVLTree<>();
        Integer[] arr = { 41, 99, 67, 90, 81, 23, 80, 84, 9, 60, 94, 78 };

        for (int i = 0; i < arr.length; i++) {
            // ageTree.insert(arr[i], i);
        }
        // ageTree.insert(99, 12);

        System.out.println("Before: ");
        ageTree.print();
        System.out.println();

        System.out.println("Delete: 67 2"); // root is 81, left 60, right 90
        ageTree.print();
        System.out.println();

        System.out.println("Delete: 9 8"); // 60 left is 23, 23 right is 41
        ageTree.print();
        System.out.println();

        System.out.println("Delete: 23 5"); // 60 left is 41
        ageTree.print();
        System.out.println();

        // ageTree.insert(30, 13)
        System.out.println("Delete: 80 6");
        ageTree.print();
        System.out.println();

        // ageTree.insert(42, 14)
        System.out.println("Delete: 78 11"); // 41 left is 30, right is 60
        ageTree.print();
        System.out.println();

        // System.out.println("Delete: 99 12"); // list left 1
        ageTree.print();
        System.out.println();
    }
}
