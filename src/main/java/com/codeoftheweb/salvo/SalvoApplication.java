package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.repository.ShipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository) {
        return (args) -> {
            Player player = new Player("tobiasheimboeck@outlook.com");
            GamePlayer gamePlayer = new GamePlayer(player);
            Player player3 = new Player("test@game.com");
            GamePlayer gamePlayer4 = new GamePlayer(player3);
            Game game = new Game();
            game.addGamePlayer(gamePlayer);
            game.addGamePlayer(gamePlayer4);

            playerRepository.save(player);
            playerRepository.save(player3);
            gameRepository.save(game);
            gamePlayerRepository.save(gamePlayer);
            gamePlayerRepository.save(gamePlayer4);

            Player player1 = new Player("test@mail.com");
            Player player2 = new Player("test@test.com");
            GamePlayer gamePlayer1 = new GamePlayer(player1);
            GamePlayer gamePlayer2 = new GamePlayer(player2);
            Game game1 = new Game();
            game1.addGamePlayer(gamePlayer1);
            game1.addGamePlayer(gamePlayer2);

            playerRepository.save(player1);
            playerRepository.save(player2);
            gameRepository.save(game1);
            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);

            Ship ship = new Ship("cruiser", gamePlayer, Arrays.asList("H1", "H2", "H3", "B4", "C5", "D6", "G8", "H8", "I8", "J3", "J4"));
            Ship ship1 = new Ship("battleship", gamePlayer1, Arrays.asList("H1", "H2", "H3"));
            Ship ship2 = new Ship("battleship", gamePlayer2, Arrays.asList("H1", "H2", "H3"));

            shipRepository.save(ship);
            shipRepository.save(ship1);
            shipRepository.save(ship2);

        };
    }
}