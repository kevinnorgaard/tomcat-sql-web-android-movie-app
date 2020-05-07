DELIMITER $$

CREATE PROCEDURE add_movie(IN movie_title VARCHAR(100), IN movie_year INTEGER, IN movie_director VARCHAR(100), IN star_name VARCHAR(100), IN genre_name VARCHAR(32), OUT ret_movie_id VARCHAR(10), OUT ret_star_id VARCHAR(10), OUT ret_genre_id INTEGER, OUT added_movie BOOLEAN, OUT added_star BOOLEAN, OUT added_genre BOOLEAN)
BEGIN
	DECLARE movie_id VARCHAR(10);
	DECLARE movie_next_id INTEGER;
	DECLARE movie_count INTEGER;
	DECLARE stars_count INTEGER;
	DECLARE genres_count INTEGER;
	DECLARE star_id VARCHAR(10);
	DECLARE star_next_id INTEGER;
	DECLARE genre_id INTEGER;
	SELECT COUNT(*) INTO movie_count FROM movies WHERE title = movie_title AND year = movie_year AND director = movie_director;
	IF (movie_count = 0) THEN
		SELECT id INTO movie_next_id FROM movies_next_id;
		UPDATE movies_next_id SET id = movie_next_id + 1;
		SELECT CONCAT("tt", movie_next_id) INTO movie_id;
		INSERT INTO movies (id, title, year, director) VALUES (movie_id, movie_title, movie_year, movie_director);
		SELECT COUNT(*) INTO stars_count FROM stars WHERE name = star_name;
		SELECT COUNT(*) INTO genres_count FROM genres WHERE name = genre_name;
		IF (stars_count > 0) THEN
			SELECT id INTO star_id FROM stars WHERE name = star_name LIMIT 1;
			SET added_star = FALSE;
		ELSE
			SELECT id INTO star_next_id FROM stars_next_id;
			UPDATE stars_next_id SET id = star_next_id + 1;
			SELECT CONCAT("nm", star_next_id) INTO star_id;
			INSERT INTO stars (id, name, birthYear) VALUES (star_id, star_name, NULL);
			SET added_star = TRUE;
		END IF;
		IF (genres_count > 0) THEN
			SELECT id INTO genre_id FROM genres WHERE name = genre_name LIMIT 1;
			SET added_genre = FALSE;
		ELSE
			INSERT INTO genres (id, name) VALUES (NULL, genre_name);
			SELECT id INTO genre_id FROM genres WHERE name = genre_name LIMIT 1;
			SET added_genre = TRUE;
		END IF;
		INSERT INTO stars_in_movies (starId, movieId) VALUES (star_id, movie_id);
		INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, movie_id);
		SET ret_movie_id = movie_id;
		SET added_movie = TRUE;
	ELSE
		SELECT id INTO ret_movie_id FROM movies WHERE title = movie_title AND year = movie_year AND director = movie_director LIMIT 1;
		SET added_movie = FALSE;
	END IF;
	SET ret_star_id = star_id;
	SET ret_genre_id = genre_id;
END
$$

DELIMITER ;