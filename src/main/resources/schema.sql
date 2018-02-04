CREATE TABLE users (
  id       INT IDENTITY PRIMARY KEY,
  email    VARCHAR(512) NOT NULL UNIQUE,
  password VARCHAR(32)  NOT NULL
);

CREATE INDEX uk_users_email_password
  ON users (email, password);

CREATE TABLE accounts (
  id       INT IDENTITY PRIMARY KEY,
  name     VARCHAR(255),
  currency VARCHAR(3),
  user_id  INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
);

CREATE INDEX uk_accounts_user_id_name
  ON accounts (user_id, name);

CREATE TABLE postings (
  id          INT IDENTITY PRIMARY KEY,
  amount      DECIMAL(7, 2) NOT NULL,
  description VARCHAR(1024),
  created     DATETIME      NOT NULL,
  account_id  INT NOT NULL,
  FOREIGN KEY (account_id) REFERENCES accounts (id)
    ON DELETE CASCADE
)