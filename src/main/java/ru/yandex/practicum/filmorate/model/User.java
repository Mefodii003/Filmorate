package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Email должен быть указан")
    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Login должен быть указан")
    // запрет пробелов в логине
    @Pattern(regexp = "\\S+", message = "Login не должен содержать пробелов")
    private String login;

    // имя может быть пустым — в сервисе/контроллере корректируем при создании
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}