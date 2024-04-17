package rs.raf.edu.rs.basketballgamestats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.raf.edu.rs.basketballgamestats.model.User;
import rs.raf.edu.rs.basketballgamestats.repository.UserRepo;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepo userRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepo userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = this.findByUsername(username);
        if(myUser == null) {
            throw new UsernameNotFoundException("User name "+username+" not found");
        }

        return new org.springframework.security.core.userdetails.User(myUser.getUsername(), myUser.getPassword(), new ArrayList<>());
    }

    public User create(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user = this.userRepository.save(user);
        System.out.println("Service: User created");
        return user;
    }

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
}

