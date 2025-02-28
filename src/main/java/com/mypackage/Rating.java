package la1;

public enum Rating {
    UNRATED(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);
    private final int value;
    Rating(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

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
            return "☆☆☆☆☆";
        }
        int empty = 5 - value;
        return "★".repeat(value) + "☆".repeat(empty);
    }
}
