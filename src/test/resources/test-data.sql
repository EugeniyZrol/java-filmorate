BEGIN;

VALUES (1, 'user@example.com', 'user1', 'User One', '1990-01-01');

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

COMMIT;