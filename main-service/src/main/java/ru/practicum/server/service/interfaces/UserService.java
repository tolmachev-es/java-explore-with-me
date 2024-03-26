package ru.practicum.server.service.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.userDtos.NewUserRequestDao;

import java.util.List;

@Service
public interface UserService {
    ResponseEntity<?> createNewUser(NewUserRequestDao userDto);

    ResponseEntity<?> getUsers(List<Long> userIds, Pageable pageable);

    ResponseEntity<?> removeUserById(long userId);
}
