package com.programming.model;

import com.programming.Utility;

import javax.swing.*;
import java.io.*;
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
            if(!Board.this.notInBlocco.contains(cell)) throw new RuntimeException("Cella "+cell.getRow()+" "+cell.getCol()+" già appartenente ad un blocco.");
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
    private final int N;
    private BoardState state;
    private Cell[][] celle;
    private Collection<AttachedBlock> blocchi;
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

    public Collection<Block> getBlocks(){
        return Collections.unmodifiableCollection(blocchi);
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
    public String toJSON(){
        StringBuilder sb = new StringBuilder(200);
        sb.append("{\"N\":"+N+",\"state\":"+(this.state==BoardState.SETTING?0:1));
        if(this.state==BoardState.PLAYING){
            sb.append(",values:[");
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
        /*
        sb.append("],\"notInBlock\":[");
        Iterator<Cell> it = notInBlocco.iterator();
        while(it.hasNext()){
            Cell cell = it.next();
            sb.append("{\"i\":"+cell.getRow()+",\"j\":"+cell.getCol()+"}");
            if(it.hasNext()) sb.append(",");
        }
        */
        sb.append("]}");
        return sb.toString();
    }
    public static Board openBoard(File jsonFile) throws Exception{
        if(!jsonFile.exists()) throw new FileNotFoundException();
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        StringBuilder text = new StringBuilder(200);
        String line;
        while((line=reader.readLine()) != null){
            text.append(line);
        }
        reader.close();
        StringTokenizer stringTokenizer = new StringTokenizer(text.toString(),":\"\t\n{}[], ");
        text=null;//libero memoria.
        String currentToken = stringTokenizer.nextToken().strip();
        System.out.println(currentToken);
        if(!currentToken.equals("N")) throw new Exception("File not valid. Found "+currentToken+" instead of N");
        currentToken = stringTokenizer.nextToken().strip();
        Board opened = new Board(Integer.parseInt(currentToken));
        currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("state")) throw new Exception("File not valid.");
        currentToken = stringTokenizer.nextToken().strip();
        int state = Integer.parseInt(currentToken);
        if(state==0) {
            opened.state=BoardState.SETTING;//ridondante
        }
        else if(state==1) {
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("values")) throw new Exception("File not valid.");
            for(int i=0; i<opened.N;i++){
                for(int j=0; j<opened.N;j++){
                    currentToken = stringTokenizer.nextToken().strip();
                    opened.celle[i][j].setValue(Integer.parseInt(currentToken));
                }
            }
        }
        else throw new Exception("Unknown Board state.");
        currentToken = stringTokenizer.nextToken().strip();
        if(!currentToken.equals("blocks")) throw new Exception("File not valid.");
        //currentToken = stringTokenizer.nextToken();

        while(stringTokenizer.hasMoreTokens()){
            //Un'iterazione equivale a leggere un blocco.
            Block current = new ConcreteBlock();
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("size")) throw new Exception("File not valid.");
            currentToken = stringTokenizer.nextToken().strip();
            int size = Integer.parseInt(currentToken);
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("result")) throw new Exception("File not valid.");
            currentToken = stringTokenizer.nextToken().strip();
            int result =Integer.parseInt(currentToken);
            if(result>0)current.setVincolo(result);
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("operation")) throw new Exception("File not valid.");
            currentToken = stringTokenizer.nextToken().strip().toUpperCase();
            switch (currentToken){
                case "ADD": current.setOperation(Operation.ADD); break;
                case "SUB": current.setOperation(Operation.SUB); break;
                case "MUL": current.setOperation(Operation.MUL); break;
                case "DIV": current.setOperation(Operation.DIV); break;
                case "NULL": break;
                default: throw new Exception("Operation "+currentToken+" not valid");
            }
            currentToken = stringTokenizer.nextToken().strip();
            if(!currentToken.equals("cells")) throw new Exception("File not valid.");
            for(int a=0;a<size;a++){
                int i,j;
                currentToken = stringTokenizer.nextToken().strip();
                if(!currentToken.equals("i")) throw new Exception("File not valid.");
                currentToken = stringTokenizer.nextToken().strip();
                i=Integer.parseInt(currentToken);
                currentToken = stringTokenizer.nextToken().strip();
                if(!currentToken.equals("j")) throw new Exception("File not valid.");
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
    public static void main(String... strings){
        JFrame frame = new JFrame();

        JFileChooser chooser = new JFileChooser();
        //frame.add(chooser);
        frame.setVisible(true);
        //chooser.addActionListener(e -> {
        //    if(e.)
        //});
        chooser.showOpenDialog(frame);
        File file =chooser.getSelectedFile();
        try{
            openBoard(file);
        }catch (Exception e){

        }
    }
}
