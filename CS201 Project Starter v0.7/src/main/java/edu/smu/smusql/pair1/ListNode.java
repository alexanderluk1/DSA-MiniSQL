package edu.smu.smusql.pair1;

public class ListNode {
    private Record record;
    private ListNode next;

    public ListNode(Record record, ListNode next) {
        this.record = record;
        this.next = next;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public ListNode getNext() {
        return next;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }    

    public Integer getKey() {
        return this.record.getId();
    }
}
