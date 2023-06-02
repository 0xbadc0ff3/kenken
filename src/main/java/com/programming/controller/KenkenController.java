package com.programming.controller;

import com.programming.model.Board;
import com.programming.model.BoardState;
import com.programming.model.Cell;
import com.programming.model.Operation;
import com.programming.view.BlockView;
import com.programming.view.BoardView;
import com.programming.view.CellView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;

public final class KenkenController {

    private final BoardView boardView;
    private final Board board;

    //Per i listener:
    private boolean isAdding=false;
    private BlockView addingTo = null;
    private Collection<CellView> canAdd = new LinkedList<>();

    public KenkenController(int n){
        board = new Board(n);
        boardView = new BoardView(board);
        for(CellView cw : boardView.getCellViews()) cw.setMenu(createMenu(cw));
    }
    public JPanel getBoardView(){
        return boardView;
    }

    private Collection<CellView> getAdiacenti(CellView cellView){
        LinkedList<CellView> adiacenti = new LinkedList<>();
        int i = cellView.getCell().getRow(), j=cellView.getCell().getCol();
        //Controllo la cella sopra.
        Cell toCheck = new Cell(i-1,j);
        if(board.getNotInBlockCells().contains(toCheck)){
            adiacenti.add(boardView.getCellView(toCheck));
        }
        //Sinistra
        toCheck = new Cell(i,j-1);
        if(board.getNotInBlockCells().contains(toCheck)) adiacenti.add(boardView.getCellView(toCheck));
        //Sotto
        toCheck = new Cell(i+1,j);
        if(board.getNotInBlockCells().contains(toCheck)) adiacenti.add(boardView.getCellView(toCheck));
        //Destra
        toCheck = new Cell(i,j+1);
        if(board.getNotInBlockCells().contains(toCheck)) adiacenti.add(boardView.getCellView(toCheck));
        return adiacenti;
    }

    private void updateMenu(){//resetta il menu delle celle a seguito di un'operazione di inserimento.
        for(CellView cellView: boardView.getCellViews()) {
            //cellView.updateView(false);
            cellView.setMenu(createMenu(cellView));
        }
    }
    private void setAddingMenu(){
        for(CellView cellView: canAdd) {
            JPopupMenu addMenu = new JPopupMenu();
            JMenuItem addToBlock = new JMenuItem("Add to selected Block");
            addToBlock.setActionCommand("add");
            addToBlock.addActionListener(e -> execute(cellView, e.getActionCommand()));
            addMenu.add(addToBlock);
            cellView.setMenu(addMenu);
        }
    }

    private void execute(CellView cellView, String actionCommand){
        switch (actionCommand){
            case "add":
                if(!canAdd.contains(cellView)) throw new IllegalArgumentException("Celle non adiacenti.");
                addingTo.addCell(cellView);
                addingTo.deselectBlock();
                isAdding = false; addingTo=null; canAdd.clear();
                updateMenu();
                break;
            case "select":
                if(isAdding) execute(null, "deselect");
                isAdding=true;
                addingTo=cellView.getBlock();
                addingTo.selectBlock();
                assert canAdd.isEmpty();
                canAdd.addAll(getAdiacenti(cellView));
                for(CellView cw: addingTo.getCellViews()) cw.setMenu(createMenu(cw));
                setAddingMenu();
                break;
            case "deselect":
                addingTo.deselectBlock();
                isAdding=false;
                addingTo=null;
                canAdd.clear();
                updateMenu();
                break;
            case "set-result":
                int result = 0;
                try{
                    result = Integer.parseInt(JOptionPane.showInputDialog(boardView, "Please provide the result which has to be obtained from the current block.", "Set Block Result", JOptionPane.PLAIN_MESSAGE));
                    cellView.getBlock().setVincolo(result);
                }catch (NumberFormatException nfe){
                    JOptionPane.showMessageDialog(boardView, "Provided value cannot be interpreted as an integer.","Error",JOptionPane.ERROR_MESSAGE);
                }catch (IllegalArgumentException illegalArgumentException){
                    JOptionPane.showMessageDialog(boardView, "Provided value must be greater than zero.","Error",JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "set-operation-add":
                cellView.getBlock().setOperation(Operation.ADD); break;
            case "set-operation-sub":
                cellView.getBlock().setOperation(Operation.SUB); break;
            case "set-operation-mul":
                cellView.getBlock().setOperation(Operation.MUL); break;
            case "set-operation-div":
                cellView.getBlock().setOperation(Operation.DIV); break;
            case "new-block":
                boardView.createBlock().addCell(cellView);
                cellView.setMenu(createMenu(cellView));
                break;
            case "remove-block":
                if(cellView.getBlock().isSelected()) execute(null, "deselect");
                boardView.removeBlock(cellView.getBlock());
                updateMenu();
                break;
            default: System.out.println("Comando non interpretato: "+actionCommand);
        }
    }

    private JPopupMenu createMenu(CellView cellView){

        ActionListener actionListener = e -> execute(cellView,e.getActionCommand());

        JPopupMenu menu = new JPopupMenu("Action to perform:");
        if(board.getState()== BoardState.SETTING){
            if(cellView.hasBlock()){
                if(isAdding && addingTo.contains(cellView.getCell())) {
                    JMenuItem deselectBlock = new JMenuItem("Deselect Block");
                    deselectBlock.setActionCommand("deselect");
                    deselectBlock.addActionListener(actionListener);
                    menu.add(deselectBlock);
                }else {
                    if (!cellView.getBlock().isFull()) {
                        JMenuItem addToBlock = new JMenuItem("Select Block");
                        addToBlock.setActionCommand("select");
                        addToBlock.addActionListener(actionListener);
                        menu.add(addToBlock);
                    }
                }
                JMenuItem setResult = new JMenuItem("Set Result");
                setResult.setActionCommand("set-result");
                setResult.addActionListener(actionListener);
                menu.add(setResult);

                JMenu setOperation = new JMenu("Set Operation");
                JMenuItem add = new JMenuItem("+");
                add.setActionCommand("set-operation-add");
                add.addActionListener(actionListener);
                setOperation.add(add);
                JMenuItem sub = new JMenuItem("-");
                sub.setActionCommand("set-operation-sub");
                sub.addActionListener(actionListener);
                setOperation.add(sub);
                JMenuItem mul = new JMenuItem("x");
                mul.setActionCommand("set-operation-mul");
                mul.addActionListener(actionListener);
                setOperation.add(mul);
                JMenuItem div = new JMenuItem("/");
                div.setActionCommand("set-operation-div");
                div.addActionListener(actionListener);
                setOperation.add(div);
                menu.add(setOperation);

                JMenuItem remove = new JMenuItem("Remove Block");
                remove.setActionCommand("remove-block");
                remove.addActionListener(actionListener);
                remove.setForeground(Color.RED);
                menu.add(remove);

            }else {
                JMenuItem newBlock = new JMenuItem("Create new Block");
                newBlock.setActionCommand("new-block");
                newBlock.addActionListener(actionListener);
                menu.add(newBlock);

            }
        }else if(board.getState()== BoardState.PLAYING){
            for(int i=1; i<=board.getN(); i++){
                JMenuItem menuItem = new JMenuItem(""+i);
                menuItem.setActionCommand(""+i);
                menuItem.addActionListener(
                        e -> {
                            try{
                                int chosen = Integer.parseInt(e.getActionCommand());
                                if(chosen<1 || chosen> board.getN()) throw new RuntimeException("Numero scelto non valido.");//ridondante
                                cellView.setValue(chosen);
                            }catch(NumberFormatException numberFormatException){
                                System.out.println("Errore nella selezione del numero.");
                                numberFormatException.printStackTrace();
                            }
                        }
                );
                menu.add(menuItem);
            }
        }
        return menu;
    }

    public void startGame(){
        boardView.startGame();
    }
    public void findSolutions(){
        //TODO
    }
    public void save(){
        //TODO
    }
    public void open(){
        //TODO
    }

}
