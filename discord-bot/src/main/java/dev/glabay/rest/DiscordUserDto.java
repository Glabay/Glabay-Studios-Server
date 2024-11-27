package dev.glabay.rest;

import java.time.LocalDateTime;

public record DiscordUserDto(Long userId, Long discordUserId, LocalDateTime updatedAt) {}