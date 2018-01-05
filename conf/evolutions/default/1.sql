# --- !Ups

create table "USER" (
    "ID" BIGINT(20) NOT NULL AUTO_INCREMENT,
    "USERNAME" VARCHAR NOT NULL UNIQUE,
    "EMAIL" VARCHAR NOT NULL UNIQUE,
    "PASSWORD" VARCHAR NOT NULL,
    PRIMARY KEY ("ID")
);

INSERT INTO USER (USERNAME, EMAIL, PASSWORD)
VALUES ('admin', 'adm@example.com', '12_ad');

# --- !Downs

drop table "USER";
