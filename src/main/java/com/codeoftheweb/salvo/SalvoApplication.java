package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
        return (args) -> {
            Player player = new Player("tobiasheimboeck@outlook.com");
            GamePlayer gamePlayer = new GamePlayer(player);
            Game game = new Game();
            game.addGamePlayer(gamePlayer);

            playerRepository.save(player);
            gameRepository.save(game);
            gamePlayerRepository.save(gamePlayer);

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
        };
    }
}