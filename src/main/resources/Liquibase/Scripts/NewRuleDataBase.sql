--liquibase formatted sql

--changeset dechernyakov:1

create table rule_query_dto
(
    id           bigserial primary key,
    product_name text,
    product_id   text,
    product_text text
);
create table rule_query
(
    id                Bigserial PRIMARY KEY,
    query             TEXT,
    negate            boolean,
    rule_query_dto_id BIGINT REFERENCES rule_query_dto (id)

);
create table rule_query_arguments(
    rule_query_id bigint references rule_query(id),
    arguments char(255)
);

--changeset dechernyakov:2
CREATE TABLE rule_stats (
                            id BIGSERIAL PRIMARY KEY,
                            rule_id BIGINT NOT NULL UNIQUE REFERENCES rule_query_dto(id) ON DELETE CASCADE,
                            count INT NOT NULL DEFAULT 0
);