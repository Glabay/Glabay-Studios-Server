package dev.glabay.profile;

import dev.glabay.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-26
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Override
    public ProfileDto findById(Long id) {
        var profile = profileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return convertToDto(profile);
    }

    @Override
    public ProfileDto findByProfileId(Long profileId) {
        var profile = profileRepository.findByProfileId(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return convertToDto(profile);
    }

    @Override
    public ProfileDto findByUsername(String username) {
        var profile = profileRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        return convertToDto(profile);
    }

    @Override
    public ProfileDto createProfile(String username, Long userId) {
        var profile = new Profile();
            profile.setUsername(username);
            profile.setDisplayName(username);
            profile.setProfileId(profile.getId());
            profile.setUserId(userId);
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(profile.getCreatedAt());
            profile.setJoined(profile.getCreatedAt());
        return convertToDto(profileRepository.save(profile));
    }

    @Override
    public Collection<ProfileDto> findAll() {
        return profileRepository.findAll().stream()
            .map(this::convertToDto)
            .toList();
    }

    private ProfileDto convertToDto(Profile profile) {
        return new ProfileDto(
            profile.getProfileId(),
            profile.getUserId(),
            profile.getUsername(),
            profile.getDisplayName(),
            profile.getJoined(),
            profile.getUpdatedAt()
        );
    }
}
