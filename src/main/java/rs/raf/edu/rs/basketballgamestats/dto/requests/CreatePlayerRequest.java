package rs.raf.edu.rs.basketballgamestats.dto.requests;

import lombok.Data;
import rs.raf.edu.rs.basketballgamestats.model.Position;

@Data
public class CreatePlayerRequest {

    private Long id;
    private String firstName;
    private String lastName;
    private Long teamId;
    private String jerseyNo;
    private Position position;
    private boolean starter;

}
