package com.programming.view;

import com.programming.model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;

public class BoardView {
    private Board board;
    private Collection<BlockView> blockViews;
    private Collection<CellView> cellViews;
    //devo visualizzare anche le celle che ancora, potenzialmente, non sono state aggiunte ad un blocco.
    private JPanel panel;
    public BoardView(int n){
        if(n>6) throw new IllegalArgumentException("Dimensione Board non supportata. (Troppo grande)");
        this.board= new Board(n);
        this.panel= new JPanel(new GridLayout(board.getN(), board.getN()));
        blockViews = new LinkedList<>();
        /*
        for(Block block: board.getBlocks()){
            blockViews.add(new BlockView(block,panel));
        }
         */
        cellViews =new LinkedList<>();
        /*
        for(Cell cell: board.getNotInBlockCells()){
            cellViews.add(new CellView(cell,panel));
        }
         */
        for(int i=0;i<board.getN();i++){
            for(int j=0;j<board.getN();j++){
                CellView current = new CellView(board.getCell(i,j));
                cellViews.add(current);
                panel.add(current.getView());
            }
        }
    }
}
