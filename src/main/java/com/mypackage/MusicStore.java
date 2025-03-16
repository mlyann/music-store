package la1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.net.URL;
import java.net.URISyntaxException;

public class MusicStore {
    private final Map<String, Album> albumMap;
    private final Map<String, Song> songMap;

    public MusicStore() {
        albumMap = new HashMap<>();
        songMap = new HashMap<>();
        loadAlbums("albums.txt");
    }

    /**
     * Generates a key for identifying an album or song based on the title and artist.
     * @param title  the title of the album or song
     * @param artist the artist of the album or song
     * @return a List containing the normalized title and artist
     */
    public String generateKey(String title, String artist) {
        return (title + "|" + artist).toLowerCase();
    }


    /**
     * Loads album information from the specified albums list file.
     *
     * @param albumsListFile the name of the file that contains the list of albums (e.g., "albums.txt")
     */
    public void loadAlbums(String albumsListFile) {
        // Update the path to point to the correct location
        String correctedPath = "src/main/resources/albums/" + albumsListFile;
        File file = new File(correctedPath);
        System.out.println("Loading albums from: "+file.getAbsolutePath());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String directory = file.getParent();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                String title = parts[0].trim();
                String artist = parts[1].trim();
                String albumFileName = directory + File.separator + title + "_" + artist + ".txt";

                // Parse album file with full path
                Album album = parseAlbumFile(albumFileName);
                if (album != null) {
                    albumMap.put(generateKey(title, artist), album);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading albums list file: " + correctedPath);
            e.printStackTrace();
        }
    }
    /**
     * Loads a song from a given input line and adds it to the song map.
     * @param line the input line containing the song details, separated by commas
     * @throws IllegalArgumentException if the input format is invalid
     * @author Haocheng
     */
    public void loadSong(String line) {
        String[] parts = line.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        if (parts.length != 2 && parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("Invalid input format. Expected 2, 3, or 4 " +
                    "parameters separated by commas.");
        }

        Song song;
        if (parts.length == 4) {
            String title = parts[0];
            String artist = parts[1];
            String genre = parts[2];
            int year = Integer.parseInt(parts[3]);
            song = new Song(title, artist, genre, year);
        } else if (parts.length == 2) {
            song = new Song(parts[0], parts[1]);
        } else {
            String title = parts[0];
            String artist = parts[1];
            String third = parts[2];
            if (third.matches("\\d+")) {
                int year = Integer.parseInt(third);
                song = new Song(title, artist, null, year); // genre is null
            } else {
                song = new Song(title, artist, third, 0);
            }
        }
        songMap.put(generateKey(song.getTitle(), song.getArtist()), song);
    }

    /**
     * This function loads all songs from the input file
     * fileName is the input file name
     * The input file should be in the format of "title, artist, genre, year"
     * Title and artist are required, genre and year are optional
     * If the title and artist are not provided, an exception will be thrown
     * If the year is not a number, an exception will be thrown
     * @param albumFileName
     */
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

    public Album getAlbumByTitle(String title) {
        return albumMap.get(title.toLowerCase());
    }

    public Album getAlbum(String title, String artist) {
        return albumMap.get(generateKey(title, artist));
    }

    public Collection<Album> getAllAlbums() {
        return albumMap.values();
    }

    public int getSongCount() {
        return songMap.size();
    }

    public Map<String, Song> getSongMap() {
        return new HashMap<>(songMap);
    }

    public Map<String, Album> getAlbumMap() {
        return new HashMap<>(albumMap);
    }
}