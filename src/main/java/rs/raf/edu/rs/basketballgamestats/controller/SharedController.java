package rs.raf.edu.rs.basketballgamestats.controller;

import com.itextpdf.text.DocumentException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.responses.GameDto;
import rs.raf.edu.rs.basketballgamestats.dto.responses.PlayerDto;
import rs.raf.edu.rs.basketballgamestats.dto.responses.TeamDto;
import rs.raf.edu.rs.basketballgamestats.service.GameService;
import rs.raf.edu.rs.basketballgamestats.service.PlayerTeamService;
import rs.raf.edu.rs.basketballgamestats.service.TeamService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/shared")
public class SharedController {

    private final GameService gameService;
    private final PlayerTeamService playerTeamService;
    private final TeamService teamService;

    public SharedController(GameService gameService, PlayerTeamService playerTeamService, TeamService teamService) {
        this.gameService = gameService;
        this.playerTeamService = playerTeamService;
        this.teamService = teamService;
    }

    @GetMapping
    @RequestMapping("/games")
    public Mono<List<GameDto>> getAllGames() {
        return Mono.just(gameService.getAllGames());
    }

    @GetMapping
    @RequestMapping("/games/{gameId}")
    public Mono<GameDto> getGame(@PathVariable Long gameId) {
        return Mono.just(gameService.getGame(gameId));
    }

    @GetMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @RequestMapping("/games/report/{gameId}")
    public Mono<byte[]> getReport(@PathVariable Long gameId) throws DocumentException {
        return Mono.just(gameService.getReport(gameId));
    }

    @GetMapping
    @RequestMapping("/player-team/{teamId}/players")
    public Mono<List<PlayerDto>> getAllPlayersOfATeam(@PathVariable("teamId") Long teamId) {
        return playerTeamService.getAllPlayers(teamId);
    }

    @GetMapping
    @RequestMapping("/team")
    public Mono<List<TeamDto>> getAllTeams() {
        return Mono.just(teamService.getAllTeams());
    }

}
