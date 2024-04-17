package rs.raf.edu.rs.basketballgamestats.service;

import com.itextpdf.text.DocumentException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreateGameRequest;
import rs.raf.edu.rs.basketballgamestats.dto.requests.InGameSubRequest;
import rs.raf.edu.rs.basketballgamestats.dto.requests.TimeoutRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.GameDto;
import rs.raf.edu.rs.basketballgamestats.dto.responses.TeamDto;
import rs.raf.edu.rs.basketballgamestats.model.*;
import rs.raf.edu.rs.basketballgamestats.repository.GameRepo;
import rs.raf.edu.rs.basketballgamestats.repository.PlayerGameRepo;
import rs.raf.edu.rs.basketballgamestats.repository.TeamRepo;
import rs.raf.edu.rs.basketballgamestats.utils.ReportUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameService {

    private final GameRepo gameRepo;
    private final PlayerGameRepo playerGameRepo;

    private final TeamRepo teamRepo;

    private final PlayerTeamService playerTeamService;

    private final SimpMessageSendingOperations template;


    public GameService(GameRepo gameRepo, PlayerGameRepo playerGameRepo, TeamRepo teamRepo, PlayerTeamService playerTeamService, SimpMessageSendingOperations template) {
        this.gameRepo = gameRepo;
        this.playerGameRepo = playerGameRepo;
        this.teamRepo = teamRepo;
        this.playerTeamService = playerTeamService;
        this.template = template;
    }

    public GameDto createGame(CreateGameRequest request) {
        Team homeTeam = teamRepo.findById(request.getHomeTeamId()).orElse(null);
        Team awayTeam = teamRepo.findById(request.getAwayTeamId()).orElse(null);

        if (homeTeam == null || awayTeam == null) return null;

        homeTeam.setPlayerList(playerTeamService.setStartersPlaying(homeTeam.getId()));
        awayTeam.setPlayerList(playerTeamService.setStartersPlaying(awayTeam.getId()));

        Game game = gameRepo.save(new Game(homeTeam, awayTeam, request.getPlayingTime() * 60));

        homeTeam.getHomeGames().add(game);
        awayTeam.getAwayGames().add(game);

        teamRepo.save(homeTeam);
        teamRepo.save(awayTeam);

        Stream.concat(homeTeam.getPlayerList().stream(), awayTeam.getPlayerList().stream())
                .map(player -> new Player_Game(player, game))
                .forEach(playerGameRepo::save);

        return createGameDto(game);
    }

    public GameDto createGameDto(Game game) {
        return GameDto.builder()
                .gameId(game.getId())
                .homeTeam(createTeamDto(game.getHomeTeam(), game.getId()))
                .awayTeam(createTeamDto(game.getAwayTeam(), game.getId()))
                .playingTimeLeft(game.getTimeLeft())
                .currentQuarter(game.getCurrentQuarter())
                .playingTime(game.getPlayingTime())
                .currentScore(getAllPoints(game, game.getHomeTeam()) + ":" + getAllPoints(game, game.getAwayTeam()))
                .awayTeamBonus(game.getAwayTeamBonus())
                .homeTeamBonus(game.getHomeTeamBonus())
                .homeTeamTimeout(game.getHomeTeamTimeout())
                .awayTeamTimeout(game.getAwayTeamTimeout())
                .homeTeamBonus(game.getHomeTeamBonus())
                .status(game.getStatus())
                .stopTimer(shouldStopTimer.apply(game.getStatus()))
                .build();
    }

    Function<Game_Status, Boolean> shouldStopTimer = status -> !status.equals(Game_Status.PLAYING);

    private String getAllPoints(Game game, Team team) {
        return team.getPlayerList().stream()
                .map(player -> playerGameRepo.findPlayer_GameByPlayerIdAndGameId(player.getId(), game.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Player_Game::getPoints)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum)
                .toString();
    }

    private TeamDto createTeamDto(Team team, Long gameId) {
        return TeamDto.builder()
                .id(team.getId())
                .badge(team.getBadge())
                .name(team.getName())
                .players(team.getPlayerList().stream()
                        .map((player -> playerTeamService.getPlayerDto(player.getId(), gameId)))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                )
                .build();
    }

    public List<GameDto> getAllGames() {
        return gameRepo.findAll().stream()
                .filter(game -> game.getStatus() != Game_Status.DONE || game.getId().equals(34L))
                .map(this::createGameDto)
                .collect(Collectors.toList());
    }

    public GameDto getGame(Long gameId) {
        return gameRepo.findById(gameId).map(this::createGameDto).orElseThrow();
    }

    public GameDto timeout(TimeoutRequest request) {
        Game game = gameRepo.findById(request.getGameId()).orElseThrow();
        game.setTimeLeft(request.getPlayingTimeLeft());
        boolean isHomeTeam = Objects.equals(game.getHomeTeam().getId(), request.getTeamId());
        if (isHomeTeam && game.getHomeTeamTimeout() > 0) {
            game.setStatus(Game_Status.TIMEOUT);
            game.updateHomeTeamTimeout();
        }

        if (!isHomeTeam && game.getAwayTeamTimeout() > 0) {
            game.setStatus(Game_Status.TIMEOUT);
            game.updateAwayTeamTimeout();
        }
        gameRepo.save(game);
        template.convertAndSend("/topic/game", "game updated successfully");
        return createGameDto(game);
    }

    public GameDto endQuarter(Long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow();
        Integer currentQuarter = game.getCurrentQuarter();
        if (currentQuarter.equals(4)) {
            game.setStatus(Game_Status.DONE);
            updatePlayersInGameTime(game);
        } else if (currentQuarter.equals(2)) {
            game.setCurrentQuarter(currentQuarter + 1);
            game.setTimeLeft(game.getPlayingTime());
            game.setStatus(Game_Status.HALFTIME);
        } else {
            game.setCurrentQuarter(currentQuarter + 1);
            game.setTimeLeft(game.getPlayingTime());
            game.setStatus(Game_Status.BREAK);
        }
        gameRepo.save(game);
        template.convertAndSend("/topic/game", "game updated successfully");
        return createGameDto(game);
    }

    private void updatePlayersInGameTime(Game game) {
        List<Player_Team> playersPlaying = Stream.concat(this.playerTeamService.getAllPlayerTeamsByTeamId(game.getHomeTeam().getId())
                .stream().filter(Player_Team::isPlaying),
                this.playerTeamService.getAllPlayerTeamsByTeamId(game.getAwayTeam().getId())
                        .stream().filter(Player_Team::isPlaying))
                .collect(Collectors.toList());

        List<Player_Game> playerGameList = new ArrayList<>();

        for (Player_Team playerTeam : playersPlaying) {
            Player_Game playerGame = playerGameRepo.findPlayer_GameByPlayerIdAndGameId(playerTeam.getPlayer().getId(), game.getId())
                    .orElseThrow();

            playerGame.setSubbedOutAt(game.getPlayingTime() * game.getCurrentQuarter());

            playerGame.updateSecondsPlayed();

            playerGameList.add(playerGame);
        }

        playerGameRepo.saveAllAndFlush(playerGameList);
    }

    public GameDto pauseGame(TimeoutRequest timeoutRequest) {
        Game game = gameRepo.findById(timeoutRequest.getGameId()).orElseThrow();
        game.setStatus(Game_Status.PAUSE);
        game.setTimeLeft(timeoutRequest.getPlayingTimeLeft());
        gameRepo.save(game);
        template.convertAndSend("/topic/game", "game updated successfully");
        return createGameDto(game);
    }

    public GameDto playGame(Long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow();
        game.setStatus(Game_Status.PLAYING);
        gameRepo.save(game);
        template.convertAndSend("/topic/game", "game updated successfully");
        return createGameDto(game);
    }


    public GameDto updateTime(TimeoutRequest request) {
        Game game = gameRepo.findById(request.getGameId()).orElseThrow();
        game.setTimeLeft(request.getPlayingTimeLeft());
        gameRepo.save(game);
        template.convertAndSend("/topic/game", "game updated successfully");
        return createGameDto(game);
    }

    public GameDto substituteInGame(InGameSubRequest inGameSubRequest) {
        Game game = gameRepo.findById(inGameSubRequest.getGameId()).orElseThrow();
        Team team = game.getHomeTeam().getId().equals(inGameSubRequest.getTeamId()) ? game.getHomeTeam() : game.getAwayTeam();

        team.setPlayerList(this.playerTeamService.substitutePlayersInGame(inGameSubRequest));

        if (game.getHomeTeam().getId().equals(inGameSubRequest.getTeamId())) {
            game.setHomeTeam(team);
        } else {
            game.setAwayTeam(team);
        }

        gameRepo.save(game);

        template.convertAndSend("/topic/game", "game updated successfully");

        return createGameDto(game);
    }

    public byte[] getReport(Long gameId) throws DocumentException {
        Game game = gameRepo.findById(gameId).orElseThrow();

        List<Player_Game> players = playerGameRepo.findAllPlayer_GameByGameId(gameId);

        List<Player_Game> homePlayers = players.stream()
                .filter(playerGame -> playerGame.getPlayerTeam().getId().equals(game.getHomeTeam().getId()))
                .collect(Collectors.toList());

        List<Player_Game> awayPlayers = players.stream()
                .filter(playerGame -> playerGame.getPlayerTeam().getId().equals(game.getAwayTeam().getId()))
                .collect(Collectors.toList());

        Map<Team, List<Player_Game>> teamPlayerMap = new HashMap<>();

        teamPlayerMap.put(game.getHomeTeam(), homePlayers);
        teamPlayerMap.put(game.getAwayTeam(), awayPlayers);

        String homePoints = getAllPoints(game, game.getHomeTeam());
        String awayPoints = getAllPoints(game, game.getAwayTeam());

        return ReportUtil.generatePdfReport(teamPlayerMap, game, homePoints, awayPoints).toByteArray();
    }
}
