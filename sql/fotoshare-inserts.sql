source fotosharedb-schema.sql;


insert into users (username, password, name, email) values('joan', MD5('joan'), 'Joan Jerez Torres', 'joanjerezt@upc.edu');
insert into users (username, password, name, email) values('roc', MD5('roc'), 'Roc Messenger', 'roc@upc.edu');
insert into users (username, password, name, email) values('sergio', MD5('sergio'), 'Sergio Machado', 'sergio@upc.edu');


insert into photos ( photoname, username , data) values ('Ur', 'joan' , '2014-04-15');
insert into photos ( photoname, username , data) values ('Morella', 'roc' , '2013-01-05');


insert into review ( photoid, username ,reviewtext ) values (2, 'joan' , 'M agrada el poble de Morella, però no l he visitat encara');


insert into scores (photoid, username , score) values (2, 'joan', 7);

insert into scores (photoid, username , score) values (1, 'roc',10);


insert into categories ( photoid , description ,  category) values(1, 'Els bonics paisatges de Catalunya','paisatges' );
insert into categories (photoid , description ,  category) values( 2,'Variats i bells petits nuclis urbans', 'pobles' );


