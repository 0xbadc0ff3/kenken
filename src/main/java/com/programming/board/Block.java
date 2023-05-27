package com.programming.board;

public interface Block extends Iterable<Cell> {
    public void add(Cell cell);
    public boolean remove(Cell cell);
    public void setVincolo(int vincolo);
    public void setOperation(Operation operation);
    public int getVincolo();
    public Operation getOperation();
    public boolean contains(Cell cell);
    public int getCurrentSize();
    default int getMaxBlockSize() { return Utility.MAX_BLOCK_SIZE; }//JAVA 8 o superiore.
}
