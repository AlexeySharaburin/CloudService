-- liqidbase formatted SQL
-- changeset Alexey:0001

create table user_data
(
    id        serial       NOT NULL,
    username  VARCHAR(255) NOT NULL primary key,
    password  VARCHAR(255) NOT NULL,
    data_path VARCHAR(255) NOT NULL,
    is_enable boolean
);

create table storage
(
    id        serial    NOT NULL primary key,
    file_name VARCHAR(255),
    is_exist  boolean,
    date      timestamp not null default now()
);

create INDEX index_user
    on user_data (username);

