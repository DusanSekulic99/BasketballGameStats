package rs.raf.edu.rs.basketballgamestats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.raf.edu.rs.basketballgamestats.dto.requests.LoginRequest;
import rs.raf.edu.rs.basketballgamestats.dto.responses.LoginResponse;
import rs.raf.edu.rs.basketballgamestats.model.User;
import rs.raf.edu.rs.basketballgamestats.service.UserService;
import rs.raf.edu.rs.basketballgamestats.utils.JwtUtil;

@RestController
@CrossOrigin
@RequestMapping("/login")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
            User user = userService.findByUsername(loginRequest.getUsername());
            if (user != null && userService.checkPassword(user, loginRequest.getPassword())) {
                return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(loginRequest.getUsername())));
            }

            return ResponseEntity.status(401).build();
    }

}
