package com.birdapp.BirdApp.entity;

import java.util.List;

public record UserResponse(User user, List<Bird> birds) {
}
