package la1;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class VICOperationsTest {

    @Test
    @DisplayName("Test noCarryAddition")
    void testNoCarryAddition() {
        // 例如 123 + 890 => 非进位加法 => 9(1+8) 9(2+9%10) 3(3+0%10) => 9 1 3? Let's see
        String result = VICOperations.noCarryAddition("123", "890");
        assertEquals("913", result);

        // 测试不同长度
        assertEquals("135", VICOperations.noCarryAddition("12", "123"));
        assertEquals("3", VICOperations.noCarryAddition("9", "4"));
        assertEquals("13", VICOperations.noCarryAddition("19", "4"));
        // Leading zeros strip
        assertEquals("4", VICOperations.noCarryAddition("001", "003"));
    }

    @Test
    @DisplayName("Test chainAddition")
    void testChainAddition() {
        // 测试扩展长度
        // for example: input=123, digit=5 => result=?
        // step example (from your logic)
        String result = VICOperations.chainAddition("123", 5);
        // manual check: "123" => length=3 => let's see final result
        // The logic can be tricky, we only do an assertion for correctness
        assertNotNull(result);
        assertEquals(5, result.length());

        // If num already >= digit
        String shortResult = VICOperations.chainAddition("123456", 3);
        // 取前3位
        assertEquals("123", shortResult);

        // Another typical test
        String r2 = VICOperations.chainAddition("9", 3);
        // 9 => pad 0 => 09 => chain => ...
        // check length
        assertEquals(3, r2.length());
    }

    @Test
    @DisplayName("Test digitPermutation")
    void testDigitPermutation() {
        // At least 10 characters
        String input = "abcdefghii"; // 10 letters
        String perm = VICOperations.digitPermutation(input);
        // Should be 10 digits (0-9 in some order)
        assertNotNull(perm);
        assertEquals(10, perm.length());
        // each character must be a digit
        for (char c : perm.toCharArray()) {
            assertTrue(Character.isDigit(c), "Permutation must contain digits");
        }

        // If input < 10 chars => return null
        assertNull(VICOperations.digitPermutation("abc"));

        // test null
        String input2 = null;
        perm = VICOperations.digitPermutation(input2);
        assertNull(perm);

        String input3 = "abc";
        perm = VICOperations.digitPermutation(input3);
        assertNull(perm);
    }

    @Test
    @DisplayName("Test straddlingCheckerboard & get2DTable")
    void testStraddlingCheckerboard() {
        // A valid digitPermutation
        String number = "0123456789";
        // A valid anagram with 2 spaces, total length=10
        // e.g. "AB CD EFGH"
        String letter = "AB CD EFGH";

        LinkedHashMap<Character, String> table = VICOperations.get2DTable(number, letter);
        assertNotNull(table, "Table should not be null for valid input");

        // straddlingCheckerboard
        ArrayList<String> list = VICOperations.straddlingCheckerboard(number, letter);
        assertNotNull(list);
        // Just check size if it matches 26 distinct letters?
        // Actually the result is the sortedBoard (A to Z?), but we have 10-len anagram, so let's do a simple check
        assertTrue(list.size() >= 26, "We expect codes for A-Z at least");

        String number2 = null;
        assertNull(VICOperations.get2DTable(number2, letter));
        number2 = "012345678";
        assertNull(VICOperations.get2DTable(number2, letter));
        number2 = "01234a6c89";
        assertNull(VICOperations.get2DTable(number2, letter));

        String letter2 = "abcdefghi";
        assertNull(VICOperations.get2DTable(number, letter2));
        letter2 = "0123456789";
        assertNull(VICOperations.get2DTable(number, letter2));
    }

    @Test
    @DisplayName("Test checkerboardEncode / decode basic")
    void testCheckerboardEncodeDecode() {
        String number = "0123456789";
        String anagram = "AB CD EFGH";
        String message = "ABC";

        String encoded = VICOperations.checkerboardEncode(number, anagram, message);
        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());

        String decoded = VICOperations.checkerboardDecode(number, anagram, encoded);
        assertEquals(message, decoded);

        String message2 = "Z";
        encoded = VICOperations.checkerboardEncode(number, anagram, message2);
        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());

        decoded = VICOperations.checkerboardDecode(number, anagram, encoded);
        assertEquals(message2, decoded);

    }


    @Test
    @DisplayName("Test insertID, extractID, extractEncodedMessage")
    void testInsertAndExtractID() {
        String input = "1234567890";
        String agentID = "99999";
        String date = "250314"; // last digit is '4'

        String inserted = VICOperations.insertID(input, agentID, date);
        // index = date.substring(5) => date=250314 => substring(5)='4' => int=4
        // so agentID inserted at index=4
        // input=1234 567890 => after => 1234 + 99999 + 567890
        assertEquals("123499999567890", inserted);

        String extractedID = VICOperations.extractID(inserted, date);
        assertEquals(agentID, extractedID);

        String extractedEncoded = VICOperations.extractEncodedMessage(inserted, date);
        // => substring(0,4) + substring(4+5)
        assertEquals("1234" + "567890", extractedEncoded);
        assertEquals("1234567890", extractedEncoded);
    }
}
