package rs.raf.edu.rs.basketballgamestats.factory;

import org.springframework.stereotype.Component;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreateTeamRequest;
import rs.raf.edu.rs.basketballgamestats.model.Team;

@Component
public class TeamFactory {

    public Team createTeam(CreateTeamRequest request) {
        return Team.builder()
                .name(request.getName())
                .badge(request.getBadge())
                .build();
    }

}
