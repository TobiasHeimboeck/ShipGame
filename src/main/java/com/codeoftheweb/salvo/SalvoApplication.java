package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

            Game game = new Game();
            game.addGamePlayer(gamePlayer);
            game.addGamePlayer(gamePlayer1);

            Score score = new Score(player, game, 1.0, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            playerRepository.save(player);
            gameRepository.save(game);
            gamePlayerRepository.save(gamePlayer);
            shipRepository.save(ship);
            salvoRepository.save(salvo);
            scoreRepository.save(score);
            playerRepository.save(player1);
            gamePlayerRepository.save(gamePlayer1);
            shipRepository.save(ship1);
            salvoRepository.save(salvo1);
            salvoRepository.save(test);
            salvoRepository.save(test1);
        };
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player person = playerRepository.findByUserName(inputName);
            if (person != null) {
                return new User(person.getUserName(), person.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/**").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/scoreboard").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/rest/**").denyAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}