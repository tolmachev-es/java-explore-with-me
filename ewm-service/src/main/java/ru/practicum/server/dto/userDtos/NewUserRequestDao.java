package ru.practicum.server.dto.userDtos;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class NewUserRequestDao {
    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    private String email;
}
