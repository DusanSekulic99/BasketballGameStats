package rs.raf.edu.rs.basketballgamestats.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.requests.SubstitutionRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.PlayerDto;
import rs.raf.edu.rs.basketballgamestats.service.PlayerTeamService;

import java.util.List;

@RestController
@RequestMapping("/player-team")
public class PlayerTeamController {

    private final PlayerTeamService playerTeamService;

    public PlayerTeamController(PlayerTeamService playerTeamService) {
        this.playerTeamService = playerTeamService;
    }


    @PutMapping
    @RequestMapping("/substitute")
    public Mono<List<PlayerDto>> substitutePlayers(@RequestBody SubstitutionRequest substitutionRequest) {
        return playerTeamService.substitutePlayers(substitutionRequest);
    }
}
