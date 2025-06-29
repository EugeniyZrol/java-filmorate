# java-filmorate
Template repository for Filmorate project.

## 🗄️ Схема базы данных Filmorate

![Диаграмма базы данных](src/main/resources/postgres%20-%20public.png)

## 📝 Пояснение к схеме БД

### Основные таблицы:
- **`users`**  
  Содержит данные пользователей: ID, имя, email, логин, дата рождения.  
  Ограничения:
    - `email` должен содержать `@`.
    - `birthday` не может быть будущей датой.

- **`films`**  
  Информация о фильмах: ID, название, описание, дата выхода, продолжительность, рейтинг MPA.  
  Ограничения:
    - `release_date` не раньше 28 декабря 1895 года.
    - `duration` должна быть положительной.

### Связи:
- **Жанры фильмов**  
  Реализованы через таблицу `film_genres` (связь многие-ко-многим между `films` и `genres`).
- **Лайки**  
  Хранятся в `film_likes` (связь пользователей и фильмов).
- **Друзья**  
  Таблица `friendships` содержит статусы дружбы (`CONFIRMED`, `PENDING`).

### Примеры запросов:
- Топ-5 популярных фильмов:
  ```sql
  SELECT 
    f.film_id,
    f.name AS film_title,
    COUNT(fl.user_id) AS likes_count,
    m.name AS mpa_rating
  FROM films f
  LEFT JOIN film_likes fl ON f.film_id = fl.film_id
  LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
  GROUP BY f.film_id, f.name, m.name
  ORDER BY likes_count DESC, film_title ASC
  LIMIT 5;
  
### Примеры запросов:
-  Список общих друзей пользователя с ID = 1 и пользователя с ID = 2:
  ```sql
  SELECT f1.friend_id
  FROM friendships f1
  JOIN friendships f2 ON f1.friend_id = f2.friend_id
  WHERE f1.user_id = 1 
  AND f2.user_id = 2
  AND f1.status = 'CONFIRMED'
  AND f2.status = 'CONFIRMED';