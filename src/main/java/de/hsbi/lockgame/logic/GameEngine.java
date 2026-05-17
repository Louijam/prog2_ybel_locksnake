package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.model.Level;
import de.hsbi.lockgame.model.Snake;

import java.util.ArrayList;
import java.util.List;

public final class GameEngine {

    private GameState gameState;
    private final List<GameStateObserver> observers = new ArrayList<>();

    public GameEngine(Level level) {

        this.gameState = new GameState(
            level,
            new Snake(java.util.List.of(level.snakeStart())),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE
        );
    }

    // -------------------------
    // OBSERVER REGISTRATION
    // -------------------------
    public void addObserver(GameStateObserver observer) {
        observers.add(observer);
    }

    // -------------------------
    // STATE ACCESS
    // -------------------------
    public GameState state() {
        return gameState;
    }

    // -------------------------
    // INPUT HANDLING
    // -------------------------
    public void update(Direction d) {

        if (gameState.status() != GameState.Status.RUNNING) {
            return;
        }

        if (d == gameState.pendingDirection().oppositeDirection()) {
            return;
        }

        gameState = new GameState(
            gameState.level(),
            gameState.snake(),
            gameState.pins(),
            gameState.status(),
            d
        );

        notifyObservers();
    }

    // -------------------------
    // GAME LOOP
    // -------------------------
    public void tick() {

        gameState = gameState.tick();
        notifyObservers();
    }

    // -------------------------
    // OBSERVER NOTIFICATION
    // -------------------------
    private void notifyObservers() {
        for (GameStateObserver o : observers) {
            o.onStateChanged(gameState);
        }
    }
}
