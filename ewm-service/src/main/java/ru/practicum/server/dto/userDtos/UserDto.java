package ru.practicum.server.dto.userDtos;


import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
