package dev.glabay.dto;

/**
 * @author Glabay | Glabay-Studios
 * @project Boneyard
 * @social Discord: Glabay
 * @since 2024-11-27
 */
public record RegistrationDto(
    String username,
    String email,
    String encryptedPassword,
    Long discordId
) {}
