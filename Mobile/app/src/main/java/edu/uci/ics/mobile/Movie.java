package edu.uci.ics.mobile;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String id;
    private String name;
    private int year;
    private String director;
    private String rating;
    private List<String> genres;
    private List<Star> stars;

    public Movie(String id, String name, int year, String director, String rating) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = new ArrayList<>();
        this.stars = new ArrayList<>();
    }

    public Movie(String id, String name, int year, String director, String rating, List<String> genres, List<Star> stars) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getRating() {
        return rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public List<Star> getStars() {
        return stars;
    }

    public void addStar(Star star) {
        stars.add(star);
    }
}