ALTER TABLE account
ADD role varchar(255);

UPDATE account SET role = 'admin'
WHERE username = 'john';
