CREATE USER doiserver WITH ENCRYPTED PASSWORD 'md5249656fe86cbd179a45c21703b0df719';
CREATE DATABASE doidb;
\connect doidb;
CREATE SCHEMA doi_schema;

CREATE TABLE doi_schema.T_DOI_USERS (
 username varchar(255) NOT NULL,
 admin boolean NOT NULL,
 email varchar(255),
 PRIMARY KEY (username)
);

CREATE TABLE doi_schema.T_DOI_PROJECT (
 suffix int NOT NULL,
 projectname varchar(1024) NOT NULL,
 PRIMARY KEY (suffix) 
);

CREATE TABLE doi_schema.T_DOI_ASSIGNATIONS (
 username varchar(255) NOT NULL,
 suffix int NOT NULL,
 PRIMARY KEY (username, suffix)
);

CREATE TABLE doi_schema.T_DOI_TOKENS (
 token varchar(255) NOT NULL,
 PRIMARY KEY (token)
);

GRANT ALL PRIVILEGES ON SCHEMA doi_schema TO doiserver;
GRANT INSERT,DELETE,SELECT,UPDATE ON ALL TABLES IN SCHEMA doi_schema TO doiserver;
