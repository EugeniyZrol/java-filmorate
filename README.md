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
  SELECT f.name, COUNT(fl.user_id) AS likes 
  FROM films f
  LEFT JOIN film_likes fl ON f.film_id = fl.film_id
  GROUP BY f.film_id
  ORDER BY likes DESC
  LIMIT 5;