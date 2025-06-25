package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayValidator implements ConstraintValidator<Birthday, LocalDate> {
    private static final LocalDate MIN_DATE = LocalDate.of(1900, 1, 1);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }
        return !date.isAfter(LocalDate.now()) && !date.isBefore(MIN_DATE);
    }
}