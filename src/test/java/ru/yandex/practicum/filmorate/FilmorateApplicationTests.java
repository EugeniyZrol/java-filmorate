package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserDbStorage.class, UserRowMapper.class}) // Добавлен UserRowMapper
@Sql(scripts = {"/schema.sql", "/test-data.sql"})
class UserDbStorageTest {

	@Autowired
	private UserDbStorage userStorage;

	@Test
	void testFindNonExistingUserById() {
		// Проверяем поиск несуществующего пользователя
		Optional<User> userOptional = userStorage.findUserById(999L);
		assertThat(userOptional).isEmpty();
	}

}