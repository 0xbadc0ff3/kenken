package com.programming.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
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
    @Disabled
    @DisplayName("Check if board understands correctly if a configuration is a solution.")
    void checkSolution() {
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

}