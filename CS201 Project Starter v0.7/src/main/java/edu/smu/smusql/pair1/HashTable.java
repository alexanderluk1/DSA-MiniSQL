package edu.smu.smusql.pair1;

public class HashTable {
    private BucketNode[] buckets;
    private int size;
    private int numElements;
    private final double MAXLOAD;

    public HashTable(int capacity) {
        this.size = capacity;
        this.buckets = new BucketNode[capacity];
        this.MAXLOAD = 0.75;
    }

    public int getSize() {
        return size;
    }

    public BucketNode getStartOfBucket(int i) {
        return buckets[i];
    }

    // index in bukets where record with key is stored
    private int hash(int id) {
        return id % size; // Basic hash function
    }

    private double loadFactor() { // number of elements vs size of table
        return (double) numElements / size;
    }

    private void addToBucket(int index, Record record) {
        BucketNode newNode = new BucketNode(record); // Create Node
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

    public Record get(int id) {
        int index = hash(id);
        BucketNode current = buckets[index];
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
        BucketNode current = buckets[index];
        BucketNode prev = null;

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
        BucketNode[] newBuckets = new BucketNode[newCapacity];
        for (BucketNode bucket : buckets) {
            BucketNode current = bucket;
            while (current != null) {
                int newIndex = current.getRecord().getId() % newCapacity;
                BucketNode next = current.getNext();
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
            BucketNode current = buckets[i];
            System.out.print("Bucket " + i + ": ");
            while (current != null) {
                System.out.print(current.getRecord() + " -> ");
                current = current.getNext();
            }
            System.out.println("null");
        }
    }
}
