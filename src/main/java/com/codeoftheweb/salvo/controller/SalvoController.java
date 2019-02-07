package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api")
public class SalvoController {

    @Autowired
    private GameRepository repository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @RequestMapping(value = "/games")
    public List<Object> getAllGames() {
        return repository.findAll().stream().map(this::getGameDTO)
                .collect(toList());
    }

    @RequestMapping(value = "/game_view/{id}")
    public Map<String, Object> getGame(@PathVariable long id) {
        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
        Map<String, Object> gameView = new HashMap<>();
        gameView.put("games", getGameDTO(gamePlayer.getGame()));
        gameView.put("ships", gamePlayer.getShips().stream().map(this::getShipDTO).collect(toList()));
        gameView.put("salvoes", gamePlayer.getSalvos().stream().map(this::getSalvoesDTO).collect(toList()));
        return gameView;
    }

    private Map<String, Object> getSalvoesDTO(Salvo salvo) {
        final Map<String, Object> gameView = new HashMap<>();
        gameView.put("turn", salvo.getTurn());
        gameView.put("player", salvo.getPlayer().getId());
        gameView.put("locations", salvo.getLocations());
        return gameView;
    }

    private Map<String, Object> getGameDTO(Game game) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGamePlayerDTO)
                .collect(toList()));
        return dto;
    }

    private Map<String, Object> getShipDTO(Ship ship) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());
        return dto;
    }

    private Map<String, Object> getGamePlayerDTO(GamePlayer gamePlayer) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", getPlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> getPlayerDTO(Player player) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }
    
    public GameRepository getRepository() {
        return repository;
    }

    public void setRepository(GameRepository repository) {
        this.repository = repository;
    }

    public GamePlayerRepository getGamePlayerRepository() {
        return gamePlayerRepository;
    }

    public void setGamePlayerRepository(GamePlayerRepository gamePlayerRepository) {
        this.gamePlayerRepository = gamePlayerRepository;
    }
}