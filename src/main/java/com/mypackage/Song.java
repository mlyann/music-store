package la1;

public class Song {
    private final String title;
    private final String artist;
    private final Genre genre;
    private final int year;

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

    public Rating getRating() {
        return rating;
    }

    // get the state of Favorite
    public boolean getFavourite() {
        return Favourite;
    }

    // when you click the favorite button, the state of Favorite will change
    public void changeFavourite() {
        Favourite = !Favourite;
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

    @Override
    public String toString() {
        return "Song: " + title + ", Rating: " + rating.getValue();
    }
}
