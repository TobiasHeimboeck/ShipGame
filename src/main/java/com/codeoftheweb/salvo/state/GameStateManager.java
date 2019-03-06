package com.codeoftheweb.salvo.state;

public class GameStateManager {

    private GameState currentState;

    public GameStateManager() {
        this.currentState = GameState.PLACING_SHIPS;
    }

    public void updateGameState() {
        switch (this.currentState) {
            case PLACING_SHIPS:
                this.currentState = GameState.WAIT_FOR_SALVO;
                break;
            case WAIT_FOR_SALVO:
                this.currentState = GameState.PLACING_SALVO;
                break;
            case PLACING_SALVO:
                this.currentState = GameState.WAIT_FOR_GAME_OVER;
                break;
            case WAIT_FOR_GAME_OVER:
                this.currentState = GameState.GAME_OVER;
                break;
            default:
                try {
                    throw new ChangeGamestateException("This gamestate is not available.");
                } catch (ChangeGamestateException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public GameState getCurrentState() {
        return this.currentState;
    }
}
