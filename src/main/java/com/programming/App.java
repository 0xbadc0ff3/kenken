package com.programming;

import com.programming.model.Cell;
import com.programming.view.BlockView;
import com.programming.view.BoardView;
import com.programming.view.CellView;

import javax.swing.*;
import java.util.Collection;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame finestra = new JFrame("KenKen");
        BoardView board = new BoardView(5);


        finestra.setSize(900,900);
        BlockView nuovo = board.createBlock();
        Collection<CellView> cellViews = board.getCellViews();
        Cell uno = new Cell(0,0);
        Cell due = new Cell(0,1);
        Cell tre = new Cell(1,0);
        CellView uno1=null,due2=null,tre3=null;
        for(CellView cellView: cellViews){
            if(cellView.getCell().equals(uno)) uno1 = cellView;
            if(cellView.getCell().equals(due)) due2 = cellView;
            if(cellView.getCell().equals(tre)) tre3 = cellView;
        }
        tre3.setValue(3);
        nuovo.addCell(uno1); nuovo.addCell(due2); nuovo.addCell(tre3);
        uno1.setValue(1);
        due2.setValue(2);
        finestra.add(board);
        finestra.setVisible(true);
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
