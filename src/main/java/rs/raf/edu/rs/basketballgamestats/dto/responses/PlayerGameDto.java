package rs.raf.edu.rs.basketballgamestats.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PlayerGameDto {

    private Long gameId;
    private Long playerId;
    private Integer points;
    private Integer rebounds;
    private Integer assists;
    private Integer fouls;
    private String currentScore;
}
