CREATE DATABASE boardgames_forum;
USE boardgames_forum;

CREATE TABLE users
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(32) NOT NULL,
    last_name    VARCHAR(32) NOT NULL,
    email        VARCHAR(50) NOT NULL UNIQUE,
    username     VARCHAR(50) NOT NULL,
    password     VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    registered_at DATETIME DEFAULT NOW(),
    user_role    ENUM ('ADMIN', 'MODERATOR', 'USER'),
    is_Blocked   tinyint DEFAULT 0,
    is_Deleted   tinyint DEFAULT 0
);

CREATE TABLE posts
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    title    VARCHAR(64)   NOT NULL,
    content  VARCHAR(8192) NOT NULL,
    likes    INT DEFAULT 0,
    dislikes INT DEFAULT 0,
    created_at DATETIME DEFAULT NOW(),
    user_id  INT           NOT NULL,

    constraint fk_posts_users
        foreign key (user_id) references users (id)
);

CREATE TABLE comments
(
    id    INT AUTO_INCREMENT PRIMARY KEY ,
    content  VARCHAR(8192) NOT NULL,
    likes    INT DEFAULT 0,
    dislikes INT DEFAULT 0,
    created_at DATETIME DEFAULT NOW(),
    user_id  INT           NOT NULL,
    post_id INT NOT NULL,
    replying_to INT,

    constraint fk_comments_posts
        foreign key (post_id) references posts (id),
    constraint fk_comments_comments
        foreign key (replying_to) references comments (id)
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




