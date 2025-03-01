package la1;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private final String title;
    private final String artist;
    private final Genre genre;
    private final int year;
    private final ArrayList<Song> songs;

    public Album(String title, String artist, String genre, int year, List<Song> songs) {
        this.title = title;
        this.artist = artist;
        this.genre = Genre.fromString(genre);
        this.year = year;
        this.songs = new ArrayList<Song>(songs); // DEEPCOPY HERE
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
        return new ArrayList<Song>(songs); // DEEPCOPY HERE
    }

    public List<String> getSongsTitles() {
        List<String> titles = new ArrayList<>();
        for (Song s : songs) {
            titles.add(s.getTitle());
        }
        return titles;
    }

    @Override
    public String toString() {
        StringBuilder result =
                new StringBuilder(String.format("%s, by %s (%d, %s)\n", title, artist, year, genre.getGenre()));
        for (Song s : songs) {
            result.append("  ").append(s.getTitle()).append("\n");
        }
        return result.toString();
    }

    public String getAlbumInfoString() {
        return String.format("%s, by %s (%d, %s)\n", title, artist, year, genre.getGenre());
    }

    public ArrayList<String> getAlbumInfo() {
        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        result.add(artist);
        result.add(String.valueOf(year));
        result.add(genre.getGenre());
        return result;
    }

    public ArrayList<String> toStringList() {
        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        result.add(artist);
        result.add(String.valueOf(year));
        result.add(genre.getGenre());
        for (Song s : songs) {
            result.add(s.getTitle());
        }
        return result;
    }
}