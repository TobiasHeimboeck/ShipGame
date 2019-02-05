package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class SalvoController {

    @Autowired
    private GameRepository repository;

    @RequestMapping(value = "/games")
    public List<Object> getAllGames() {
        return repository.findAll().stream().map(this::getGameDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/game_view/{id}")
    public Map<String, Object> getGame(@PathVariable long id) {
        return getGameDTO(repository.getOne(id));
    }

    private Map<String, Object> getGameDTO(Game game) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGamePlayerDTO)
                .collect(Collectors.toList()));
        dto.put("ships", game.getGamePlayers().stream().map(this::getShipDTO).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> getShipDTO(GamePlayer player) {
        final Map<String, Object> dto = new HashMap<>();

        for(Ship current : player.getShips()) {
            dto.put("type", current.getType());
            dto.put("locations", current.getLocations());
        }

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
}