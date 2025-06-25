-- Начинаем транзакцию для атомарного заполнения
BEGIN;

-- 1. Добавляем пользователей
--INSERT INTO users (user_id, name, email, login, birthday) VALUES
--(1, 'Иван Петров', 'ivan@mail.ru', 'ivan_petrov', '1990-05-15'),
--(2, 'Анна Сидорова', 'anna@yandex.ru', 'anna_s', '1995-08-22'),
--(3, 'Сергей Иванов', 'sergey@gmail.com', 'sergey_i', '1988-11-03'),
--(4, 'Мария Кузнецова', 'maria@mail.ru', 'maria_k', '1993-04-10');
--
-- 2. Добавляем рейтинги MPA (если ещё не добавлены)
INSERT INTO mpa_ratings (mpa_id, name, description)
SELECT 1, 'G', 'Нет возрастных ограничений' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 1);

INSERT INTO mpa_ratings (mpa_id, name, description)
SELECT 2, 'PG', 'Детям рекомендуется смотреть с родителями' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 2);

INSERT INTO mpa_ratings (mpa_id, name, description)
SELECT 3, 'PG-13', 'Детям до 13 лет просмотр нежелателен' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 3);

INSERT INTO mpa_ratings (mpa_id, name, description)
SELECT 4, 'R', 'Лицам до 17 лет — только с взрослым' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 4);

INSERT INTO mpa_ratings (mpa_id, name, description)
SELECT 5, 'NC-17', 'Лицам до 18 лет запрещено' WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_id = 5);

-- 3. Добавляем жанры (если ещё не добавлены)
INSERT INTO genres (genre_id, name)
SELECT 1, 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 1);

INSERT INTO genres (genre_id, name)
SELECT 2, 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 2);

INSERT INTO genres (genre_id, name)
SELECT 3, 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 3);

INSERT INTO genres (genre_id, name)
SELECT 4, 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 4);

INSERT INTO genres (genre_id, name)
SELECT 5, 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 5);

INSERT INTO genres (genre_id, name)
SELECT 6, 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = 6);

---- 4. Добавляем фильмы
--INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
--(1, 'Крепкий орешек', 'Полицейский против террористов', '1988-07-15', 132, 4),
--(2, 'Король Лев', 'История львёнка Симбы', '1994-06-24', 88, 1),
--(3, 'Форрест Гамп', 'Жизнь человека с добрым сердцем', '1994-07-06', 142, 3);
--
---- 5. Связываем фильмы с жанрами
--INSERT INTO film_genres (film_id, genre_id) VALUES
--(1, 6),  -- Крепкий орешек -> Боевик
--(1, 4),  -- Крепкий орешек -> Триллер
--(2, 3),  -- Король Лев -> Мультфильм
--(2, 2),  -- Король Лев -> Драма
--(3, 2),  -- Форрест Гамп -> Драма
--(3, 1);  -- Форрест Гамп -> Комедия
--
---- 6. Добавляем лайки к фильмам
--INSERT INTO film_likes (film_id, user_id) VALUES
--(1, 1),  -- Иван лайкнул "Крепкий орешек"
--(1, 2),  -- Анна лайкнула "Крепкий орешек"
--(2, 1),  -- Иван лайкнул "Король Лев"
--(2, 3),  -- Сергей лайкнул "Король Лев"
--(3, 2),  -- Анна лайкнула "Форрест Гамп"
--(3, 4);  -- Мария лайкнула "Форрест Гамп"
--
---- 7. Добавляем друзей
--INSERT INTO friendships (user_id, friend_id, is_confirmed) VALUES
--(1, 2, TRUE),   -- Иван и Анна - друзья (подтверждено)
--(1, 3, FALSE),  -- Иван отправил заявку Сергею (не подтверждено)
--(2, 4, TRUE),   -- Анна и Мария - друзья (подтверждено)
--(3, 4, FALSE);  -- Сергей отправил заявку Марии (не подтверждено)

-- Фиксируем изменения
COMMIT;