-- liqidbase formatted SQL
-- changeset Alexey:0001

create table user_data
(
    id_user        serial       NOT NULL,
    username  VARCHAR(255) NOT NULL primary key,
    password  VARCHAR(255) NOT NULL,
    data_path VARCHAR(255) NOT NULL,
    is_enable boolean
);

create table storage
(
    id_file        serial    NOT NULL primary key,
    file_name VARCHAR(255),
    is_exist  boolean,
    date      timestamp not null default now(),
    username  VARCHAR(255) NOT NULL,
    file_size integer,
    FOREIGN KEY (username) REFERENCES user_data(username)
--     constraint FK_username FOREIGN KEY (username)
--     references user_data(username)
);

create INDEX index_user
    on user_data (username);

