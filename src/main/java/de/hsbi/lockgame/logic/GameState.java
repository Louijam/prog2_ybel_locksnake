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

    public Level level() { return level; }
    public Snake snake() { return snake; }
    public List<Pin> pins() { return pins; }
    public Status status() { return status; }
    public Direction pendingDirection() { return pendingDirection; }

   GameState tick() {

        // -------------------------
        // Spiel läuft nicht
        // -------------------------
        if (!status.isRunning() || pendingDirection == Direction.NONE) {
            return this;
        }

        // nächstes Feld berechnen
        Position next = snake.nextHead(pendingDirection);

        // -------------------------
        // OUT OF BOUNDS
        // -------------------------
        if (!level.isInside(next)) {
            return lose(Status.LOST_OUT_OF_BOUNDS);
        }

        CellType cell = level.cellAt(next);

        // -------------------------
        // WALL -> blockieren
        // -------------------------
        if (cell == CellType.WALL) {
            return new GameState(
                level,
                snake,
                pins,
                status,
                Direction.NONE
            );
        }

        // -------------------------
        // PIN SLOT
        // -------------------------
        if (cell == CellType.PIN_SLOT) {

            int pinIndex = -1;
            Pin pin = null;

            // passenden Pin suchen
            for (int i = 0; i < pins.size(); i++) {
                if (pins.get(i).position().equals(next)) {
                    pin = pins.get(i);
                    pinIndex = i;
                    break;
                }
            }

            // Sicherheitscheck
            if (pin == null) {
                return new GameState(
                    level,
                    snake,
                    pins,
                    status,
                    Direction.NONE
                );
            }

            // Pin bereits gesetzt -> blockieren
            if (pin.state().isSet()) {
                return new GameState(
                    level,
                    snake,
                    pins,
                    status,
                    Direction.NONE
                );
            }

            // falsche Richtung -> blockieren
            if (pin.activationDirection() != pendingDirection) {
                return new GameState(
                    level,
                    snake,
                    pins,
                    status,
                    Direction.NONE
                );
            }

            // -------------------------
            // Pin aktivieren
            // -------------------------
            List<Pin> newPins = new ArrayList<>(pins);

            newPins.set(
                pinIndex,
                pin.withState(Pin.State.HIGH)
            );

            boolean won = newPins.stream()
                .allMatch(p -> p.state().isSet());

            // Snake bleibt stehen
            // nur Pin + Richtung ändern
            return new GameState(
                level,
                snake,
                newPins,
                won ? Status.WON : status,
                pendingDirection.oppositeDirection()
            );
        }

        // -------------------------
        // SELF COLLISION
        // -------------------------
        if (snake.occupies(next)) {
            return lose(Status.LOST_SELF_COLLISION);
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

    private GameState lose(Status s) {
        return new GameState(level, snake, pins, s, Direction.NONE);
    }

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
