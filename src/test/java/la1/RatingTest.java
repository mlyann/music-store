package la1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RatingTest {

    @Test
    public void testGetValue() {
        assertEquals(0, Rating.UNRATED.getValue());
        assertEquals(1, Rating.ONE.getValue());
        assertEquals(2, Rating.TWO.getValue());
        assertEquals(3, Rating.THREE.getValue());
        assertEquals(4, Rating.FOUR.getValue());
        assertEquals(5, Rating.FIVE.getValue());
    }

    @Test
    public void testFromIntValid() {
        assertEquals(Rating.UNRATED, Rating.fromInt(0));
        assertEquals(Rating.ONE, Rating.fromInt(1));
        assertEquals(Rating.TWO, Rating.fromInt(2));
        assertEquals(Rating.THREE, Rating.fromInt(3));
        assertEquals(Rating.FOUR, Rating.fromInt(4));
        assertEquals(Rating.FIVE, Rating.fromInt(5));
    }

    @Test
    public void testFromIntInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Rating.fromInt(-1));
        assertTrue(exception.getMessage().contains("Rating must be between 0 and 5"));

        exception = assertThrows(IllegalArgumentException.class, () -> Rating.fromInt(6));
        assertTrue(exception.getMessage().contains("Rating must be between 0 and 5"));
    }

    @Test
    public void testToStringRepresentation() {
        assertEquals("UNRATED", Rating.UNRATED.toString());
        assertEquals("★☆☆☆☆", Rating.ONE.toString());
        assertEquals("★★☆☆☆", Rating.TWO.toString());
        assertEquals("★★★☆☆", Rating.THREE.toString());
        assertEquals("★★★★☆", Rating.FOUR.toString());
        assertEquals("★★★★★", Rating.FIVE.toString());
    }
}