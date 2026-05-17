package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.model.Level;
import de.hsbi.lockgame.model.Snake;
import de.hsbi.lockgame.ui.GamePanel;

public final class GameEngine {

    private GameState gameState;
    private GamePanel gamePanel;

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

    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
    }

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

        benachrichtigeUI();
    }

    public void tick() {

        gameState = gameState.tick();
        benachrichtigeUI();
    }

    private void benachrichtigeUI() {
        if (gamePanel != null) {
            gamePanel.update(gameState);
        }
    }
}
