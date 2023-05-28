package com.programming.view;

import com.programming.model.Block;
import com.programming.model.Cell;

import javax.swing.*;
import java.util.Collection;
import java.util.LinkedList;

public class BlockView {
    private Block block;
    private JPanel panel;
    private Collection<CellView> cellViews;

    public BlockView(Block block, JPanel panel){
        this.block=block;
        this.panel = panel;
        cellViews = new LinkedList<>();
        for(Cell cell:block.getCells()){
            cellViews.add(new CellView(cell));
        }
    }

}
