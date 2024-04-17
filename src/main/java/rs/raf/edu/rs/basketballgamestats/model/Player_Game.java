package rs.raf.edu.rs.basketballgamestats.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "player_game")
public class Player_Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToOne
    @JoinColumn(name = "player_team_id")
    private Team playerTeam;

    private Integer points;
    private Integer rebounds;
    private Integer assists;
    private Integer fouls;

    private Boolean playing;

    private Integer secondsPlayed;

    private Integer subbedInAt;

    private Integer subbedOutAt;

    public Player_Game(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.playerTeam = player.getTeam();
        this.points = 0;
        this.rebounds = 0;
        this.assists = 0;
        this.fouls = 0;
        this.playing = false;
        this.secondsPlayed = 0;
        this.subbedInAt = 0;
        this.subbedOutAt = 0;
    }

    public Player_Game() {

    }

    public void updatePoints(Integer points) {
        this.points += points;
    }

    public void updateRebounds() {
        this.rebounds++;
    }
    public void updateAssists() {
        this.assists++;
    }
    public void updateFouls() {
        this.fouls++;
    }

    public void updateSecondsPlayed() {
        this.secondsPlayed += this.subbedOutAt - this.subbedInAt;
    }
}
