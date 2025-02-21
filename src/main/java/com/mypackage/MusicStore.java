package la1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class MusicStore {
    // albumMap stores all albums in the store, each item is a class Album
    private final Map<String, Album> albumMap;

    // songMap stores all songs in the store, each item is a class song
    private final Map<String, Song> songMap;

    public MusicStore() {
        albumMap = new HashMap<>();
        songMap = new HashMap<>();
        loadAlbums("albums.txt");
    }

    public String generateKey(String title, String artist) {
        return (title.trim().toLowerCase() + "|" + artist.trim().toLowerCase());
    }

    // This function loads all albums from the albumsListFile
    public void loadAlbums(String albumsListFile) {
        try (BufferedReader br = new BufferedReader(
                new FileReader(new File(albumsListFile).getAbsolutePath()))) {
            String line;
            String directory = new File(albumsListFile).getParent();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    System.err.println("Invalid album entry: " + line);
                    continue;
                }
                String title = parts[0].trim();
                String artist = parts[1].trim();
                String albumFileName = directory + File.separator +
                                     title + "_" + artist + ".txt";

                // Parse album file with full path
                Album album = parseAlbumFile(albumFileName);
                if (album != null) {
                    albumMap.put(generateKey(title, artist), album);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading albums list file: " + albumsListFile);
        }
    }

    /**
     * This function loads all songs from the input file
     * @param line is the input line
     * The input line should be in the format of "title, artist, genre, year"
     * Title and artist are required, genre and year are optional
     * If the title and artist are not provided, an exception will be thrown
     * @author Haocheng
     */
    public void loadSong(String line) {
        String[] parts = line.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        // if the input format is invalid, throw an exception
        if (parts.length != 2 && parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("Invalid input format. Expected 2, 3, or 4 " +
                    "parameters separated by commas.");
        }

        Song song;
        if (parts.length == 4) {
            // input format: title, artist, genre, year
            String title = parts[0];
            String artist = parts[1];
            String genre = parts[2];
            int year = Integer.parseInt(parts[3]); // if year not number, throw NumberFormatException
            song = new Song(title, artist, genre, year);
        } else if (parts.length == 2) {
            // input format: title, artist
            song = new Song(parts[0], parts[1]);
        } else { // parts.length == 3
            // input format: title, artist, third
            // figure if third is genre(String) or year(pure number)
            String title = parts[0];
            String artist = parts[1];
            String third = parts[2];
            // if third is a number
            if (third.matches("\\d+")) {
                int year = Integer.parseInt(third);
                song = new Song(title, artist, null, year;
            } else {
                // if third is a string
                song = new Song(title, artist, third, 0);
            }
        }

        // add the song to the songMap
        songMap.put(generateKey(song.getTitle(), song.getArtist()), song);
    }


    public Album parseAlbumFile(String albumFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(albumFileName))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("Empty album file: " + albumFileName);
                return null;
            }

            String[] headerParts = headerLine.split(",");
            if (headerParts.length < 4) {
                System.err.println("Invalid header format in album file: " + albumFileName);
                return null;
            }

            String title = headerParts[0].trim();
            String artist = headerParts[1].trim();
            String genre = headerParts[2].trim();
            int year;
            try {
                year = Integer.parseInt(headerParts[3].trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid year in album file: " + albumFileName);
                return null;
            }

            // I upload the type of song from String to class Song for better implementation
            // Also I add the songMap to store all songs in the store
            // Haocheng
            List<Song> songs = new ArrayList<>();
            String songLine;
            while ((songLine = br.readLine()) != null) {
                if (!songLine.trim().isEmpty()) {
                    Song song = new Song(songLine.trim(), artist, genre, year);
                    songs.add(song);
                    songMap.put(generateKey(songLine.trim(), artist), song);
                }
            }
            Album album = new Album(title, artist, genre, year, songs);
            for (Song song : album.getSongsDirect()) {
                song.setAlbum(album);
            }
            return album;
        } catch (IOException e) {
            System.err.println("Error reading album file: " + albumFileName);
            return null;
        }
    }

    public Album getAlbum(String title, String artist) {
        return albumMap.get(generateKey(title, artist));
    }

    public Collection<Album> getAllAlbums() {
        return albumMap.values();
    }

    public Map<String, Song> getSongMap() {
        return new HashMap<>(songMap);
    }

    public Map<String, Album> getAlbumMap() {
        return new HashMap<>(albumMap);
    }


}