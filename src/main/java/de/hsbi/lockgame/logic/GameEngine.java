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

    public GameState state() {
        return gameState;
    }

    // Observer registration
    public void addObserver(GameStateObserver o) {
        observers.add(o);
    }

    private void notifyObservers() {
        observers.forEach(o -> o.onStateChanged(gameState));
    }

    // INPUT
    public void update(Direction d) {

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

    // TICK
    public void tick() {
        gameState = gameState.tick();
        notifyObservers();
    }
}
