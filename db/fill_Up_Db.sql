USE boardgames_forum;

INSERT INTO users (first_name, last_name, email, username, password, phone_number, user_role, is_Blocked, is_Deleted)
VALUES
    ('John', 'Doe', 'john.doe@example.com', 'johndoe', 'password123', '1234567890', 'USER', 0, 0),
    ('Jane', 'Smith', 'jane.smith@example.com', 'janesmith', 'password123', '0987654321', 'MODERATOR', 0, 0),
    ('Alice', 'Johnson', 'alice.johnson@example.com', 'alicej', 'password123', '1231231234', 'USER', 0, 0),
    ('Bob', 'Brown', 'bob.brown@example.com', 'bobb', 'password123', '2342342345', 'USER', 0, 0),
    ('Charlie', 'Davis', 'charlie.davis@example.com', 'charlied', 'password123', '3453453456', 'ADMIN', 0, 0),
    ('Eve', 'Miller', 'eve.miller@example.com', 'evem', 'password123', '4564564567', 'USER', 0, 0),
    ('Frank', 'Wilson', 'frank.wilson@example.com', 'frankw', 'password123', '5675675678', 'USER', 0, 0),
    ('Grace', 'Moore', 'grace.moore@example.com', 'gracem', 'password123', '6786786789', 'USER', 0, 0),
    ('Hank', 'Taylor', 'hank.taylor@example.com', 'hankt', 'password123', '7897897890', 'USER', 0, 0),
    ('Ivy', 'Anderson', 'ivy.anderson@example.com', 'ivya', 'password123', '8908908901', 'USER', 0, 0);

INSERT INTO posts (title, content, likes, dislikes, user_id)
VALUES
    ('First Post', 'This is the content of the first post.', 10, 2, 1),
    ('Hello World', 'Hello, this is my first post!', 20, 1, 2),
    ('Tips and Tricks', 'Here are some useful tips and tricks.', 15, 3, 3),
    ('Favorite Recipes', 'Sharing my favorite recipes with you.', 5, 0, 4),
    ('Travel Guide', 'A guide to my favorite travel destinations.', 8, 1, 5),
    ('Movie Reviews', 'My thoughts on the latest movies.', 25, 4, 6),
    ('Book Club', 'Join our book club!', 3, 0, 7),
    ('Fitness Journey', 'Documenting my fitness journey.', 7, 1, 8),
    ('Tech News', 'Latest updates in the tech world.', 12, 2, 9),
    ('Gardening Tips', 'How to start your own garden.', 9, 0, 10);

INSERT INTO comments (content, likes, dislikes, user_id, post_id, replying_to)
VALUES
    ('Great post!', 5, 0, 2, 1, NULL),
    ('I found this very helpful.', 10, 1, 3, 1, NULL),
    ('Thanks for sharing!', 8, 0, 4, 2, NULL),
    ('Can you share more details?', 2, 0, 5, 3, NULL),
    ('I love these recipes!', 7, 1, 6, 4, NULL),
    ('Very informative.', 6, 0, 7, 5, NULL),
    ('I disagree with your review.', 3, 2, 8, 6, NULL),
    ('I would love to join!', 4, 0, 9, 7, 3),
    ('Great progress!', 5, 0, 10, 8, NULL),
    ('Interesting read!', 9, 1, 1, 9, 2);

INSERT INTO tags (name)
VALUES
    ('Grand Strategy'),
    ('Party'),
    ('Card Game'),
    ('City Building'),
    ('Fantasy'),
    ('Horror'),
    ('Humour'),
    ('Economic'),
    ('Gardening'),
    ('Book');

INSERT INTO posts_tags (post_id, tag_id)
VALUES
    (1, 4),
    (2, 1),
    (3, 10),
    (4, 3),
    (5, 2),
    (6, 7),
    (7, 8),
    (8, 5),
    (9, 1),
    (10, 9);




