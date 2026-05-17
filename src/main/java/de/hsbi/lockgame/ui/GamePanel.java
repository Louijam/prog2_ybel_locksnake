package de.hsbi.lockgame.ui;

import de.hsbi.lockgame.logic.GameEngine;
import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.logic.GameStateObserver;
import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.settings.GameConstants;
import de.hsbi.lockgame.settings.InputConstants;
import de.hsbi.lockgame.ui.render.GameRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class GamePanel extends JPanel implements GameStateObserver {

    private GameState state;
    private final GameRenderer renderer;
    private GameEngine gameEngine;

    public GamePanel(GameState initialState, GameRenderer renderer) {
        this.state = initialState;
        this.renderer = renderer;

        var width = initialState.level().width() * GameConstants.TILE_SIZE;
        var height = initialState.level().height() * GameConstants.TILE_SIZE;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setFocusable(true);

        InputConstants.BINDINGS.forEach(this::setupKeyBindings);
    }

    @Override
    public void onStateChanged(GameState newState) {
        this.state = newState;
        repaint();
    }

    public void setGameEngine(GameEngine engine) {
        this.gameEngine = engine;
        engine.addObserver(this);
    }

    private void setupKeyBindings(Direction direction, Iterable<Integer> keyCodes) {

        var inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = getActionMap();

        var actionKey = "move_" + direction.name();

        var action =
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameEngine.update(direction);
                }
            };

        keyCodes.forEach(k -> inputMap.put(KeyStroke.getKeyStroke(k, 0), actionKey));
        actionMap.put(actionKey, action);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, state, GameConstants.TILE_SIZE);
    }
}
