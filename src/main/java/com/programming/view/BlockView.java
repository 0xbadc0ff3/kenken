package com.programming.view;

import com.programming.Utility;
import com.programming.model.*;

import javax.swing.*;
import java.util.*;

public class BlockView {
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
        if(forceUpdate){
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
        //TODO: Rappresentazione grafica del vincolo!
        //Il vincolo viene mostrato nella cella avente min(i) e poi min(j) del blocco.
        updateDisplayCell(true);
        displayCell.addConstraints();
    }

    public void setOperation(Operation operation){
        this.block.setOperation(operation);
        //TODO: Rappresentazione grafica dell'operazione, vicino al vincolo.
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
    public void isValid(){
        //TODO: Colorare di verde il blocco se valido, di rosso altrimenti.
    }
    public void delete(Board board){
        board.removeBlock(this.block);
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
