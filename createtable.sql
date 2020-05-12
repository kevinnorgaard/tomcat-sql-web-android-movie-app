CREATE DATABASE moviedb;

use moviedb;

DROP TABLE IF EXISTS movies;
CREATE TABLE movies (
    id VARCHAR(10) NOT NULL,
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS stars;
CREATE TABLE stars (
    id VARCHAR(10) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthYear INTEGER,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS stars_in_movies;
CREATE TABLE stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars(id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS genres;
CREATE TABLE genres (
	id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS genres_in_movies;
CREATE TABLE genres_in_movies (
	genreId INTEGER NOT NULL,
	movieId VARCHAR(10) NOT NULL,
	FOREIGN KEY (genreId) REFERENCES genres(id) ON DELETE CASCADE,
	FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS sales;
CREATE TABLE sales (
	id INTEGER NOT NULL AUTO_INCREMENT,
	customerId INTEGER NOT NULL,
	movieId VARCHAR(10) NOT NULL,
	saleDate DATE NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS creditcards;
CREATE TABLE creditcards (
	id VARCHAR(20) NOT NULL,
	firstName VARCHAR(50) NOT NULL,
	lastName VARCHAR(50) NOT NULL,
	expiration DATE NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS ratings;
CREATE TABLE ratings (
	movieId VARCHAR(10) NOT NULL,
	ratings FLOAT NOT NULL,
	numVotes INTEGER NOT NULL,
	FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS customers;
CREATE TABLE customers (
	id INTEGER NOT NULL AUTO_INCREMENT,
	firstName VARCHAR(50) NOT NULL,
	lastName VARCHAR(50) NOT NULL,
	ccId VARCHAR(20) NOT NULL,
	address VARCHAR(200) NOT NULL,
	email VARCHAR(50) NOT NULL,
	password VARCHAR(20) NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

DROP TABLE IF EXISTS employees;
CREATE TABLE employees (
	email VARCHAR(50) NOT NULL,
	password VARCHAR(20) NOT NULL,
	fullname VARCHAR(100),
	PRIMARY KEY (email)
);

DROP TABLE IF EXISTS movies_next_id;
CREATE TABLE movies_next_id (
	id INTEGER
);

DROP TABLE IF EXISTS stars_next_id;
CREATE TABLE stars_next_id (
	id INTEGER
);

INSERT INTO movies_next_id (id) VALUES (499470);
INSERT INTO stars_next_id (id) VALUES (9423092);
INSERT INTO employees (email, password, fullname) VALUES ('classta@email.edu', 'classta', 'TA CS122B');