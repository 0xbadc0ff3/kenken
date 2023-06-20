package com.programming.view;

import com.programming.Utility;
import com.programming.model.Cell;
import com.programming.model.Operation;

import javax.swing.*;
import java.awt.*;

import static com.programming.Utility.BLOCK_BORDER_SIZE;
import static com.programming.Utility.DEFAULT_BORDER_SIZE;

public class CellView {
    private final Cell cell;
    private final JPanel panel;
    private final JTextField textField;
    private JTextField constraints;
    private BlockView block;
    //private BoardView boardView;
    private int top, left, bottom, right;
    private Operation operation = null;
    private Integer result = null;
    private boolean hasConstraints = false;
    private CellState state = CellState.UNKOWN;

    //private final int N;
    public CellView(Cell cell){
        this.block=null;
        this.cell = cell;
        this.panel = new JPanel(new BorderLayout());
        //Assumo valore iniziale della cella pari a zero
        textField = new JTextField("",1);
        textField.setFont(Utility.FONT);
        if(cell.getValue()>0) textField.setText(""+cell.getValue());
        textField.setEditable(false);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.getCaret().setVisible(false);
        //Inizializzo la grafica supponendo che, inizialmente, non appartenga ad alcun blocco.
        top = DEFAULT_BORDER_SIZE; left = DEFAULT_BORDER_SIZE; bottom = DEFAULT_BORDER_SIZE; right = DEFAULT_BORDER_SIZE;
        //Pattern FACTORY METHOD di Java.
        panel.setBorder(BorderFactory.createMatteBorder(top,left,bottom,right,Color.BLACK));
        panel.add(textField,BorderLayout.CENTER);
    }
    public Component getView(){
        return panel;
    }
    public void addBlock(BlockView block){
        if(this.block!=null) throw new RuntimeException("Cella già inserita in un blocco.");
        this.block=block;
    }
    public void removeBlock(){
        this.block=null;
        updateView(false);
    }
    public void removeValue(){
        this.cell.setValue(0);
        textField.setText("");
    }
    public int getValue(){
        return cell.getValue();
    }
    public int getRow() {
        return cell.getRow();
    }
    public int getCol(){
        return cell.getCol();
    }
    public void updateView(boolean isSelected){
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
        Color color = isSelected? Color.CYAN : Color.BLACK;
        panel.setBorder(BorderFactory.createMatteBorder(top,left,bottom,right,color));
    }
    public void setValue(int value){
        //Assumo input valido
        this.cell.setValue(value);
        this.textField.setText(""+value);
    }
    public void updateText(){
        if(cell.getValue()>0) this.textField.setText(""+cell.getValue());
    }
    public Cell getCell(){
        return cell;
    }
    public void setMenu(JPopupMenu menu){
        this.textField.setComponentPopupMenu(menu);
    }
    public boolean hasBlock(){
        return block!=null;
    }
    public BlockView getBlock() { return block; }
    public boolean hasConstraints(){ return hasConstraints; }
    public void addConstraints(){
        this.result=block.getResult(); this.operation=block.getOperation();
        if(!hasConstraints){
            hasConstraints = true;
            constraints = new JTextField();
            constraints.getCaret().setVisible(false);//Non funzionante?
            constraints.setEditable(false);
            this.panel.add(constraints, BorderLayout.NORTH);
            constraints.setVisible(true);
            this.panel.updateUI();
        }
        StringBuilder toDisplay = new StringBuilder(5);
        if(result != null && result>0) toDisplay.append(result);
        if(operation!=null) {
            switch (operation) {
                case ADD:
                    toDisplay.append("+");
                    break;
                case SUB:
                    toDisplay.append("-");
                    break;
                case MUL:
                    toDisplay.append("x");
                    break;
                case DIV:
                    toDisplay.append("/");
                    break;
            }
        }
        constraints.setText(toDisplay.toString());
    }
    public void setState(CellState state){
        if(this.state == CellState.NOT_VALID_BLOCK && state == CellState.NOT_VALID_REPEATED_VALUE || this.state==state) return;
        this.state=state;
        switch (state){
            case VALID: this.textField.setForeground(Utility.VALID_COLOR); break;
            case NOT_VALID_REPEATED_VALUE:
            case NOT_VALID_BLOCK: this.textField.setForeground(Utility.WARNING_COLOR); break;
            case UNKOWN: this.textField.setForeground(Utility.DEFAULT_COLOR); break;
            default: System.out.println("Stato sconosciuto: "+state); this.state=CellState.UNKOWN; break;
        }
    }
    public CellState getState() { return state; }
    public void removeConstraints(){
        if(!hasConstraints) return;
        hasConstraints = false;
        this.operation = null;
        this.result = null;
        this.panel.remove(constraints);
        constraints = null;
    }
}
