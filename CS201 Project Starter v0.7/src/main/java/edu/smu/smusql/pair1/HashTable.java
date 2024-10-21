package edu.smu.smusql.pair1;

public class HashTable {
    private ListNode[] buckets;
    private int size;
    private int numElements;

    public HashTable(int capacity) {
        this.size = capacity;
        this.buckets = new ListNode[capacity];
    }

    private int hashFunction(int key) {
        return key % size;
    }

    private double loadFactor() { // number of elements vs size of table
        return (double) numElements / size;
    }

    // Key is record.id
    public void insert(int key, Record record) {

    }

    public Record search(int key) {
        return null;
    }

    public void delete(int key) {
        int index = hashFunction(key);
        ListNode current = buckets[index];
        ListNode prev = null;

        while (current != null) {
            if (current.getKey() == key) { // finding record in bucket
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
                int newIndex = current.getKey() % newCapacity;
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
        ht.insert(1, null);
        ht.insert(2, new Record());
        ht.print();
    }
}
