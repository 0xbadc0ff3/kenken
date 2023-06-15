package com.programming.controller;

import com.programming.model.Board;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class KenkenControllerTest {
    KenkenController controller = new KenkenController(3);
    @Test
    @DisplayName("Check if controller does not allow to start the game if the board is blank.")
    void startGame() {
        assertFalse(controller.startGame());
    }

    @Test
    @Disabled("Not implementated yet.")
    void findSolutions() {
    }

    @Test
    @DisplayName("Check if file name is correctly manipulated in order to add missing json extension.")
    void getFileAfterSaving() {
        File f = new File("ciao");
        assertAll(
                ()->assertEquals(KenkenController.getFileAfterSaving(f).getName(),"ciao.json"),
                ()->{
                    File f2 = new File("ciao.json");
                    assertEquals(KenkenController.getFileAfterSaving(f2).getName(),"ciao.json");
                }
        );
    }

    @Test
    @DisplayName("Check game saving.")
    void save() {
        assertAll(
                ()->assertThrows(RuntimeException.class,()->controller.save()),
                ()->{
                    controller.openBoard(new File("/Users/alby/IdeaProjects/kenken/template.json"));
                    File f = new File("ciao.json");
                    f.deleteOnExit();
                    controller.save(f);
                    KenkenController c2 = new KenkenController();
                    c2.openBoard(f);
                    assertEquals(controller,c2);
                }
        );
    }

    @Test
    void setChecking() {
        controller.setChecking(true);
        assertAll(
                ()->assertTrue(controller.isChecking()),
                ()->{
                    controller.setChecking(false);
                    assertFalse(controller.isChecking());
                }
        );
    }
}