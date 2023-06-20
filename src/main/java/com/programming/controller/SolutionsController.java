package com.programming.controller;

import com.programming.memento.Memento;
import com.programming.model.Board;
import com.programming.view.BoardView;
import com.programming.view.SolutionView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class SolutionsController{
    private JPanel panel = new JPanel(new BorderLayout());
    private SolutionView view;
    private ArrayList<Memento> solutions;
    //private ListIterator<Memento> listIterator;
    private int current =-1;
    public SolutionsController(BoardView bw, int n){
        bw.clearBoard();
        view = new SolutionView(bw);
        solutions = new ArrayList<>();
        KenKenSolver solver = bw.getSolver(n,solutions);
        solver.risolvi();
        //listIterator=solutions.listIterator();
        if(solutions.isEmpty()) System.out.println("Non sono state trovate soluzioni.");
        else{
            System.out.println("Trovate "+solutions.size()+" soluzioni.");
            nextSolution();
        }
    }
    public boolean hasNextSolution(){
        return current<solutions.size()-1;
    }
    public boolean hasPreviousSolution(){
        return current>0;
    }
    public void nextSolution(){
        if(!hasNextSolution()) throw new NoSuchElementException();
        current++;
        view.displaySolution(solutions.get(current));
    }
    public void previousSolution(){
        if(!hasPreviousSolution()) throw new NoSuchElementException();
        current--;
        view.displaySolution(solutions.get(current));
    }
    public JPanel getPanel(){
        return view.getView();
    }
    public static void main(String... args) throws Exception{
        BoardView b = new BoardView(Board.openBoard(new File("template1.json")));
        b.startGame();
        //BoardView b2 = new BoardView(b.getTemplate());
        //LinkedList<Memento> soluzioni = new LinkedList<>();
        //KenKenSolver solver = b2.getSolver(4, soluzioni);
        //solver.risolvi();
        //System.out.println(soluzioni.toString());
        SolutionsController controller = new SolutionsController(new BoardView(b.getTemplate()),4);
    }
}
