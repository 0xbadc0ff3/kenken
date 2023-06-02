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
            assert Board.this.state==BoardState.PLAYING;
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
            if( Board.this.state==BoardState.PLAYING ) throw new RuntimeException("Impossibile modificare la board durante la partita");
            boolean status = block.remove(cell);
            if(status){
                Board.this.notInBlocco.add(cell);
                Board.this.initializationStatus=false;
            }
            return status;
            //TODO?
        }

        @Override
        public void setVincolo(int vincolo) {
            if( Board.this.state==BoardState.PLAYING ) throw new RuntimeException("Impossibile modificare la board durante la partita");
            block.setVincolo(vincolo);
        }

        @Override
        public void setOperation(Operation operation) {
            if( Board.this.state==BoardState.PLAYING ) throw new RuntimeException("Impossibile modificare la board durante la partita");
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
        public boolean isFull() {
            return block.isFull();
        }

        @Override
        public Collection<Cell> getCells() {
            return Collections.unmodifiableCollection(block.getCells());
        }

        @Override
        public Iterator<Cell> iterator() {
            return new BlockIterator();
        }
        private class BlockIterator implements Iterator<Cell>{//Voglio bloccare l'operazione di Remove.
        private Iterator<Cell> iterator = block.iterator();
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Cell next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Rimozione tramite iteratore non supportata.");
            }
        }
    }
    private final int N;
    private BoardState state;
    private Cell[][] celle;
    private Collection<AttachedBlock> blocchi;
    private boolean initializationStatus = false;//indica se ogni cella appartiene ad un blocco. (corrisponde ad notInBlocco.size()==0)
    private Set<Cell> notInBlocco;

    public Board(int n){
        if(n<3) throw new IllegalArgumentException("Board troppo piccola.");
        this.N = n;
        this.state = BoardState.SETTING;
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
        if(initializationStatus || state==BoardState.PLAYING) throw new RuntimeException("Impossibile creare un nuovo blocco.");
        AttachedBlock attachedBlock = new AttachedBlock(block);
        blocchi.add( attachedBlock );
        for(Cell cell: attachedBlock) notInBlocco.remove(cell);
        initializationStatus = notInBlocco.isEmpty();
        return attachedBlock;
    }
    public void removeBlock(Block block){
        if(state==BoardState.PLAYING) throw new RuntimeException("Impossibile modificare la board durante la partita.");
        if(blocchi.remove(block)){
            initializationStatus=false;
            for(Cell cell: block) notInBlocco.add(cell);
        }
    }
    public boolean checkSolution(){
        if(state!=BoardState.PLAYING) return false;
        for(Block b: blocchi)
            if(!b.isValid()) return false;
        return true;
    }

    public Collection<Block> getBlocks(){
        return Collections.unmodifiableCollection(blocchi);
    }
    public Cell getCell(int i, int j){
        return celle[i][j];
    }
    public Collection<Cell> getNotInBlockCells(){
        return Collections.unmodifiableCollection(notInBlocco);
    }
    /*
    public void setCell(Cell cell){
        celle[cell.getRow()][cell.getCol()] = cell;
    }
    */
    public int getN(){
        return N;
    }

    private boolean isReadyToPlay(){
        if(!initializationStatus) return false;
        for(Block block: blocchi){
            if(!block.hasConstraints()) return false;
        }
        return true;
    }
    public boolean startGame(){
        if(!isReadyToPlay()) return false;
        this.state=BoardState.PLAYING;
        return true;
    }
    public void edit(){
        this.state = BoardState.SETTING;
    }
    public void editWithFieldReset(){
        this.state = BoardState.SETTING;
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                celle[i][j].setValue(0);
            }
        }
    }
    public BoardState getState(){
        return state;
    }

}
