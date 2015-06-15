drop database if exists fotosharedb;
create database fotosharedb;
 
use fotosharedb;

create table users(
        username	varchar(20) not null primary key,
	password	varchar(100) not null,
	name		varchar(50) not null,
	email		varchar(50) not null
);
create table photos(
	photoid    	int not null auto_increment unique,
	photoname 	varchar(50),
	username 	varchar(50) not null,
	data		date,
	
foreign key(username) references users (username) on delete cascade

);

create table review(

	reviewid	 int not null auto_increment unique,
	photoid		int not null,
	username 	varchar(50) not null,
	reviewtext	varchar(500),
	data	timestamp,
foreign key(username) references users (username) on delete cascade,
foreign key(photoid) references photos (photoid) on delete cascade
);

create table scores(
	scoreid  int not null auto_increment unique,
	photoid		int not null,
	username	varchar(50) not null,
	score	int not null,
foreign key(username) references users (username) on delete cascade,
foreign key(photoid) references photos (photoid) on delete cascade

);

create table categories(
	tagid    	int not null auto_increment unique,
	description 	varchar(500),
	photoid 	int not null,
	category 	varchar(30),
foreign key(photoid) references photos (photoid) on delete cascade
);

