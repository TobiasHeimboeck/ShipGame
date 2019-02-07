package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

}