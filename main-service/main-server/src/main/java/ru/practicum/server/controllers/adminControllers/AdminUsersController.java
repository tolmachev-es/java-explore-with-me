package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.NewUserRequestDao;
import ru.practicum.server.service.interfaces.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUsersController {
    private final UserService userService;

    @PostMapping
    ResponseEntity<?> createUser(@Valid @RequestBody NewUserRequestDao newUser) {
        return userService.createNewUser(newUser);
    }

    @GetMapping
    ResponseEntity<?> getUsers(@Nullable @RequestParam(name = "ids", defaultValue = "") List<Long> ids,
                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return userService.getUsers(ids, pageable);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> removeUser(@NotNull @PathVariable long id) {
        return userService.removeUserById(id);
    }
}
