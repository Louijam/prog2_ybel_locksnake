package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;

import java.util.List;

public class TestLevelFactory {

    static Level simpleLevel() {
        return new Level(
            5,
            5,
            new CellType[5][5],
            List.of(),
            new Position(2, 2)
        );
    }

    static Level levelWithWallRightOfSnake() {

        CellType[][] cells = new CellType[5][5];

        cells[3][2] = CellType.WALL;

        return new Level(
            5,
            5,
            cells,
            List.of(),
            new Position(2, 2)
        );
    }

    static Level smallLevel() {
        return simpleLevel();
    }
}
