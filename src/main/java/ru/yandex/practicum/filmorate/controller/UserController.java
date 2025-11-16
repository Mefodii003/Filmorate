package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    //гет./users
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    //пост./users
    @PostMapping
    public User save(@RequestBody User user) {
        try {
            validateUser(user, true);
        } catch (ValidationException e) {
            log.error("Ошибка при добавлении пользователя: {}", e.getMessage());
            throw e;
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    //пут./users
    @PutMapping
    public User update(@RequestBody User user) {
        try {
            if (user.getId() == null || !users.containsKey(user.getId())) {
                throw new ConditionsNotMetException("Пользователь с таким Id не найден");
            }
            validateUser(user, false); // false — обновление
        } catch (ValidationException | ConditionsNotMetException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }

        User existingUser = users.get(user.getId());
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());

        log.info("Пользователь обновлён: {}", existingUser);
        return existingUser;
    }

    //Делаем типо провкрку для того чтобы не дублировать код
    private void validateUser(User user, boolean isNew) {
        // Email не пустой и содержит "@"
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email должен быть указан");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ @");
        }

        // Email уникален
        boolean emailExists = users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) &&
                        (isNew || !u.getId().equals(user.getId())));
        if (emailExists) {
            throw new DuplicatedDataException("Этот email уже используется");
        }

        // Логин не пустой и без пробелов
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        // Если имя пустое — используем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        // Дата рождения не в будущем
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
    }

}
