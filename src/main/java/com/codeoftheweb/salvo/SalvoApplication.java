package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository, SalvoRepository salvoRepository,
                                      ScoreRepository scoreRepository) {
        return (args) -> {

            Player player = new Player("tobiasheimboeck@outlook.com", "gregbsej");
            GamePlayer gamePlayer = new GamePlayer(player);
            Ship ship = new Ship("battleship", gamePlayer, Arrays.asList("A1", "A2", "A3"));
            Salvo salvo = new Salvo(gamePlayer, 1, Arrays.asList("C4", "C5"));

            Salvo test = new Salvo(gamePlayer, 2, Collections.singletonList("G6"));
            Salvo test1 = new Salvo(gamePlayer, 3, Arrays.asList("I2", "I3", "I4"));

            Player player1 = new Player("test@mail.com", "ehgueuf");
            GamePlayer gamePlayer1 = new GamePlayer(player1);
            Ship ship1 = new Ship("battleship", gamePlayer1, Arrays.asList("C2", "C3", "C4"));
            Salvo salvo1 = new Salvo(gamePlayer1, 1, Collections.singletonList("C3"));

            Player player2 = new Player("gameplayer@mail.com", "testpw");
            GamePlayer gamePlayer2 = new GamePlayer(player2);
            Ship ship2 = new Ship("battleship", gamePlayer2, Arrays.asList("I6", "I7", "I8"));
            Salvo salvo2 = new Salvo(gamePlayer2, 1, Collections.singletonList("A1"));

            Game game = new Game();
            game.addGamePlayer(gamePlayer);
            game.addGamePlayer(gamePlayer1);
            game.addGamePlayer(gamePlayer2);

            Score score = new Score(player, game, 1.0, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            playerRepository.save(player);
            gameRepository.save(game);
            gamePlayerRepository.save(gamePlayer);
            shipRepository.save(ship);
            salvoRepository.save(salvo);
            scoreRepository.save(score);
            playerRepository.save(player2);
            gamePlayerRepository.save(gamePlayer2);
            shipRepository.save(ship2);
            salvoRepository.save(salvo2);
            playerRepository.save(player1);
            gamePlayerRepository.save(gamePlayer1);
            shipRepository.save(ship1);
            salvoRepository.save(salvo1);
            salvoRepository.save(test);
            salvoRepository.save(test1);
        };
    }
}