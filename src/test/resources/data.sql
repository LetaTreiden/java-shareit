INSERT INTO USERS (NAME, EMAIL)
VALUES ('Buffy', 'buffy@vampire.com'),
       ('Leo', 'leo@angel.com'),
       ('Kuzya', 'kuzya@brownie.com'),
       ('Sheldon cooper', 'cooper@physicist.com');

INSERT INTO REQUESTS (DESCRIPTION, REQUESTOR_ID, CREATED)
VALUES ('I need a fork', '1', '2022-11-25 12:32:59'),
       ('I need wings', '2', '2022-11-25 12:32:59'),
       ('I need jam', '3', '2022-11-25 12:32:59'),
       ('I need a Nobel prize', '4', '2022-11-25 12:32:59');

INSERT INTO ITEMS (NAME, DESCRIPTION, AVAILABLE, OWNER_ID, REQUEST_ID)
VALUES ('Fork', 'It is needed in order to eat', true, '3', '1'),
       ('Wings', 'Need to fly', true, '1', '2'),
       ('Jam', 'Очень вкусное и сладкое', true, '4', '3'),
       ('Nobel prize', 'One of the most prestigious international awards', true, '2', '4');

INSERT INTO BOOKINGS (START_DATE, END_DATE, ITEM_ID, BOOKER_ID, STATUS)
VALUES ('2022-11-11 12:32:59', '2022-11-25 12:32:59', '1', '4', 'WAITING'),
       ('2022-11-11 12:32:59', '2024-11-25 12:32:59', '2', '3', 'WAITING'),
       ('2024-11-11 12:32:59', '2025-11-25 12:32:59', '3', '2', 'WAITING'),
       ('2023-11-11 12:32:59', '2023-11-25 12:32:59', '4', '1', 'WAITING');

INSERT INTO COMMENTS (TEXT, ITEM_ID, AUTHOR_ID, CREATED)
VALUES ('The best fork', '1', '4', '2022-11-25 12:34:59');
