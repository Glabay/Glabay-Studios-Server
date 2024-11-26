package dev.glabay.discord.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "discord_user")
public class DiscordUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discord_user_seq")
    @SequenceGenerator(name = "discord_user_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    // The ID of the user within our database
    private Long userId;

    @Column(name = "DISCORD_USER_ID", updatable = false)
    private Long discordUserId; // The ID of the user

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updatedAt;
}