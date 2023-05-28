package com.programming.view;

import com.programming.Utility;
import com.programming.model.Block;
import com.programming.model.Cell;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static com.programming.Utility.BLOCK_BORDER_SIZE;
import static com.programming.Utility.DEFAULT_BORDER_SIZE;

public class CellView {
    private Cell cell;
    private JPanel panel;
    private JTextField textField;
    private Block block;
    private Border border;
    int top, left, bottom, right;
    public CellView(Cell cell){
        this.block=null;
        this.cell = cell;
        this.panel = new JPanel();
        //Assumo valore iniziale della cella pari a zero
        textField = new JTextField("",1);
        textField.setEditable(false);
        //Inizializzo la grafica supponendo che, inizialmente, non appartenga ad alcun blocco.
        top = DEFAULT_BORDER_SIZE; left = DEFAULT_BORDER_SIZE; bottom = DEFAULT_BORDER_SIZE; right = DEFAULT_BORDER_SIZE;
        border = new MatteBorder(top, left, bottom, right,Color.BLACK);
        panel.setBorder(border);
    }
    public Component getView(){
        return panel;
    }
    public void updateBlock(Block block){
        if(this.block!=null) throw new RuntimeException("Cella già inserita in un blocco.");
        this.block=block;
    }
    public void updateView(){
        if(block==null) return;
        int i,j;
        i=this.cell.getRow(); j=this.cell.getCol();
        //Controllo quali celle adiacenti fanno parte del blocco.
        //sopra
        Cell toCheck = new Cell(i-1,j);
        if(block.contains(toCheck)) top=BLOCK_BORDER_SIZE;
        else top=DEFAULT_BORDER_SIZE;
        //a sinistra
        toCheck = new Cell(i,j-1);
        if(block.contains(toCheck)) left=BLOCK_BORDER_SIZE;
        else left=DEFAULT_BORDER_SIZE;
        //sotto
        toCheck = new Cell(i+1,j);
        if(block.contains(toCheck)) bottom=BLOCK_BORDER_SIZE;
        else bottom=DEFAULT_BORDER_SIZE;
        //a destra
        toCheck = new Cell(i,j+1);
        if(block.contains(toCheck)) right=BLOCK_BORDER_SIZE;
        else right=DEFAULT_BORDER_SIZE;

        border = new MatteBorder(top,left,bottom,right,Color.BLACK);
        this.panel.setBorder(border);
    }
}
