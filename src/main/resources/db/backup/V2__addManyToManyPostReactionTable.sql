USE boardgames_forum;

CREATE TABLE posts_users_reactions
(
    reaction_id INT NOT NULL,
    post_id     INT NOT NULL,
    user_id     INT NOT NULL,
    PRIMARY KEY (reaction_id, post_id, user_id),
    constraint fk_posts_users_reactions_posts
        foreign key (post_id) references posts (id)
            ON DELETE CASCADE,
    constraint fk_posts_users_reactions_users
        foreign key (user_id) references users (id)
            ON DELETE CASCADE,
    constraint fk_posts_users_reactions_reactions
        foreign key (reaction_id) references reactions (id)
);
