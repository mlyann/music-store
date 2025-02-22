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
        this.songs = new ArrayList<Song>(songs); // 深拷贝，避免外部修改
    }

    // Getter 方法
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
        return new ArrayList<Song>(songs); // 深拷贝，避免外部修改
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
}