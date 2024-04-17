package rs.raf.edu.rs.basketballgamestats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.edu.rs.basketballgamestats.model.Team;

import java.util.Optional;

@Repository
public interface TeamRepo extends JpaRepository<Team, Long> {

    @Override
    Optional<Team> findById(Long aLong);
}
