package rs.raf.edu.rs.basketballgamestats.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import rs.raf.edu.rs.basketballgamestats.dto.requests.CreateGameRequest;
import rs.raf.edu.rs.basketballgamestats.dto.requests.InGameSubRequest;
import rs.raf.edu.rs.basketballgamestats.dto.requests.TimeoutRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.GameDto;
import rs.raf.edu.rs.basketballgamestats.service.GameService;

@RestController
@RequestMapping("/game")
public class GameController {


    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public Mono<GameDto> createGame(@RequestBody CreateGameRequest request) {
        return Mono.just(gameService.createGame(request));
    }

    @PutMapping
    @RequestMapping("/timeout")
    public Mono<GameDto> timeout(@RequestBody TimeoutRequest request) {
        return Mono.just(gameService.timeout(request));
    }

    @PutMapping
    @RequestMapping("/pause")
    public Mono<GameDto> pause(@RequestBody TimeoutRequest request) {
        return Mono.just(gameService.pauseGame(request));
    }

    @PutMapping
    @RequestMapping("/{gameId}/endQuarter")
    public Mono<GameDto> endQuarter(@PathVariable Long gameId) {
        return Mono.just(gameService.endQuarter(gameId));
    }

    @PutMapping
    @RequestMapping("/{gameId}/playGame")
    public Mono<GameDto> playGame(@PathVariable Long gameId) {
        return Mono.just(gameService.playGame(gameId));
    }

    @PutMapping
    @RequestMapping("/updateTime")
    public Mono<GameDto> updateTime(@RequestBody TimeoutRequest request) {
        return Mono.just(gameService.updateTime(request));
    }

    @PutMapping
    @RequestMapping("/substitute")
    public Mono<GameDto> substituteInGame(@RequestBody InGameSubRequest inGameSubRequest) throws Exception {
        return Mono.just(gameService.substituteInGame(inGameSubRequest));
    }
}
