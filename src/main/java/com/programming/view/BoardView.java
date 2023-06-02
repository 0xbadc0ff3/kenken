package com.programming.view;

import com.programming.Utility;
import com.programming.model.Board;
import com.programming.model.BoardState;
import com.programming.model.Cell;
import com.programming.model.Operation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class BoardView extends JPanel{
    private Board board;
    private List<BlockView> blockViews;
    private CellView[][] cellViews;

    //Variabili per il listener:
    private boolean isAdding = false;
    private BlockView addingTo = null;
    private Collection<CellView> canAdd = new LinkedList<>();
    public BoardView(int n){
        super(new GridLayout(n, n));
        if(n>Utility.MAX_BOARD_SIZE) throw new IllegalArgumentException("Dimensione Board non supportata. (Troppo grande)");
        this.board= new Board(n);
        //this.panel= new JPanel(new GridLayout(board.getN(), board.getN()));
        blockViews = new LinkedList<>();
        /*
        for(Block block: board.getBlocks()){
            blockViews.add(new BlockView(block,panel));
        }
         */
        cellViews =new CellView[n][n];
        /*
        for(Cell cell: board.getNotInBlockCells()){
            cellViews.add(new CellView(cell,panel));
        }
         */
        for(int i=0;i<board.getN();i++){
            for(int j=0;j<board.getN();j++){
                CellView current = new CellView(board.getCell(i,j), this);
                //current.setMenu(createMenu(current));
                cellViews[i][j]=current;
                this.add(current.getView());
            }
        }
    }
    public BoardView(Board board){
        super(new GridLayout(board.getN(),board.getN()));
        if(board.getN()> Utility.MAX_BOARD_SIZE) throw new IllegalArgumentException("Dimensione Board non supportata. (Troppo grande)");
        this.board=board;
        blockViews = new LinkedList<>();
        cellViews = new CellView[board.getN()][board.getN()];
        for(int i=0;i<board.getN();i++){
            for(int j=0;j<board.getN();j++){
                CellView current = new CellView(board.getCell(i,j), this);
                //current.setMenu(createMenu(current));
                cellViews[i][j]=current;
                this.add(current.getView());
            }
        }
    }
    public CellView getCellView(int i, int j){
        return cellViews[i][j];
    }
    public CellView getCellView(Cell cell){
        return cellViews[cell.getRow()][cell.getCol()];
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
        LinkedList<CellView> cellViews1 = new LinkedList<>();
        for(int i=0;i<cellViews.length;i++){
            for(int j=0;j<cellViews[0].length;j++)
                cellViews1.add(cellViews[i][j]);
        }
        return Collections.unmodifiableList(cellViews1);
    }
    public void edit(){
        board.edit();
    }
    public void startGame(){
        if(!board.startGame())
            JOptionPane.showMessageDialog(this,"Cannot start the game: Board is incomplete.","Error",JOptionPane.ERROR_MESSAGE);
    }
    public int getN() { return board.getN(); }
    /*
    JPopupMenu createMenu(CellView cellView){
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("new-block")) {
                    if (isAdding) return;
                    //System.out.println(e.getSource().getClass());
                    BlockView current = BoardView.this.createBlock();
                    current.addCell(cellView);
                }
                if (e.getActionCommand().equals("add")) {
                    BoardView.this.isAdding = true;
                    BoardView.this.addingTo = cellView.getBlock();
                    int i = cellView.getCell().getRow(), j = cellView.getCell().getCol();
                    //Controllo sopra
                    Cell toCheck = new Cell(i - 1, j);
                    if (BoardView.this.board.getNotInBlockCells().contains(toCheck)) {
                        canAdd.add(BoardView.this.cellViews[i - 1][j]);
                    }
                    //Sinistra
                    toCheck = new Cell(i, j - 1);
                    if (BoardView.this.board.getNotInBlockCells().contains(toCheck)) {
                        canAdd.add(BoardView.this.cellViews[i][j - 1]);
                    }
                    //Sotto
                    toCheck = new Cell(i + 1, j);
                    if (BoardView.this.board.getNotInBlockCells().contains(toCheck)) {
                        canAdd.add(BoardView.this.cellViews[i + 1][j]);
                    }
                    //Destra
                    toCheck = new Cell(i, j + 1);
                    if (BoardView.this.board.getNotInBlockCells().contains(toCheck)) {
                        canAdd.add(BoardView.this.cellViews[i][j + 1]);

                    }
                }
                if (e.getActionCommand().equals("set-result")){
                    if(isAdding) return;
                    int result = Integer.parseInt(JOptionPane.showInputDialog(BoardView.this,"Please provide the result which has to be obtained from the current block.","0"));
                    cellView.getBlock().setVincolo(result);
                }
            }
        };
        JPopupMenu menu = new JPopupMenu("Action to perform:");
        if(board.getState()== BoardState.SETTING){
            if(cellView.hasBlock()){
                JMenuItem addToBlock = new JMenuItem("Add Cell to Block");
                addToBlock.setActionCommand("add");
                addToBlock.addActionListener(actionListener);
                menu.add(addToBlock);

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
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try{
                                    int choosed = Integer.parseInt(e.getActionCommand());
                                    if(choosed<1 || choosed> board.getN()) throw new RuntimeException("Numero scelto non valido.");//ridondante
                                    cellView.setValue(choosed);
                                }catch(NumberFormatException numberFormatException){
                                    System.out.println("Errore nella selezione del numero.");
                                    numberFormatException.printStackTrace();
                                }
                            }
                        }
                );
                menu.add(menuItem);
            }
        }
        return menu;
    }
    private JPopupMenu createAddMenu(){
        System.out.println("ciao");
        return null;
    }
*/
}
