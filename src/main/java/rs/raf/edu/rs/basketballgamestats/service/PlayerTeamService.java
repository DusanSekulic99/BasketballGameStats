package rs.raf.edu.rs.basketballgamestats.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.requests.InGameSubRequest;
import rs.raf.edu.rs.basketballgamestats.dto.requests.SubstitutionRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.PlayerDto;
import rs.raf.edu.rs.basketballgamestats.model.Player;
import rs.raf.edu.rs.basketballgamestats.model.Player_Game;
import rs.raf.edu.rs.basketballgamestats.model.Player_Team;
import rs.raf.edu.rs.basketballgamestats.repository.PlayerGameRepo;
import rs.raf.edu.rs.basketballgamestats.repository.PlayerTeamRepo;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PlayerTeamService {

    private final PlayerTeamRepo playerTeamRepo;
    private final PlayerGameRepo playerGameRepo;

    public PlayerTeamService(PlayerTeamRepo playerTeamRepo, PlayerGameRepo playerGameRepo) {
        this.playerTeamRepo = playerTeamRepo;
        this.playerGameRepo = playerGameRepo;
    }

    public Mono<List<PlayerDto>> getAllPlayers(Long teamId) {
        return Mono.just(playerTeamRepo.findAllByTeamId(teamId).stream()
                .filter(playerTeam -> isPlayerTeamActive.test(playerTeam.getActive()))
                .map(playerTeam1 -> createPlayerDto(playerTeam1, -1L))
                .collect(Collectors.toList()));
    }

    public List<Player_Team> getAllPlayerTeamsByTeamId(Long teamId) {
        return playerTeamRepo.findAllByTeamId(teamId).stream()
                .filter(playerTeam -> isPlayerTeamActive.test(playerTeam.getActive()))
                .collect(Collectors.toList());
    }

    Predicate<Boolean> isPlayerTeamActive = isActive -> Optional.of(isActive).orElse(Boolean.FALSE);

    private PlayerDto createPlayerDto(Player_Team playerTeam, Long gameId) {
        Optional<Player_Game> playerGame = Optional.of(new Player_Game());
        if(gameId != -1) {
             playerGame = playerGameRepo.findPlayer_GameByPlayerIdAndGameId(playerTeam.getPlayer().getId(), gameId);
        }
        return PlayerDto.builder()
                .id(String.valueOf(playerTeam.getPlayer().getId()))
                .teamId(String.valueOf(playerTeam.getTeam().getId()))
                .starter(playerTeam.isStarter())
                .playing(playerTeam.isPlaying())
                .firstName(playerTeam.getPlayer().getFirstName())
                .lastName(playerTeam.getPlayer().getLastName())
                .jerseyNo(playerTeam.getPlayer().getJerseyNo())
                .position(playerTeam.getPlayer().getPosition().name())
                .points(String.valueOf(playerGame.map(Player_Game::getPoints).orElse(0)))
                .assists(String.valueOf(playerGame.map(Player_Game::getAssists).orElse(0)))
                .rebounds(String.valueOf(playerGame.map(Player_Game::getRebounds).orElse(0)))
                .fouls(String.valueOf(playerGame.map(Player_Game::getFouls).orElse(0)))
                .build();
    }

    public Mono<List<PlayerDto>> substitutePlayers(SubstitutionRequest substitutionRequest) {
        Player_Team oldStarter = playerTeamRepo.findByPlayerId(Long.valueOf(substitutionRequest.getStarterId()));
        Player_Team newStarter = playerTeamRepo.findByPlayerId(Long.valueOf(substitutionRequest.getNewStarterId()));

        oldStarter.setStarter(false);
        oldStarter.setPlaying(false);
        newStarter.setStarter(true);
        newStarter.setPlaying(true);
        playerTeamRepo.saveAllAndFlush(List.of(oldStarter, newStarter));

        return this.getAllPlayers(newStarter.getTeam().getId());

    }

    public PlayerDto getPlayerDto(Long playerId, Long gameId) {
        return Optional.ofNullable(playerTeamRepo.findByPlayerId(playerId))
                .filter(playerTeam -> isPlayerTeamActive.test(playerTeam.getActive()))
                .map(playerTeam -> this.createPlayerDto(playerTeam, gameId))
                .orElse(null);
    }

    public List<Player> substitutePlayersInGame(InGameSubRequest inGameSubRequest) {
        Player_Team oldPlayer = playerTeamRepo.findByPlayerIdAndTeamId(inGameSubRequest.getOldPlayerId(), inGameSubRequest.getTeamId());
        Player_Team newPlayer = playerTeamRepo.findByPlayerIdAndTeamId(inGameSubRequest.getNewPlayerId(), inGameSubRequest.getTeamId());

        Player_Game oldPlayerGame = playerGameRepo.findPlayer_GameByPlayerIdAndGameId(inGameSubRequest.getOldPlayerId(), inGameSubRequest.getGameId())
                .orElseThrow();
        Player_Game newPlayerGame = playerGameRepo.findPlayer_GameByPlayerIdAndGameId(inGameSubRequest.getNewPlayerId(), inGameSubRequest.getGameId())
                .orElseThrow();


        oldPlayer.setPlaying(false);
        newPlayer.setPlaying(true);

        oldPlayerGame.setSubbedOutAt(inGameSubRequest.getPlayedTime() + oldPlayerGame.getGame().getPlayingTime() * (inGameSubRequest.getQuarter() - 1));
        newPlayerGame.setSubbedInAt(inGameSubRequest.getPlayedTime() + newPlayerGame.getGame().getPlayingTime() * (inGameSubRequest.getQuarter() - 1));

        oldPlayerGame.updateSecondsPlayed();

        playerGameRepo.saveAllAndFlush(List.of(oldPlayerGame, newPlayerGame));

        playerTeamRepo.saveAllAndFlush(List.of(oldPlayer, newPlayer));

        return playerTeamRepo.findAllByTeamId(inGameSubRequest.getTeamId()).stream()
                .map(Player_Team::getPlayer)
                .collect(Collectors.toList());
    }

    public List<Player> setStartersPlaying(Long teamId) {
        return playerTeamRepo.findAllByTeamId(teamId).stream()
                .filter(Player_Team::getActive)
                .peek(playerTeam -> playerTeam.setPlaying(playerTeam.isStarter()))
                .map(playerTeamRepo::save)
                .map(Player_Team::getPlayer)
                .collect(Collectors.toList());
    }
}
