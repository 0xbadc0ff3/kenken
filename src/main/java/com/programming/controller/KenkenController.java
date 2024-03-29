package com.programming.controller;

import com.programming.Utility;
import com.programming.memento.Memento;
import com.programming.model.Board;
import com.programming.model.BoardState;
import com.programming.model.Cell;
import com.programming.model.Operation;
import com.programming.view.BlockView;
import com.programming.view.BoardView;
import com.programming.view.CellState;
import com.programming.view.CellView;
import jdk.jshell.execution.Util;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class KenkenController {

    private final BoardView boardView;
    //Per i listener:
    private boolean isAdding=false;
    private boolean saved = true;
    private boolean checking = false;
    private BlockView addingTo = null;
    private Collection<CellView> canAdd = new LinkedList<>();
    private String fileName = "";
    public KenkenController(File file) throws Exception{
        boardView = new BoardView(Board.openBoard(file));
        for(CellView cw : boardView.getCellViews()) cw.setMenu(createMenu(cw));
    }
    public KenkenController(int n){
        boardView = new BoardView(new Board(n));
        for(CellView cw : boardView.getCellViews()) cw.setMenu(createMenu(cw));
    }
    public KenkenController(){
        boardView = BoardView.blankBoard();
    }

    public JPanel getBoardView(){
        return boardView;
    }

    private Collection<CellView> getAdiacenti(Collection<CellView> cellViews){
        LinkedList<CellView> adiacenti = new LinkedList<>();
        for(CellView cellView: cellViews) {
            int i = cellView.getCell().getRow(), j = cellView.getCell().getCol();
            //Controllo la cella sopra.
            Cell toCheck = new Cell(i - 1, j);
            if (boardView.getNotInBlockCells().contains(toCheck)) {
                adiacenti.add(boardView.getCellView(toCheck));
            }
            //Sinistra
            toCheck = new Cell(i, j - 1);
            if (boardView.getNotInBlockCells().contains(toCheck)) adiacenti.add(boardView.getCellView(toCheck));
            //Sotto
            toCheck = new Cell(i + 1, j);
            if (boardView.getNotInBlockCells().contains(toCheck)) adiacenti.add(boardView.getCellView(toCheck));
            //Destra
            toCheck = new Cell(i, j + 1);
            if (boardView.getNotInBlockCells().contains(toCheck)) adiacenti.add(boardView.getCellView(toCheck));
        }
        return adiacenti;
    }

    private void updateMenu(){//resetta il menu delle celle in seguito a un'operazione d' inserimento.
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
                if(addingTo.getOperation()==Operation.DIV || addingTo.getOperation()==Operation.SUB){
                    JOptionPane.showMessageDialog(boardView,"Error: Cannot add a third cell in a block with binary operation.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addingTo.addCell(cellView);
                addingTo.deselectBlock();
                isAdding = false; addingTo=null; canAdd.clear();
                updateMenu();
                saved=false;
                break;
            case "select":
                if(isAdding) execute(null, "deselect");
                isAdding=true;
                addingTo=cellView.getBlock();
                addingTo.selectBlock();
                assert canAdd.isEmpty();
                canAdd.addAll(getAdiacenti(addingTo.getCellViews()));
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
                int result;
                try{
                    result = Integer.parseInt(JOptionPane.showInputDialog(boardView, "Please provide the result which has to be obtained from the current block.", "Set Block Result", JOptionPane.PLAIN_MESSAGE));
                    cellView.getBlock().setVincolo(result);
                    saved=false;
                }catch (NumberFormatException nfe){
                    JOptionPane.showMessageDialog(boardView, "Provided value cannot be interpreted as an integer.","Error",JOptionPane.ERROR_MESSAGE);
                }catch (IllegalArgumentException illegalArgumentException){
                    JOptionPane.showMessageDialog(boardView, "Provided value must be greater than zero.","Error",JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "set-operation-add":
                cellView.getBlock().setOperation(Operation.ADD); saved=false; break;
            case "set-operation-sub":
                cellView.getBlock().setOperation(Operation.SUB); saved=false; break;
            case "set-operation-mul":
                cellView.getBlock().setOperation(Operation.MUL); saved=false; break;
            case "set-operation-div":
                cellView.getBlock().setOperation(Operation.DIV); saved=false; break;
            case "new-block":
                boardView.createBlock().addCell(cellView);
                cellView.setMenu(createMenu(cellView));
                saved=false;
                break;
            case "remove-block":
                if(cellView.getBlock().isSelected()) execute(null, "deselect");
                boardView.removeBlock(cellView.getBlock());
                updateMenu();
                saved=false;
                break;
            default: System.out.println("Comando non interpretato: "+actionCommand);
        }
    }

    private JPopupMenu createMenu(CellView cellView){

        ActionListener actionListener = e -> execute(cellView,e.getActionCommand());

        JPopupMenu menu = new JPopupMenu("Action to perform:");
        if(boardView.getState()== BoardState.SETTING){
            if(cellView.hasBlock()){
                if(isAdding && addingTo.contains(cellView.getCell())) {
                    JMenuItem deselectBlock = new JMenuItem("Deselect Block");
                    deselectBlock.setActionCommand("deselect");
                    deselectBlock.addActionListener(actionListener);
                    menu.add(deselectBlock);
                }else {
                    if (!(cellView.getBlock().isFull() || boardView.getNotInBlockCells().isEmpty())) {
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
                //System.out.println(cellView.getBlock().getCurrentSize());
                if(cellView.getBlock().getCurrentSize()>1) {
                    JMenu setOperation = new JMenu("Set Operation");
                    JMenuItem add = new JMenuItem("+");
                    add.setActionCommand("set-operation-add");
                    add.addActionListener(actionListener);
                    setOperation.add(add);

                    JMenuItem mul = new JMenuItem("x");
                    mul.setActionCommand("set-operation-mul");
                    mul.addActionListener(actionListener);
                    setOperation.add(mul);

                    if(cellView.getBlock().getCurrentSize()==2) {
                        JMenuItem sub = new JMenuItem("-");
                        sub.setActionCommand("set-operation-sub");
                        sub.addActionListener(actionListener);
                        setOperation.add(sub);

                        JMenuItem div = new JMenuItem("/");
                        div.setActionCommand("set-operation-div");
                        div.addActionListener(actionListener);
                        setOperation.add(div);
                    }

                    menu.add(setOperation);
                }
                JMenuItem remove = new JMenuItem("Remove Block");
                remove.setActionCommand("remove-block");
                remove.addActionListener(actionListener);
                remove.setForeground(Utility.WARNING_COLOR);
                menu.add(remove);

            }else {
                JMenuItem newBlock = new JMenuItem("Create new Block");
                newBlock.setActionCommand("new-block");
                newBlock.addActionListener(actionListener);
                menu.add(newBlock);

            }
        }else if(boardView.getState()==BoardState.PLAYING){
            for(int i=1; i<=boardView.getN(); i++){
                JMenuItem menuItem = new JMenuItem(""+i);
                menuItem.setActionCommand(""+i);
                menuItem.addActionListener(
                        e -> {
                            try{
                                int chosen = Integer.parseInt(e.getActionCommand());
                                if(chosen<1 || chosen> boardView.getN()) throw new RuntimeException("Numero scelto non valido.");//ridondante
                                int previousValue = cellView.getValue();
                                cellView.setValue(chosen);
                                if(checking) check(cellView, previousValue);
                                saved=false;
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

    public boolean startGame(){
        if(boardView.startGame()){
            LinkedList<Memento> solutions = new LinkedList<>();
            new BoardView(boardView.getTemplate()).getSolver(1,solutions).risolvi();
            if(solutions.isEmpty()) {
                int input = JOptionPane.showConfirmDialog(boardView,"No solution was found for this template.\nDo you wanna play anyways?","Warning!",JOptionPane.YES_NO_OPTION);
                if(input!=JOptionPane.YES_OPTION){
                    boardView.edit();
                    return true;
                }
            }
            for(CellView cellView: boardView.getCellViews()) cellView.setMenu(createMenu(cellView));
            return true;
        }
        return false;
    }
    public void editBoard(){
        boardView.edit();
        for(CellView cellView: boardView.getCellViews()) cellView.setMenu(createMenu(cellView));
    }
    public void clearBoard(){
        boardView.clearBoard();
        for(CellView cellView: boardView.getCellViews()) cellView.setMenu(createMenu(cellView));
    }
    public void findSolutions(int n){
        if(boardView.getState()!=BoardState.PLAYING) {
            JOptionPane.showMessageDialog(boardView, "Board is not completely setted yet.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFrame solutionsFrame = new JFrame("Solutions");
        JTextField navigation = new JTextField("0/0",8);
        navigation.setForeground(Utility.DEFAULT_COLOR);
        navigation.setEditable(false);
        navigation.setVisible(true);

        SolutionsController solutionsController = new SolutionsController(new BoardView(boardView.getTemplate()), n);
        if(solutionsController.getTotalSolutions()>0) navigation.setText("1/"+solutionsController.getTotalSolutions());

        solutionsFrame.add(solutionsController.getPanel(), BorderLayout.CENTER);
        JButton previous = new JButton("Previous"), next = new JButton("Next");
        previous.setEnabled(false); next.setEnabled(solutionsController.hasNextSolution());
        ActionListener actionListener = e -> {
            if(e.getSource()==previous){
                solutionsController.previousSolution();
                previous.setEnabled(solutionsController.hasPreviousSolution());
                next.setEnabled(solutionsController.hasNextSolution());
                navigation.setText(solutionsController.getCurrentSolutionNumber()+"/"+solutionsController.getTotalSolutions());
            }else if(e.getSource()==next){
                solutionsController.nextSolution();
                next.setEnabled(solutionsController.hasNextSolution());
                previous.setEnabled(solutionsController.hasPreviousSolution());
                navigation.setText(solutionsController.getCurrentSolutionNumber()+"/"+solutionsController.getTotalSolutions());
            }else{
                System.out.println("Unknown event source: "+e.getSource());
            }
        };
        next.addActionListener(actionListener); previous.addActionListener(actionListener);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(previous); bottomPanel.add(next);
        bottomPanel.setVisible(true);
        bottomPanel.add(navigation);
        solutionsFrame.add(bottomPanel,BorderLayout.SOUTH);

        solutionsFrame.setIconImage(Utility.APP_LOGO);
        solutionsFrame.setSize(Utility.WIDTH,Utility.HEIGHT);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        solutionsFrame.setLocation(dim.width/2-solutionsFrame.getSize().width/2, dim.height/2-solutionsFrame.getSize().height/2);

        boardView.setVisible(false);

        solutionsFrame.setVisible(true);
        solutionsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                boardView.setVisible(true);
            }
        });

    }
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }
    public static File getFileAfterSaving(File file){
        String extension = getFileExtension(file);
        if(extension.equals(".json")) return file;
        String path = file.getAbsolutePath();
        path = path+".json";
        file = new File(path);
        return file;
    }
    public void save(File file){
        String json = boardView.toJSON();
        file = getFileAfterSaving(file);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(json);
            writer.flush();
            writer.close();
            //System.out.println("File salvato");
            saved = true;
        }catch (IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something went wrong while trying to save the file.","Error",JOptionPane.ERROR_MESSAGE);
        }

    }
    public void save(){
        if(fileName.equals("")) throw new RuntimeException("No file name was specified.");
        this.save(new File(fileName));
    }
    public String toJSON(){
        return boardView.toJSON();
    }
    public boolean isSaved(){
        return saved;
    }
    public BoardState getState(){
        BoardState state;
        try{
            state = boardView.getState();
        }catch(NullPointerException e){
            state = null;
        }
        return state;
    }
    public boolean isChecking() { return checking; }
    public void setChecking(boolean value) {
        checking=value;
        if(checking){
            for(BlockView blockView: boardView.getBlocks()){
                checkBlock(blockView);
            }
            for(int i=0;i< boardView.getN();i++){
                List<CellView> row = boardView.getRow(i);
                LinkedList<CellView>[] bucket = new LinkedList[boardView.getN()];
                for(CellView cellView: row){
                    int val = cellView.getValue();
                    if(val<=0) continue;
                    if (bucket[val-1] == null) {
                        LinkedList<CellView> linkedList = new LinkedList<>();
                        linkedList.add(cellView);
                        bucket[val-1] = linkedList;
                    }
                    else{
                        bucket[val-1].add(cellView);
                    }
                }
                for(LinkedList<CellView> list: bucket){
                    if(list !=null && list.size()>1){
                        for(CellView cellView: list){
                            cellView.setState(CellState.NOT_VALID_REPEATED_VALUE);
                        }
                    }
                }
            }
            for(int j=0;j< boardView.getN();j++){
                List<CellView> col = boardView.getCol(j);
                LinkedList<CellView>[] bucket = new LinkedList[boardView.getN()];
                for(CellView cellView: col){
                    int val = cellView.getValue();
                    if(val<=0) continue;
                    if (bucket[val-1] == null) {
                        LinkedList<CellView> linkedList = new LinkedList<>();
                        linkedList.add(cellView);
                        bucket[val-1]= linkedList;
                    }
                    else{
                        bucket[val-1].add(cellView);
                    }
                }
                for(LinkedList<CellView> list: bucket){
                    if(list!=null && list.size()>1){
                        for(CellView cellView: list){
                            cellView.setState(CellState.NOT_VALID_REPEATED_VALUE);
                        }
                    }
                }
            }
            return;
        }
        for(CellView cellView: boardView.getCellViews()) cellView.setState(CellState.UNKOWN);
    }
    private void checkBlock(BlockView blockView){
        if(blockView.isValid())
            for(CellView cw: blockView.getCellViews()) {
                if(cw.getState() != CellState.NOT_VALID_REPEATED_VALUE)
                    cw.setState(CellState.VALID);
            }
        else {
            boolean hasValues = true;
            for(CellView cw: blockView.getCellViews()) if(cw.getValue()<=0) hasValues=false;
            if(hasValues) for(CellView cw: blockView.getCellViews()) cw.setState(CellState.NOT_VALID_BLOCK);
        }
    }

    private void check(CellView cellView, int previousValue){
        cellView.setState(CellState.UNKOWN);
        List<CellView> toCheck = boardView.getRow(cellView.getRow());
        LinkedList<CellView>[] bucket = new LinkedList[boardView.getN()];
        for(CellView cw : toCheck){
            int val = cw.getValue();
            if(val<=0) continue;
            if (bucket[val-1] == null) {
                LinkedList<CellView> linkedList = new LinkedList<>();
                linkedList.add(cw);
                bucket[val-1] = linkedList;
            }
            else{
                bucket[val-1].add(cw);
            }
        }
        //Aggiorno eventuali celle rimaste evidenziate a causa di precedenti errori:
        if(previousValue>0 && bucket[previousValue-1]!=null && bucket[previousValue-1].size()==1) {
            CellView current = bucket[previousValue-1].getFirst();
            if(current.getBlock().isValid()) current.setState(CellState.VALID);
            else {
               if(current.getState()!=CellState.NOT_VALID_BLOCK) current.setState(CellState.UNKOWN);
            }
        }
        for(LinkedList<CellView> list: bucket){
            if(list != null && list.size()>1){
                for(CellView cw: list){
                    cw.setState(CellState.NOT_VALID_REPEATED_VALUE);
                }
            }
        }
        bucket = new LinkedList[boardView.getN()];
        toCheck = boardView.getCol(cellView.getCol());
        for(CellView cw : toCheck){
            int val = cw.getValue();
            if(val<=0) continue;
            if (bucket[val-1] == null) {
                LinkedList<CellView> linkedList = new LinkedList<>();
                linkedList.add(cw);
                bucket[val-1]= linkedList;
            }
            else{
                bucket[val-1].add(cw);
            }
        }
        //Aggiorno eventuali celle rimaste evidenziate a causa di precedenti errori:
        if(previousValue>0 && bucket[previousValue-1]!=null && bucket[previousValue-1].size()==1) {
            CellView current = bucket[previousValue-1].getFirst();
            if(current.getBlock().isValid()) current.setState(CellState.VALID);
            else {
                if(current.getState()!=CellState.NOT_VALID_BLOCK) current.setState(CellState.UNKOWN);
            }
        }
        for(LinkedList<CellView> list: bucket){
            if(list != null && list.size()>1){
                for(CellView cw: list){
                    cw.setState(CellState.NOT_VALID_REPEATED_VALUE);
                }
            }
        }
        checkBlock(cellView.getBlock());
    }

    public void setNewBoard(int n){
        if(n<3 || n> Utility.MAX_BOARD_SIZE) throw new IllegalArgumentException("Dimensione nuova board non valida.");
        boardView.changeBoard(new Board(n));
        for(CellView cellView : boardView.getCellViews()) cellView.setMenu(createMenu(cellView));
        canAdd = new LinkedList<>();
        addingTo = null;
        isAdding = false;

        for(CellView cw : boardView.getCellViews()) cw.setMenu(createMenu(cw));
    }
    public void openBoard(File jsonFile) throws Exception{
        boardView.changeBoard(Board.openBoard(jsonFile));
        canAdd = new LinkedList<>();
        addingTo = null;
        isAdding = false;

        for(CellView cw : boardView.getCellViews()) cw.setMenu(createMenu(cw));
    }
    public String getFileName(){
        return fileName;
    }
    public void setFileName(String name){
        fileName=name;
    }
    public boolean hasFileName(){
        return !fileName.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KenkenController that = (KenkenController) o;
        return boardView.equals(that.boardView);
    }

}
