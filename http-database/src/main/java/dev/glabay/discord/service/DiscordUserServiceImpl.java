package dev.glabay.discord.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import dev.glabay.dto.DiscordNewUserRequestDto;
import dev.glabay.dto.DiscordUserDto;
import dev.glabay.discord.model.DiscordUser;
import dev.glabay.discord.repo.DiscordUserRepository;
import org.springframework.web.client.RestClient;

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
    private final RestClient restClient;

    @Override
    public Optional<DiscordUserDto> getDiscordUserDtoByDiscordUserId(Long discordUserId) {
        return discordUserRepository.findByDiscordUserId(discordUserId)
            .map(this::convertToDto);
    }

    @Override
    public Optional<DiscordUserDto> getDiscordUserDtoByUsername(String username) {
        var profileDto = restClient.get()
            .uri("http://localhost:8080/api/v1/profile/username/%s", username)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(DiscordUserDto.class)
            .getBody();
        if (profileDto != null) {
            var discordUserDto = restClient.get()
                .uri("http://localhost:8080/api/v1/discord/user/id/%s", profileDto.userId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(DiscordUserDto.class)
                .getBody();
            if (discordUserDto != null)
                return Optional.of(discordUserDto);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DiscordUserDto> createDiscordUser(Long discordId) {
        var existingUser = discordUserRepository.findByDiscordUserId(discordId);
        if (existingUser.isPresent())
            return Optional.empty();

        var newUser = new DiscordUser();
            newUser.setUserId(newUser.getId());
            newUser.setDiscordUserId(discordId);
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
