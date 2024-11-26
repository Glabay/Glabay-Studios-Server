package dev.glabay.rest;

import dev.glabay.client.HttpClient;
import org.springframework.http.MediaType;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-26
 */
public class DiscordClient extends HttpClient {

    private static DiscordClient discordClient;

    public static DiscordClient getInstance() {
        if (discordClient == null) discordClient = new DiscordClient();
        return discordClient;
    }

    private final String baseUrl = "http://localhost:8080/api/v1/discord";

    public DiscordUserDto getDiscordUserByDiscordId(Long discordId) {
        return getRestClient()
            .get()
            .uri("%s/user/id/%s", baseUrl, String.valueOf(discordId))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(DiscordUserDto.class)
            .getBody();
    }
    public DiscordUserDto getDiscordUserByUsername(String username) {
        return getRestClient()
            .get()
            .uri("%s/user/name/%s", baseUrl, username)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(DiscordUserDto.class)
            .getBody();
    }


    // TODO: Create a way to POST a DTO to DiscordController, and ProfileController

}
