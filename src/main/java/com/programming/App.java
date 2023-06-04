package com.programming;

import com.programming.controller.KenkenController;
import com.programming.model.Board;
import com.programming.model.BoardState;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class App 
{
    private static boolean exitConsent(){
        //TODO
        return false;
    }

    public static void main( String[] args )
    {
        //Set Look & Feel
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame window = new JFrame("KenKen");
        JPanel panel = new JPanel(new GridLayout());
        window.setIconImage(Utility.APP_LOGO);
        window.setSize(900,900);
        KenkenController controller = new KenkenController(5);
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

        JMenu game = new JMenu("Game");
        JMenuItem startGame = new JMenuItem("Start game");
        startGame.setActionCommand("start");
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
            JFrame chooserWindow;
            System.out.println("Ricevuto comando "+e.getActionCommand());
            switch (e.getActionCommand()){
                //File
                case "new":
                    if(!controller.isSaved())
                        if(!exitConsent()) break;
                    Integer sizes[] = {3,4,5,6};
                    int n = (int) JOptionPane.showInputDialog(window,"Select new Board size: ", "New Board",JOptionPane.PLAIN_MESSAGE,null,sizes,sizes[0]);
                    controller.setNewBoard(n);
                    break;
                case "open":
                    if(!controller.isSaved())
                        if(!exitConsent()) break;
                    fileChooser = new JFileChooser();
                    chooserWindow = new JFrame();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
                    if(fileChooser.showOpenDialog(window)!=JFileChooser.APPROVE_OPTION) break;
                    try{
                        controller.openBoard(fileChooser.getSelectedFile());
                        //});
                        if(controller.getState() == BoardState.PLAYING){
                            edit.setEnabled(true);
                            clear.setEnabled(true);
                            startGame.setEnabled(false);
                            findSolutions.setEnabled(true);
                        }
                    }catch(Exception exception){
                        JOptionPane.showMessageDialog(null, "File missing or not valid.", "Error", JOptionPane.ERROR_MESSAGE);
                        exception.printStackTrace();
                    }
                    System.out.println(controller.toJSON());
                    break;
                case "save":
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
                                        return;
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
                    chooserWindow = new JFrame();
                    fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
                    if(fileChooser.showSaveDialog(chooserWindow)!=JFileChooser.APPROVE_OPTION) break;
                    controller.save(fileChooser.getSelectedFile());
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
                    controller.findSolutions(0);
                    //TODO
                    break;
                case "quit":
                    if(!controller.isSaved() && !exitConsent()) break;
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

        nuovo.addActionListener(actionListener); open.addActionListener(actionListener); save.addActionListener(actionListener);
        file.add(nuovo); file.add(open); file.add(save);
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
