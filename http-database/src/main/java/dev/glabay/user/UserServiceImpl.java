package dev.glabay.user;

import dev.glabay.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserDto> getUserDtoByUserId(Long userId) {
        return userRepository.findByUserId(userId)
            .map(this::convertToDto);
    }

    @Override
    public Optional<UserDto> createUser(UserDto userDto) {
        var existingUser = userRepository.findByUserId(userDto.userId());
        if (existingUser.isPresent())
            return Optional.empty();
        var newUser = new User();
            newUser.setUserId(userDto.userId());
            newUser.setUpdatedAt(LocalDateTime.now());
            newUser.setCreatedAt(LocalDateTime.now());

        var savedUser = userRepository.save(newUser);
        return Optional.of(convertToDto(savedUser));
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
            user.getUserId(),
            user.getUpdatedAt()
        );
    }
}
