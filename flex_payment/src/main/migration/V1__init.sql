CREATE TABLE accounts
(
    id          VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    api_token   VARCHAR(255) NOT NULL,
    active      BOOLEAN      NOT NULL,
    webhook_url VARCHAR(255),
    allowed_ips VARCHAR(255),
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE TABLE payments
(
    id                  VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_by          VARCHAR(255),
    last_update_comment VARCHAR(500),
    payment_id          VARCHAR(255) NOT NULL,
    customer_id         VARCHAR(255),
    order_id            VARCHAR(255),
    amount              DECIMAL      NOT NULL,
    currency            VARCHAR(3)   NOT NULL,
    payment_method      VARCHAR(255) NOT NULL,
    api_token           VARCHAR(255) NOT NULL,
    account_id          VARCHAR(255),
    status              VARCHAR(255) NOT NULL,
    reference           VARCHAR(255),
    description         VARCHAR(255),
    timestamp           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE roles
(
    name       VARCHAR(20) NOT NULL,
    deleted    BOOLEAN,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (name)
);

CREATE TABLE user_roles
(
    role_name VARCHAR(20)  NOT NULL,
    user_id   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_name, user_id)
);

CREATE TABLE users
(
    id         VARCHAR(255) NOT NULL,
    deleted    BOOLEAN,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(255),
    name       VARCHAR(50),
    username   VARCHAR(50),
    email      VARCHAR(100),
    password   VARCHAR(120),
    active     BOOLEAN      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_api_token UNIQUE (api_token);

ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_name UNIQUE (name);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_payment UNIQUE (payment_id);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_reference UNIQUE (reference);

ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uk_users_username UNIQUE (username);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role_entity FOREIGN KEY (role_name) REFERENCES roles (name);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);