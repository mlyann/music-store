package la1;

import java.util.ArrayList;

/**
 * Represents a song with properties such as title, artist, genre, year, album, rating, and favorite status.
 * This class extends {@code TablePrinter} for table display functionality.
 */
public class Song extends TablePrinter {
    private final String title;
    private final String artist;
    private final Genre genre;
    private final int year;
    private String albumTitle;

    private transient Album album;

    private Rating rating = Rating.UNRATED;
    private boolean Favourite = false;


    /**
     * Constructs a {@code Song} with the specified title and artist.
     * @param title  the title of the song
     * @param artist the artist of the song
     * @throws IllegalArgumentException if title or artist is null or empty
     */
    public Song(String title, String artist) {
        this(title, artist, null, 0);
    }

    public Song(String title, String artist, String genre, int year) {
        this.albumTitle = null;
        if (title == null || title.trim().isEmpty() || artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("Title and artist are required.");
        }
        this.title = title;
        this.artist = artist;
        if (genre == null || genre.trim().isEmpty()) {
            this.genre = Genre.UNKNOWN;
        } else {
            this.genre = Genre.fromString(genre);
        }
        this.year = year;
    }

    /**
     * Constructs a {@code Song} with the specified title, artist, genre, and year.
     * @param other  the title of the song
     * @throws IllegalArgumentException if title or artist is null or empty
     */
    public Song(Song other) {
        this.title = other.title;
        this.artist = other.artist;
        this.genre = other.genre;
        this.year = other.year;
        this.albumTitle = other.albumTitle;
        this.rating = other.rating;
        this.Favourite = other.Favourite;
    }

    public void setAlbum(Album album) {
        this.album = album;
        this.albumTitle = (album != null) ? album.getTitle() : null;
    }

    public String getAlbumTitle() {
        if (albumTitle == null) {
            return null;
        }
        return albumTitle;
    }


    public Album getAlbum() {
        if (album == null) {
            return null;
        }
        return album;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    // return the genre of the song, in string format
    public String getGenre() {
        return genre.getGenre();
    }

    public int getYear() {
        return year;
    }

    public String getRating() {
        return rating.toString();
    }

    public int getRatingInt() {
        return rating.getValue();
    }

    // get the state of Favorite
    public String getFavourite() {
        if (Favourite) {
            return "♥";
        }
        return "♡";
    }

    public boolean isFavourite() {
        boolean result = Favourite;
        return result;
    }

    public void setFavourite(boolean favourite) {
        Favourite = favourite;
    }

    // when you click the favorite button, the state of Favorite will change
    public void changeFavourite() {
        Favourite = !Favourite;
    }


    public void setAlbum(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    /**
     * set numbers for rating, if null, it will be Rating.UNRATED。
     */
    public void setRating(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null. Use Rating.UNRATED to represent no rating.");
        }
        this.rating = rating;
    }

    /**
     * Returns a string representation of the song, including its title, artist, genre, year,
     * favorite status, rating, and album title
     * @return a formatted string with song details
     */
    public String toString() {
        String result = String.format("[%s, by %s (%s, %d) [%s, %s]", title, artist,
                genre.getGenre(), year, getFavourite(), rating.toString());
        if (albumTitle != null) {
            result += " [" + albumTitle + "]";
        }
        return result;
    }


    /**
     * Returns the song information as a list of strings.*
     * @return an {@code ArrayList<String>} representing the song's details
     */
    public ArrayList<String> toStringList() {
        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        result.add(artist);
        result.add(genre.getGenre());
        result.add(String.valueOf(year));
        result.add(getFavourite());
        result.add(rating.toString());
        if (albumTitle != null) {
            result.add(albumTitle);
        } else {
            result.add(null);
        }
        return result;
    }
}
