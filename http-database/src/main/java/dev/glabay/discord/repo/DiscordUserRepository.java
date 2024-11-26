package dev.glabay.discord.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.glabay.discord.model.DiscordUser;

import java.util.Optional;

public interface DiscordUserRepository extends JpaRepository<DiscordUser, Long> {
    Optional<DiscordUser> findByDiscordUserId(Long discordUserId);
}