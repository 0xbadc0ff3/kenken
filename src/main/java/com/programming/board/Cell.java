package com.programming.board;

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
}
