package com.programming.view;

import com.programming.Utility;
import com.programming.model.*;

import javax.swing.*;
import java.util.*;

public class BlockView {//Proxy
    private Block block;
    private boolean selected = false;
    //private JPanel panel;
    private List<CellView> cellViews;
    private CellView displayCell = null;

    public BlockView(Board board){
        this.block= new ConcreteBlock();
        this.block = board.attachBlock(this.block);
        cellViews = new LinkedList<>();
    }
    public BlockView(Board board, BoardView boardView, Block block){
        if(!(block.isAttached() && board.getBlocks().contains(block))) throw new IllegalArgumentException("Block must be attached to the board.");
        this.block = block;
        cellViews = new LinkedList<>();
        for(Cell cell: block){
            cellViews.add(boardView.getCellView(cell));
            boardView.getCellView(cell).addBlock(this);
        }
        boolean reset = false;
        if(boardView.getState()==BoardState.PLAYING) reset = true;
        boardView.edit();
        this.setOperation(block.getOperation());
        this.setVincolo(block.getVincolo());
        if(reset) boardView.startGame();
        for(CellView cw : cellViews){
            cw.updateView(false);
        }
        updateDisplayCell(block.hasConstraints());
    }
    private void updateDisplayCell(boolean forceUpdate){
        Integer result = block.getVincolo(); Operation operation = block.getOperation();
        if(displayCell!=null) {
            displayCell.removeConstraints();
        }
        int min_i = Utility.MAX_BOARD_SIZE +1, min_j = Utility.MAX_BOARD_SIZE +1;
        CellView candidata = null;
        for(CellView cw : cellViews){
            if(cw.getCell().getRow()<min_i){
                min_i = cw.getCell().getRow(); candidata = cw;
            }
        }
        for(CellView cw : cellViews){
            if(cw.getCell().getRow()==min_i && cw.getCell().getCol()<min_j){
                candidata = cw;
                min_j = cw.getCell().getCol();
            }
        }
        if(forceUpdate /*|| result > 0 || operation != null*/){
            candidata.addConstraints();
        }
        this.displayCell=candidata;
    }
    public int getResult(){
        return block.getVincolo();
    }
    public Operation getOperation(){
        return block.getOperation();
    }

    public void addCell(CellView cellView){
        this.block.add(cellView.getCell());
        cellView.addBlock(this);
        cellViews.add(cellView);
        for(CellView cw : cellViews){
            cw.updateView(false);
        }
        updateDisplayCell(displayCell!=null && displayCell.hasConstraints());
    }
    public Collection<CellView> getCellViews(){
        return Collections.unmodifiableList(cellViews);
    }
    public void setVincolo(int vincolo){
        this.block.setVincolo(vincolo);
        //Il vincolo viene mostrato nella cella avente min(i) e poi min(j) del blocco.
        updateDisplayCell(true);
        displayCell.addConstraints();
    }

    public void setOperation(Operation operation){
        this.block.setOperation(operation);
        updateDisplayCell(true);
        displayCell.addConstraints();
    }
    public void removeCell(CellView cellView){
        cellView.removeBlock();
        cellViews.remove(cellView.getCell());
        for(CellView cw : cellViews){
            cw.updateView(selected);
        }
    }
    public int getCurrentSize(){
        return block.getCurrentSize();
    }
    public boolean isValid(){
        return block.isValid();
    }
    public void delete(Board board){
        board.removeBlock(this.block);
        displayCell.removeConstraints();
        for(CellView cw: cellViews){
            cw.removeBlock();
        }
        cellViews.clear();
    }
    public void selectBlock(){
        selected = true;
        for( CellView cellView : cellViews ){
            cellView.updateView(selected);
        }
    }
    public void deselectBlock(){
        selected = false;
        for(CellView cellView: cellViews) cellView.updateView(selected);
    }
    public boolean isSelected(){
        return selected;
    }
    public boolean isFull(){
        return block.isFull();
    }
    public boolean contains(Cell cell){
        return this.block.contains(cell);
    }
    public String toString(){
        StringBuilder sb = new StringBuilder(100);
        for(CellView cw : cellViews) sb.append(""+cw.getCell().getRow()+"\n");
        return sb.toString();
    }
}
