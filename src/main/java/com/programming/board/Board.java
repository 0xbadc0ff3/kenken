package com.programming.board;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class Board {
    private final class AttachedBlock implements Block {//DECORATOR
        private Block block;
        public AttachedBlock(Block b){
            if(b instanceof AttachedBlock) throw new IllegalArgumentException("Blocco già associato alla board.");
            this.block=new ConcreteBlock();
            for(Cell c: b)
                this.add(c);
        }
        @Override
        public void add(Cell cell){
            Coppia coord = new Coppia(cell.getRow(),cell.getCol());
            if(Board.this.inBlocco.contains(coord)) throw new RuntimeException("Cella già appartenente ad un blocco.");
            block.add(cell);
            Board.this.inBlocco.add(coord);
            if(Board.this.inBlocco.size()==Board.this.N*Board.this.N) Board.this.initializationStatus =true;
            //TODO?
        }
        @Override
        public boolean remove(Cell cell){
            boolean status = block.remove(cell);
            if(status){//Si potrebbe evitare l'if.
                Coppia coord = new Coppia(cell.getRow(),cell.getCol());
                Board.this.inBlocco.remove(coord);
                if(Board.this.initializationStatus) Board.this.initializationStatus =false;
            }
            return status;
            //TODO?
        }

        @Override
        public void setVincolo(int vincolo) {
            block.setVincolo(vincolo);
        }

        @Override
        public void setOperation(Operation operation) {
            block.setOperation(operation);
        }

        @Override
        public int getVincolo() {
            return block.getVincolo();
        }

        @Override
        public Operation getOperation() {
            return block.getOperation();
        }
        @Override
        public boolean contains(Cell cell){
            return block.contains(cell);
        }
        @Override
        public int getCurrentSize(){
            return block.getCurrentSize();
        }
        @Override
        public Iterator<Cell> iterator() {
            return block.iterator();
        }
    }
    private static class Coppia{
        int i,j;
        public Coppia(int a, int b){ i=a; j=b; }
        public boolean equals(Coppia c) { return i==c.i && j==c.j; }
        public boolean equals(int a, int b){ return i==a && j==b; }
    }
    private final int N;
    private Cell[][] celle;
    private LinkedList<AttachedBlock> blocchi;
    private boolean initializationStatus = false;
    private Set<Coppia> inBlocco;

    public Board(int n){
        if(n<3) throw new IllegalArgumentException("Board troppo piccola.");
        this.N = n;
        celle = new Cell[N][N];
        blocchi = new LinkedList<AttachedBlock>();
        inBlocco = new TreeSet<Coppia>();
        //Inizializzazione della matrice
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                celle[i][j] = new Cell(i,j);
    }

    public Block attachBlock(ConcreteBlock block){//PATTERN DECORATOR
        return new AttachedBlock(block);
    }
    public void addBlock(Block block){
        if(initializationStatus) throw new RuntimeException("Impossibile creare un nuovo blocco.");
        if(!(block instanceof AttachedBlock)) throw new RuntimeException("Il blocco non è stato associato alla board.");
        blocchi.add( (AttachedBlock)block );
    }

}
