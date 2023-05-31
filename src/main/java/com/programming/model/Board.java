package com.programming.model;

import java.util.*;

public class Board {
    private final class AttachedBlock implements Block {//DECORATOR
        private Block block;
        public AttachedBlock(Block b){
            if(b instanceof AttachedBlock) throw new IllegalArgumentException("Blocco già associato alla board.");
            this.block=new ConcreteBlock();
            Operation currentOp = b.getOperation();
            if(currentOp!=null) this.block.setOperation(currentOp);
            int vincolo = b.getVincolo();
            if(vincolo>0) this.block.setVincolo(vincolo);
            for(Cell c: b)
                this.add(c);
        }
        @Override
        public void add(Cell cell){
            if(!Board.this.notInBlocco.contains(cell)) throw new RuntimeException("Cella già appartenente ad un blocco.");
            /*
            if(cell.getRow()<0 || cell.getRow() >= Board.this.N) throw new RuntimeException("Indice riga non valido.");
            if(cell.getCol()<0 || cell.getCol() >= Board.this.N) throw new RuntimeException("Indice colonna non valido.");
             */
            //Assumo cella valida.
            block.add(cell);
            Board.this.notInBlocco.remove(cell);
            if(Board.this.notInBlocco.isEmpty()) Board.this.initializationStatus =true;
            //TODO?
        }
        @Override
        public boolean remove(Cell cell){
            boolean status = block.remove(cell);
            if(status){//Si potrebbe evitare l'if.
                Board.this.notInBlocco.add(cell);
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AttachedBlock attachedBlock = (AttachedBlock) o;
            return Objects.equals(block, attachedBlock.block);
        }

        @Override
        public int hashCode() {
            return Objects.hash(block);
        }

        @Override
        public int getCurrentSize(){
            return block.getCurrentSize();
        }

        @Override
        public boolean isValid() {
            return block.isValid();
        }

        @Override
        public boolean hasConstraints() {
            return block.hasConstraints();
        }

        @Override
        public Collection<Cell> getCells() {
            return block.getCells();
        }

        @Override
        public Iterator<Cell> iterator() {
            return block.iterator();
        }
    }
    private final int N;
    private Cell[][] celle;
    private Collection<AttachedBlock> blocchi;
    private boolean initializationStatus = false;
    private Set<Cell> notInBlocco;

    public Board(int n){
        if(n<3) throw new IllegalArgumentException("Board troppo piccola.");
        this.N = n;
        celle = new Cell[N][N];
        blocchi = new LinkedList<AttachedBlock>();
        notInBlocco = new HashSet<Cell>();
        //Inizializzazione della matrice
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++) {
                celle[i][j] = new Cell(i, j);
                notInBlocco.add(celle[i][j]);
            }
    }

    public Block attachBlock(Block block){//PATTERN DECORATOR
        if(initializationStatus) throw new RuntimeException("Impossibile creare un nuovo blocco.");
        AttachedBlock attachedBlock = new AttachedBlock(block);
        blocchi.add( attachedBlock );
        return attachedBlock;
    }
    public void removeBlock(Block block){
        if(blocchi.remove(block)){
            if(initializationStatus) initializationStatus=false;

        }

    }
    public boolean checkSolution(){
        if(!initializationStatus) return false;
        for(Block b: blocchi)
            if(!b.isValid()) return false;
        return true;
    }

    public Collection<Block> getBlocks(){
        Collection<Block> blocks = new LinkedList<>();
        for(AttachedBlock attachedBlock: blocchi){
            blocks.add(attachedBlock);
        }
        return blocks;
    }
    public Cell getCell(int i, int j){
        return celle[i][j];
    }
    public Collection<Cell> getNotInBlockCells(){
        return notInBlocco;
    }
    /*
    public void setCell(Cell cell){
        celle[cell.getRow()][cell.getCol()] = cell;
    }
    */
    public int getN(){
        return N;
    }

}
