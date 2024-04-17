package rs.raf.edu.rs.basketballgamestats.dto.requests;

import lombok.Data;

@Data
public class TimeoutRequest {
    private Integer playingTimeLeft;
    private Long gameId;
    private Long teamId;
}
