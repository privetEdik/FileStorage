create table users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       login varchar(32) UNIQUE NOT NULL,
                       password varchar(32) NOT NULL
);