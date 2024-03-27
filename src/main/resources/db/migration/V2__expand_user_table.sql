ALTER TABLE users
    ADD COLUMN is_subscriber BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE users
    ADD COLUMN subscriber_percent FLOAT;