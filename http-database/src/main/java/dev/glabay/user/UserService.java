package dev.glabay.user;

import dev.glabay.dto.UserDto;

import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-25
 */
public interface UserService {
    Optional<UserDto> getUserDtoByUserId(Long userId);
    Optional<UserDto> createUser(UserDto userDto);
}
