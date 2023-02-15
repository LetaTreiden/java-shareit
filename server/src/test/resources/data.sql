INSERT INTO USERS (NAME, EMAIL)
VALUES ('Aelin', 'aelin@whitethorn.com'),
       ('Rowan', 'rowan@whitethorn.com'),
       ('Dorin', 'dorian@havilliard.com'),
       ('Manon', 'manon@blackbeack.com');

INSERT INTO REQUESTS (DESCRIPTION, REQUESTER_ID, CREATED)
VALUES ('waiting for fight', '1', '2023-02-11 19:00:01'),
       ('going to survey', '2', '2023-02-11 19:00:01'),
       ('just wanted to read', '3', '2023-02-11 19:00:01'),
       ('Where is my crown?', '4', '2023-02-11 19:00:01');

INSERT INTO ITEMS (NAME, DESCRIPTION, AVAILABLE, OWNER_ID, REQUEST_ID)
VALUES ('Sword', 'For fights', true, '3', '1'),
       ('Knives', 'very sharp', true, '1', '2'),
       ('Old books', 'very old and maybe dangerous', true, '4', '3'),
       ('Crown', 'Very shiny', true, '2', '4');

INSERT INTO BOOKINGS (START_DATE, END_DATE, ITEM_ID, BOOKER_ID, STATUS)
VALUES ('2022-11-11 12:32:59', '2023-02-11 19:00:01', '1', '4', 'WAITING'),
       ('2022-11-11 12:32:59', '2024-11-25 12:32:59', '2', '3', 'WAITING'),
       ('2024-11-11 12:32:59', '2025-11-25 12:32:59', '3', '2', 'WAITING'),
       ('2023-11-11 12:32:59', '2023-02-11 19:00:01', '4', '1', 'WAITING');

INSERT INTO COMMENTS (TEXT, ITEM_ID, AUTHOR_ID, CREATED)
VALUES ('Amazing sword', '1', '4', '2022-11-25 12:34:59');
