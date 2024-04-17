package rs.raf.edu.rs.basketballgamestats.dto.requests;

import lombok.Data;

@Data
public class CreateGameRequest {

    private Long homeTeamId;
    private Long awayTeamId;
    private Integer playingTime;

}
