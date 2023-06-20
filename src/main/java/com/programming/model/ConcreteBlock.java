package com.programming.model;
import com.programming.Utility;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

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
        if(v>=0) this.vincolo=v;//Il valore zero implica la cancellazione del valore precedente. (reset)
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
        if(celle.size()== Utility.MAX_BLOCK_SIZE) {
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
    public boolean isValid() {
        if(celle.size()==1) {
            if (vincolo > 0) return celle.iterator().next().getValue() == vincolo;
            else return false;
        }

        if(!this.hasConstraints()) throw new RuntimeException("I vincoli del blocco non sono ancora stati definiti.");
        int value;
        Iterator<Cell> it = this.iterator();
        int second;
        switch (op){
            case ADD:
                value=0;
                while(it.hasNext()){
                    int cur = it.next().getValue();
                    if(cur<=0) return false;
                    value += cur;
                }
                return value==vincolo;
            case SUB:
                //Assumo dimensione del blocco pari a 2.
                value = it.next().getValue();
                second = it.next().getValue();
                if(value<=0 || second<=0) return false;
                return value-second==vincolo || second-value==vincolo;
            case MUL:
                value=1;
                while(it.hasNext()) {
                    value = value * it.next().getValue();
                }
                return value==vincolo;
            case DIV:
                //Assumo dimensione del blocco pari a 2.
                value = it.next().getValue();
                second = it.next().getValue();
                if(value<=0 || second<=0) return false;
                return value/second==vincolo || second/value==vincolo;
            default: return this.getCurrentSize()==1;
        }
    }

    @Override
    public boolean hasConstraints() {
        return this.vincolo>0 && (this.op!=null || this.getCurrentSize()==1);
    }
    @Override
    public boolean isFull() {
        return celle.size()==Utility.MAX_BLOCK_SIZE;
    }

    @Override
    public Collection<Cell> getCells() {
        LinkedList<Cell> toReturn = new LinkedList<>();
        for(Cell cell: celle) toReturn.add(cell);
        return toReturn;
    }

    @Override
    public boolean isAttached() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcreteBlock concreteBlock = (ConcreteBlock) o;
        return vincolo == concreteBlock.vincolo && Objects.equals(celle, concreteBlock.celle) && op == concreteBlock.op;
    }

    @Override
    public int hashCode() {
        return Objects.hash(celle, vincolo, op);
    }

    @Override
    public Iterator<Cell> iterator() {
        return celle.iterator();
    }
}
