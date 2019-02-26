package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
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

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

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
        return new ResponseEntity<>(getPlayersDTO(player.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication auth) {
        Player player = playerRepository.findByUserName(auth.getName());
        if (player == null) {
            return new ResponseEntity<>(createResponse("error", "Player is null"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = new Game();
            GamePlayer gamePlayer = new GamePlayer(player, game);
            gameRepository.save(game);
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(createResponse("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/game/{id}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long id, Authentication auth) {
        Player currentUser = playerRepository.findByUserName(auth.getName());
        if (currentUser == null) {
            return new ResponseEntity<>(createResponse("error", "Player is null"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameRepository.getOne(id);
            if (game == null) {
                return new ResponseEntity<>(createResponse("error", "No such game"), HttpStatus.FORBIDDEN);
            } else {
                if (game.getGamePlayers().size() == 1) {
                    GamePlayer newGamePlayer = new GamePlayer(currentUser, game);
                    gamePlayerRepository.save(newGamePlayer);
                    return new ResponseEntity<>(createResponse("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>(createResponse("error", "Game is full"), HttpStatus.FORBIDDEN);
                }
            }
        }
    }

    @RequestMapping(value = "/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable long id, Authentication auth) {
        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
        if (gamePlayer.getPlayer() == playerRepository.findByUserName(auth.getName())) {
            Map<String, Object> gameView = new HashMap<>();
            gameView.put("games", getGameDTO(gamePlayer.getGame()));
            gameView.put("ships", gamePlayer.getShips().stream().map(this::getShipDTO).collect(toList()));

            gameView.put("salvoes", gamePlayer.getSalvos().stream().map(this::getSalvoesDTO)
                    .collect(toList()));

            if (gamePlayer.getGame().getGamePlayers().size() == 2) {
                gameView.put("enemy_salvoes", getEnemy(gamePlayer, auth).getSalvos().stream().map(this::getSalvoesDTO)
                        .collect(toList()));
            }

            return new ResponseEntity<>(createResponse("gameview", gameView), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(createResponse("error", "un"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Object> placeSalvo(@PathVariable long gamePlayerId, @RequestBody List<String> salvo, Authentication auth) {
        Player currentUser = playerRepository.findByUserName(auth.getName());

        if (currentUser == null) {
            return new ResponseEntity<>(createResponse("error", "Player is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepository.getOne(gamePlayerId);

        if (gamePlayer == null) {
            return new ResponseEntity<>(createResponse("error", "GamePlayer is null"), HttpStatus.UNAUTHORIZED);
        }

        if (currentUser != gamePlayer.getPlayer()) {
            return new ResponseEntity<>(createResponse("error", "Current User and GamePlayer are not equal"), HttpStatus.UNAUTHORIZED);
        }

        int currentTurn = gamePlayer.getSalvos().size() + 1;
        Salvo salvoObject = new Salvo(gamePlayer, currentTurn, salvo);

        salvoRepository.save(salvoObject);

        return new ResponseEntity<>(createResponse("created", "Salvo added successfully"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/games/players/{gpid}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable long gpid, @RequestBody List<Ship> ships, Authentication auth) {
        Player currentUser = playerRepository.findByUserName(auth.getName());

        if (currentUser == null) {
            return new ResponseEntity<>(createResponse("error", "Player is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepository.getOne(gpid);

        if (gamePlayer == null) {
            return new ResponseEntity<>(createResponse("error", "GamePlayer is null"), HttpStatus.UNAUTHORIZED);
        }

        if (currentUser != gamePlayer.getPlayer()) {
            return new ResponseEntity<>(createResponse("error", "Current User and GamePlayer are not equal"), HttpStatus.UNAUTHORIZED);
        }

        if (!gamePlayer.getShips().isEmpty()) {
            return new ResponseEntity<>(createResponse("error", "User has already placed ships"), HttpStatus.FORBIDDEN);
        }

        ships.forEach(ship -> {
            gamePlayer.addShip(ship);
            shipRepository.save(ship);
        });

        return new ResponseEntity<>(createResponse("created", "Ships placed successfully"), HttpStatus.CREATED);
    }

    private GamePlayer getEnemy(GamePlayer player, Authentication auth) {
        Player currentPlayer = playerRepository.findByUserName(auth.getName());
        GamePlayer response = null;
        for (GamePlayer current : player.getGame().getGamePlayers()) {
            if (current.getPlayer() != currentPlayer) {
                response = current;
            }
        }
        return response;
    }

    private Map<String, Object> createResponse(String key, Object value) {
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

    private Map<String, Object> getPlayersDTO(Object value) {
        final Map<String, Object> dto = new HashMap<>();
        dto.put("id", value);
        return dto;
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