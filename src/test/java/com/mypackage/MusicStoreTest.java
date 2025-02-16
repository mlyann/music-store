package com.mypackage;

import la1.Album;
import la1.MusicStore;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MusicStoreTest {

    static class TestMusicStore extends MusicStore {
        @Override
        public void loadAlbums(String albumsListFile) {
            super.loadAlbums(albumsListFile);
        }
    }

    private TestMusicStore store;

    @BeforeEach
    void setUp() {
        store = new TestMusicStore() {
        };
    }

    /**
     * Test a valid "albums.txt" file referencing two valid album files.
     */
    @Test
    void testLoadAlbums_Valid() throws IOException {
        // 1. Create a temporary directory to hold test files
        Path tempDir = Files.createTempDirectory("musicStoreTest");

        // 2. Create the "albums list" file with two album entries
        Path albumsListFile = tempDir.resolve("albums.txt");
        List<String> albumListLines = List.of(
                "Dark Side of the Moon, Pink Floyd",
                "Abbey Road, The Beatles"
        );
        Files.write(albumsListFile, albumListLines);

        // 3. Create the individual album files that parseAlbumFile(...) will read
        Path dsotmFile = tempDir.resolve("Dark Side of the Moon_Pink Floyd.txt");
        List<String> dsotmLines = List.of(
                "Dark Side of the Moon, Pink Floyd, Rock, 1973",
                "Speak to Me",
                "Breathe",
                "Time"
        );
        Files.write(dsotmFile, dsotmLines);

        Path abbeyRoadFile = tempDir.resolve("Abbey Road_The Beatles.txt");
        List<String> abbeyRoadLines = List.of(
                "Abbey Road, The Beatles, Rock, 1969",
                "Come Together",
                "Something"
        );
        Files.write(abbeyRoadFile, abbeyRoadLines);

        // 4. Load albums using our test "albums.txt" file
        store.loadAlbums(albumsListFile.toString());

        // 5. Verify that both albums got loaded
        Map<String, Album> map = store.getAlbumMap();
        assertEquals(2, map.size(), "Should have 2 albums loaded");

        // 6. Check details of the first album
        Album dsotm = store.getAlbum("Dark Side of the Moon", "Pink Floyd");
        assertNotNull(dsotm, "Dark Side of the Moon album should exist");
        assertEquals("Dark Side of the Moon", dsotm.getTitle());
        assertEquals("Pink Floyd", dsotm.getArtist());
        assertEquals("Rock", dsotm.getGenre());
        assertEquals(1973, dsotm.getYear());
        assertEquals(List.of("Speak to Me", "Breathe", "Time"), dsotm.getSongs());

        // 7. Check details of the second album
        Album abbeyRoad = store.getAlbum("Abbey Road", "The Beatles");
        assertNotNull(abbeyRoad, "Abbey Road album should exist");
        assertEquals("Abbey Road", abbeyRoad.getTitle());
        assertEquals("The Beatles", abbeyRoad.getArtist());
        assertEquals("Rock", abbeyRoad.getGenre());
        assertEquals(1969, abbeyRoad.getYear());
        assertEquals(List.of("Come Together", "Something"), abbeyRoad.getSongs());
    }

    /**
     * Test that lines with fewer than 2 parts in the "albums list" file are skipped.
     */
    @Test
    void testLoadAlbums_InvalidLine() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");

        // Write one valid line and one invalid line
        Files.write(albumsListFile, List.of(
                "Dark Side of the Moon, Pink Floyd",
                "InvalidLineWithoutComma"
        ));

        // Create the album file for the valid entry
        Path dsotmFile = tempDir.resolve("Dark Side of the Moon_Pink Floyd.txt");
        Files.write(dsotmFile, List.of(
                "Dark Side of the Moon, Pink Floyd, Rock, 1973",
                "Speak to Me"
        ));

        store.loadAlbums(albumsListFile.toString());

        // Only the valid line should load
        assertEquals(1, store.getAlbumMap().size(), "Only one valid album entry should be loaded");
        assertNotNull(store.getAlbum("Dark Side of the Moon", "Pink Floyd"));
    }

    /**
     * Test behavior when the main "albums list" file does not exist.
     * Should not crash, and albumMap should remain empty.
     */
    @Test
    void testLoadAlbums_FileNotFound() {
        // Attempt to load a non-existent file
        store.loadAlbums("non_existent_albums.txt");
        assertTrue(store.getAlbumMap().isEmpty(), "Map should be empty if file is not found");
    }

    /**
     * Test an album file with an invalid header format (less than 4 parts).
     */
    @Test
    void testLoadAlbums_InvalidAlbumHeader() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");
        // Only one album entry
        Files.write(albumsListFile, List.of("MyAlbum, MyArtist"));

        // Create the album file with an invalid header (only 3 parts)
        Path albumFile = tempDir.resolve("MyAlbum_MyArtist.txt");
        Files.write(albumFile, List.of(
                "MyAlbum, MyArtist, MissingYear"
        ));

        store.loadAlbums(albumsListFile.toString());

        // The invalid album file should not be loaded
        assertTrue(store.getAlbumMap().isEmpty(), "Album should not be loaded due to invalid header");
    }

    /**
     * Test generateKey(...) directly.
     */
    @Test
    void testGenerateKey() {
        String key = store.generateKey("  The Wall  ", "  PINK FLOYD ");
        assertEquals("the wall|pink floyd", key, "generateKey should trim and lowercase both parts");
    }

    /**
     * Test that when a valid album file is loaded,
     * the album map, collection, and album fields are not empty.
     */
    @Test
    void testLoadAlbums_ResultsNotEmpty() throws IOException {
        // Create a temporary directory for test files
        Path tempDir = Files.createTempDirectory("musicStoreTest");

        // Create a valid "albums list" file with one entry
        Path albumsListFile = tempDir.resolve("albums.txt");
        Files.write(albumsListFile, List.of("Test Album, Test Artist"));

        // Create the album file: "Test Album_Test Artist.txt"
        Path albumFile = tempDir.resolve("Test Album_Test Artist.txt");
        Files.write(albumFile, List.of(
                "Test Album, Test Artist, Pop, 2020",
                "Song 1",
                "Song 2"
        ));

        // Load the albums
        store.loadAlbums(albumsListFile.toString());

        // Verify that the album map is not empty
        Map<String, Album> albumMap = store.getAlbumMap();
        assertFalse(albumMap.isEmpty(), "Album map should not be empty");

        // Verify that getAllAlbums() returns a non-empty collection
        Collection<Album> allAlbums = store.getAllAlbums();
        assertFalse(allAlbums.isEmpty(), "getAllAlbums() should not be empty");

        // Verify that the album fields are not empty
        Album album = store.getAlbum("Test Album", "Test Artist");
        assertNotNull(album, "Album should exist");
        assertFalse(album.getTitle().isEmpty(), "Album title should not be empty");
        assertFalse(album.getArtist().isEmpty(), "Album artist should not be empty");
        assertFalse(album.getGenre().isEmpty(), "Album genre should not be empty");
        assertTrue(album.getYear() > 0, "Album year should be greater than 0");
        assertFalse(album.getSongs().isEmpty(), "Album songs list should not be empty");
    }

    /**
     * Test that constructor initializes empty map
     */
    @Test
    void testConstructor() {
        store = new TestMusicStore();
        assertNotNull(store.getAlbumMap());
        assertTrue(store.getAlbumMap().isEmpty());
    }

    /**
     * Tests that loadAlbums handles an empty albums list file correctly.
     */
    @Test
    void testLoadAlbums_EmptyFile() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");

        // Create empty file
        Files.write(albumsListFile, List.of(""));

        store.loadAlbums(albumsListFile.toString());
        assertTrue(store.getAlbumMap().isEmpty());
    }

    /**
     * Tests that parseAlbumFile correctly handles an empty album file.
     */
    @Test
    void testLoadAlbums_InvalidYear() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");
        Files.write(albumsListFile, List.of("Test Album, Test Artist"));

        Path albumFile = tempDir.resolve("Test Album_Test Artist.txt");
        Files.write(albumFile, List.of(
                "Test Album, Test Artist, Rock, NotAYear",
                "Song 1"
        ));

        store.loadAlbums(albumsListFile.toString());
        assertTrue(store.getAlbumMap().isEmpty());
    }

    /**
     * Tests handling of an album file with only an empty line.
     */
    @Test
    void testLoadAlbums_EmptyAlbumFile() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");
        Files.write(albumsListFile, List.of("Test Album, Test Artist"));

        Path albumFile = tempDir.resolve("Test Album_Test Artist.txt");
        Files.write(albumFile, List.of(""));

        store.loadAlbums(albumsListFile.toString());
        assertTrue(store.getAlbumMap().isEmpty());
    }

    /**
     * Tests that getAllAlbums returns the correct collection of albums.
     */
    @Test
    void testGetAllAlbums() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");
        Files.write(albumsListFile, List.of("Test Album, Test Artist"));

        Path albumFile = tempDir.resolve("Test Album_Test Artist.txt");
        Files.write(albumFile, List.of(
                "Test Album, Test Artist, Rock, 2020",
                "Song 1"
        ));

        store.loadAlbums(albumsListFile.toString());
        Collection<Album> albums = store.getAllAlbums();

        assertEquals(1, albums.size());
        Album album = albums.iterator().next();
        assertEquals("Test Album", album.getTitle());
    }

    /**
     * Tests that an album file with an invalid year does not get loaded.
     */
    @Test
    void testParseAlbumFile_IOError() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");

        // Create albums.txt with a reference to a non-existent album file
        Files.write(albumsListFile, List.of("NonExistent, Artist"));

        // Try to load albums - parseAlbumFile will fail with IOException when trying to read non-existent file
        store.loadAlbums(albumsListFile.toString());
        assertTrue(store.getAlbumMap().isEmpty());
    }

    /**
     * Tests if parseAlbumFile correctly handles a completely empty album file.
     */
    @Test
    void testParseAlbumFile_CompletelyEmpty() throws IOException {
        Path tempDir = Files.createTempDirectory("musicStoreTest");
        Path albumsListFile = tempDir.resolve("albums.txt");
        Files.write(albumsListFile, List.of("Test Album, Test Artist"));

        // Create a completely empty album file (not even a newline)
        Path albumFile = tempDir.resolve("Test Album_Test Artist.txt");
        Files.writeString(albumFile, "");

        store.loadAlbums(albumsListFile.toString());
        assertTrue(store.getAlbumMap().isEmpty(), "Album map should be empty for empty file");
    }
}
