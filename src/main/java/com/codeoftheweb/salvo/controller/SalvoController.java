package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository repository;

    @RequestMapping(value = "/games", headers = "/")
    public Set<Game> getGames(List<Game> games) {
        return new HashSet<>(games);
    }
}