package com.codeoftheweb.salvo.state;

public enum GameState {

    PLACING_SHIPS("Player place his ships"),
    WAIT_FOR_OPPONENT_PLACE_SHIPS("Wait for the opponent to place his ships"),
    PLACING_SALVO("Player place his salvos"),
    WAIT_FOR_OPPONENT_PLACING_SALVO("Wait for the opponent to place his salvos");

    private String info;

    GameState(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public static String getInfo(GameState gameState) {
        return gameState.getInfo();
    }
}
