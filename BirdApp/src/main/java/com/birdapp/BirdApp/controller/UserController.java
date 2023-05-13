package com.birdapp.BirdApp.controller;

import com.birdapp.BirdApp.entity.Bird;
import com.birdapp.BirdApp.entity.User;
import com.birdapp.BirdApp.entity.UserResponse;
import com.birdapp.BirdApp.repository.BirdRepository;
import com.birdapp.BirdApp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final BirdRepository birdRepository;

    @PostMapping
    public ResponseEntity<UserResponse> insertUser(@RequestBody User user) {
        var u = userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(u, u.getBirds()));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(user, user.getBirds()))
                .toList());
    }

    @PostMapping("/{id}/birds")
    public ResponseEntity<UserResponse> addBirdToUser(@PathVariable String id, @RequestBody Bird bird) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        user.setBirds(Optional.ofNullable(user.getBirds()).orElseGet(ArrayList::new));
        user.getBirds().add(bird);
        birdRepository.save(bird);
        userRepository.save(user);

        return ResponseEntity.ok(new UserResponse(user, user.getBirds()));
    }
}
