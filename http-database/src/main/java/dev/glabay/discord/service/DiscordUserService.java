package dev.glabay.discord.service;

import dev.glabay.dto.DiscordNewUserRequestDto;
import dev.glabay.dto.DiscordUserDto;

import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
public interface DiscordUserService {
    Optional<DiscordUserDto> getDiscordUserDtoByDiscordUserId(Long discordUserId);
    Optional<DiscordUserDto> getDiscordUserDtoByUsername(String username);
    Optional<DiscordUserDto> createDiscordUser(DiscordNewUserRequestDto request);
}
