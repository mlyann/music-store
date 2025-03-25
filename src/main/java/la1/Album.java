package la1;

import java.util.ArrayList;
import java.util.List;


public class Album {
    private final String title;
    private final String artist;
    private final la1.Genre genre;
    private final int year;
    private final ArrayList<Song> songs;
    private final ArrayList<Song> songInLibrary;

    public Album(String title, String artist, String genre, int year, List<Song> songs) {
        this.title = title;
        this.artist = artist;
        this.genre = Genre.fromString(genre);
        this.year = year;
        this.songs = new ArrayList<>();
        for (Song song : songs) {
            this.songs.add(new Song(song)); // DEEP COPY HERE
            song.setAlbum(this);
        }
        this.songInLibrary = new ArrayList<Song>();
    }

    public Album(Album other) {
        this.title = other.title;
        this.artist = other.artist;
        this.genre = other.genre;
        this.year = other.year;
        this.songs = new ArrayList<>(other.songs.size());
        for (Song song : other.songs) {
            this.songs.add(new Song(song)); // DEEP COPY HERE
        }
        this.songInLibrary = new ArrayList<Song>(other.songInLibrary);
    }
    // Getter
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre.getGenre();
    }

    public int getYear() {
        return year;
    }

    public ArrayList<Song> getSongsDirect() {
        return songs;
    }


    public ArrayList<Song> getSongs() {
        ArrayList<Song> deepCopy = new ArrayList<>(songInLibrary.size());
        for (Song song : songInLibrary) {
            deepCopy.add(new Song(song)); // DEEP COPY HERE
        }
        return deepCopy; // DEEPCOPY HERE
    }


    /**
     * Get the some titles of the album
     *
     * @return a list of song names
     */
    public List<String> getSongsTitles() {
        List<String> titles = new ArrayList<>();
        for (Song s : songs) {
            titles.add(s.getTitle());
        }
        return titles;
    }
    /**
     * Returns a string representation of the album.
     * The format is: "title, by artist (year, genre)" followed by a list of song titles,
     * each indented by two spaces.
     *
     * @return the formatted album information with songs
     */
    @Override
    public String toString() {
        StringBuilder result =
                new StringBuilder(String.format("%s, by %s (%d, %s)\n", title, artist, year, genre.getGenre()));
        for (Song s : songs) {
            result.append("  ").append(s.getTitle()).append("\n");
        }
        return result.toString();
    }

    public boolean isComplete() {
        return songInLibrary.size() == songs.size();
    }

    public String getAlbumInfoString() {
        StringBuilder result =
                new StringBuilder(String.format("%s, by %s (%d, %s), ", title, artist, year, genre.getGenre()));
        if (!isComplete()) {
            result.append(" Not all songs are in the library.");
        }
        return result.toString();
    }

    public void removeSongFromAlbumLibrary(Song song) {
        songInLibrary.remove(song);
    }

    /**
     * Returns the album's basic information as an ArrayList of strings.
     * The list contains the title, artist, year (as a string), and genre.
     *
     * @return an ArrayList containing the album information
     */
    public ArrayList<String> getAlbumInfo() {
        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        result.add(artist);
        result.add(String.valueOf(year));
        result.add(genre.getGenre());
        return result;
    }
    /**
     * Returns a list representation of the album information including its songs.
     * The list starts with the album's title, artist, year, and genre,
     * followed by the title of each song in the album.
     *
     * @return an ArrayList of strings with album and song titles
     */
    public ArrayList<String> toStringList(boolean isMusicStore) {
        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        result.add(artist);
        result.add(String.valueOf(year));
        result.add(genre.getGenre());
        if (isMusicStore) {
            for (Song s : songs) {
                result.add(s.getTitle());
            }
        } else {
            for (Song s : songInLibrary) {
                result.add(s.getTitle());
            }
        }
        return result;
    }

    public void addAllSongsToLibrary() {
        for (Song song : songs) {
            boolean contains = false;
            for (Song s : songInLibrary) {
                if (s.compare(song)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                songInLibrary.add(song);
            }
        }
    }

    public void addSongToAlbumLibrary(Song song) {
        boolean contains = false;
        for (Song s : songInLibrary) {
            if (s.compare(song)) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            songInLibrary.add(song);
        }
    }

}