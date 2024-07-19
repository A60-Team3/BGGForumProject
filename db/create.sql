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
    registered_at DATETIME DEFAULT current_timestamp(),
    user_role     ENUM ('ADMIN', 'MODERATOR', 'USER') NOT NULL,
    is_blocked    tinyint  DEFAULT 0,
    is_deleted    tinyint  DEFAULT 0
);

CREATE TABLE phones
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    number  VARCHAR(20) NOT NULL,
    user_id INT         NOT NULL,
    constraint fk_phones_users
        foreign key (user_id) references users (id)
            ON DELETE CASCADE
);


CREATE TABLE posts
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(64)   NOT NULL,
    content    VARCHAR(8192) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP(),
    user_id    INT           NOT NULL,

    constraint fk_posts_users
        foreign key (user_id) references users (id)
            ON DELETE CASCADE
);

CREATE TABLE comments
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    content    VARCHAR(8192) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP(),
    user_id    INT           NOT NULL,
    post_id    INT           NOT NULL,

    constraint fk_comments_users
        foreign key (user_id) references users (id)
            ON DELETE CASCADE,
    constraint fk_comments_posts
        foreign key (post_id) references posts (id)
            ON DELETE CASCADE

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
        foreign key (post_id) references posts (id)
            ON DELETE CASCADE,
    constraint fk_posts_tags_tags
        foreign key (tag_id) references tags (id)
            ON DELETE CASCADE
);


CREATE TABLE reactions
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    post_id       INT                      NOT NULL,
    user_id       INT                      NOT NULL,
    reaction_type ENUM ('LIKE', 'DISLIKE') NOT NULL,
    constraint fk_reactions_posts
        foreign key (post_id) references posts (id)
            ON DELETE CASCADE,
    constraint fk_reactions_users
        foreign key (user_id) references users (id)
            ON DELETE CASCADE
);