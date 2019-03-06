package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import com.codeoftheweb.salvo.state.GameStateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Consumer;

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

    private GameStateManager gameStateManager = new GameStateManager();

    //<editor-fold desc="getPlayers">
    @RequestMapping(value = "/scoreboard")
    public List<Object> getPlayers() {
        return playerRepository.findAll().stream().map(this::getScoreDTO).collect(toList());
    }
    //</editor-fold>

    //<editor-fold desc="getAll">
    @RequestMapping(value = "/games")
    public Map<String, Object> getAll() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("games", gameRepository.findAll().stream().map(this::makeDTO).collect(toList()));
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="registerPlayer">
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
    //</editor-fold>

    //<editor-fold desc="createGame">
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
    //</editor-fold>

    //<editor-fold desc="joinGame">
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
    //</editor-fold>

    //<editor-fold desc="getGame">
    @RequestMapping(value = "/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable long id, Authentication auth) {
        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);
        if (gamePlayer.getPlayer() == playerRepository.findByUserName(auth.getName())) {
            Map<String, Object> gameView = new HashMap<>();
            gameView.put("games", getGameDTO(gamePlayer.getGame()));
            gameView.put("ships", gamePlayer.getShips().stream().map(this::getShipDTO).collect(toList()));
            gameView.put("infos", getInformations(gamePlayer, auth));
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
    //</editor-fold>

    //<editor-fold desc="placeSalvo">
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
    //</editor-fold>

    //<editor-fold desc="placeShips">
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
    //</editor-fold>

    //<editor-fold desc="getInformations">
    private Map<String, Object> getInformations(GamePlayer gamePlayer, Authentication auth) {
        final Map<String, Object> infos = new HashMap<>();
        final Map<Ship, Integer> remainingShipLocations = new HashMap<>();

        final List<Salvo> salvos = gamePlayer.getSalvos().stream()
                .sorted(Comparator.comparingInt(Salvo::getTurn))
                .collect(toList());

        this.ifEnemyIsPresent(gamePlayer, auth, enemy -> {

            Set<Ship> enemyShips = enemy.getShips();

            for (Ship ship : enemyShips) {
                remainingShipLocations.put(ship, ship.getLocations().size());
            }

            infos.put("player_hitted_ships", this.getHits(gamePlayer, auth));

            for (Salvo salvo : salvos) {
                System.out.println(this.getSunk(enemyShips, salvo, remainingShipLocations));
                infos.put("sunken_ships", this.getSunk(enemyShips, salvo, remainingShipLocations));
            }
        });

        return infos;
    }
    //</editor-fold>

    //<editor-fold desc="getSunk">
    private List<String> getSunk(Set<Ship> ships, Salvo salvo, Map<Ship, Integer> remainingShipLocations) {
        final List<String> response = new ArrayList<>();
        for (Ship current : ships) {

            int size = remainingShipLocations.get(current);

            for (String sLocation : salvo.getLocations()) {
                String shotLocation = "P" + this.removeFirstChar(sLocation);
                for (String shipLocation : current.getLocations()) {
                    if (shotLocation.equals(shipLocation)) {
                        size--;

                        if (size == 0) {
                            response.add(current.getType());
                        }
                    }
                }
            }
        }
        return response;
    }
    //</editor-fold>

    //<editor-fold desc="getHits">
    private List<String> getHits(GamePlayer gamePlayer, Authentication auth) {
        final List<String> response = new ArrayList<>();
        final GamePlayer enemy = this.getEnemy(gamePlayer, auth);
        final List<String> salvos = this.getSalvoLocations(gamePlayer);
        final List<String> ships = this.getShipLocations(enemy);

        for (String salvo : salvos) {
            String s = removeFirstChar(salvo);
            if (ships.contains("P" + s)) {
                if (!response.contains("E" + s)) {
                    response.add("E" + s);
                }
            }
        }

        return response;
    }
    //</editor-fold>

    //<editor-fold desc="removeFirstChar">
    private String removeFirstChar(String s) {
        return s.substring(1);
    }
    //</editor-fold>

    //<editor-fold desc="getSalvoLocations">
    private List<String> getSalvoLocations(GamePlayer gamePlayer) {
        final List<String> response = new ArrayList<>();
        for (Salvo salvo : gamePlayer.getSalvos()) {
            response.addAll(salvo.getLocations());
        }
        return response;
    }
    //</editor-fold>

    //<editor-fold desc="getShipLocations">
    private List<String> getShipLocations(GamePlayer gamePlayer) {
        final List<String> response = new ArrayList<>();
        for (Ship ship : gamePlayer.getShips()) {
            response.addAll(ship.getLocations());
        }
        return response;
    }
    //</editor-fold>

    //<editor-fold desc="getEnemy">
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
    //</editor-fold>

    //<editor-fold desc="createResponse">
    private Map<String, Object> createResponse(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    //</editor-fold>

    //<editor-fold desc="makeDTO">
    private Map<String, Object> makeDTO(Game game) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGPDTO)
                .collect(toList()));
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="getGPDTO">
    private Map<String, Object> getGPDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("gpid", gamePlayer.getId());
        dto.put("id", gamePlayer.getPlayer().getId());
        dto.put("name", gamePlayer.getPlayer().getUserName());
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="getScoreDTO">
    private Map<String, Object> getScoreDTO(Player player) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("player", player.getUserName());
        dto.put("total", player.getScores().stream().mapToDouble(Score::getScore).sum());
        dto.put("wins", player.getScores().stream().filter(score -> score.getScore() == 1.0).count());
        dto.put("losses", player.getScores().stream().filter(score -> score.getScore() == 0.0).count());
        dto.put("ties", player.getScores().stream().filter(score -> score.getScore() == 0.5).count());
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="getPlayersDTO">
    private Map<String, Object> getPlayersDTO(Object value) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", value);
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="getSalvoesDTO">
    private Map<String, Object> getSalvoesDTO(Salvo salvo) {
        Map<String, Object> gameView = new HashMap<>();
        gameView.put("turn", salvo.getTurn());
        gameView.put("player", salvo.getPlayer().getId());
        gameView.put("locations", salvo.getLocations());
        return gameView;
    }
    //</editor-fold>

    //<editor-fold desc="getGameDTO">
    private Map<String, Object> getGameDTO(Game game) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGamePlayerDTO)
                .collect(toList()));
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="getShipDTO">
    private Map<String, Object> getShipDTO(Ship ship) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="getGamePlayerDTO">
    private Map<String, Object> getGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", getPlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="getPlayerDTO">
    private Map<String, Object> getPlayerDTO(Player player) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="ifUserIsLoggedIn">
    private void ifUserIsLoggedIn(GamePlayer gamePlayer, Authentication auth, Consumer<GamePlayer> action) {
        if (gamePlayer.getPlayer() == playerRepository.findByUserName(auth.getName())) {
            action.accept(gamePlayer);
        } else {
            throw new UnsupportedOperationException("Player is not logged in");
        }
    }
    //</editor-fold>

    //<editor-fold desc="ifEnemyIsPresent">
    private void ifEnemyIsPresent(GamePlayer gamePlayer, Authentication auth, Consumer<GamePlayer> action) {
        if (this.getEnemy(gamePlayer, auth) != null) {
            action.accept(this.getEnemy(gamePlayer, auth));
        }
    }
    //</editor-fold>
}