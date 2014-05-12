DROP KEYSPACE GR;
CREATE KEYSPACE GR
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE GR;
CREATE TABLE users (username text PRIMARY KEY, password text);
CREATE TABLE following (
    username text,
    followed text,
    PRIMARY KEY(username, followed)
);
CREATE TABLE followers (
    username  text,
    following text,
    PRIMARY KEY(username, following)
);
CREATE TABLE tweets (tweetid uuid PRIMARY KEY, username text, body text);
CREATE TABLE userline (
    tweetid  timeuuid,
    username text,
    body     text,
    PRIMARY KEY(username, tweetid)
);
CREATE TABLE timeline (
    username  text,
    tweetid   timeuuid,
    posted_by text,
    body      text,
    PRIMARY KEY(username, tweetid)
);