CREATE TABLE account (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO account (username) VALUES ('john');
