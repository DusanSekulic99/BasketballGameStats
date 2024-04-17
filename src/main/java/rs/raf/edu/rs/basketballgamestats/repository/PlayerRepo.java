package rs.raf.edu.rs.basketballgamestats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.edu.rs.basketballgamestats.model.Player;

import java.util.List;

@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {

    @Override
    List<Player> findAll();
}
