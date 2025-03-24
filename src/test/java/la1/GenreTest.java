package la1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenreTest {

    @Test
    public void testNullInput() {
        // Null input should return UNKNOWN
        assertEquals(Genre.UNKNOWN, Genre.fromString(null));
    }

    @Test
    public void testEmptyAndWhitespaceInput() {
        // Empty or whitespace-only strings should return UNKNOWN
        assertEquals(Genre.UNKNOWN, Genre.fromString(""));
        assertEquals(Genre.UNKNOWN, Genre.fromString("   "));
    }

    @Test
    public void testExactValidGenres() {
        // Test each valid genre (using the exact string values)
        assertEquals(Genre.POP, Genre.fromString("Pop"));
        assertEquals(Genre.ROCK, Genre.fromString("Rock"));
        assertEquals(Genre.JAZZ, Genre.fromString("Jazz"));
        assertEquals(Genre.HIPHOP, Genre.fromString("HipHop"));
        assertEquals(Genre.CLASSICAL, Genre.fromString("Classical"));
        assertEquals(Genre.COUNTRY, Genre.fromString("Traditional Country"));
        assertEquals(Genre.ELECTRONIC, Genre.fromString("Electronic"));
        assertEquals(Genre.FOLK, Genre.fromString("Folk"));
        assertEquals(Genre.BLUES, Genre.fromString("Blues"));
        assertEquals(Genre.LATIN, Genre.fromString("Latin"));
        assertEquals(Genre.REGGAE, Genre.fromString("Reggae"));
        assertEquals(Genre.METAL, Genre.fromString("Metal"));
        assertEquals(Genre.PUNK, Genre.fromString("Punk"));
        assertEquals(Genre.RAP, Genre.fromString("Rap"));
        assertEquals(Genre.RNB, Genre.fromString("RnB"));
        assertEquals(Genre.SOUL, Genre.fromString("Soul"));
        assertEquals(Genre.INDIE, Genre.fromString("Indie"));
        assertEquals(Genre.DANCE, Genre.fromString("Dance"));
        assertEquals(Genre.FUNK, Genre.fromString("Funk"));
        assertEquals(Genre.TECHNO, Genre.fromString("Techno"));
        assertEquals(Genre.DISCO, Genre.fromString("Disco"));
        assertEquals(Genre.SKA, Genre.fromString("Ska"));
        assertEquals(Genre.SWING, Genre.fromString("Swing"));
        assertEquals(Genre.GRUNGE, Genre.fromString("Grunge"));
        assertEquals(Genre.DUBSTEP, Genre.fromString("Dubstep"));
        assertEquals(Genre.HOUSE, Genre.fromString("House"));
        assertEquals(Genre.TRANCE, Genre.fromString("Trance"));
        assertEquals(Genre.DRUM_AND_BASS, Genre.fromString("DrumAndBass"));
        assertEquals(Genre.GARAGE, Genre.fromString("Garage"));
        assertEquals(Genre.AMBIENT, Genre.fromString("Ambient"));
        assertEquals(Genre.DUB, Genre.fromString("Dub"));
        assertEquals(Genre.REGGAETON, Genre.fromString("Reggaeton"));
        assertEquals(Genre.PSYCHEDELIC, Genre.fromString("Psychedelic"));
        assertEquals(Genre.ELECTRO, Genre.fromString("Electro"));
        assertEquals(Genre.ACOUSTIC, Genre.fromString("Acoustic"));
        assertEquals(Genre.SINGER, Genre.fromString("Singer/Songwriter"));
        assertEquals(Genre.OTHER, Genre.fromString("Other"));
    }

    @Test
    public void testCaseInsensitiveMatching() {
        // Test that matching is case-insensitive
        assertEquals(Genre.POP, Genre.fromString("pop"));
        assertEquals(Genre.ROCK, Genre.fromString("rOcK"));
        assertEquals(Genre.JAZZ, Genre.fromString("JAZZ"));
        assertEquals(Genre.HIPHOP, Genre.fromString("hiphop"));
        assertEquals(Genre.CLASSICAL, Genre.fromString("classical"));
        assertEquals(Genre.COUNTRY, Genre.fromString("COUNTRY"));
    }

    @Test
    public void testLeadingAndTrailingWhitespace() {
        // Extra spaces before/after valid genre names should still match
        assertEquals(Genre.POP, Genre.fromString("  Pop "));
        assertEquals(Genre.ROCK, Genre.fromString(" Rock  "));
        assertEquals(Genre.JAZZ, Genre.fromString("   Jazz"));
    }

    @Test
    public void testInvalidGenresReturnOther() {
        assertEquals(Genre.OTHER, Genre.fromString("Hip Hop"));
        assertEquals(Genre.OTHER, Genre.fromString("Classics"));
        assertEquals(Genre.OTHER, Genre.fromString("Rock'n'Roll"));
        assertEquals(Genre.OTHER, Genre.fromString("Electronica"));
        assertEquals(Genre.OTHER, Genre.fromString("  unknown genre "));
    }
}