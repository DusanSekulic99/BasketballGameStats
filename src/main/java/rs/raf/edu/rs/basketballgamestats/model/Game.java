package rs.raf.edu.rs.basketballgamestats.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    private Game_Status status;

    private Integer playingTime;

    private Integer homeTeamBonus;
    private Integer homeTeamTimeout;

    private Integer awayTeamBonus;
    private Integer awayTeamTimeout;

    private Integer currentQuarter;

    private Integer timeLeft;

    public Game(Team homeTeam, Team awayTeam, Integer playingTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.playingTime = playingTime;
        this.status = Game_Status.NOT_STARTED_YET;
        this.timeLeft = playingTime;
        this.currentQuarter = 1;
        this.homeTeamBonus = 0;
        this.awayTeamBonus = 0;
        this.homeTeamTimeout = 6;
        this.awayTeamTimeout = 6;
    }

    public Game() {

    }

    public void updateHomeTeamBonus() {
        this.homeTeamBonus++;
    }
    public void updateHomeTeamTimeout() {
        this.homeTeamTimeout--;
    }

    public void updateAwayTeamBonus() {
        this.awayTeamBonus++;
    }
    public void updateAwayTeamTimeout() {
        this.awayTeamTimeout--;
    }

}
