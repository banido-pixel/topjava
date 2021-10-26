DELETE
FROM user_roles;
DELETE
FROM meals;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, datetime, description, calories)
VALUES (100000, '2020-10-30 09:00', 'Завтрак', 600),
       (100000, '2020-10-30 14:00', 'Обед', 1200),
       (100000, '2020-11-30 09:00', 'Завтрак', 500),
       (100000, '2020-11-30 14:00', 'Обед', 1600),
       (100001, '2020-11-30 09:00', 'Завтрак Админ', 500),
       (100001, '2020-11-30 19:00', 'Ужин Админ', 400);
