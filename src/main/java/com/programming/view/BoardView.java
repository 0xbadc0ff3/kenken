package com.programming.view;

import com.programming.model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;

public class BoardView extends JPanel{
    private Board board;
    private Collection<BlockView> blockViews;
    private Collection<CellView> cellViews;
    //devo visualizzare anche le celle che ancora, potenzialmente, non sono state aggiunte ad un blocco.
    //private JPanel panel;
    public BoardView(int n){
        super(new GridLayout(n, n));
        if(n>6) throw new IllegalArgumentException("Dimensione Board non supportata. (Troppo grande)");
        this.board= new Board(n);
        //this.panel= new JPanel(new GridLayout(board.getN(), board.getN()));
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
                CellView current = new CellView(board.getCell(i,j), board.getN());
                cellViews.add(current);
                this.add(current.getView());
            }
        }
    }
    public BlockView createBlock(){
        BlockView created = new BlockView(board);
        blockViews.add(created);
        return created;
    }
    public void removeBlock(BlockView block){
        blockViews.remove(block);
        block.delete(board);
    }

    public Collection<CellView> getCellViews(){
        return cellViews;
    }
}
