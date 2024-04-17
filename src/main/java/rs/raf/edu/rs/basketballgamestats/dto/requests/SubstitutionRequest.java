package rs.raf.edu.rs.basketballgamestats.dto.requests;

import lombok.Data;

@Data
public class SubstitutionRequest {

    private String starterId;
    private String newStarterId;
    private Long gameId;
}
