package rs.raf.edu.rs.basketballgamestats.dto.requests;

import lombok.Data;

@Data
public class InGameSubRequest {

    private Long gameId;
    private Long teamId;
    private Long oldPlayerId;
    private Long newPlayerId;
    private Integer quarter;
    private Integer playedTime;
}
