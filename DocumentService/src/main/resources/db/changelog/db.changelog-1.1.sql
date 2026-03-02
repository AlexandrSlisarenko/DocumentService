--liquibase formatted sql

--changeset aslisarenko:1
alter table document_db.approval_register
    rename column cvuuid to uuid;

alter table document_db.history
    rename column cvuuid to uuid;