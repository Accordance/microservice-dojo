CREATE TABLE account (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

insert into account (username) values ('igor');
