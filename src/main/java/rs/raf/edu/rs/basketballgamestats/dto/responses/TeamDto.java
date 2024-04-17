package rs.raf.edu.rs.basketballgamestats.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Builder
@Getter
@Setter
public class TeamDto {

    private Long id;
    private String name;
    private String badge;
    private Integer timeout;
    private List<PlayerDto> players;
}
