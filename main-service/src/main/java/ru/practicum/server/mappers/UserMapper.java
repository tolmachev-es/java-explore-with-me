package ru.practicum.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.server.dto.userDtos.NewUserRequestDao;
import ru.practicum.server.dto.userDtos.UserDto;
import ru.practicum.server.dto.userDtos.UserShortDto;
import ru.practicum.server.models.User;
import ru.practicum.server.repository.entities.UserEntity;

@Mapper
public interface UserMapper {
    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    User fromEntity(UserEntity userEntity);

    UserEntity toEntity(User user);

    User fromNewUserRequestDao(NewUserRequestDao newUserRequest);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
