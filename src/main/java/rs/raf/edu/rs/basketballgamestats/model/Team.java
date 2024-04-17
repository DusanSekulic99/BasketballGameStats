package rs.raf.edu.rs.basketballgamestats.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String badge;

    @OneToMany(mappedBy = "team")
    private List<Player> playerList;

    @Transient
    private List<Player> playingTeam;

    @OneToMany(mappedBy = "homeTeam")
    private List<Game> homeGames;

    @OneToMany(mappedBy = "awayTeam")
    private List<Game> awayGames;

}
