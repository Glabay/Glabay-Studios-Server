package dev.glabay.dto;

import java.time.LocalDateTime;

public record UserDto(Long userId, LocalDateTime updatedAt) {}