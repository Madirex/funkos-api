INSERT INTO Category (id, type, created_at, updated_at, active)
VALUES (1, 'DISNEY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
       (2, 'MOVIE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
       (3, 'SERIES', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
       (4, 'SUPERHEROS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
       (5, 'OTHER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

INSERT INTO Funko (id, name, price, quantity, image, category_id, created_at, updated_at)
VALUES
    (UUID(), 'Mickey Mouse', 19.99, 10, 'https://www.madirex.com/funko/mickeymouse', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'Harry Potter', 24.99, 8, 'https://www.madirex.com/funko/harrypotter', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'Stranger Things', 29.99, 5, 'https://www.madirex.com/funko/strangerthings', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'Spider-Man', 14.99, 12, 'https://www.madirex.com/funko/spiderman', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'Monkey D. Luffy', 34.99, 7, 'https://www.madirex.com/funko/monkeydluffy', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'Doctor Who', 39.99, 3, 'https://www.madirex.com/funko/doctorwho', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'Random Funko', 34.99, 7, 'https://www.madirex.com/funko/random', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO USERS (id, name, surname, username, email, password, created_at, updated_at, is_deleted)
VALUES
    ('a2bcb7b3-4c36-4073-9ab8-9d8f4a57301b', 'Madirex', 'Land', 'Madirex', 'contact@madirex.com', '$2a$10$/iB5BY9exKMl6Soofb7EquzGHWm3vOPHBCBb6Cg3sWcfz9kHUyIpC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
    ('d4515840-8fe2-4107-8bde-0f1c545ef4c5', 'User', 'User', 'User', 'user@madirex.com', '$2a$10$/iB5BY9exKMl6Soofb7EquzGHWm3vOPHBCBb6Cg3sWcfz9kHUyIpC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

insert into USER_ROLES (user_id, roles)
values ('a2bcb7b3-4c36-4073-9ab8-9d8f4a57301b', 'USER');
insert into USER_ROLES (user_id, roles)
values ('a2bcb7b3-4c36-4073-9ab8-9d8f4a57301b', 'ADMIN');
insert into USER_ROLES (user_id, roles)
values ('d4515840-8fe2-4107-8bde-0f1c545ef4c5', 'USER');
