package rs.raf.edu.rs.basketballgamestats.service;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rs.raf.edu.rs.basketballgamestats.dto.responses.GameDto;
import rs.raf.edu.rs.basketballgamestats.dto.responses.PlayerGameDto;
import rs.raf.edu.rs.basketballgamestats.model.*;
import rs.raf.edu.rs.basketballgamestats.repository.PlayerGameRepo;

import java.util.Objects;
import java.util.Optional;

@Service
public class PlayerGameService {

    private final PlayerGameRepo playerGameRepo;
    private final GameService gameService;

    private final SimpMessageSendingOperations template;

    public PlayerGameService(PlayerGameRepo playerGameRepo, GameService gameService, SimpMessageSendingOperations template) {
        this.playerGameRepo = playerGameRepo;
        this.gameService = gameService;
        this.template = template;
    }

    public GameDto addPoints(Long gameId, Long playerId, Integer points) throws Exception {
        return updateStats(gameId, playerId, points, StatEnum.POINTS);
    }



    public GameDto addRebound(Long gameId, Long playerId) throws Exception {
        return updateStats(gameId, playerId, 0, StatEnum.REBOUNDS);
    }

    public GameDto addAssist(Long gameId, Long playerId) throws Exception {
        return updateStats(gameId, playerId, 0, StatEnum.ASSIST);
    }

    public GameDto addFoul(Long gameId, Long playerId) throws Exception {
        return updateStats(gameId, playerId, 0, StatEnum.FOUL);
    }

    public GameDto addMissedShot(Long gameId, Long playerId) throws Exception {
        return updateStats(gameId, playerId, 0, StatEnum.MISS);
    }

    public GameDto updateStats(Long gameId, Long playerId, Integer points, StatEnum statEnum) throws Exception {
        Player_Game playerGame = playerGameRepo.findPlayer_GameByPlayerIdAndGameId(playerId, gameId)
                .orElse(null);

        checkPlayerGame(playerGame);

            switch (statEnum) {
                case POINTS: {
                    playerGame.updatePoints(points);
                    break;
                }
                case REBOUNDS: {
                    playerGame.updateRebounds();
                    break;
                }
                case ASSIST: {
                    playerGame.updateAssists();
                    break;
                }
                case FOUL: {
                    playerGame.updateFouls();
                    if (playerGame.getPlayerTeam().equals(playerGame.getGame().getHomeTeam())) {
                        playerGame.getGame().updateHomeTeamBonus();
                    } else {
                        playerGame.getGame().updateAwayTeamBonus();
                    }
                    break;
                }
                default: {
                    throw new Exception();
                }
            }

            playerGameRepo.save(playerGame);

            GameDto gameDto = gameService.createGameDto(playerGame.getGame());

            template.convertAndSend("/topic/game", "game updated successfully");

            return gameDto;
    }



    private PlayerGameDto makePlayerGameDto(Player_Game playerGame) {
        Game game = playerGame.getGame();
        return PlayerGameDto.builder()
                .gameId(playerGame.getGame().getId())
                .playerId(playerGame.getPlayer().getId())
                .points(playerGame.getPoints())
                .assists(playerGame.getAssists())
                .rebounds(playerGame.getRebounds())
                .fouls(playerGame.getFouls())
                .currentScore(getAllPoints(game, game.getHomeTeam()) + ":" + getAllPoints(game, game.getAwayTeam()))
                .build();
    }

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

    private void checkPlayerGame(Player_Game playerGame) throws Exception {
        if (playerGame == null || playerGame.getGame().getStatus().equals(Game_Status.DONE)) throw new Exception();
    }

}
