package com.programming;

import com.programming.controller.KenkenController;
import com.programming.model.BoardState;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;


public class App
{
    private static boolean exitConsent(JFrame parent){
        int chosen = JOptionPane.showConfirmDialog(parent,"Unsaved changes may be lost. Do you want to continue?","Warning",JOptionPane.YES_NO_OPTION);
        return chosen==JOptionPane.YES_OPTION;
    }

    public static void main( String[] args )
    {
        //Set Dock Icon for macOS
        try {
            Taskbar.getTaskbar().setIconImage(Utility.APP_LOGO);
        }catch (Exception e){
        }
        //Set Look & Feel
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (Exception e) {
        }


        JFrame window = new JFrame("KenKen");
        JPanel panel = new JPanel(new GridLayout());
        window.setIconImage(Utility.APP_LOGO);
        window.setSize(Utility.WIDTH,Utility.HEIGHT);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
        KenkenController controller = new KenkenController();
        panel.add(controller.getBoardView());
        window.add(panel);


        //Create Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem nuovo = new JMenuItem("New");
        nuovo.setActionCommand("new");
        JMenuItem open = new JMenuItem("Open");
        open.setActionCommand("open");
        JMenuItem save = new JMenuItem("Save");
        save.setActionCommand("save");
        JMenuItem saveAs = new JMenuItem("Save As");
        saveAs.setActionCommand("save-as");

        JMenu game = new JMenu("Game");
        JMenuItem startGame = new JMenuItem("Start game");
        startGame.setActionCommand("start");
        startGame.setEnabled(false);
        JMenuItem edit = new JMenuItem("Edit Board");
        edit.setEnabled(false);
        edit.setActionCommand("edit");
        JMenuItem clear = new JMenuItem("Clear board");
        clear.setActionCommand("clear");
        clear.setEnabled(false);
        JMenuItem enableCheck = new JMenuItem("Enable check");
        enableCheck.setActionCommand("check");
        enableCheck.setEnabled(false);
        JMenuItem findSolutions = new JMenuItem("Find Solutions");
        findSolutions.setActionCommand("find-solutions");
        findSolutions.setEnabled(false);
        JMenuItem quit = new JMenuItem("Quit Game");
        quit.setActionCommand("quit");

        JMenu about = new JMenu("About");
        JMenuItem help= new JMenuItem("Help");
        help.setActionCommand("help");
        String helpContent = "Software version: " +Utility.APP_VERSION+"\n"+
                "This software is designed to create KenKen's board template, store them, play them and find every valid solution." +
                "\nAuthor: Alberto Febbraro";


        //Action Listener:
        ActionListener actionListener = e -> {
            JFileChooser fileChooser;
            System.out.println("Ricevuto comando "+e.getActionCommand());
            switch (e.getActionCommand()){
                //File
                case "new":
                    if(!controller.isSaved())
                        if(!exitConsent(window)) break;
                    Integer[] sizes = {3,4,5,6};
                    int n = (int) JOptionPane.showInputDialog(window,"Select new Board size: ", "New Board",JOptionPane.PLAIN_MESSAGE,null,sizes,sizes[0]);
                    controller.setFileName("");
                    if(controller.getState()==BoardState.PLAYING) {
                        controller.editBoard();
                        edit.setEnabled(false);
                        clear.setEnabled(false);
                        startGame.setEnabled(true);
                        enableCheck.setEnabled(false);
                        findSolutions.setEnabled(false);
                    }
                    controller.setNewBoard(n);
                    startGame.setEnabled(true);
                    break;
                case "open":
                    if(!controller.isSaved())
                        if(!exitConsent(window)) break;
                    fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
                    if(fileChooser.showOpenDialog(window)!=JFileChooser.APPROVE_OPTION) break;
                    try{
                        controller.openBoard(fileChooser.getSelectedFile());
                        controller.setFileName(fileChooser.getSelectedFile().getName());
                        if(controller.getState() == BoardState.PLAYING){
                            edit.setEnabled(true);
                            clear.setEnabled(true);
                            startGame.setEnabled(false);
                            enableCheck.setEnabled(true);
                            findSolutions.setEnabled(true);
                        }
                        else if(controller.getState()==BoardState.SETTING){
                            edit.setEnabled(false);
                            clear.setEnabled(false);
                            startGame.setEnabled(true);
                            enableCheck.setEnabled(false);
                            findSolutions.setEnabled(false);
                        }
                    }catch(Exception exception){
                        JOptionPane.showMessageDialog(null, "File missing or not valid.", "Error", JOptionPane.ERROR_MESSAGE);
                        exception.printStackTrace();
                    }
                    startGame.setEnabled(true);
                    break;
                case "save":
                    if(controller.isSaved()) break;
                    if(controller.hasFileName()){
                        System.out.println("salvo");
                        controller.save();
                        break;
                    }
                case "save-as":
                    fileChooser = new JFileChooser(){
                        @Override
                        public void approveSelection(){
                            File f = KenkenController.getFileAfterSaving(getSelectedFile());
                            if(f.exists() && getDialogType() == SAVE_DIALOG){
                                int result = JOptionPane.showConfirmDialog(this,"This file already exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                                switch(result){
                                    case JOptionPane.YES_OPTION:
                                        super.approveSelection();
                                        return;
                                    case JOptionPane.NO_OPTION:
                                    case JOptionPane.CLOSED_OPTION:
                                        return;
                                    case JOptionPane.CANCEL_OPTION:
                                        cancelSelection();
                                        return;
                                }
                            }
                            super.approveSelection();
                        }
                    };
                    fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
                    if(fileChooser.showSaveDialog(window)!=JFileChooser.APPROVE_OPTION) break;
                    controller.save(fileChooser.getSelectedFile());
                    if(!controller.hasFileName()) controller.setFileName(fileChooser.getSelectedFile().getName());
                    break;
                //Game
                case "start":
                    if(!controller.startGame()){
                        JOptionPane.showMessageDialog(window,"Cannot start the game: Board is incomplete.","Error",JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                    startGame.setEnabled(false);
                    edit.setEnabled(true);
                    clear.setEnabled(true);
                    enableCheck.setEnabled(true);
                    findSolutions.setEnabled(true);
                    break;
                case "edit":
                    controller.editBoard();
                    startGame.setEnabled(true);
                    edit.setEnabled(false);
                    clear.setEnabled(false);
                    enableCheck.setEnabled(false);
                    findSolutions.setEnabled(false);
                    break;
                case "clear":
                    controller.clearBoard();
                    startGame.setEnabled(true);
                    edit.setEnabled(false);
                    clear.setEnabled(false);
                    enableCheck.setEnabled(false);
                    findSolutions.setEnabled(false);
                    break;
                case "check":
                    if(controller.isChecking()){
                        enableCheck.setText("Enable check");
                        controller.setChecking(false);
                    }else {
                        enableCheck.setText("Disable check");
                        controller.setChecking(true);
                    }
                    break;
                case "find-solutions":
                    int num;
                    try {
                        num = Integer.parseInt(JOptionPane.showInputDialog(null,"How many solutions do you want to find?","Find Solutions",JOptionPane.PLAIN_MESSAGE));
                        if(num<1) break;
                        controller.findSolutions(num);
                    }catch(NumberFormatException exception){
                        JOptionPane.showMessageDialog(null, "Provided value cannot be interpreted as an integer.","Error",JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case "quit":
                    if(!controller.isSaved() && !exitConsent(window)) break;
                    System.exit(0);
                    break;
                //About
                case "help":
                    JOptionPane.showMessageDialog(window,helpContent,"About",JOptionPane.PLAIN_MESSAGE);
                    break;
                default:
                    System.out.println("Unsupported command: "+e.getActionCommand());
            }
        };

        nuovo.addActionListener(actionListener); open.addActionListener(actionListener); save.addActionListener(actionListener); saveAs.addActionListener(actionListener);
        file.add(nuovo); file.add(open); file.add(save); file.add(saveAs);
        startGame.addActionListener(actionListener); edit.addActionListener(actionListener); clear.addActionListener(actionListener);
        enableCheck.addActionListener(actionListener); findSolutions.addActionListener(actionListener); quit.addActionListener(actionListener);
        game.add(startGame); game.add(edit); game.add(clear); game.add(enableCheck); game.add(findSolutions); game.add(quit);
        help.addActionListener(actionListener);
        about.add(help);
        menuBar.add(file); menuBar.add(game); menuBar.add(about);

        window.setJMenuBar(menuBar);

        window.setVisible(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
