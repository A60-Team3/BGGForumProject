USE boardgames_forum;

INSERT INTO roles (role_type)
VALUES ('ADMIN'),
       ('MODERATOR'),
       ('USER');


INSERT INTO users (first_name, last_name, email, username, password)
VALUES ('John', 'Doe', 'john.doe@example.com', 'john_doe', 'password123'),                      -- Admin
       ('Jane', 'Smith', 'jane.smith@example.com', 'jane_smith', 'password123'),                -- Moderator
       ('Alice', 'Johnson', 'alice.johnson@example.com', 'alice_johnson', 'password123'),       -- User
       ('Bob', 'Brown', 'bob.brown@example.com', 'bob_brown', 'password123'),                   -- User
       ('Charlie', 'Davis', 'charlie.davis@example.com', 'charlie_davis', 'password123'),       -- Admin
       ('Eva', 'Miller', 'eva.miller@example.com', 'eva_miller', 'password123'),                -- Admin
       ('Frank', 'Wilson', 'frank.wilson@example.com', 'frank_wilson', 'password123'),          -- User
       ('Grace', 'Moore', 'grace.moore@example.com', 'grace_moore', 'password123'),             -- User
       ('Hank', 'Taylor', 'hank.taylor@example.com', 'hank_taylor', 'password123'),             -- User
       ('Ivy', 'Anderson', 'ivy.anderson@example.com', 'ivy_anderson', 'password123'),          -- User
       ('Jack', 'Thomas', 'jack.thomas@example.com', 'jack_thomas', 'password123'),             -- Admin
       ('Karen', 'Jackson', 'karen.jackson@example.com', 'karen_jackson', 'password123'),       -- User
       ('Leo', 'White', 'leo.white@example.com', 'leo_white', 'password123'),                   -- User
       ('Mia', 'Harris', 'mia.harris@example.com', 'mia_harris', 'password123'),                -- User
       ('Nina', 'Martin', 'nina.martin@example.com', 'nina_martin', 'password123'),             -- User
       ('Oscar', 'Garcia', 'oscar.garcia@example.com', 'oscar_garcia', 'password123'),          -- User
       ('Paul', 'Martinez', 'paul.martinez@example.com', 'paul_martinez', 'password123'),       -- User
       ('Quincy', 'Robinson', 'quincy.robinson@example.com', 'quincy_robinson', 'password123'), -- User
       ('Rita', 'Clark', 'rita.clark@example.com', 'rita_clark', 'password123'),                -- User
       ('Sam', 'Rodriguez', 'sam.rodriguez@example.com', 'sam_rodriguez', 'password123');       -- User


INSERT INTO phones (number, user_id)
VALUES ('123-456-7890', 1), -- Admin
       ('333-333-3333', 5), -- Admin
       ('444-444-4444', 6), -- Admin
       ('555-555-5555', 11); -- Admin


INSERT INTO posts (title, content, user_id)
VALUES ('Best Board Games of 2024', 'Let\'s discuss the best board games of 2024...', 1),
       ('Tips for New Players', 'Here are some tips for new board game players...', 2),
       ('Board Game Recommendations', 'Can anyone recommend some good board games?', 3),
       ('Favorite Strategy Games', 'What are your favorite strategy board games?', 4),
       ('Best Cooperative Games', 'Share your favorite cooperative board games!', 5),
       ('Top Card Games', 'What are the top card games this year?', 6),
       ('Family Game Night Ideas', 'Ideas for a fun family game night.', 7),
       ('Solo Board Games', 'Good board games to play solo.', 8),
       ('Game Reviews', 'Post your board game reviews here.', 9),
       ('Upcoming Game Releases', 'Discuss upcoming board game releases.', 10),
       ('Classic Board Games', 'Which classic board games do you love?', 11),
       ('Best Party Games', 'Best board games for parties.', 12),
       ('Educational Games', 'Board games that are educational.', 13),
       ('Most Challenging Games', 'What are the most challenging board games?', 14),
       ('Quick Play Games', 'Games that are quick to play.', 15),
       ('Best Game Components', 'Games with the best components.', 16),
       ('Unique Game Mechanics', 'Discuss unique game mechanics.', 17),
       ('Game Strategies', 'Share your game strategies here.', 18),
       ('Games for Large Groups', 'Best games for large groups.', 19),
       ('Games for Kids', 'Board games suitable for kids.', 20);


INSERT INTO comments (content, user_id, post_id)
VALUES ('I think Gloomhaven is the best!', 3, 1),
       ('Thanks for the tips!', 4, 2),
       ('I recommend Catan and Carcassonne.', 2, 3),
       ('I love Twilight Struggle.', 1, 4),
       ('Pandemic is a great cooperative game.', 5, 5),
       ('I enjoy playing Dominion.', 6, 6),
       ('We love Ticket to Ride for family night.', 7, 7),
       ('Friday is a great solo game.', 8, 8),
       ('Here is my review of Wingspan.', 9, 9),
       ('Looking forward to the new expansion.', 10, 10),
       ('Chess is a classic favorite.', 11, 11),
       ('Codenames is perfect for parties.', 12, 12),
       ('We use board games in our classroom.', 13, 13),
       ('Mage Knight is very challenging.', 14, 14),
       ('Love games that are quick and fun.', 15, 15),
       ('Scythe has amazing components.', 16, 16),
       ('Azul has unique mechanics.', 17, 17),
       ('Here are some strategies for Risk.', 18, 18),
       ('Werewolf is great for large groups.', 19, 19),
       ('My kids love playing Candy Land.', 20, 20);


INSERT INTO tags (name)
VALUES ('Strategy'),
       ('Family'),
       ('Card Game'),
       ('Cooperative'),
       ('Solo'),
       ('Party'),
       ('Educational'),
       ('Classic'),
       ('Challenging'),
       ('Quick Play');


INSERT INTO posts_tags (post_id, tag_id)
VALUES (1, 1),
       (1, 4),
       (2, 2),
       (3, 1),
       (3, 2),
       (4, 1),
       (5, 4),
       (6, 3),
       (7, 2),
       (8, 5),
       (9, 1),
       (10, 1),
       (11, 8),
       (12, 6),
       (13, 7),
       (14, 9),
       (15, 10),
       (16, 1),
       (17, 1),
       (18, 1),
       (19, 2),
       (20, 2);


INSERT INTO reactions (post_id, user_id, reaction_type)
VALUES (1, 2, 'LIKE'),
       (1, 3, 'LIKE'),
       (2, 1, 'DISLIKE'),
       (2, 4, 'LIKE'),
       (3, 1, 'LIKE'),
       (4, 2, 'DISLIKE'),
       (5, 3, 'LIKE'),
       (6, 4, 'LIKE'),
       (7, 5, 'LIKE'),
       (8, 6, 'DISLIKE'),
       (9, 7, 'LIKE'),
       (10, 8, 'LIKE'),
       (11, 9, 'DISLIKE'),
       (12, 10, 'LIKE'),
       (13, 11, 'LIKE'),
       (14, 12, 'DISLIKE'),
       (15, 13, 'LIKE'),
       (16, 14, 'LIKE'),
       (17, 15, 'DISLIKE'),
       (18, 16, 'LIKE'),
       (19, 17, 'LIKE'),
       (20, 18, 'DISLIKE');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 3),
       (5, 3),
       (6, 3),
       (7, 3),
       (8, 3),
       (9, 3),
       (10, 2),
       (11, 1),
       (12, 3),
       (13, 3),
       (14, 3),
       (15, 3),
       (16, 3),
       (17, 3),
       (18, 3),
       (19, 3),
       (20, 3);