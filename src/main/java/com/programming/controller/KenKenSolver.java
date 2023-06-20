package com.programming.controller;

import com.programming.memento.Memento;
import com.programming.model.Block;
import com.programming.model.Board;
import com.programming.model.Cell;
import com.programming.model.ConcreteBlock;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class KenKenSolver extends Problema<Cell,Integer>{
    private Board board;
    private Map<Block,ArrayList<Cell>> map = new HashMap<>();
    private ArrayList<Block> blocks;
    private int currentBlock, currentCell;
    private Collection<Memento> output;
    public KenKenSolver(Board board, int n, Collection<Memento> output){
        super(n);
        this.board=board;
        blocks = new ArrayList<>(board.getBlocks());
        currentBlock=0; currentCell=0;
        for(Block b: blocks) map.put(b,new ArrayList<>(b.getCells()));
        if(!board.startGame()) throw new IllegalArgumentException("Board is incomplete.");
        this.output=output;
    }
    @Override
    protected Cell primoPuntoDiScelta() {
        return map.get(blocks.get(0)).get(0);
    }

    @Override
    protected Cell prossimoPuntoDiScelta(Cell ps) {
        if(currentCell==blocks.get(currentBlock).getCurrentSize()-1){
            currentCell=-1;
            currentBlock++;
            if(currentBlock==blocks.size()) throw new IllegalArgumentException("Punti di scelta finiti!");
        }
        currentCell++;
        return map.get(blocks.get(currentBlock)).get(currentCell);
    }

    @Override
    protected Cell ultimoPuntoDiScelta() {
        ArrayList<Cell> last = map.get(blocks.get(blocks.size()-1));
        return last.get(last.size()-1);
    }

    @Override
    protected Integer primaScelta(Cell ps) {
        return 1;
    }

    @Override
    protected Integer prossimaScelta(Integer integer) {
        return integer+1;
    }

    @Override
    protected Integer ultimaScelta(Cell ps) {
        return board.getN();
    }

    @Override
    protected boolean assegnabile(Integer scelta, Cell puntoDiScelta) {
        for(int i=0;i<board.getN();i++)
            if(board.getCell(i,puntoDiScelta.getCol()).getValue()==scelta) return false;
        for(int j=0;j<board.getN();j++)
            if(board.getCell(puntoDiScelta.getRow(),j).getValue()==scelta) return false;
        Block copia = new ConcreteBlock();
        for(Cell cell: blocks.get(currentBlock).getCells()){
            if(!cell.equals(puntoDiScelta)) {
                if (cell.getValue() == 0) return true;
                copia.add(cell);
            }
        }
        Cell simulated = new Cell(puntoDiScelta.getRow(),puntoDiScelta.getCol());
        simulated.setValue(scelta);
        copia.add(simulated);
        copia.setOperation(blocks.get(currentBlock).getOperation());
        copia.setVincolo(blocks.get(currentBlock).getVincolo());
        return copia.isValid();
    }

    @Override
    protected void assegna(Integer scelta, Cell puntoDiScelta) {
        //System.out.println("Assegno "+scelta+" alla cella "+puntoDiScelta.getRow()+" "+puntoDiScelta.getCol());
        puntoDiScelta.setValue(scelta);
    }

    @Override
    protected void deassegna(Integer scelta, Cell puntoDiScelta) {
        //System.out.println("Deassegno "+scelta+" alla cella "+puntoDiScelta.getRow()+" "+puntoDiScelta.getCol());
        puntoDiScelta.setValue(0);
    }

    @Override
    protected Cell precedentePuntoDiScelta(Cell puntoDiScelta) {
        if(currentCell==0){
            if(currentBlock==0)
                throw new RuntimeException("Punto di scelta precedente inesistente!");
            currentBlock--;
            currentCell=blocks.get(currentBlock).getCurrentSize();
        }
        currentCell--;
        return map.get(blocks.get(currentBlock)).get(currentCell);
    }

    @Override
    protected Integer ultimaSceltaAssegnataA(Cell puntoDiScelta) {
        return puntoDiScelta.getValue();
    }

    @Override
    protected void scriviSoluzione(int nr_sol) {
        output.add(board.takeSnapshot());
        //System.out.println(board.toJSON());
    }
}
