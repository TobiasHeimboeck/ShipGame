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

    public Score getScore(Game game) {
        Score response = null;
        for(Score current : game.getScores()) {
            if(current.getGame().getId() == game.getId()) {
                response = current;
            }
        }
        return response;
    }

    public void addGamePlayer(GamePlayer player) {
        player.setPlayer(this);
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

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Score> getScores() {
        return scores;
    }
}
