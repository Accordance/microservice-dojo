ALTER TABLE account
ADD full_name varchar(255);

UPDATE account SET full_name = 'John Doe'
WHERE username = 'john';
