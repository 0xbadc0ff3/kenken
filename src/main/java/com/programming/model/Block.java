package com.programming.model;

import com.programming.Utility;

import java.util.Collection;

public interface Block extends Iterable<Cell> {
    void add(Cell cell);
    boolean remove(Cell cell);
    void setVincolo(int vincolo);
    void setOperation(Operation operation);
    int getVincolo();
    Operation getOperation();
    boolean contains(Cell cell);
    int getCurrentSize();
    boolean isValid();
    boolean hasConstraints();
    boolean isFull();
    Collection<Cell> getCells();
    boolean isAttached();
    default int getMaxBlockSize() { return Utility.MAX_BLOCK_SIZE; }//JAVA 8 o superiore.
}
