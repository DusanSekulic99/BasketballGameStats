package rs.raf.edu.rs.basketballgamestats.service;

import org.springframework.stereotype.Service;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreateTeamRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.TeamDto;
import rs.raf.edu.rs.basketballgamestats.factory.TeamFactory;
import rs.raf.edu.rs.basketballgamestats.model.Team;
import rs.raf.edu.rs.basketballgamestats.repository.TeamRepo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepo teamRepo;

    private final PlayerTeamService playerTeamService;
    private final TeamFactory teamFactory;

    public TeamService(TeamRepo teamRepo, PlayerTeamService playerTeamService, TeamFactory teamFactory) {
        this.teamRepo = teamRepo;
        this.playerTeamService = playerTeamService;
        this.teamFactory = teamFactory;
    }

    public TeamDto createTeam(CreateTeamRequest request) {
        return createTeamDto(teamRepo.save(teamFactory.createTeam(request)));
    }

    public List<TeamDto> getAllTeams() {
        return teamRepo.findAll().stream()
                .map(this::createTeamDto)
                .collect(Collectors.toList());
    }

    private TeamDto createTeamDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .badge(team.getBadge())
                .name(team.getName())
                .players(Optional.ofNullable(team.getPlayerList())
                        .orElse(Collections.emptyList()).stream()
                        .map(player -> playerTeamService.getPlayerDto(player.getId(), -1L))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();
    }

    public void deleteTeam(Long teamId) {
        teamRepo.deleteById(teamId);
    }

}


