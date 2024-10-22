package edu.smu.smusql.pair1;

public class BucketNode {
    private Record record;
    private BucketNode next;

    public BucketNode(Record record) {
        this.record = record;
        this.next = null;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public BucketNode getNext() {
        return next;
    }

    public void setNext(BucketNode next) {
        this.next = next;
    }    
}
