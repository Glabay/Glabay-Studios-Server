package dev.glabay.dto;

import java.time.LocalDateTime;
public record DiscordUserDto(Long userId, Long discordUserId, LocalDateTime updatedAt) {}