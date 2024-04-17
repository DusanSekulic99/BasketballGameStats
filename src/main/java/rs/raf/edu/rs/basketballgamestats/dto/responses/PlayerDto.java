package rs.raf.edu.rs.basketballgamestats.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PlayerDto {

    private String id;
    private String teamId;
    private String firstName;
    private String lastName;
    private String jerseyNo;
    private String position;
    private boolean starter;
    private boolean playing;
    private String points;
    private String assists;
    private String rebounds;
    private String fouls;

}
