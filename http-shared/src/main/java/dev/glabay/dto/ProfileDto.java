package dev.glabay.dto;

import java.time.LocalDateTime;

public record ProfileDto(
    Long profileId,
    String username,
    String displayName,
    LocalDateTime joined,
    LocalDateTime updatedAt
) {}