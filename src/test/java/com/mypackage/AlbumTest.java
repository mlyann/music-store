package la1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AlbumTest {
    private Song createSong(String title) {
        return new Song(title, "A");
    }

    @Test
    public void testConstructorCopy() {
        List<Song> songs = new ArrayList<>();
        songs.add(createSong("A"));
        songs.add(createSong("B"));
        Album album = new Album("1", "AA", "Rock", 2021, songs);
        songs.clear();
        assertEquals(2, album.getSongsDirect().size());
    }

    @Test
    public void testGetSongsCopy() {
        List<Song> songs = new ArrayList<>();
        songs.add(createSong("A"));
        Album album = new Album("1", "AA", "Rock", 2021, songs);

        ArrayList<Song> copy = album.getSongs();
        copy.add(createSong("B"));
        assertEquals(1, album.getSongsDirect().size());
    }

    @Test
    public void testGetSongsTitles() {
        List<Song> songs = new ArrayList<>();
        songs.add(createSong("A"));
        songs.add(createSong("B"));
        Album album = new Album("1", "AA", "Pop", 2019, songs);

        assertEquals(Arrays.asList("A", "B"), album.getSongsTitles());
    }

    @Test
    public void testToStringEmpty() {
        List<Song> empty = new ArrayList<>();
        Album album = new Album("1", "AA", "Jazz", 2000, empty);
        String expected = String.format("%s, by %s (%d, %s)\n", "1", "AA", 2000, "Jazz");
        assertEquals(expected, album.toString());
    }

    @Test
    public void testToStringWithSongs() {
        List<Song> songs = new ArrayList<>();
        songs.add(createSong("1"));
        songs.add(createSong("2"));
        Album album = new Album("1", "AA", "Blues", 1995, songs);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s, by %s (%d, %s)\n", "1", "AA", 1995, "Blues"));
        for (Song s : songs) {
            sb.append("  ").append(s.getTitle()).append("\n");
        }
        assertEquals(sb.toString(), album.toString());
    }

    @Test
    public void testGetInfoString() {
        List<Song> songs = new ArrayList<>();
        Album album = new Album("1", "AA", "Country", 1988, songs);
        String expected = String.format("%s, by %s (%d, %s)\n", "1", "AA", 1988, "Country");
        assertEquals(expected, album.getAlbumInfoString());
    }

    @Test
    public void testGetInfo() {
        List<Song> songs = new ArrayList<>();
        Album album = new Album("1", "AA", "HipHop", 2010, songs);
        ArrayList<String> expected = new ArrayList<>(Arrays.asList("1", "AA", "2010", "HipHop"));
        assertEquals(expected, album.getAlbumInfo());
    }

    @Test
    public void testGetGenre() {
        Album album = new Album("1", "AA", "HipHop", 2010, new ArrayList<>());
        assertEquals("HipHop", album.getGenre());
    }

    @Test
    public void testGetYear() {
        Album album = new Album("1", "AA", "HipHop", 2010, new ArrayList<>());
        assertEquals(2010, album.getYear());
    }

    @Test
    public void testToStringList() {
        List<Song> songs = new ArrayList<>();
        songs.add(createSong("1"));
        songs.add(createSong("2"));
        Album album = new Album("1", "AA", "Electronic", 2022, songs);
        ArrayList<String> expected = new ArrayList<>(Arrays.asList("1", "AA", "2022", "Electronic", "1", "2"));
        assertEquals(expected, album.toStringList());
    }
}
