package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    private double score;

    private String finishedDate;

    public Score() {

    }

    public Score(Player player, Game game, double score, String finishedDate) {
        this.player = player;
        this.game = game;
        this.score = score;
        this.finishedDate = finishedDate;
    }

    //<editor-fold desc="getId">
    public long getId() {
        return id;
    }
    //</editor-fold>

    //<editor-fold desc="setId">
    public void setId(long id) {
        this.id = id;
    }
    //</editor-fold>

    //<editor-fold desc="getPlayer">
    public Player getPlayer() {
        return player;
    }
    //</editor-fold>

    //<editor-fold desc="setPlayer">
    public void setPlayer(Player player) {
        this.player = player;
    }
    //</editor-fold>

    //<editor-fold desc="getGame">
    public Game getGame() {
        return game;
    }
    //</editor-fold>

    //<editor-fold desc="setGame">
    public void setGame(Game game) {
        this.game = game;
    }
    //</editor-fold>

    //<editor-fold desc="getScore">
    public double getScore() {
        return score;
    }
    //</editor-fold>

    //<editor-fold desc="setScore">
    public void setScore(double score) {
        this.score = score;
    }
    //</editor-fold>

    //<editor-fold desc="getFinishedDate">
    public String getFinishedDate() {
        return finishedDate;
    }
    //</editor-fold>

    //<editor-fold desc="setFinishedDate">
    public void setFinishedDate(String finishedDate) {
        this.finishedDate = finishedDate;
    }
    //</editor-fold>
}
