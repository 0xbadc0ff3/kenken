package com.programming.view;

import com.programming.Utility;
import com.programming.model.Block;
import com.programming.model.Cell;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;

import static com.programming.Utility.BLOCK_BORDER_SIZE;
import static com.programming.Utility.DEFAULT_BORDER_SIZE;

public class CellView {
    private Cell cell;
    private JPanel panel;
    private JTextField textField;
    private Block block;
    private int top, left, bottom, right;
    private JPopupMenu menu;
    private final int N;
    public CellView(Cell cell, int n){
        this.N = n;
        this.block=null;
        this.cell = cell;
        this.panel = new JPanel(new BorderLayout());
        //Assumo valore iniziale della cella pari a zero
        textField = new JTextField("",1);
        textField.setEditable(false);
        textField.setHorizontalAlignment(JTextField.CENTER);
        //Inizializzo la grafica supponendo che, inizialmente, non appartenga ad alcun blocco.
        top = DEFAULT_BORDER_SIZE; left = DEFAULT_BORDER_SIZE; bottom = DEFAULT_BORDER_SIZE; right = DEFAULT_BORDER_SIZE;
        //Pattern FACTORY METHOD di Java.
        panel.setBorder(BorderFactory.createMatteBorder(top,left,bottom,right,Color.BLACK));
        panel.add(textField,BorderLayout.CENTER);
        menu = new JPopupMenu("Scegli un valore.");
        for(int i=1; i<=N; i++){
            JMenuItem menuItem = new JMenuItem(""+i);
            menuItem.setActionCommand(""+i);
            menuItem.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try{
                                int choosed = Integer.parseInt(e.getActionCommand());
                                if(choosed<1 || choosed>N) throw new RuntimeException("Numero scelto non valido.");//ridondante
                                setValue(choosed);
                            }catch(NumberFormatException numberFormatException){
                                System.out.println("Errore nella selezione del numero.");
                                numberFormatException.printStackTrace();
                            }
                        }
                    }
            );
            menu.add(menuItem);
        }
        this.textField.setComponentPopupMenu(menu);
        this.panel.setVisible(true); //Evita che il click ai bordi della cella vengano intercettati dal JPopupMenu del textField
    }
    public Component getView(){
        return panel;
    }
    public void addBlock(Block block){
        if(this.block!=null) throw new RuntimeException("Cella già inserita in un blocco.");
        this.block=block;
        //updateView();
    }
    public void removeBlock(){
        this.block=null;
        updateView();
    }
    public void updateView(){
        if(block==null) {
            top = DEFAULT_BORDER_SIZE;
            left = DEFAULT_BORDER_SIZE;
            bottom = DEFAULT_BORDER_SIZE;
            right = DEFAULT_BORDER_SIZE;
            panel.setBorder(BorderFactory.createMatteBorder(top,left,bottom,right,Color.BLACK));
            return;
        }
        int i,j;
        i=this.cell.getRow(); j=this.cell.getCol();
        //Controllo quali celle adiacenti fanno parte del blocco.
        //sopra
        Cell toCheck = new Cell(i-1,j);
        if(block.contains(toCheck)) top=DEFAULT_BORDER_SIZE;
        else top=BLOCK_BORDER_SIZE;
        //a sinistra
        toCheck = new Cell(i,j-1);
        if(block.contains(toCheck)) left=DEFAULT_BORDER_SIZE;
        else left=BLOCK_BORDER_SIZE;
        //sotto
        toCheck = new Cell(i+1,j);
        if(block.contains(toCheck)) bottom=DEFAULT_BORDER_SIZE;
        else bottom=BLOCK_BORDER_SIZE;
        //a destra
        toCheck = new Cell(i,j+1);
        if(block.contains(toCheck)) right=DEFAULT_BORDER_SIZE;
        else right=BLOCK_BORDER_SIZE;

        panel.setBorder(BorderFactory.createMatteBorder(top,left,bottom,right,Color.BLACK));
    }
    public void setValue(int value){
        //Assumo input valido
        this.cell.setValue(value);
        System.out.println("setValue: "+cell.getValue());
        this.textField.setText(""+value);
    }
    public Cell getCell(){
        return cell;
    }
}
