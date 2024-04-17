package rs.raf.edu.rs.basketballgamestats.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreatePlayerRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.PlayerDto;
import rs.raf.edu.rs.basketballgamestats.exceptions.MaximumStartersException;
import rs.raf.edu.rs.basketballgamestats.service.PlayerService;
import rs.raf.edu.rs.basketballgamestats.service.PlayerTeamService;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerTeamService playerTeamService;

    public PlayerController(PlayerService playerService, PlayerTeamService playerTeamService) {
        this.playerService = playerService;
        this.playerTeamService = playerTeamService;
    }

    @PostMapping
    public Mono<CreatePlayerRequest> createPlayer(@RequestBody CreatePlayerRequest request) {
        return playerService.createPlayer(request);
    }

    @DeleteMapping
    @RequestMapping("/{playerId}")
    public Mono<List<PlayerDto>> removePlayer(@PathVariable Long playerId) {

        return playerTeamService.getAllPlayers(playerService.removePlayer(playerId));
    }

    @ExceptionHandler(MaximumStartersException.class)
    public ResponseEntity<String> maximumStartersException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Maximum 5 players can be starter on team!");
    }
}
