drop user 'fotoshare'@'localhost';
create user 'fotoshare'@'localhost' identified by 'fotoshare';
grant all privileges on fotosharedb.* to 'fotoshare'@'localhost';
flush privileges;