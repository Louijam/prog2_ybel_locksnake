package de.hsbi.lockgame.logic;

public interface GameStateObserver {
    void onStateChanged(GameState state);
}
