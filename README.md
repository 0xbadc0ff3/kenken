# KenKen

A desktop application for creating, playing, validating, and saving KenKen puzzles, developed in Java as a Software Engineering course project.

## Overview

KenKen is a logic puzzle inspired by Sudoku, where players fill an $$n \times n$$ grid with numbers while respecting row, column, and cage constraints. This project focuses on the full puzzle lifecycle: board creation, template design, interactive play, solution search, and JSON-based persistence.

## Features

- Create a new board with sizes from **3x3** to **6x6**.
- Design custom KenKen templates by creating blocks (cages), assigning cells, and defining arithmetic constraints.
- Start a game only after a valid template has been defined.
- Find puzzle solutions with a backtracking-based solver and choose an upper bound for the number of solutions to compute.
- Browse multiple solutions through a dedicated navigation interface.
- Enable or disable dynamic constraint checking during gameplay.
- Save the current game state to **JSON** files.
- Load an existing board from a **JSON** file.
- Use the application through a graphical user interface designed around mouse-driven interactions.

## Project goals

The project was designed around four main functional goals:

1. **Puzzle template design**: let users build KenKen puzzles from scratch.
2. **Puzzle admissibility checking**: verify whether a puzzle admits solutions and inspect them.
3. **Gameplay support**: provide an intuitive GUI for solving puzzles.
4. **Persistence**: save and reopen puzzle configurations.

## Architecture and design patterns

The project applies several software engineering patterns and design choices:

- **MVC (Model-View-Controller)** to separate game logic from the graphical interface. (Please Note: I wrote this project while I was still gaining experience, so the MVC separation is not as clean as it should be)
- **Observer** as part of the MVC interaction model.
- **Decorator** to manage blocks attached to a specific board while enforcing consistency rules.
- **Proxy / Iterator control** to expose iteration over block cells while preventing unsafe removal operations.
- **Template Method** to structure the backtracking solver.
- **Memento** to store and restore board states while navigating computed solutions.

## Persistence format

Boards are serialized as **JSON**. The saved representation contains the board size, game state, current values, and block definitions, so a puzzle can be restored later from the file system.

## Technical notes

- Language: **Java**
- Build tool: **Maven**
- Testing: **JUnit 5**
- Interface: **Desktop GUI**

## Gameplay notes

During play, the optional dynamic check highlights whether entered values currently satisfy cage and row/column constraints. This check is local to the affected area after each edit, which keeps the interaction responsive.

The solver computes solutions independently from the player’s current partial choices. If a template has no valid solution, the application warns the user before starting the game.

## Limitations

- The application does **not** generate KenKen templates automatically.
- Dynamic checking does **not** prove that the current partial configuration can still lead to a full solution.
- The maximum block size is assumed to be **4 cells** in the current implementation, although this can be changed in code.

## Running the project

The application is started from the `main` method in `App.java`.

If the repository is not already configured with the Maven Exec plugin, the project can also be run directly from an IDE by launching `App.java`.

## Intended use

This repository was created as an academic project for a Software Engineering exam. It is both a playable KenKen application and a practical example of applying object-oriented design patterns, GUI design, persistence, and testing in Java.
