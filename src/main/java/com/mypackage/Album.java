package la1;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private final String title;
    private final String artist;
    private final String genre;
    private final int year;
    private final List<String> songs;

    public Album(String title, String artist, String genre, int year, List<String> songs) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.year = year;
        this.songs = new ArrayList<>(songs); // 深拷贝，避免外部修改
    }

    // Getter 方法
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public List<String> getSongs() {
        return new ArrayList<>(songs);
    }

    @Override
    public String toString() {
        return String.format("%s by %s (%d, %s)", title, artist, year, genre);
    }
}