package rs.raf.edu.rs.basketballgamestats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreateTeamRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.TeamDto;
import rs.raf.edu.rs.basketballgamestats.service.TeamService;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {


    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public Mono<TeamDto> createTeam(@RequestBody CreateTeamRequest request) {
        return Mono.just(teamService.createTeam(request));
    }

//    @PutMapping
//    public Mono<ResponseEntity<TeamDto>> editTeam(@RequestBody CreateTeamRequest request) {
//        return Mono.just(ResponseEntity.ok(teamService.editTeam(request)));
//    }

    @DeleteMapping("/{teamId}")
    public Mono<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return Mono.empty();
    }

}
