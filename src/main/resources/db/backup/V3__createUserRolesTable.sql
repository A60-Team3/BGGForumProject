CREATE TABLE users_roles
(
    user_id INT NOT NULL,
    role_id  INT NOT NULL,
    constraint fk_users_roles_users
        foreign key (user_id) references users (id)
            ON DELETE CASCADE,
    constraint fk_users_roles_roles
        foreign key (role_id) references roles (id)
            ON DELETE CASCADE
);