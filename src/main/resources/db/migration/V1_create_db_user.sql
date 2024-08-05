create table storage.users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username varchar(32) UNIQUE NOT NULL,
                       password varchar(32) NOT NULL
);