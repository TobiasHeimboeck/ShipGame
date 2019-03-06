package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations;

    public Ship() {

    }

    public Ship(String type, GamePlayer gamePlayer, List<String> locations) {
        this.type = type;
        this.gamePlayer = gamePlayer;
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

    //<editor-fold desc="getGamePlayer">
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
    //</editor-fold>

    //<editor-fold desc="setGamePlayer">
    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
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

    //<editor-fold desc="getType">
    public String getType() {
        return type;
    }
    //</editor-fold>

    //<editor-fold desc="setType">
    public void setType(String type) {
        this.type = type;
    }
    //</editor-fold>
}
