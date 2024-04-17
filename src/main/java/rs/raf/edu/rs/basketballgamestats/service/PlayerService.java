package rs.raf.edu.rs.basketballgamestats.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreatePlayerRequest;
import rs.raf.edu.rs.basketballgamestats.exceptions.MaximumStartersException;
import rs.raf.edu.rs.basketballgamestats.factory.PlayerFactory;
import rs.raf.edu.rs.basketballgamestats.model.Player;
import rs.raf.edu.rs.basketballgamestats.model.Player_Team;
import rs.raf.edu.rs.basketballgamestats.model.Team;
import rs.raf.edu.rs.basketballgamestats.repository.PlayerRepo;
import rs.raf.edu.rs.basketballgamestats.repository.PlayerTeamRepo;
import rs.raf.edu.rs.basketballgamestats.repository.TeamRepo;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepo playerRepo;
    private final TeamRepo teamRepo;

    private final PlayerFactory playerFactory;
    private final PlayerTeamRepo playerTeamRepo;

    public PlayerService(PlayerRepo playerRepo, TeamRepo teamRepo, PlayerFactory playerFactory,
                         PlayerTeamRepo playerTeamRepo) {
        this.playerRepo = playerRepo;
        this.teamRepo = teamRepo;
        this.playerFactory = playerFactory;
        this.playerTeamRepo = playerTeamRepo;
    }

    public Mono<CreatePlayerRequest> createPlayer(CreatePlayerRequest request) {
        boolean starter = request.isStarter();
        if (playerTeamRepo.countAllByTeamIdAndStarterAndActive(
                request.getTeamId(), true, true) < 5) {
            starter = true;
        } else if (starter) {
            return Mono.error(new MaximumStartersException());
        }
        Team team = teamRepo.findById(request.getTeamId()).orElseThrow();
        Player player = playerRepo.save(playerFactory.createPlayer(request, team));
        Player_Team playerTeam = new Player_Team();
        playerTeam.setTeam(team);
        playerTeam.setPlayer(player);
        playerTeam.setActive(true);
        playerTeam.setStarter(starter);
        playerTeamRepo.save(playerTeam);
        request.setId(player.getId());
        request.setPosition(player.getPosition());
        request.setStarter(playerTeam.isStarter());
        return Mono.just(request);
    }

    public Long removePlayer(Long playerId) {
        return this.playerRepo.findById(playerId)
                .map(this::setPlayerInactive)
                .map(Player_Team::getTeam)
                .map(Team::getId)
                .orElseThrow();
    }

    private Player_Team makeRandomPlayerSubstitution(Player_Team playerTeam) {
        if (playerTeam.isStarter()) {
            Optional<Player_Team> newStarter = playerTeamRepo.findAllByTeamId(playerTeam.getTeam().getId()).stream()
                    .filter(playerTeam1 -> !playerTeam1.isStarter() && playerTeam1.getActive())
                    .findFirst();
            playerTeam.setStarter(false);
            playerTeamRepo.saveAndFlush(playerTeam);
            if (newStarter.isPresent()) {
                Player_Team presentStarter = newStarter.get();
                presentStarter.setStarter(true);
                playerTeamRepo.saveAndFlush(presentStarter);
            }

        }
        return playerTeam;
    }

    public Player_Team setPlayerInactive(Player player) {
        Player_Team playerTeam = playerTeamRepo.findAllByPlayer(player)
                .stream()
                .filter(Player_Team::getActive)
                .map(this::makeRandomPlayerSubstitution)
                .findFirst()
                .orElseThrow();
        playerTeam.setActive(false);
        playerTeamRepo.saveAndFlush(playerTeam);
        return playerTeam;
    }
}
