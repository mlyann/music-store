package la1;

import java.util.ArrayList;

public class Song {
    private final String title;
    private final String artist;
    private final Genre genre;
    private final int year;
    private Album album;

    // The rating of the song. Default is UNRATED, can be rated later
    private Rating rating = Rating.UNRATED;
    // The favourite status of the song. Default is false, can be changed later
    private boolean Favourite = false;

    public Song(String title, String artist) {
        this(title, artist, null, 0);
    }

    public Song(String title, String artist, String genre, int year) {
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
        this.album = null;
    }
//ming: it's a copy constructor
    public Song(Song other) {
        this.title = other.title;
        this.artist = other.artist;
        this.genre = other.genre;
        this.year = other.year;
        this.album = other.album;
        this.rating = other.rating;
        this.Favourite = other.Favourite;
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

    public String getAlbum() {
        if (album == null) {
            return null;
        }
        return album.getTitle();
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    /**
     * 设置歌曲评分。传入 null 会抛出异常，若想表示未评分，请使用 Rating.UNRATED。
     */
    public void setRating(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null. Use Rating.UNRATED to represent no rating.");
        }
        this.rating = rating;
    }

    public String toStringStore() {
        String result = String.format("[%s, by %s (%s, %d)", title, artist, genre.getGenre(), year);
        if (album != null) {
            result += ", Album: " + album.getTitle();
        }
        result += "]";
        return result;
    }

    public String toString() {
        String result = String.format("[%s, by %s (%s, %d) [%s, %s]", title, artist,
                genre.getGenre(), year, getFavourite(), rating.toString());
        if (album != null) {
            result += " [" + album.getTitle() + "]";
        }
        return result;
    }

    public ArrayList<String> toStringList() {
        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        result.add(artist);
        result.add(genre.getGenre());
        result.add(String.valueOf(year));
        result.add(getFavourite());
        result.add(rating.toString());
        if (album != null) {
            result.add(album.getTitle());
        } else {
            result.add(null);
        }
        return result;
    }
}
