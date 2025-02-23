package la1;

import java.util.ArrayList;
import java.util.List;
import la1.TablePrinter;
import la1.Song;

public class Playlist {
    private ArrayList<Song> songs;

    public Playlist() {
        this.songs = new ArrayList<>();
    }

    /**
     * take all the songs
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void insertSong(Song song) {
        songs.add(0, song);
    }

    public void getPlaying() {
        if (songs.isEmpty()) {
            System.out.println("The playlist is empty.");
        } else {
            System.out.println("Now playing: [" + songs.get(0).getTitle() + "]");
        }
    }

    public void playNext() {
        if (songs.isEmpty()) {
            System.out.println("The playlist is empty.");
        } else {
            System.out.println("Playing: [" + songs.get(0).getTitle() + "]");
            songs.remove(0);
        }
    }

    /**
     * add a song
     */
    public void addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
            System.out.println("Song [" + song.getTitle() + "] added to the playlist.");
        } else {
            System.out.println("Song [" + song.getTitle() + "] is already in the playlist.");
        }
    }

    /**
     * remove a song
     */
    public void removeSong(Song song) {
        if (songs.remove(song)) {
            System.out.println("Song [" + song.getTitle() + "] removed from the playlist.");
        } else {
            System.out.println("Song [" + song.getTitle() + "] not found in the playlist.");
        }
    }

    /**
     * clean up songs
     */
    public void clear() {
        songs.clear();
        System.out.println("The playlist has been cleared.");
    }

    /**
     * use table to print
     */
    public void printAsTable(String name) {
        if (songs.isEmpty()) {
            System.out.println("The playlist is empty.");
            return;
        }

        // table head
        List<String> header = new ArrayList<>();
        header.add("No.");
        header.add("Title");
        header.add("Artist");
        header.add("Genre");
        header.add("Year");

        // combine table
        List<List<String>> rows = new ArrayList<>();
        rows.add(header);

        int index = 1;
        for (Song s : songs) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(index++));
            row.add(s.getTitle());
            row.add(s.getArtist());
            row.add(s.getGenre());
            row.add(String.valueOf(s.getYear()));
            rows.add(row);
        }

        // TablePrinter prints information in a table
        TablePrinter.printDynamicTable(name, rows);
    }

    /**
     * string representation
     */
    @Override
    public String toString() {
        if (songs.isEmpty()) {
            return "The playlist is empty.";
        }
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Song song : songs) {
            sb.append(index++).append(") ").append(song.getTitle()).append("\n");
        }
        return sb.toString();
    }
}