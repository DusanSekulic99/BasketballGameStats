package rs.raf.edu.rs.basketballgamestats.factory;

import org.springframework.stereotype.Component;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreatePlayerRequest;
import rs.raf.edu.rs.basketballgamestats.model.Player;
import rs.raf.edu.rs.basketballgamestats.model.Team;

@Component
public class PlayerFactory {

    public Player createPlayer(CreatePlayerRequest request, Team team) {
        return Player.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .jerseyNo(request.getJerseyNo())
                .team(team)
                .position(request.getPosition())
                .build();
    }
}
