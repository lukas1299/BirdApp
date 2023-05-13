package com.birdapp.BirdApp.controller;

import com.birdapp.BirdApp.entity.Bird;
import com.birdapp.BirdApp.repository.BirdRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/birds")
@Slf4j
public class BirdController {

    private final BirdRepository birdRepository;

    @GetMapping
    public ResponseEntity<List<Bird>> getAllBirds(){
        return ResponseEntity.ok(birdRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bird> getById(@NonNull @PathVariable String id){
        Bird bird = birdRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bird does not exist"));
        return ResponseEntity.ok(bird);
    }

    @PostMapping
    public ResponseEntity<Bird> insertBird(@RequestBody Bird bird) {

        if(birdRepository.existsById(bird.getId())){
            throw new EntityExistsException("Bird already exist.");
        }

        birdRepository.save(bird);
        return ResponseEntity.ok(bird);
    }

}
