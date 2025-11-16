package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(of = "id")
@Data
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
