CREATE TABLE IF NOT EXISTS STATISTIC (
ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
APPLICATION VARCHAR(255) NOT NULL,
URI VARCHAR(255) NOT NULL,
IP VARCHAR(50) NOT NULL,
REQUEST_TIME TIMESTAMP NOT NULL,
CONSTRAINT STAT_ID PRIMARY KEY (ID)
);