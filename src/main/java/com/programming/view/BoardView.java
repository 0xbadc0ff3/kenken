package com.programming.view;

import com.programming.Utility;
import com.programming.controller.KenKenSolver;
import com.programming.memento.Memento;
import com.programming.memento.Originator;
import com.programming.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class BoardView extends JPanel implements Originator {
    private Board board;
    private List<BlockView> blockViews;
    private CellView[][] cellViews;

    public BoardView(int n){
        super(new GridLayout(n, n));
        if(n>Utility.MAX_BOARD_SIZE) throw new IllegalArgumentException("Dimensione Board non supportata. (Troppo grande)");
        this.board= new Board(n);
        blockViews = new LinkedList<>();
        cellViews =new CellView[n][n];
        for(int i=0;i<board.getN();i++){
            for(int j=0;j<board.getN();j++){
                CellView current = new CellView(board.getCell(i,j));
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
                CellView current = new CellView(board.getCell(i,j));
                cellViews[i][j]=current;
                //current.setValue(board.getCell(i,j).getValue());
                this.add(current.getView());
            }
        }
        for(Block b: board.getBlocks()){
            BlockView blockView = new BlockView(this.board,this, b);
            blockViews.add(blockView);
        }
    }
    private BoardView(){
        super(new BorderLayout());
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
        return Collections.unmodifiableList(cellViews1);//superfluo
    }
    public int getN() { return board.getN(); }
    public Collection<Cell> getNotInBlockCells(){
        return board.getNotInBlockCells();
    }
    public List<CellView> getRow(int i){
        List<CellView> row = new ArrayList<>(board.getN());
        for(int j=0; j<board.getN();j++) row.add(cellViews[i][j]);
        return Collections.unmodifiableList(row);//superfluo
    }
    public List<CellView> getCol(int j){
        List<CellView> col = new ArrayList<>(board.getN());
        for(int i=0; i<board.getN();i++) col.add(cellViews[i][j]);
        return Collections.unmodifiableList(col);//superfluo
    }
    public Collection<BlockView> getBlocks(){
        return Collections.unmodifiableCollection(blockViews);
    }
    public BoardState getState(){
        return board.getState();
    }
    public String toJSON(){
        return board.toJSON();
    }
    public boolean startGame(){
        return board.startGame();
    }
    public void edit(){
        board.edit();
    }
    public void clearBoard(){
        board.editWithFieldReset();
        for(int i=0; i<board.getN(); i++)
            for(int j=0; j<board.getN(); j++){
                cellViews[i][j].removeValue();
            }
    }
    public void changeBoard( Board board ){
        this.removeAll();
        setLayout(new GridLayout(board.getN(),board.getN()));
        this.board = board;
        blockViews = new LinkedList<>();
        cellViews =new CellView[board.getN()][board.getN()];
        for(int i=0;i<board.getN();i++){
            for(int j=0;j<board.getN();j++){
                CellView current = new CellView(board.getCell(i,j));
                cellViews[i][j]=current;
                this.add(current.getView());
            }
        }
        for(Block block: board.getBlocks()){
            blockViews.add(new BlockView(board, this, block));
        }
        this.updateUI();
    }
    public KenKenSolver getSolver(int n, Collection<Memento> out){
        Board copy = new Board(board,false);
        return new KenKenSolver(copy,n,out);
    }
    @Override
    public Memento takeSnapshot(){
        return board.takeSnapshot();
    }

    @Override
    public void restore(Memento memento) {
        board.restore(memento);
        for(int i=0;i<board.getN();i++)
            for(int j=0;j<board.getN();j++)
                cellViews[i][j].updateText();
    }

    public Board getTemplate(){
        if(!board.isReadyToPlay()) throw new IllegalStateException("Template is not completely defined yet.");
        return new Board(board,false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardView boardView = (BoardView) o;
        return board.equals(boardView.board);
    }
    public static BoardView blankBoard(){
        return new BoardView();
    }
}
