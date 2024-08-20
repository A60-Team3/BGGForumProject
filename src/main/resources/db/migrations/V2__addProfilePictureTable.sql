CREATE TABLE profile_pictures
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    photo_url VARCHAR(255) NOT NULL,
    user_id   INT          NOT NULL,
    foreign key (user_id) references users (id)
        ON DELETE CASCADE
);