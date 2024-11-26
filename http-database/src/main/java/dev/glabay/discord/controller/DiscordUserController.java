package dev.glabay.discord.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dev.glabay.dto.DiscordNewUserRequestDto;
import dev.glabay.discord.service.DiscordUserService;
import dev.glabay.dto.DiscordUserDto;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discord")
public class DiscordUserController {

    private final DiscordUserService discordUserService;

    @GetMapping("/user/id/{discordUserId}")
    public ResponseEntity<DiscordUserDto> getDiscordUserById(@PathVariable Long discordUserId) {
        var discordUserDto = discordUserService.getDiscordUserDtoByDiscordUserId(discordUserId);

        return discordUserDto.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{discordUserId}")
    public ResponseEntity<DiscordUserDto> createDiscordUser(@PathVariable Long discordUserId) {
        var discordUserDto = discordUserService.createDiscordUser(discordUserId);

        return discordUserDto.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}
