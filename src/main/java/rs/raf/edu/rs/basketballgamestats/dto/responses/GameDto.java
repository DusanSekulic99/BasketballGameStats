package rs.raf.edu.rs.basketballgamestats.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import rs.raf.edu.rs.basketballgamestats.model.Game_Status;

@Builder
@Getter
@Setter
public class GameDto {

    private Long gameId;

    private Integer currentQuarter;
    private Integer playingTime;
    private Integer playingTimeLeft;
    private TeamDto homeTeam;
    private TeamDto awayTeam;
    private String currentScore;
    private Integer homeTeamBonus;
    private Integer homeTeamTimeout;
    private Integer awayTeamBonus;
    private Integer awayTeamTimeout;
    private Game_Status status;
    private boolean stopTimer;

}
