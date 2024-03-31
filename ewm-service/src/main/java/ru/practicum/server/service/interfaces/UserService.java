package ru.practicum.server.service.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.userDtos.NewUserRequestDao;
import ru.practicum.server.dto.userDtos.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto createNewUser(NewUserRequestDao userDto);

    List<UserDto> getUsers(List<Long> userIds, Pageable pageable);

    void removeUserById(long userId);
}
