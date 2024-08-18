USE boardgames_forum;

ALTER TABLE profile_pictures DROP CONSTRAINT profile_pictures_ibfk_1;
ALTER TABLE profile_pictures DROP COLUMN user_id;

CREATE TABLE profile_pictures_users
(
    photo_id int NOT NULL,
    user_id   INT          NOT NULL,
    constraint fk_profile_pictures_users_pictures
        foreign key (photo_id) references roles (id)
            ON DELETE CASCADE,
    constraint fk_profile_pictures_users_users
        foreign key (user_id) references users (id)
        ON DELETE CASCADE
);