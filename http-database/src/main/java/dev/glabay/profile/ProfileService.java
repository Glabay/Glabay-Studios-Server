package dev.glabay.profile;

import dev.glabay.dto.ProfileDto;

import java.util.Collection;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
public interface ProfileService {
   ProfileDto findById(Long id);
   ProfileDto findByProfileId(Long profileId);
   ProfileDto findByUsername(String username);
    Collection<ProfileDto> findAll();

}
