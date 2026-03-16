--liquibase formatted sql

--changeset aslisarenko:1
CREATE TABLE IF NOT EXISTS document_db.document_data
(
    uuid_doc UUID primary key ,
    text_doc TEXT NOT NULL
);
--rollback DROP TABLE IF EXISTS document_db.document;




