package la1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FavoriteListTest {
    @Test
    public void testAddSongIncreasesSize() {
        FavoriteList favList = new FavoriteList();
        Song song = new Song("Title1", "Artist1", "Pop", 2020);
        assertEquals(0, favList.getSize());
        favList.addSong(song,false);
        assertEquals(1, favList.getSize());
    }
    @Test
    public void testRemoveSongDecreasesSize() {
        FavoriteList favList = new FavoriteList();
        Song song = new Song("Title1", "Artist1", "Pop", 2020);
        favList.addSong(song,false);
        assertEquals(1, favList.getSize());
        favList.removeSong(song);
        assertEquals(0, favList.getSize());
    }
    @Test
    public void testToStringEmptyList() {
        FavoriteList favList = new FavoriteList();
        String output = favList.toString();
        assertEquals("The favorite list is empty.", output);
    }
    @Test
    public void testToStringWithSongs() {
        FavoriteList favList = new FavoriteList();
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        favList.addSong(song1,false);
        favList.addSong(song2,false);

        String output = favList.toString();
        String expectedPattern = "Favorite Songs:\\s*1\\) Title1\\s*2\\) Title2\\s*";
        assertTrue(Pattern.compile(expectedPattern).matcher(output).find());
    }
    @Test
    public void testPrintAsTableEmpty() {
        FavoriteList favList = new FavoriteList();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));
        favList.printAsTable("title");
        System.out.flush();
        System.setOut(originalOut);
        String printed = baos.toString();
        assertTrue(printed.contains("The playlist is empty."));
    }

    @Test
    public void testPrintAsTableWithoutAlbum() {
        FavoriteList favList = new FavoriteList();
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        favList.addSong(song1,false);
        favList.addSong(song2,false);
        song1.setAlbum(null);
        song2.setAlbum(null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));
        favList.printAsTable("title");
        System.out.flush();
        System.setOut(originalOut);

        String printed = baos.toString();
        assertTrue(printed.contains("No."));
        assertTrue(printed.contains("Title"));
        assertTrue(printed.contains("Artist"));
        assertTrue(printed.contains("Genre"));
        assertTrue(printed.contains("Year"));
        assertTrue(printed.contains("Favorite"));
        assertTrue(printed.contains("Rating"));
        assertFalse(printed.contains("ALBUM"));
    }
    @Test
    public void testPrintAsTableWithAlbum() {
        FavoriteList favList = new FavoriteList();
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        song2.setAlbum(new DummyAlbum("BestOf"));
        favList.addSong(song1,false);
        favList.addSong(song2,false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        favList.printAsTable("title");
        System.out.flush();
        System.setOut(originalOut);
        String printed = baos.toString();
        assertTrue(printed.contains("Album"));
        assertTrue(printed.contains("BestOf"));
    }

    @Test
    public void testMultipleOperations() {
        FavoriteList favList = new FavoriteList();
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        Song song3 = new Song("Title3", "Artist3", "Jazz", 2021);
        song1.setAlbum(new DummyAlbum("Album1"));
        song3.setAlbum(new DummyAlbum("Album3"));

        favList.addSong(song1,false);
        favList.addSong(song2,false);
        favList.addSong(song3,false);
        assertEquals(3, favList.getSize());
        favList.removeSong(song2);
        assertEquals(2, favList.getSize());

        String output = favList.toString();
        assertTrue(output.contains("Title1"));
        assertTrue(output.contains("Title3"));
        assertFalse(output.contains("Title2"));
    }

    @Test
    public void testAlbumColumnExtraction() {
        FavoriteList favList = new FavoriteList();
        DummySongShort songShort = new DummySongShort("ShortSong", "ArtistX");
        DummySongLong songLong = new DummySongLong("LongSong", "ArtistY", "DummyAlbum");
        favList.addSong(songShort,false);
        favList.addSong(songLong,false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));
        favList.printAsTable("title");
        System.out.flush();
        System.setOut(originalOut);

        String printed = baos.toString();
        assertTrue(printed.contains("Album"));
        assertTrue(printed.contains("DummyAlbum"));
    }

    private static class DummyAlbum extends Album {
        public DummyAlbum(String title) {
            super(title, "DummyArtist", "Unknown", 0, new ArrayList<Song>());
        }

        @Override
        public String getTitle() {
            return super.getTitle();
        }
    }
    private static class DummySongShort extends Song {
        public DummySongShort(String title, String artist) {
            super(title, artist, "Genre", 2000);
        }

        @Override
        public ArrayList<String> toStringList() {
            ArrayList<String> list = new ArrayList<>();
            list.add(getTitle());
            list.add(getArtist());
            list.add(getGenre());
            list.add(String.valueOf(getYear()));
            list.add(getFavourite());
            list.add(getRating());
            return list;
        }
    }
    private static class DummySongLong extends Song {
        public DummySongLong(String title, String artist, String albumTitle) {
            super(title, artist, "Genre", 2000);
            setAlbum(new DummyAlbum(albumTitle));
        }
        @Override
        public ArrayList<String> toStringList() {
            ArrayList<String> list = new ArrayList<>();
            list.add(getTitle());
            list.add(getArtist());
            list.add(getGenre());
            list.add(String.valueOf(getYear()));
            list.add(getFavourite());
            list.add(getRating());
            if (getAlbum() != null) {
                list.add(getAlbum().getTitle());
            } else {
                list.add(null);
            }
            return list;
        }
    }
}