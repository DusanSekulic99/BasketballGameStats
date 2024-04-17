package rs.raf.edu.rs.basketballgamestats.controller;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.responses.GameDto;
import rs.raf.edu.rs.basketballgamestats.service.PlayerGameService;

@RestController
@RequestMapping("/player-game")
public class PlayerGameController {

    private final PlayerGameService playerGameService;
    private final SimpMessageSendingOperations messagingTemplate;

    public PlayerGameController(PlayerGameService playerGameService, SimpMessageSendingOperations messagingTemplate) {
        this.playerGameService = playerGameService;
        this.messagingTemplate = messagingTemplate;
    }

    @PutMapping
    @RequestMapping("/{gameId}/{playerId}/points/{numberOfPoints}")
    public Mono<GameDto> updatePoints(@PathVariable Long gameId,
                                      @PathVariable Long playerId,
                                      @PathVariable Integer numberOfPoints) throws Exception {
        return Mono.just(playerGameService.addPoints(gameId, playerId, numberOfPoints));

    }


    @PutMapping
    @RequestMapping("/{gameId}/{playerId}/rebound")
    public Mono<GameDto> updateRebounds(@PathVariable Long gameId,
                                        @PathVariable Long playerId) throws Exception {

        return Mono.just(playerGameService.addRebound(gameId, playerId));

    }

    @PutMapping
    @RequestMapping("/{gameId}/{playerId}/assist")
    public Mono<GameDto> updateAssists(@PathVariable Long gameId,
                                       @PathVariable Long playerId) throws Exception {
        GameDto response = playerGameService.addAssist(gameId, playerId);
        return Mono.just(response);

    }

    @PutMapping
    @RequestMapping("/{gameId}/{playerId}/foul")
    public Mono<GameDto> updateFouls(@PathVariable Long gameId,
                                     @PathVariable Long playerId) throws Exception {
        return Mono.just(playerGameService.addFoul(gameId, playerId));
    }

    @PutMapping
    @RequestMapping("/{gameId}/{playerId}/miss")
    public Mono<GameDto> updateShots(@PathVariable Long gameId,
                                     @PathVariable Long playerId) throws Exception {
        return Mono.just(playerGameService.addMissedShot(gameId, playerId));
    }

}
