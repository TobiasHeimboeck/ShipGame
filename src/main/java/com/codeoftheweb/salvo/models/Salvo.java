package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ElementCollection
    @Column(name = "locations")
    private List<String> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo() {
        
    }

    public Salvo(GamePlayer gamePlayer, int turn, List<String> locations) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.locations = locations;
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
    public GamePlayer getPlayer() {
        return gamePlayer;
    }
    //</editor-fold>

    //<editor-fold desc="setPlayer">
    public void setPlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    //</editor-fold>

    //<editor-fold desc="getTurn">
    public int getTurn() {
        return turn;
    }
    //</editor-fold>

    //<editor-fold desc="setTurn">
    public void setTurn(int turn) {
        this.turn = turn;
    }
    //</editor-fold>

    //<editor-fold desc="getLocations">
    public List<String> getLocations() {
        return locations;
    }
    //</editor-fold>

    //<editor-fold desc="setLocations">
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
    //</editor-fold>
}