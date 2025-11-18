create table roles
(
    role_id bigint auto_increment
        primary key,
    name    enum ('ROLE_ADMIN', 'ROLE_USER') null
);

create table tags
(
    tag_id bigint auto_increment
        primary key,
    name   varchar(255) not null,
    constraint UKt48xdq560gs3gap9g7jg36kgc
        unique (name)
);

create table users
(
    is_blocked        bit          not null,
    created_at        datetime(6)  not null,
    updated_at        datetime(6)  null,
    user_id           bigint auto_increment
        primary key,
    email             varchar(255) not null,
    first_name        varchar(255) not null,
    last_name         varchar(255) not null,
    password          varchar(255) not null,
    phone_number      varchar(255) null,
    profile_photo_url varchar(255) null,
    username          varchar(255) not null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint UKr43af9ap4edm43mmtq01oddj6
        unique (username)
);

create table posts
(
    likes_count int           null,
    created_at  datetime(6)   not null,
    post_id     bigint auto_increment
        primary key,
    updated_at  datetime(6)   null,
    user_id     bigint        not null,
    title       varchar(64)   not null,
    content     varchar(8192) not null,
    constraint FK5lidm6cqbc7u4xhqpxm898qme
        foreign key (user_id) references users (user_id)
);

create table comments
(
    likes_count       int           not null,
    comment_id        bigint auto_increment
        primary key,
    created_at        datetime(6)   not null,
    parent_comment_id bigint        null,
    post_id           bigint        not null,
    updated_at        datetime(6)   null,
    user_id           bigint        not null,
    content           varchar(1000) not null,
    constraint FK7h839m3lkvhbyv3bcdv7sm4fj
        foreign key (parent_comment_id) references comments (comment_id),
    constraint FK8omq0tc18jd43bu5tjh6jvraq
        foreign key (user_id) references users (user_id),
    constraint FKh4c7lvsc298whoyd4w9ta25cr
        foreign key (post_id) references posts (post_id)
);

create table comments_likes
(
    comment_id bigint not null,
    user_id    bigint not null,
    primary key (comment_id, user_id),
    constraint FKiwf3mhli7ej3pgf9ktj6vv08p
        foreign key (user_id) references users (user_id),
    constraint FKogmkq8clqlxqis53e9tlu4w96
        foreign key (comment_id) references comments (comment_id)
);

create table post_likes
(
    post_id bigint not null,
    user_id bigint not null,
    primary key (post_id, user_id),
    constraint FKa5wxsgl4doibhbed9gm7ikie2
        foreign key (post_id) references posts (post_id),
    constraint FKkgau5n0nlewg6o9lr4yibqgxj
        foreign key (user_id) references users (user_id)
);

create table posts_tags
(
    post_id bigint not null,
    tag_id  bigint not null,
    primary key (post_id, tag_id),
    constraint FK4svsmj4juqu2l8yaw6whr1v4v
        foreign key (tag_id) references tags (tag_id),
    constraint FKcreclgob71ibo58gsm6l5wp6
        foreign key (post_id) references posts (post_id)
);

create table user_roles
(
    role_id bigint not null,
    user_id bigint not null,
    primary key (user_id, role_id),
    constraint FKh8ciramu9cc9q3qcqiv4ue8a6
        foreign key (role_id) references roles (role_id),
    constraint FKhfh9dx7w3ubf1co1vdev94g3f
        foreign key (user_id) references users (user_id)
);

