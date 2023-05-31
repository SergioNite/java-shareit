drop table if exists users cascade;
drop table if exists items cascade;
drop table if exists bookings;
drop table if exists comments;
drop table if exists requests;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT email_not_empty CHECK (users.email IS NOT NULL AND users.email <> ''),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT REFERENCES users (user_id) NOT NULL,
    request_id BIGINT,
    PRIMARY KEY (item_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items (item_id) NOT NULL,
    booker_id BIGINT REFERENCES users (user_id) NOT NULL,
    status VARCHAR(64) NOT NULL,
    PRIMARY KEY (booking_id)
);


CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512) NOT NULL,
    requester_id BIGINT REFERENCES users (user_id) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(512) NOT NULL,
    item_id BIGINT REFERENCES items (item_id) NOT NULL,
    author_id BIGINT REFERENCES users (user_id) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    PRIMARY KEY (comment_id)
);