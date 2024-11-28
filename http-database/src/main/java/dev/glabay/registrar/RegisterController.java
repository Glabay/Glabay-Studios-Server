package dev.glabay.registrar;

import dev.glabay.discord.service.DiscordUserService;
import dev.glabay.dto.RegistrationDto;
import dev.glabay.profile.ProfileService;
import dev.glabay.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author Glabay | Glabay-Studios
 * @project Boneyard
 * @social Discord: Glabay
 * @since 2024-11-27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registrar")
public class RegisterController {

    private final UserService userService;
    private final ProfileService profileService;
    private final DiscordUserService discordUserService;

    @PostMapping("/")
    public ResponseEntity<String> registerNewAccount(@RequestBody RegistrationDto dto) {
        if (Objects.isNull(dto)) return ResponseEntity.badRequest().build();
        var userDto = userService.createUser();
        if (userDto.isEmpty())
            return ResponseEntity.status(500).build(); // couldn't create for some reason
        var profileDto = profileService.createProfile(dto.username(), userDto.get().userId());
        if (Objects.isNull(profileDto))
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        var discordUser = discordUserService.createDiscordUser(dto.discordId());
        if (Objects.isNull(discordUser))
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        return ResponseEntity.ok("Registered");
    }
}
