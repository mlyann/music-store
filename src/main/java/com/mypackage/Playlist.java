package la1;

import java.util.ArrayList;
import java.util.Collections;
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
        if (songs.isEmpty() || songs.size() == 1) {
            System.out.println("No song to play next.");
        } else {
            songs.remove(0);
            System.out.println("Playing: [" + songs.get(0).getTitle() + "]");
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
        ArrayList<ArrayList<String>> playString = new ArrayList<>();
        for (Song s : songs) {
            playString.add(s.toStringList());
        }

        // table head
        List<String> header = new ArrayList<>();
        header.add("No.");
        header.add("Title");
        header.add("Artist");
        header.add("Genre");
        header.add("Year");
        header.add("Favorite");
        header.add("Rating  ");


        boolean anyAlbum = false;
        for (List<String> row : playString) {
            if (row.size() > 6 && row.get(6) != null) {
                anyAlbum = true;
                break;
            }
        }
        if (anyAlbum) {
            header.add("THE ALBUM");
        }

        // Combine into a 2D structure for TablePrinter.
        List<List<String>> tableRows = new ArrayList<>();
        tableRows.add(header);

        int index = 1;
        for (Song s : songs) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(index++));
            ArrayList<String> info = s.toStringList();
            row.add(info.get(0)); // Title
            row.add(info.get(1)); // Artist
            row.add(info.get(2)); // Genre
            row.add(info.get(3)); // Year
            row.add(info.get(4)); // Favorite
            row.add(info.get(5)); // Rating
            if (anyAlbum) {
                String album = (info.size() > 6) ? info.get(6) : "NO ALBUM";
                row.add(album == null ? "NO ALBUM" : album);
            }
            tableRows.add(row);
        }

        // TablePrinter prints information in a table
        TablePrinter.printDynamicTable(name, tableRows);
    }

    public void shuffle() {
        if (songs.isEmpty()) {
            System.out.println("The playlist is empty.");
            return;
        }
        System.out.println("Shuffling the playlist...");
        Collections.shuffle(songs);
        System.out.println("The playlist has been shuffled.");
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