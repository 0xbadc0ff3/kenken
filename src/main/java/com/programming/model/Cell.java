package com.programming.model;

import java.util.Objects;

public final class Cell {
    private final int i,j;
    private int value;
    public Cell(int row_index,int col_index){
        //Assumo indici validi.
        this.i = row_index;
        this.j = col_index;
    }
    public void setValue(int val){
        this.value=val;
    }
    public int getRow(){
        return i;
    }
    public int getCol(){
        return j;
    }
    public int getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return i == cell.i && j == cell.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }
}
