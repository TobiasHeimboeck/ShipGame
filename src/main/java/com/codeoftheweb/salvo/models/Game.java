package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    public void addGamePlayer(GamePlayer player) {
        player.setGame(this);
        this.gamePlayers.add(player);
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Set<Score> getScores() {
        return scores;
    }
}
