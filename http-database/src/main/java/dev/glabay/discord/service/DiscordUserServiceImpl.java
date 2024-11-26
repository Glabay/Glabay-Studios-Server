package dev.glabay.discord.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.glabay.discord.dto.DiscordNewUserRequestDto;
import dev.glabay.discord.dto.DiscordUserDto;
import dev.glabay.discord.model.DiscordUser;
import dev.glabay.discord.repo.DiscordUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class DiscordUserServiceImpl implements DiscordUserService {

    private final DiscordUserRepository discordUserRepository;

    @Override
    public Optional<DiscordUserDto> getDiscordUserDtoByDiscordUserId(Long discordUserId) {
        return discordUserRepository.findByDiscordUserId(discordUserId)
            .map(this::convertToDto);
    }

    @Override
    public Optional<DiscordUserDto> createDiscordUser(DiscordNewUserRequestDto request) {
        var existingUser = discordUserRepository.findByDiscordUserId(request.discordUserId());
        if (existingUser.isPresent())
            return Optional.empty();

        var newUser = new DiscordUser();
            newUser.setUserId(request.userId());
            newUser.setDiscordUserId(request.discordUserId());
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

        var savedUser = discordUserRepository.save(newUser);
        return Optional.of(convertToDto(savedUser));
    }

    private DiscordUserDto convertToDto(DiscordUser discordUser) {
        return new DiscordUserDto(
            discordUser.getUserId(),
            discordUser.getDiscordUserId(),
            discordUser.getUpdatedAt()
        );
    }
}
