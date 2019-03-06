package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {

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

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private List<Salvo> salvos;

    public GamePlayer() {

    }

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.ships = new HashSet<>();
        this.salvos = new ArrayList<>();
    }

    //<editor-fold desc="addSalvo">
    public void addSalvo(Salvo salvo) {
        salvo.setPlayer(this);
        salvos.add(salvo);
    }
    //</editor-fold>

    //<editor-fold desc="addShip">
    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }
    //</editor-fold>

    //<editor-fold desc="getScore">
    public Score getScore() {
        return this.player.getScore(this.game);
    }
    //</editor-fold>

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

    //<editor-fold desc="getShips">
    public Set<Ship> getShips() {
        return ships;
    }
    //</editor-fold>

    //<editor-fold desc="setShips">
    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }
    //</editor-fold>

    //<editor-fold desc="getSalvos">
    public List<Salvo> getSalvos() {
        return salvos;
    }
    //</editor-fold>

    //<editor-fold desc="setSalvos">
    public void setSalvos(List<Salvo> salvos) {
        this.salvos = salvos;
    }
    //</editor-fold>
}
