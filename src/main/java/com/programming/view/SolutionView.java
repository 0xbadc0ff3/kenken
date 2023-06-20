package com.programming.view;

import com.programming.memento.Memento;
import com.programming.model.Board;

import javax.swing.*;
import java.io.IOException;

public class SolutionView {
    private BoardView boardView;
    public SolutionView(BoardView template){
        this.boardView = template;
    }
    public JPanel getView(){
        return boardView;
    }
    public void displaySolution(Memento memento){
        boardView.restore(memento);
    }
}
