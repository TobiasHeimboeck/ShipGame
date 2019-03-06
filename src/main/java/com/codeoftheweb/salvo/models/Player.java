package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Player() {

    }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.gamePlayers = new HashSet<>();
        this.scores = new HashSet<>();
    }

    //<editor-fold desc="getScore">
    public Score getScore(Game game) {
        Score response = null;
        for(Score current : game.getScores()) {
            if(current.getGame().getId() == game.getId()) {
                response = current;
            }
        }
        return response;
    }
    //</editor-fold>

    //<editor-fold desc="addGamePlayer">
    public void addGamePlayer(GamePlayer player) {
        player.setPlayer(this);
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

    //<editor-fold desc="getUserName">
    public String getUserName() {
        return this.userName;
    }
    //</editor-fold>

    //<editor-fold desc="setUserName">
    public void setUserName(String userName) {
        this.userName = userName;
    }
    //</editor-fold>

    //<editor-fold desc="getPassword">
    public String getPassword() {
        return password;
    }
    //</editor-fold>

    //<editor-fold desc="setPassword">
    public void setPassword(String password) {
        this.password = password;
    }
    //</editor-fold>

    //<editor-fold desc="getScores">
    public Set<Score> getScores() {
        return scores;
    }
    //</editor-fold>
}
