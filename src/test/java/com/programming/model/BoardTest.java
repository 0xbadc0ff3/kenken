package com.programming.model;

import com.programming.memento.Memento;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    Board board = new Board(4);
    @Test
    @DisplayName("Check blocks operations")
    void testBlocks() {
        Cell cell = new Cell(1,1);
        Block block = board.attachBlock(new ConcreteBlock(cell));
        assertAll(
                ()->assertTrue(board.getBlocks().contains(block)),
                ()->assertFalse(board.getNotInBlockCells().contains(cell)),
                ()->{
                    board.removeBlock(block);
                    assertFalse(board.getBlocks().contains(block));
                },
                ()->assertTrue(board.getNotInBlockCells().contains(cell))
        );
    }

    @Test
    //@Disabled
    @DisplayName("Check if board understands correctly if a configuration is a solution.")
    void checkSolution() {
        try {
            Board b = Board.openBoard(new File("template1_solution.json"));
            assertTrue(b.checkSolution());
        }catch (Exception e){
            fail("Test couldn't correctly read the file.");
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Check if Board's Status is correctly updated.")
    void checkState(){
        board = new Board(3);
        assertAll(
                ()->assertEquals(BoardState.SETTING,board.getState()),
                ()->{
                    for(int i=0;i<3;i++) {
                        Block b = board.attachBlock(new ConcreteBlock());
                        b.add(new Cell(i,0));
                        b.add(new Cell(i,1));
                        b.add(new Cell(i,2));
                        b.setVincolo(i+1);
                        b.setOperation(Operation.ADD);
                    }
                    board.startGame();
                    assertEquals(BoardState.PLAYING,board.getState());
                },
                ()->{
                    board.edit();
                    assertEquals(BoardState.SETTING,board.getState());
                },
                ()->{
                    board.startGame();
                    board.editWithFieldReset();
                    assertEquals(BoardState.SETTING,board.getState());
                }

        );
    }

    @Test
    @DisplayName("Check if saved board and opened board from file are equals.")
    void toJSONAndOpenBoard() {
        File tmp = new File("tmp.json");
        try {
            tmp.createNewFile();
            PrintWriter pw = new PrintWriter(tmp);
            pw.println(board.toJSON());
            pw.close();
            Board b2 = Board.openBoard(tmp);
            tmp.delete();
            assertAll(
                    ()->assertEquals(board,b2),
                    ()->assertEquals(board.toJSON(),b2.toJSON())
            );
        }catch(Exception e){
            e.printStackTrace();
            fail("Error occurred.");
        }

    }

    @Test
    @DisplayName("Check if memento pattern is correctly implemented")
    void memento(){
        try {
            board = Board.openBoard(new File("template1_solution.json"));
            board.startGame();
            Memento snapshot = board.takeSnapshot();
            board.editWithFieldReset();
            board.startGame();
            Board b = Board.openBoard(new File("template1_solution.json"));
            board.restore(snapshot);
            assertAll(
                    ()->assertEquals(board, b),
                    ()->{
                        b.getCell(0,0).setValue( (board.getCell(0,0).getValue()+1)% board.getN() );
                        assertNotEquals(board, b);
                    },
                    ()->{
                        Board b1 = Board.openBoard(new File("template2.json"));
                        assertThrows(IllegalStateException.class, ()->board.restore(b1.takeSnapshot()));
                    }
            );
        }catch (IOException e){
            fail("Test couldn't read template file.");
        }

    }
    @Test
    @DisplayName("Check if constructor by copy works.")
    void constructorByCopy(){
        try {
            board = Board.openBoard(new File("template.json"));
        } catch (IOException e) {
            fail("Couldn't open template file.");
        }
        board.edit();
        Board copy = new Board(board,true);
        assertAll(
                ()->assertEquals(board, copy),
                ()->{
                    copy.getCell(2,3).setValue( (board.getCell(2,3).getValue()+1)% board.getN() );
                    assertNotEquals(board,copy);
                },
                ()->{
                    copy.removeBlock(copy.getBlocks().get(0));
                    assertNotEquals(copy.getBlocks().size(),board.getBlocks().size());
                },
                ()->assertFalse(board.getCell(0,0)==copy.getCell(0,0)),
                ()->assertFalse(board.getBlocks()==copy.getBlocks())
        );
    }
}