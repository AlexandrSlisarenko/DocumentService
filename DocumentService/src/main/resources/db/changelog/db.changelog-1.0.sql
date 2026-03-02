--liquibase formatted sql

--changeset aslisarenko:1
CREATE SCHEMA IF NOT EXISTS document_db;
--rollback DROP SCHEMA IF EXISTS document_db;

--changeset aslisarenko:2
CREATE TABLE IF NOT EXISTS document_db.document
(
    id   BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    name VARCHAR(64) NOT NULL,
    author VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    create_update TIMESTAMP NOT NULL
);
--rollback DROP TABLE IF EXISTS document_db.document;

--changeset aslisarenko:3
CREATE TABLE IF NOT EXISTS document_db.history
(
    id   BIGSERIAL PRIMARY KEY,
    cvUuid UUID NOT NULL,
    author_chang VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    command VARCHAR(32) NOT NULL,
    date_chang TIMESTAMP NOT NULL,
    comment VARCHAR(128)
);
--rollback DROP TABLE IF EXISTS document_db.history;

--changeset aslisarenko:4
CREATE TABLE IF NOT EXISTS document_db.approval_register
(
    id   BIGSERIAL PRIMARY KEY,
    cvUuid UUID NOT NULL
);
--rollback DROP TABLE IF EXISTS document_db.approval_register;

