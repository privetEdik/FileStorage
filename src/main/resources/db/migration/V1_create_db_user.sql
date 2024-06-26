create table storage.users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username varchar(32) UNIQUE NOT NULL,
                       password varchar(32) NOT NULL,
                       role varchar(32) not null
);

insert into storage.users (username, password, role) values
    ('edik','123', 'USER');