package com.programming.board;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class ConcreteBlock implements Block {
    protected Collection<Cell> celle;
    protected int vincolo;
    protected Operation op;

    public ConcreteBlock(){
        celle = new LinkedList<Cell>();
    }
    public ConcreteBlock(Cell c){
        celle = new LinkedList<Cell>();
        celle.add(c);
    }
    @Override
    public void setVincolo(int v){
        if(v>0) this.vincolo=v;
        else throw new IllegalArgumentException("Sono ammessi esclusivamente vincoli strettamente maggiori di zero.");
    }
    @Override
    public void setOperation(Operation op){
        this.op=op;
    }

    @Override
    public int getVincolo() {
        return vincolo;
    }

    @Override
    public Operation getOperation() {
        return op;
    }
    @Override
    public void add(Cell c){
        if(celle.size()==Utility.MAX_BLOCK_SIZE) {
            throw new RuntimeException("Blocco pieno.");
        }
        celle.add(c);
        //Assunzione: Il client non può inviare una cella già inserita nel blocco. (Controllare ogni volta se è già presente potrebbe risultare oneroso);
        //Assunzione: Il client non può inviare una cella NON adiacente ad almeno una cella già presente nel blocco.
    }
    @Override
    public boolean remove(Cell c){
        return celle.remove(c);
    }
    @Override
    public boolean contains(Cell c){
        return celle.contains(c);
    }

    @Override
    public int getCurrentSize(){
        return celle.size();
    }

    @Override
    public Iterator<Cell> iterator() {
        return celle.iterator();
    }
}
