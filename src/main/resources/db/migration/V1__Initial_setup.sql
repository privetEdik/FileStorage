create table storage.users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username varchar(255) UNIQUE NOT NULL,
                       password varchar(255) NOT NULL
);