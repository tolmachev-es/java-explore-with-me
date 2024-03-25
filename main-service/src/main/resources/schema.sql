CREATE TABLE IF NOT EXISTS CATEGORY (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS COMPILATIONS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    TITLE VARCHAR(50) NOT NULL,
    PINNED BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENTS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ANNOTATION VARCHAR(2000) NOT NULL,
    CATEGORY_ID BIGINT NOT NULL,
    DESCRIPTION VARCHAR(7000) NOT NULL,
    EVENT_DATE TIMESTAMP NOT NULL,
    LIMIT_GUESTS BIGINT NOT NULL,
    MODERATION BOOLEAN NOT NULL,
    TITLE VARCHAR(120) NOT NULL,
    OWNER_ID BIGINT NOT NULL,
    CREATED_TIME TIMESTAMP NOT NULL,
    PUBLISHED_TIME TIMESTAMP,
    STATUS VARCHAR(10) NOT NULL,
    PAID BOOLEAN NOT NULL,
    LAT REAL NOT NULL,
    LON REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS REQUESTS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    USER_ID BIGINT NOT NULL,
    EVENT_ID BIGINT NOT NULL,
    CONFIRMED VARCHAR(10) NOT NULL,
    CREATED TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS USERS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME VARCHAR(250) NOT NULL,
    EMAIL VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS COMPILATION_EVENTS (
    COMPILATION_ID BIGINT NOT NULL,
    EVENT_ID BIGINT NOT NULL
);