ALTER TABLE profile_pictures
    DROP CONSTRAINT profile_pictures_ibfk_1;
ALTER TABLE profile_pictures
    DROP COLUMN user_id;

CREATE TABLE profile_pictures_users
(
    user_id  INT NOT NULL,
    photo_id int NOT NULL,
    constraint fk_profile_pictures_users_pictures
        foreign key (photo_id) references profile_pictures (id)
            ON DELETE CASCADE,
    constraint fk_profile_pictures_users_users
        foreign key (user_id) references users (id)
            ON DELETE CASCADE
);