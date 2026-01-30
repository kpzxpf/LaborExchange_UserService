DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

CREATE TABLE roles
(
    id        SERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

ALTER TABLE roles
    ADD CONSTRAINT chk_role_name_not_empty CHECK (length(trim(role_name)) > 0),
    ADD CONSTRAINT chk_role_name_uppercase CHECK (role_name = UPPER(role_name));

CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    username     VARCHAR(50) UNIQUE  NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    first_name   VARCHAR(50),
    last_name    VARCHAR(50),
    phone_number VARCHAR(20),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role_id      INTEGER             NOT NULL REFERENCES roles (id)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);


ALTER TABLE users
    ADD CONSTRAINT chk_email_format
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE users
    ADD CONSTRAINT chk_phone_format
        CHECK (phone_number IS NULL OR phone_number ~ '^\+?[0-9]{10,15}$');

INSERT INTO roles (role_name)
VALUES ('JOB_SEEKER');
INSERT INTO roles (role_name)
VALUES ('EMPLOYER');
INSERT INTO roles (role_name)
VALUES ('ADMIN');
