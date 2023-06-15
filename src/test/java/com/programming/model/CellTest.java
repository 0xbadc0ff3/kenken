package com.programming.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    int i=3, j=6;
    Cell cell = new Cell(i,j);

    @Test
    @DisplayName("Checks if data is correctly stored inside Cell Objects.")
    void checkData() {
        int value = 4;
        cell.setValue(value);
        assertAll(
                ()->assertEquals(value, cell.getValue()),
                ()->assertEquals(i,cell.getRow()),
                ()->assertEquals(j,cell.getCol())
        );
    }

    @Test
    @DisplayName("Checks if Cell equals works correctly (compares index and ignores values)")
    void testEquals() {
        int value1 = 3, value2 = 5;
        cell.setValue(value1);
        Cell cell2 = new Cell(i,j);
        assertAll(
                ()->assertEquals(cell,cell2),
                ()->{
                    cell2.setValue(value2);
                    assertEquals(cell,cell2);
                }
        );
    }
}