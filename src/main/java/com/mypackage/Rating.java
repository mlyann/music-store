package la1;


/**
 * Represents a rating with values ranging from 0 (unrated) to 5.
 * Each rating is associated with a numeric value and a visual representation using stars.
 */

public enum Rating {
    UNRATED(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);
    private final int value;
    Rating(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    /**
     * Converts an integer to the corresponding {@code Rating}.
     * @param value the integer value to convert
     * @return the corresponding {@code Rating} for the given value
     * @throws IllegalArgumentException if the value is not between 0 and 5
     */
    public static Rating fromInt(int value) {
        return switch (value) {
            case 0 -> UNRATED;
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> FIVE;
            default -> throw new IllegalArgumentException("Rating must be between 0 and 5 (0 means unrated).");
        };
    }

    @Override
    public String toString() {
        if (value == 0) {
            return "DEFAULT";
        }
        int empty = 5 - value;
        return "★".repeat(value) + "☆".repeat(empty);
    }
}
