package com.programming.view;

import com.programming.memento.Memento;

import javax.swing.*;

public class SolutionView {
    private final BoardView boardView;
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
