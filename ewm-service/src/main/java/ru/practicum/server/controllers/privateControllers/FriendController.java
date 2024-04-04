package ru.practicum.server.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.userDtos.UserDto;
import ru.practicum.server.repository.entities.FriendsEntity;
import ru.practicum.server.service.interfaces.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/friends")
@RequiredArgsConstructor
@Slf4j
@Validated
public class FriendController {
    private final UserService userService;

    @PostMapping("/{userId}/request/{friendId}")
    public ResponseEntity<Void> createFriendRequest(@NotNull @PathVariable(name = "userId") Long userId,
                                                    @NotNull @PathVariable(name = "friendId") Long friendId) {
        log.info("Has new friend request from user with id = {} to user with id = {}", userId, friendId);
        userService.addFriend(userId, friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{userId}/request/{friendId}")
    public ResponseEntity<Void> changeStatusFriendRequest(@NotNull @PathVariable(name = "userId") Long userId,
                                                          @NotNull @PathVariable(name = "friendId") Long friendId,
                                                          @NotNull @RequestParam(name = "state") FriendsEntity.FriendState state) {
        log.info("Has {} friend request from user {} to user {}", state, userId, friendId);
        userService.changeFriendRequestStatus(userId, friendId, state);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/request/{friendId}")
    public ResponseEntity<Void> deleteFriendRequest(@NotNull @PathVariable(name = "userId") Long userId,
                                                    @NotNull @PathVariable(name = "friendId") Long friendId) {
        log.info("Has request to delete friend {} from user {}", friendId, userId);
        userService.deleteFriendRequest(userId, friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/friendship/{friendId}")
    public ResponseEntity<?> deleteFromFriends(@NotNull @PathVariable(name = "userId") Long userId,
                                               @NotNull @PathVariable(name = "friendId") Long friendId) {
        log.info("Has request to delete friend {} from user {}", friendId, userId);
        userService.deleteFriend(userId, friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}/request/outbox")
    public ResponseEntity<List<UserDto>> getOutboxUserRequest(@NotNull @PathVariable(name = "userId") Long userId,
                                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Has request to get outbox request");
        return new ResponseEntity<>(userService.getOutboxFriendRequest(userId, PageRequest.of(from / size, size)),
                HttpStatus.OK);
    }

    @GetMapping("/{userId}/request/inbox")
    public ResponseEntity<List<UserDto>> getInboxUserRequest(@NotNull @PathVariable(name = "userId") Long userId,
                                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Has request to get inbox request");
        return new ResponseEntity<>(userService.getInboxFriendRequest(userId, PageRequest.of(from / size, size)),
                HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserDto>> getFriends(@NotNull @PathVariable(name = "userId") Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Has request to get inbox request");
        return new ResponseEntity<>(userService.getFriends(userId, PageRequest.of(from / size, size)),
                HttpStatus.OK);
    }

}
