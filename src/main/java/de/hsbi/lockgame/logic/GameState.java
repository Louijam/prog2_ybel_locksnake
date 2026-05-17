package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;

import java.util.ArrayList;
import java.util.List;

public final class GameState {

    private final Level level;
    private final Snake snake;
    private final List<Pin> pins;
    private final Status status;
    private final Direction pendingDirection;

    public GameState(Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
        this.level = level;
        this.snake = snake;
        this.pins = List.copyOf(pins);
        this.status = status;
        this.pendingDirection = pendingDirection;
    }

    public Level level() {
        return level;
    }

    public Snake snake() {
        return snake;
    }

    public List<Pin> pins() {
        return pins;
    }

    public Status status() {
        return status;
    }

    public Direction pendingDirection() {
        return pendingDirection;
    }

    // =========================================================
    // CORE GAME LOGIC
    // =========================================================
    public GameState tick() {

        if (!status.isRunning() || pendingDirection == Direction.NONE) {
            return this;
        }

        Position next = snake.nextHead(pendingDirection);

        // -------------------------
        // OUT OF BOUNDS
        // -------------------------
        if (!level.isInside(next)) {
            return lose(Status.LOST_OUT_OF_BOUNDS);
        }

        // -------------------------
        // SELF COLLISION
        // -------------------------
        if (snake.body().contains(next)) {
            return lose(Status.LOST_SELF_COLLISION);
        }

        // -------------------------
        // WALL CHECK (MUST BE BEFORE PIN)
        // -------------------------
        CellType cell = level.cellAt(next);

        if (cell == CellType.WALL) {
            return new GameState(level, snake, pins, status, Direction.NONE);
        }

        // -------------------------
        // PIN CHECK
        // -------------------------
        Pin pin = null;
        int pinIndex = -1;

        for (int i = 0; i < pins.size(); i++) {
            if (pins.get(i).position().equals(next)) {
                pin = pins.get(i);
                pinIndex = i;
                break;
            }
        }

        if (pin != null) {

            // block conditions
            if (pin.state().isSet() ||
                pin.activationDirection() != pendingDirection) {

                return new GameState(level, snake, pins, status, Direction.NONE);
            }

            List<Pin> newPins = new ArrayList<>(pins);
            newPins.set(pinIndex, pin.withState(Pin.State.HIGH));

            boolean won = newPins.stream().allMatch(p -> p.state().isSet());

            return new GameState(
                level,
                snake,
                newPins,
                won ? Status.WON : status,
                pendingDirection.oppositeDirection()
            );
        }

        // -------------------------
        // NORMAL MOVE
        // -------------------------
        return new GameState(
            level,
            snake.grow(pendingDirection),
            pins,
            status,
            pendingDirection
        );
    }

    // =========================================================
    // LOSS HELPER
    // =========================================================
    private GameState lose(Status s) {
        return new GameState(level, snake, pins, s, Direction.NONE);
    }

    // =========================================================
    // STATUS
    // =========================================================
    public enum Status {
        RUNNING,
        WON,
        LOST_SELF_COLLISION,
        LOST_OUT_OF_BOUNDS;

        public boolean isRunning() {
            return this == RUNNING;
        }
    }
}
