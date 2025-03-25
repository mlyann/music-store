package la1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    private final ArrayList<Song> songs;
    private final String name;

    public Playlist(String name) {
        this.songs = new ArrayList<>();
        this.name = name;
    }

    /**
     * take all the songs
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    public int getSize() {
        return songs.size();
    }

    public String getName() {
        return name;
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
    public void addSong(Song song, boolean notAuto) {
        if (!songs.contains(song)) {
            songs.add(song);
            if (notAuto) { System.out.println("Song [" + song.getTitle() + "] added to the playlist." );}
        } else {
            if (notAuto) { System.out.println("Song [" + song.getTitle() + "] is already in the playlist.");}
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
    public void clear(boolean notAuto) {
        songs.clear();
        if (notAuto) {System.out.println("The playlist has been cleared.");}
    }

    /**
     * use table to print
     */
    public void printAsTable(String key) {
        if (key.toLowerCase().equals("title")){sortSongsByTitle();}
        else if (key.toLowerCase().equals("rating")){sortSongsByRating();}
        else if (key.toLowerCase().equals("artist")){sortSongsByArtist();}
        else if (key.toLowerCase().equals("shuffle")){shuffle();}
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
            row.add(info.get(0));
            row.add(info.get(1));
            row.add(info.get(2));
            row.add(info.get(3));
            row.add(info.get(4));
            row.add(info.get(5));
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

//    Tutorials for how to customize sorting algorithm. @Author: Ming
//    https://stackoverflow.com/questions/16425127/how-to-use-collections-sort-in-java
    public void sortSongsByTitle() {
        Collections.sort(songs, (s1, s2) -> {
            int cmp = s1.getTitle().compareToIgnoreCase(s2.getTitle());
            if (cmp != 0) {
                return cmp;
            }
            cmp = s1.getArtist().compareToIgnoreCase(s2.getArtist());
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(s1.getRatingInt(), s2.getRatingInt());
        });
    }

    // rating sorting
    public void sortSongsByRating() {
        Collections.sort(songs, (s1, s2) -> {
            int cmp = Integer.compare(s2.getRatingInt(), s1.getRatingInt()); // 降序
            if (cmp != 0) {
                return cmp;
            }
            cmp = s1.getTitle().compareToIgnoreCase(s2.getTitle());
            if (cmp != 0) {
                return cmp;
            }
            return s1.getArtist().compareToIgnoreCase(s2.getArtist());
        });
    }

    // year sorting
    public void sortSongsByArtist() {
        Collections.sort(songs, (s1, s2) -> {
            int cmp = s1.getArtist().compareToIgnoreCase(s2.getArtist());
            if (cmp != 0) {
                return cmp;
            }
            cmp = s1.getTitle().compareToIgnoreCase(s2.getTitle());
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(s1.getRatingInt(), s2.getRatingInt());
        });
    }
}