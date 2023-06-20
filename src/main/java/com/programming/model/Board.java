package com.programming.model;

import com.programming.Utility;
import com.programming.memento.Memento;
import com.programming.memento.Originator;

import java.io.*;
import java.util.*;

public class Board implements Originator {
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
            if(!Board.this.notInBlocco.contains(cell)) throw new RuntimeException("Cella "+cell.getRow()+" "+cell.getCol()+" già appartenente ad un blocco.");
            assert Board.this.state==BoardState.SETTING;
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
        public boolean isAttached() {
            return true;
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
    private static class BoardMemento implements Memento {
        private int[][] values;
        private int hash;
        public BoardMemento(Board b){
            values = new int[b.N][b.N];
            for(int i=0;i<values.length;i++)
                for(int j=0;j<values[0].length;j++)
                    values[i][j] = b.celle[i][j].getValue();
            hash = b.hashCode();
            //L'hash NON tiene conto del valore delle celle! Viene utilizzato per confrontare la struttura interna della board.
        }
        private int[][] getValues(){
            return values;
        }
        private int getHash(){
            return hash;
        }
    }
    private final int N;
    private BoardState state;
    private Cell[][] celle;
    private List<AttachedBlock> blocchi;
    private boolean initializationStatus = false;//indica se ogni cella appartiene ad un blocco. (corrisponde ad notInBlocco.size()==0)
    private Set<Cell> notInBlocco;

    public Board(int n){
        if(n<3) throw new IllegalArgumentException("Board troppo piccola.");
        if(n> Utility.MAX_BOARD_SIZE) throw new IllegalArgumentException("Board troppo grande");
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
    public Board(Board b, boolean copyValues){
        blocchi = new LinkedList<>();
        notInBlocco = new HashSet<>();
        N = b.N;
        celle = new Cell[N][N];
        this.state = BoardState.SETTING;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                celle[i][j] = new Cell(i, j);
                notInBlocco.add(celle[i][j]);
                if(copyValues) celle[i][j].setValue(b.celle[i][j].getValue());
            }
        //for(Cell cell: b.notInBlocco) notInBlocco.add(celle[cell.getRow()][cell.getCol()]);
        for(AttachedBlock ab : b.blocchi){
            AttachedBlock block = new AttachedBlock(new ConcreteBlock());
            blocchi.add(block);
            for(Cell c: ab) {
                block.add(celle[c.getRow()][c.getCol()]);
            }
            block.setOperation(ab.getOperation()); block.setVincolo(ab.getVincolo());
        }
        initializationStatus=notInBlocco.isEmpty();
        this.state=b.state;
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
        //TODO CONTROLLARE VALORI CELLE!!!!!
        if(state!=BoardState.PLAYING) return false;
        for(Block b: blocchi)
            if(!b.isValid()) return false;
        return true;
    }

    public List<Block> getBlocks(){
        return Collections.unmodifiableList(blocchi);
    }
    public Cell getCell(int i, int j){
        return celle[i][j];
    }
    public Collection<Cell> getNotInBlockCells(){
        return Collections.unmodifiableCollection(notInBlocco);
    }
    public int getN(){
        return N;
    }
    public boolean isReadyToPlay(){
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
    public String toJSON(){
        StringBuilder sb = new StringBuilder(200);
        sb.append("{\"N\":"+N+",\"state\":"+(this.state==BoardState.SETTING?0:1));
        if(this.state==BoardState.PLAYING){
            sb.append(",\"values\":[");
            for(int i=0; i<N;i++)
                for(int j=0; j<N; j++){
                    sb.append(celle[i][j].getValue()+",");
                }
            sb.delete(sb.length()-1,sb.length());
            sb.append("]");
        }
        sb.append(",\"blocks\":[");
        Iterator<AttachedBlock> iterator = blocchi.iterator();
        while(iterator.hasNext()){
            Block block = iterator.next();
            sb.append("{\"size\":"+block.getCurrentSize()+"," +"\"result\":"+block.getVincolo()+",\"operation\":\""+block.getOperation()+"\","+
                    "\"cells\":[");
            Iterator<Cell> it = block.iterator();
            while(it.hasNext()){
                Cell cell = it.next();
                sb.append("{\"i\":"+cell.getRow()+",\"j\":"+cell.getCol()+"}");
                if(it.hasNext()) sb.append(",");
            }
            sb.append("]}");
            if(iterator.hasNext()) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }
    @Override
    public Memento takeSnapshot(){
        if(state!=BoardState.PLAYING) throw new IllegalStateException("Cannot take a snapshot while board is not configurated!");
        return new BoardMemento(this);
    }
    @Override
    public void restore(Memento memento){
        if(!(memento instanceof BoardMemento)) throw new IllegalArgumentException("Invalid Board Snapshot.");
        BoardMemento boardMemento = (BoardMemento) memento;
        if(boardMemento.getHash()!=this.hashCode()) throw new IllegalArgumentException("Cannot restore from a different configuration.");
        //Assunzione: L'hash non presenta collisioni.
        //Controllo "lasco", nel caso in cui si presenti una collisione dell'hash il programma NON cade in errore, ma permette un uso improprio.
        int[][] values = boardMemento.getValues();
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                celle[i][j].setValue(values[i][j]);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return N == board.N && initializationStatus == board.initializationStatus && state == board.state && equalsCelle(celle, board.celle) && Objects.equals(blocchi, board.blocchi) && notInBlocco.equals(board.notInBlocco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(N, state, blocchi, initializationStatus, notInBlocco);
    }
    private boolean equalsCelle(Cell[][] c1, Cell[][] c2) {
        if(c1.length!=c2.length) return false;
        if(c1[0]==null || c2[0]==null || c1[0].length!=c2[0].length) return false;//ridondante, essendo matrici quadrate.
        for(int i=0;i<c1.length;i++)
            for(int j=0;j<c1[0].length;j++){
                if(c1[i][j].getValue()!=c2[i][j].getValue()) return false;
            }
        return true;
    }

    public static Board openBoard(String json) throws IOException{
        StringTokenizer stringTokenizer = new StringTokenizer(json,":\"\t\n{}[], ");
        String currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("N")) throw new IOException("Format not valid. Found "+currentToken+" instead of N");
        currentToken = stringTokenizer.nextToken().strip();
        Board opened = new Board(Integer.parseInt(currentToken));
        currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("state")) throw new IOException("Format not valid.");
        currentToken = stringTokenizer.nextToken().strip();
        int state = Integer.parseInt(currentToken);
        if(state==0) {
            opened.state=BoardState.SETTING;//ridondante
        }
        else if(state==1) {
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("values")) throw new IOException("Format not valid.");
            for(int i=0; i<opened.N;i++){
                for(int j=0; j<opened.N;j++){
                    currentToken = stringTokenizer.nextToken().strip();
                    opened.celle[i][j].setValue(Integer.parseInt(currentToken));
                }
            }
        }
        else throw new IOException("Unknown Board state.");
        currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("blocks")) throw new IOException("Format not valid.");
        //currentToken = stringTokenizer.nextToken();

        while(stringTokenizer.hasMoreTokens()){
            //Un'iterazione equivale a leggere un blocco.
            Block current = new ConcreteBlock();
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("size")) throw new IOException("Format not valid.");
            currentToken = stringTokenizer.nextToken().strip();
            int size = Integer.parseInt(currentToken);
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("result")) throw new IOException("Format not valid.");
            currentToken = stringTokenizer.nextToken().strip();
            int result =Integer.parseInt(currentToken);
            if(result>0)current.setVincolo(result);
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("operation")) throw new IOException("Format not valid.");
            currentToken = stringTokenizer.nextToken().strip().toUpperCase();
            switch (currentToken){
                case "ADD": current.setOperation(Operation.ADD); break;
                case "SUB": current.setOperation(Operation.SUB); break;
                case "MUL": current.setOperation(Operation.MUL); break;
                case "DIV": current.setOperation(Operation.DIV); break;
                case "NULL": break;
                default: throw new IOException("Operation "+currentToken+" not valid");
            }
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("cells")) throw new IOException("Format not valid.");
            for(int a=0;a<size;a++){
                int i,j;
                currentToken = stringTokenizer.nextToken().strip();
                if(!currentToken.equals("i")) throw new IOException("Format not valid.");
                currentToken = stringTokenizer.nextToken().strip();
                i=Integer.parseInt(currentToken);
                currentToken = stringTokenizer.nextToken().strip();
                if(!currentToken.equals("j")) throw new IOException("Format not valid.");
                currentToken = stringTokenizer.nextToken().strip();
                j=Integer.parseInt(currentToken);
                current.add(opened.getCell(i,j));
            }
            opened.attachBlock(current);
            //currentToken = stringTokenizer.nextToken().strip();//size se c'è un altro blocco, notInBlock altrimenti
        }
        if(state==1) opened.state=BoardState.PLAYING;
        return opened;
    }

    public static Board openBoard(File jsonFile) throws IOException{
        if(!jsonFile.exists()) throw new FileNotFoundException();
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        StringBuilder text = new StringBuilder(200);
        String line;
        while((line=reader.readLine()) != null){
            text.append(line);
        }
        reader.close();
        return openBoard(text.toString());

    }
}
