package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping(value = "/scoreboard")
    public List<Object> getPlayers() {
        return playerRepository.findAll().stream().map(this::getScoreDTO).collect(toList());
    }

    @RequestMapping(value = "/games")
    public Map<String, Object> getAll() {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("games", gameRepository.findAll().stream().map(this::makeDTO).collect(toList()));
        return dto;
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> registerPlayer(@RequestParam String username, String password) {
        if (username.isEmpty())
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        if (playerRepository.findByUserName(username) != null)
            return new ResponseEntity<>("Name is already in use", HttpStatus.FORBIDDEN);

        Player player = new Player(username, password);

        playerRepository.save(player);
        return new ResponseEntity<>(getPlayersDTO("id", player.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(@RequestParam String username) {
        Player player = playerRepository.findByUserName(username);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "Player is null"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = new Game();
            GamePlayer gamePlayer = new GamePlayer();
            player.addGamePlayer(gamePlayer);
            game.addGamePlayer(gamePlayer);
            gameRepository.save(game);
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable long id, Authentication auth) {
        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
        if (gamePlayer.getPlayer() == playerRepository.findByUserName(auth.getName())) {
            Map<String, Object> gameView = new HashMap<>();
            gameView.put("games", getGameDTO(gamePlayer.getGame()));
            gameView.put("ships", gamePlayer.getShips().stream().map(this::getShipDTO).collect(toList()));
            loadSalvos(gameView, gamePlayer.getGame());
            return new ResponseEntity<>(makeMap("gameview", gameView), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("error", "un"), HttpStatus.UNAUTHORIZED);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private Map<String, Object> makeDTO(Game game) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGPDTO)
                .collect(toList()));
        return dto;
    }

    private Map<String, Object> getGPDTO(GamePlayer gamePlayer) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("gpid", gamePlayer.getId());
        dto.put("id", gamePlayer.getPlayer().getId());
        dto.put("name", gamePlayer.getPlayer().getUserName());
        return dto;
    }

    private Map<String, Object> getScoreDTO(Player player) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("player", player.getUserName());
        dto.put("total", player.getScores().stream().mapToDouble(Score::getScore).sum());
        dto.put("wins", player.getScores().stream().filter(score -> score.getScore() == 1.0).count());
        dto.put("losses", player.getScores().stream().filter(score -> score.getScore() == 0.0).count());
        dto.put("ties", player.getScores().stream().filter(score -> score.getScore() == 0.5).count());
        return dto;
    }

    private Map<String, Object> getPlayersDTO(String key, Object value) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put(key, value);
        return dto;
    }

    private void loadSalvos(Map<String, Object> gameView, Game game) {
        for (GamePlayer current : game.getGamePlayers()) {
            if (current.getId() == game.getId()) {
                gameView.put("salvoes", current.getSalvos().stream().map(this::getSalvoesDTO)
                        .collect(toList()));
            } else {
                gameView.put("enemy_salvoes", current.getSalvos().stream().map(this::getSalvoesDTO)
                        .collect(toList()));
            }
        }
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
}