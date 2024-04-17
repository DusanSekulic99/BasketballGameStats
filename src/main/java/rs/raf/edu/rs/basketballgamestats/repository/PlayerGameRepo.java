package rs.raf.edu.rs.basketballgamestats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.edu.rs.basketballgamestats.model.Player_Game;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerGameRepo extends JpaRepository<Player_Game, Long> {

    Optional<Player_Game> findPlayer_GameByPlayerIdAndGameId(Long playerId, Long gameId);

    List<Player_Game> findAllPlayer_GameByGameId(Long gameId);
}
