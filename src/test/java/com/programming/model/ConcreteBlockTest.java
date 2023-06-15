package com.programming.model;

import com.programming.Utility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteBlockTest {
    Block block = new ConcreteBlock();
    @Test
    @DisplayName("Check if block's result has been correctly stored.")
    void setVincolo() {
        int vincolo = 1+new Random().nextInt(200);
        block.setVincolo(vincolo);
        assertEquals(vincolo, block.getVincolo());
    }

    @Test
    @DisplayName("Check if block's operation has been correctly stored and if method hasConstraints works well.")
    void setOperation() {
        Operation op = Operation.MUL;
        block.setVincolo(4);
        block.setOperation(null);
        assertAll(
                ()->assertFalse(block.hasConstraints()),
                ()->{
                    block.setOperation(op);
                    assertEquals(op,block.getOperation());
                },
                ()->{
                    block.setOperation(op);
                    assertTrue(block.hasConstraints());
                }
        );
    }

    @Test
    @DisplayName("Check if add, remove, contains and size are working.")
    void checkAddRemoveContains() {
        Cell cell = new Cell(1,2);
        int initialSize = block.getCurrentSize();
        block.add(cell);
        assertAll(
                ()->assertTrue(block.contains(cell)),
                ()->assertTrue(block.getCells().contains(cell)),
                ()->assertEquals(block.getCells().size(),block.getCurrentSize()),
                ()->assertEquals(initialSize+1,block.getCurrentSize()),
                ()->assertTrue(block.contains(new Cell(1,2))),
                ()->assertTrue(block.remove(cell)),
                ()->assertFalse(block.contains(cell)),
                ()->assertFalse(block.getCells().contains(cell)),
                ()->assertEquals(initialSize,block.getCurrentSize())
        );
    }

    @Test
    @DisplayName("Check if block's constraints are correctly verified.")
    void isValid() {
        block = new ConcreteBlock();
        Cell uno = new Cell(0,0);
        uno.setValue(1);
        Cell due = new Cell(0,1);
        due.setValue(2);
        block.add(uno);
        block.add(due);
        assertAll(
                ()->assertFalse(block.hasConstraints()),
                ()->{
                    //ADD
                    block.setOperation(Operation.ADD);
                    block.setVincolo(3);
                    assertTrue(block.isValid());
                },
                ()->assertTrue(block.hasConstraints()),
                ()->{
                    //SUB
                    block.setOperation(Operation.SUB);
                    block.setVincolo(1);
                    assertTrue(block.isValid());
                },
                ()->{
                    //MUL
                    block.setOperation(Operation.MUL);
                    block.setVincolo(2);
                    assertTrue(block.isValid());
                },
                ()->{
                    //DIV
                    block.setOperation(Operation.DIV);
                    block.setVincolo(2);
                    assertTrue(block.isValid());
                },
                ()->{
                    block.setVincolo(5);
                    assertFalse(block.isValid());
                },
                ()->{
                    block.setOperation(Operation.SUB);
                    block.setVincolo(3);
                    assertFalse(block.isValid());
                },
                ()->{
                    block.setVincolo(2);
                    block.setOperation(null);
                    block.remove(uno);
                    assertTrue(block.isValid());
                },
                ()->{
                    block.setVincolo(1);
                    assertFalse(block.isValid());
                },
                ()->assertTrue(block.hasConstraints())
        );

    }

    @Test
    @DisplayName("Check full block.")
    void isFull() {
        block = new ConcreteBlock();
        for(int i=0;i< Utility.MAX_BLOCK_SIZE;i++){
            block.add(new Cell(0,i));
        }
        assertAll(
                ()->assertTrue(block.isFull()),
                ()->assertThrows(RuntimeException.class,()->block.add(new Cell(1,1))),
                ()->{
                    block.remove(block.getCells().iterator().next());
                    assertFalse(block.isFull());
                },
                ()->assertDoesNotThrow(()->block.add(new Cell(1,1)))
        );
    }


    @Test
    @DisplayName("Check if attached block are correctly detected")
    void isAttached() {
        assertAll(
                ()->assertFalse(block.isAttached()),
                ()->{
                    Block block2 = new Board(4).attachBlock(block);
                    assertTrue(block2.isAttached());
                }
        );
    }

    @Test
    @DisplayName("Check block equals")
    void testEquals() {
        block = new ConcreteBlock();
        Block block1 = new ConcreteBlock();
        Cell uno = new Cell(1,1);
        assertAll(
                ()->assertEquals(block,block1),
                ()->{
                    block.add(uno);
                    assertNotEquals(block,block1);
                },
                ()->{
                    block1.add(uno);
                    assertEquals(block,block1);
                },
                ()->{
                    block.setOperation(Operation.MUL);
                    assertNotEquals(block,block1);
                },
                ()->{
                    block1.setOperation(Operation.MUL);
                    assertEquals(block,block1);
                },
                ()->{
                    block.setVincolo(4);
                    assertNotEquals(block,block1);
                },
                ()->{
                    block1.setVincolo(4);
                    assertEquals(block,block1);
                }
        );
    }
}