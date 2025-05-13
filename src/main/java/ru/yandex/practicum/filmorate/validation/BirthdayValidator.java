package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayValidator implements ConstraintValidator<Birthday, LocalDate> {

    @Override
    public void initialize(Birthday constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // или false, если Вы хотите, чтобы null считался недопустимым
        }
        return date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now());
    }
}