package la1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MusicStore {
    private final Map<String, Album> albumMap;

    public MusicStore() {
        albumMap = new HashMap<>();
        loadAlbums("albums.txt");
    }

    private String generateKey(String title, String artist) {
        return (title.trim().toLowerCase() + "|" + artist.trim().toLowerCase());
    }

    private void loadAlbums(String albumsListFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(albumsListFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    System.err.println("Invalid album entry: " + line);
                    continue;
                }
                String title = parts[0].trim();
                String artist = parts[1].trim();
                String albumFileName = title + "_" + artist + ".txt";

                // 解析专辑文件
                Album album = parseAlbumFile(albumFileName);
                if (album != null) {
                    albumMap.put(generateKey(title, artist), album);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading albums list file: " + albumsListFile);
        }
    }

    private Album parseAlbumFile(String albumFileName) {
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

            List<String> songs = new ArrayList<>();
            String songLine;
            while ((songLine = br.readLine()) != null) {
                if (!songLine.trim().isEmpty()) {
                    songs.add(songLine.trim());
                }
            }

            return new Album(title, artist, genre, year, songs);
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
}