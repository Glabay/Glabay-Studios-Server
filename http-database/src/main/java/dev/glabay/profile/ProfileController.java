package dev.glabay.profile;

import dev.glabay.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable Long id) {
        var profile = profileService.findById(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profileId/{profileId}")
    public ResponseEntity<ProfileDto> getProfileByProfileId(@PathVariable Long profileId) {
        var profile = profileService.findByProfileId(profileId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ProfileDto> getProfileByUsername(@PathVariable String username) {
        var profile = profileService.findByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<Collection<ProfileDto>> getAllProfiles() {
        var profiles = profileService.findAll();
        return ResponseEntity.ok(profiles);
    }
}
