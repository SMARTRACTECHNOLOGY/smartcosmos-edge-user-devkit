-- *************************************
--        AUTHORITY
-- *************************************
CREATE TABLE authority (
  authority VARCHAR(255) NOT NULL,
  PRIMARY KEY (authority)
);

-- *************************************
--        ROLE
-- *************************************
CREATE TABLE role (
  id           BINARY(16)   NOT NULL,
  active       BIT          NOT NULL,
  created      DATETIME,
  description  VARCHAR(255),
  lastModified DATETIME     NOT NULL,
  name         VARCHAR(255) NOT NULL,
  tenantId     BINARY(16)   NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE role
  ADD CONSTRAINT UK_ROLE_01 UNIQUE (name, tenantId);

-- *************************************
--        ROLE_AUTHORITIES
-- *************************************
CREATE TABLE role_authorities (
  role      BINARY(16)   NOT NULL,
  authority VARCHAR(255) NOT NULL,
  PRIMARY KEY (role, authority)
);

-- *************************************
--       ROLE_USER
-- *************************************
CREATE TABLE role_user (
  role_id  BINARY(16) NOT NULL,
  users_id BINARY(16) NOT NULL,
  PRIMARY KEY (role_id, users_id)
);

-- *************************************
--        TENANT
-- *************************************
CREATE TABLE tenant (
  id           BINARY(16)   NOT NULL,
  active       BIT          NOT NULL,
  created      DATETIME,
  lastModified DATETIME     NOT NULL,
  name         VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE tenant
  ADD CONSTRAINT UK_TENANT_01 UNIQUE (name);

-- *************************************
--        USER
-- *************************************
CREATE TABLE user (
  id           BINARY(16)   NOT NULL,
  active       BIT          NOT NULL,
  created      DATETIME,
  emailAddress VARCHAR(255),
  givenName    VARCHAR(255),
  lastModified DATETIME     NOT NULL,
  password     VARCHAR(255) NOT NULL,
  surname      VARCHAR(255),
  tenantId     BINARY(16)   NOT NULL,
  username     VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE user
  ADD CONSTRAINT UK_USER_01 UNIQUE (username);

-- *************************************
--       USER_ROLES
-- *************************************
CREATE TABLE user_roles (
  user BINARY(16) NOT NULL,
  role BINARY(16) NOT NULL,
  PRIMARY KEY (user, role)
);
