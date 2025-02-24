package la1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TablePrinterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        outContent.reset();
    }

    /**
     * Test valid table with consistent column count
     */
    @Test
    public void testPrintDynamicTableValidData() {
        List<List<String>> rows = Arrays.asList(
                Arrays.asList("No.", "Title", "Artist"),
                Arrays.asList("1", "Song A", "Artist A"),
                Arrays.asList("2", "Song B", "Artist B"),
                Arrays.asList("3", "Song C", "Artist C")
        );

        TablePrinter.printDynamicTable("ValidDataTest", rows);
        String output = outContent.toString();

        assertTrue(output.contains("🎉 ValidDataTest 🎉"), "Should display the table title");
        assertTrue(output.contains("| 1   | Song A | Artist A |"), "Should display the full row correctly");
    }

    /**
     * Test valid marker row usage with consistent columns
     */
    @Test
    public void testPrintDynamicTableWithMarkers() {
        List<List<String>> rows = Arrays.asList(
                Arrays.asList("No.", "Title", "Artist"),
                Arrays.asList("###SEPARATOR###"),
                Arrays.asList("1", "Song A", "Artist A"),
                Arrays.asList("###SEPARATOR###"),
                Arrays.asList("2", "Song B", "Artist B")
        );

        TablePrinter.printDynamicTable("MarkerTest", rows);
        String output = outContent.toString();
        assertTrue(output.contains("🎉 MarkerTest 🎉"), "Should display the table title");
        assertTrue(output.contains("+-----+--------+----------+"), "Should display separator lines correctly");
        assertTrue(output.contains("| 2   | Song B | Artist B |"), "Should handle full rows with padding");
    }

    /**
     * Test for handling null and empty input
     */
    @Test
    public void testEmptyOrNullInput() {
        // Null input scenario
        TablePrinter.printDynamicTable("NullInputTest", null);
        String output = outContent.toString().trim();
        assertEquals("No data to display.", output, "Should handle null input gracefully");

        outContent.reset();

        // Empty list input scenario
        TablePrinter.printDynamicTable("EmptyInputTest", Collections.emptyList());
        output = outContent.toString().trim();
        assertEquals("No data to display.", output, "Should handle empty input gracefully");
    }

    @Test
    public void testPrintDynamicTableWithNullCell() {
        List<List<String>> rows = Arrays.asList(
                Arrays.asList("Header1", "Header2"),
                Arrays.asList(null, "NonNull")
        );

        TablePrinter.printDynamicTable("NullCellTest", rows);
        String output = outContent.toString();
        assertTrue(output.contains("| "), "The printed row should contain the left boundary for the null cell.");
        assertTrue(output.contains("NonNull"), "The non-null cell should be printed correctly.");

        // Ensure that there is no "null" string in the output.
        assertFalse(output.contains("null"), "The null cell should be printed as an empty string, not 'null'.");
    }

}