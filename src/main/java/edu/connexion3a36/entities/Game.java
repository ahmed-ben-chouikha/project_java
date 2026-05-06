package edu.connexion3a36.entities;

import java.util.Objects;

/**
 * Entity representing a game from RAWG API
 */
public class Game {
    private long id;
    private String name;
    private String description;
    private double rating;
    private int reviewCount;
    private String backgroundImage;
    private String rawgUrl;
    private String releaseDate;
    private String[] genres;
    private String[] platforms;

    // Empty constructor
    public Game() {
    }

    // Full constructor
    public Game(long id, String name, String description, double rating, 
                int reviewCount, String backgroundImage, String rawgUrl, 
                String releaseDate, String[] genres, String[] platforms) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.backgroundImage = backgroundImage;
        this.rawgUrl = rawgUrl;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.platforms = platforms;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getRawgUrl() {
        return rawgUrl;
    }

    public void setRawgUrl(String rawgUrl) {
        this.rawgUrl = rawgUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id == game.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
