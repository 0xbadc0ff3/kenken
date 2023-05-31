package com.programming.view;

import com.programming.model.Block;
import com.programming.model.Board;
import com.programming.model.ConcreteBlock;
import com.programming.model.Operation;

import javax.swing.*;
import java.util.Collection;
import java.util.LinkedList;

public class BlockView {
    private Block block;
    //private JPanel panel;
    private Collection<CellView> cellViews;

    public BlockView(Board board){
        this.block= new ConcreteBlock();
        this.block = board.attachBlock(this.block);
        cellViews = new LinkedList<>();
    }

    public void addCell(CellView cellView){
        this.block.add(cellView.getCell());
        cellView.addBlock(this.block);
        cellViews.add(cellView);
        for(CellView cw : cellViews){
            cw.updateView();
        }
    }
    public void setVincolo(int vincolo){
        this.block.setVincolo(vincolo);
        //TODO: Rappresentazione grafica del vincolo!!
        //Il vincolo viene mostrato nella cella avente min(i) e min(j) del blocco.
    }
    public void removeCell(CellView cellView){
        cellView.removeBlock();
        cellViews.remove(cellView.getCell());
        for(CellView cw : cellViews){
            cw.updateView();
        }
    }
    public void setOperation(Operation operation){
        this.block.setOperation(operation);
        //TODO: Rappresentazione grafica dell'operazione, vicino al vincolo.
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

}
