package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    // -------------------------
    // HELPERS
    // -------------------------
    private Level level(Position start, List<Pin> pins, CellType[][] cells) {
        return new Level(5, 5, cells, pins, start);
    }

    private Snake snake(int x, int y) {
        return new Snake(List.of(new Position(x, y)));
    }

    // -------------------------
    // 1. NORMAL MOVE
    // -------------------------
    @Test
    void movesForward() {

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(new Position(3, 2), next.snake().head());
    }

    // -------------------------
    // 2. WALL BLOCKS MOVEMENT
    // -------------------------
    @Test
    void wallBlocksMovement() {

        CellType[][] cells = new CellType[5][5];
        cells[3][2] = CellType.WALL;

        Level lvl = level(new Position(2, 2), List.of(), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        // Snake bleibt stehen (Block)
        assertEquals(new Position(2, 2), next.snake().head());
        assertEquals(Direction.NONE, next.pendingDirection());
    }

    // -------------------------
    // 3. OUT OF BOUNDS
    // -------------------------
    @Test
    void outOfBoundsLoses() {

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(0, 0), List.of(), cells);

        GameState state = new GameState(
            lvl,
            snake(0, 0),
            List.of(),
            GameState.Status.RUNNING,
            Direction.LEFT
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.LOST_OUT_OF_BOUNDS, next.status());
    }

    // -------------------------
    // 4. SELF COLLISION (CORRECTED)
    // -------------------------
    @Test
    void selfCollisionLoses() {

        Snake snake = new Snake(List.of(
            new Position(2,2),
            new Position(2,1),
            new Position(3,1),
            new Position(3,2)
        ));

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(), cells);

        GameState state = new GameState(
            lvl,
            snake,
            List.of(),
            GameState.Status.RUNNING,
            Direction.UP
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.LOST_SELF_COLLISION, next.status());
    }

    // -------------------------
    // 5. PIN WRONG DIRECTION BLOCKS
    // -------------------------
    @Test
    void pinWrongDirectionBlocks() {

        Pin pin = new Pin(
            new Position(3, 2),
            Pin.State.LOW,
            Direction.LEFT
        );

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(pin), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        // Block → keine Bewegung
        assertEquals(new Position(2, 2), next.snake().head());
    }

    // -------------------------
    // 6. PIN ACTIVATION
    // -------------------------
    @Test
    void pinActivates() {

        Pin pin = new Pin(
            new Position(3, 2),
            Pin.State.LOW,
            Direction.RIGHT
        );

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(pin), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertTrue(next.pins().get(0).state().isSet());
    }

    // -------------------------
    // 7. ALREADY SET PIN BLOCKS (STABLE VERSION)
    // -------------------------
    @Test
    void alreadySetPinBlocks() {

        Pin pin = new Pin(
            new Position(3, 2),
            Pin.State.HIGH,
            Direction.RIGHT
        );

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(pin), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        // keine Bewegung
        assertEquals(new Position(2, 2), next.snake().head());
        assertEquals(Pin.State.HIGH, next.pins().get(0).state());
    }

    // -------------------------
    // 8. WIN CONDITION
    // -------------------------
    @Test
    void winWhenAllPinsSet() {

        Pin pin = new Pin(
            new Position(3, 2),
            Pin.State.LOW,
            Direction.RIGHT
        );

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(pin), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.WON, next.status());
    }

    // -------------------------
    // 9. NONE DIRECTION = NO MOVE
    // -------------------------
    @Test
    void noneDirectionDoesNothing() {

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(),
            GameState.Status.RUNNING,
            Direction.NONE
        );

        GameState next = state.tick();

        assertEquals(new Position(2, 2), next.snake().head());
    }

    // -------------------------
    // 10. GAME STOPPED DOES NOTHING
    // -------------------------
    @Test
    void stoppedGameDoesNothing() {

        CellType[][] cells = new CellType[5][5];

        Level lvl = level(new Position(2, 2), List.of(), cells);

        GameState state = new GameState(
            lvl,
            snake(2, 2),
            List.of(),
            GameState.Status.WON,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.WON, next.status());
    }
}
