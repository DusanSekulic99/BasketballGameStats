package rs.raf.edu.rs.basketballgamestats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.edu.rs.basketballgamestats.model.Player;
import rs.raf.edu.rs.basketballgamestats.model.Player_Team;

import java.util.List;

@Repository
public interface PlayerTeamRepo extends JpaRepository<Player_Team, Long> {

    List<Player_Team> findAllByTeamId(Long teamId);
    List<Player_Team> findAllByPlayer(Player player);
    Player_Team findByPlayerId(Long playerId);
    Player_Team findByPlayerIdAndTeamId(Long playerId, Long teamId);

    Long countAllByTeamIdAndStarterAndActive(Long teamId, boolean starter, boolean active);
}
