package edu.smu.smusql.pair1;

public class HashTable {
    private ListNode[] buckets;
    private int size;
    private int numElements;
    private final double MAXLOAD;

    public HashTable(int capacity) {
        this.size = capacity;
        this.buckets = new ListNode[capacity];
        this.MAXLOAD = 0.75;
    }

    // index in bukets where record with key is stored
    private int hash(int id) {
        return id % size; // Basic hash function
    }

    private double loadFactor() { // number of elements vs size of table
        return (double) numElements / size;
    }

    private void addToBucket(int index, Record record) {
        ListNode newNode = new ListNode(record); // Create Node
        newNode.setNext(buckets[index]); // Node points to top of bucket
        buckets[index] = newNode; // newNode becomes top of bucket
    }

    public void put(Record record) {
        if (loadFactor() > MAXLOAD) {
            resize(size * 2); // Increase size if load factor exceeds
        }

        int index = hash(record.getId());
        addToBucket(index, record);
        numElements++;
    }

    public Record search(int id) {
        int index = hash(id);
        ListNode current = buckets[index];
        while (current != null) {
            if (current.getRecord().getId() == id) {
                return current.getRecord(); // Found the record
            }
            current = current.getNext();
        }
        return null; // Record not found
    }

    public void remove(int id) {
        int index = hash(id);
        ListNode current = buckets[index];
        ListNode prev = null;

        while (current != null) {
            if (current.getRecord().getId() == id) { // finding record in bucket
                if (prev == null) { // first node of bucket
                    buckets[index] = current.getNext(); 
                } else { // remove node from bucket
                    prev.setNext(current.getNext()); 
                }

                numElements--;

                if (loadFactor() < 0.25 && size > 1) { // less than 1/4 elements
                    resize(size / 2); // shrink table
                }

                return;
            }
            prev = current;
            current = current.getNext();
        }

    }

    public void resize(int newCapacity) {
        ListNode[] newBuckets = new ListNode[newCapacity];
        for (ListNode bucket : buckets) {
            ListNode current = bucket;
            while (current != null) {
                int newIndex = current.getRecord().getId() % newCapacity;
                ListNode next = current.getNext();
                current.setNext(newBuckets[newIndex]);
                newBuckets[newIndex] = current;
                current = next; 
            }
        }
        this.buckets = newBuckets;
        this.size = newCapacity;
    }

    // debugging purposes
    public void print() {
        for (int i = 0; i < size; i++) {
            ListNode current = buckets[i];
            System.out.print("Bucket " + i + ": ");
            while (current != null) {
                System.out.print(current.getRecord() + " -> ");
                current = current.getNext();
            }
            System.out.println("null");
        }
    }

    public static void main(String[] args) {
        HashTable ht = new HashTable(1);
        // ht.insert(new Record(1, "AB", 18, 3.2, true));
        ht.put(new Record(4, "B", 19, 3.4, false));
        // ht.insert(new Record(3, "B", 19, 3.4, false));
        ht.put(new Record(8, "B", 19, 3.4, false));
        // ht.insert(new Record(5, "B", 19, 3.4, false));
        ht.put(new Record(12, "B", 19, 3.4, false));
        ht.put(new Record(20, "B", 19, 3.4, false));
        ht.put(new Record(16, "B", 19, 3.4, false));
        // ht.insert(new Record(9, "B", 19, 3.4, false));
        // ht.insert(new Record(10, "B", 19, 3.4, false));
        // ht.insert(new Record(12, "B", 19, 3.4, false));
        // ht.insert(new Record(16, "B", 19, 3.4, false));

        // ht.remove(1);
        // ht.remove(3);
        // ht.remove(4);
        // ht.remove(5);
        // ht.remove(9);
        ht.print();
    }
}
