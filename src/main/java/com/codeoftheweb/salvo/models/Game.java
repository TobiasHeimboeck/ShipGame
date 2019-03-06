package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private final Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private final Set<Score> scores;

    public Game() {
        this.creationDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.gamePlayers = new HashSet<>();
        this.scores = new HashSet<>();
    }

    //<editor-fold desc="addGamePlayer">
    public void addGamePlayer(GamePlayer player) {
        player.setGame(this);
        this.gamePlayers.add(player);
    }
    //</editor-fold>

    //<editor-fold desc="getGamePlayers">
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
    //</editor-fold>

    //<editor-fold desc="getId">
    public long getId() {
        return this.id;
    }
    //</editor-fold>

    //<editor-fold desc="setId">
    public void setId(long id) {
        this.id = id;
    }
    //</editor-fold>

    //<editor-fold desc="getCreationDate">
    public String getCreationDate() {
        return this.creationDate;
    }
    //</editor-fold>

    //<editor-fold desc="setCreationDate">
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    //</editor-fold>

    //<editor-fold desc="getScores">
    Set<Score> getScores() {
        return scores;
    }
    //</editor-fold>
}
