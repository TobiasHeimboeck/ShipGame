package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private long playerID;

    private long gameID;

    private String joinDate;

    public GamePlayer() {

    }

    public GamePlayer(Player player, Game game) {
        this.playerID = player.getId();
        this.gameID = game.getId();
        this.joinDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlayerID() {
        return this.playerID;
    }

    public void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    public long getGameID() {
        return this.gameID;
    }

    public void setGameID(long gameID) {
        this.gameID = gameID;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}
