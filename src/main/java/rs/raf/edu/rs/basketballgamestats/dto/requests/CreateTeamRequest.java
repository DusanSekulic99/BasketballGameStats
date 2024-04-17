package rs.raf.edu.rs.basketballgamestats.dto.requests;

import lombok.Data;

@Data
public class CreateTeamRequest {

    private Long id;
    private String name;
    private String badge;

}
