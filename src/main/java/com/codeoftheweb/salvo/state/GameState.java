package com.codeoftheweb.salvo.state;

public enum GameState {

    PLACING_SHIPS(0),
    WAIT_FOR_SALVO(1),
    PLACING_SALVO(2),
    WAIT_FOR_GAME_OVER(3),
    GAME_OVER(4);

    private int id;

    GameState(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
