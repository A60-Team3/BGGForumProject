DROP DATABASE boardgames_forum;

CREATE DATABASE boardgames_forum;
USE boardgames_forum;

CREATE TABLE users
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(32)                         NOT NULL,
    last_name     VARCHAR(32)                         NOT NULL,
    email         VARCHAR(50)                         NOT NULL UNIQUE,
    username      VARCHAR(50)                         NOT NULL,
    password      VARCHAR(50)                         NOT NULL,
    phone_number  VARCHAR(20),
    registered_at DATETIME DEFAULT current_timestamp(),
    user_role     ENUM ('ADMIN', 'MODERATOR', 'USER') NOT NULL,
    is_blocked    tinyint  DEFAULT 0,
    is_deleted    tinyint  DEFAULT 0
);

CREATE TABLE posts
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(64),
    content    VARCHAR(8192) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP(),
    user_id    INT           NOT NULL,
    comment_to INT,

    constraint fk_posts_users
        foreign key (user_id) references users (id),
    constraint fk_comments_comments
        foreign key (comment_to) references posts (id)
);

CREATE TABLE tags
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE posts_tags
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL,
    constraint fk_posts_tags_posts
        foreign key (post_id) references posts (id),
    constraint fk_posts_tags_tags
        foreign key (tag_id) references tags (id)
);


CREATE TABLE reactions
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    post_id       INT                      NOT NULL,
    user_id       INT                      NOT NULL,
    reaction_type ENUM ('LIKE', 'DISLIKE') NOT NULL,
    constraint fk_reactions_posts
        foreign key (post_id) references posts (id),
    constraint fk_reactions_users
        foreign key (user_id) references users (id)
);